package com.jack.buy4u;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.amitshekhar.DebugDB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GroupDialogFragment.OnGroupNameListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int RC_ADD_ITEM = 200;
    private List<Group> groups;
    private int groupId;
    private RecyclerView recyclerView;
    private ItemRecycleAdapter adapter;
    private List<Item> items;
    private ArrayAdapter<Group> groupAdapter;
    private Spinner groupSpinner;
    private ProgressBar progressBar;



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DebugDB.getAddressLog();

        setupRecyclerView();
        progressBar = findViewById(R.id.progress);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        groupId = PreferenceManager.getDefaultSharedPreferences(this)
                .getInt("group_id",0);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddItemActivity.class);
                startActivityForResult(intent,RC_ADD_ITEM);
            }
        });

        Cursor cursor =
        BuyDBHelper.getInstance(this).getReadableDatabase()
                .query("groups",null,null,null,null,null,null);
        if(cursor.getCount() <=5){
            showGroupNameDialog();
        }

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("Hello, World!");
        //write to Firebase
//        writeToFirebase();

    }

    private void writeToFirebase() {
        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        DatabaseReference contacts = firebase.getReference("contacts");

        FireBaseContacts c = new FireBaseContacts();
        c.setName("TOM");
        c.setPhone("09104587896");
        contacts.child("2").setValue(c).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
//                task.isComplete()  完成時 可能成功或失敗
//                task.isSuccessful() 成功時
                Log.e("Successful :",task.isSuccessful() + "");
                if (task.isSuccessful()) {
                    readFormFirebase();
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_ADD_ITEM && resultCode == RESULT_OK){
            setupRecyclerView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupRecyclerView();
    }

    private void readFormFirebase() {
        FirebaseDatabase firebase = FirebaseDatabase.getInstance();//建立資料庫連線
        //參考遠端資料庫的 Reference
        //重資料庫取出contacts集合所有資料 也可以只取出特定位置 如下
        //firebase.getReference("contacts/xxx/fgf/cx");
        DatabaseReference contactsReference = firebase.getReference("contacts");


        //只想要抓取一次資料 所以不必更新利用下行
        //contactsReference.addListenerForSingleValueEvent();

        //讀一個值，持續監聽，有改變時會監聽
        //contactsReference.addValueEventListener()


        contactsReference.addChildEventListener(new ChildEventListener() {
            @Override //資料來了會做的第一件事情
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //firebase 讀回來的資料 可以直接在程式中 建立一個表格對應，直接將它塞進去本機的model
                FireBaseContacts contacts = dataSnapshot.getValue(FireBaseContacts.class);

                Log.e("sds", contacts.getName() + contacts.getPhone());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        setupItemTouchHelper();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                items = ItemDataBase.getDatabase(MainActivity.this).itemDao().getAll();
                Log.e("remove 之前",items.toString());

                Log.d(TAG, "setupRecyclerView: size:" + items.size());
                adapter = new ItemRecycleAdapter(items);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setAdapter(adapter);
                    }
                });
                //uploadToFirebase();
            }
        });
    }

    private void uploadToFirebase() {
        if (items != null) {

            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setAlpha(0.5f);
            progressBar.setMax(items.size());
            FirebaseDatabase firebase = FirebaseDatabase.getInstance();
            final DatabaseReference groups = firebase.getReference("groups");
            for (final Item item : items) {
                //
                groups.child(groupId + "")
                        .child("items")
                        .child(item.getId()+"")
                        .setValue(item).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.e("TASK　", task.isSuccessful()+"");
                    }
                });

                // photo file to firebase Storage
                Uri uri = Uri.fromFile(new File(item.getPhotoPath()));// 路徑 轉 uri

                StorageReference ref = FirebaseStorage.getInstance().getReference("photos"+item.getId());
                ref.child(System.currentTimeMillis() + "");
                ref.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            //可以在這裡做進度條的部分
                            String photoUrl = task.getResult().getDownloadUrl().toString();
                            groups.child(groupId + "")
                                    .child("items")
                                    .child(item.getId() + "")
                                    .child("photoUrl")
                                    .setValue(photoUrl);
                            progressBar.incrementProgressBy(1);
                        }else {

                        }

                        if (items.lastIndexOf(item) == items.size()-1){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this,
                                            "Upload completed", Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);
                                    recyclerView.setAlpha(1);

                                }
                            });
                        }

                    }
                });
            }
        }
    }

    private void setupItemTouchHelper() {
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {//第一個值為上下滑 第二的值為左右滑

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();

                items.remove(position);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        items = ItemDataBase.getDatabase(MainActivity.this).itemDao().getAll();
                        ItemDataBase.getDatabase(MainActivity.this).itemDao().delete(items.get(position));
                        setResult(RESULT_OK);
                    }
                }).start();
                Log.e("remove 之後",items.toString());
                setupRecyclerView();
                adapter.notifyDataSetChanged();
            }
        };
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
    }

    private void showGroupNameDialog() {
        GroupDialogFragment dialog = new GroupDialogFragment();
        dialog.show(getFragmentManager(),"groupDialog");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.getItem(0);

        groupSpinner = (Spinner) item.getActionView();
        setupGroupSpinner();

            return true;
    }

    private void setupGroupSpinner() {
        Cursor cursor = BuyDBHelper.getInstance(this)
                .getReadableDatabase()
                .query("groups", null, null, null, null, null, null);

        groups = new ArrayList<>();
        int selectedIndex = 0;
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            int id = cursor.getInt(cursor.getColumnIndex("_id"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            groups.add(new Group(id, name));
            if (id == groupId){
                selectedIndex = i;
            }
        }
        groupAdapter  = new ArrayAdapter<Group>(this,
                        android.R.layout.simple_list_item_1, groups);
        groupSpinner.setAdapter(groupAdapter);
        groupSpinner.setOnItemSelectedListener(this);
        groupSpinner.setSelection(selectedIndex);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.action_upload_firebase:
                uploadToFirebase();
                break;
            case R.id.action_groups:
                showGroupNameDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void groupNameInputCompleted(String name) {
        Log.d(TAG, "groupNameInputCompleted: "+ name);
        ContentValues values = new ContentValues();
        values.put("name",name);
        BuyDBHelper.getInstance(this)
                .getWritableDatabase()
                .insert("groups",null,values);
        setupGroupSpinner();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Log.d(TAG, "onItemSelected: " + position);
        Group group = groups.get(position);
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putInt("group_id", group.getId())
                .apply();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
