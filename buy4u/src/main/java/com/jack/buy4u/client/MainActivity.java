package com.jack.buy4u.client;

import android.content.DialogInterface;
import android.content.Intent;
import android.drm.DrmStore;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Array;
import java.util.Arrays;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private static final int RC_SIGN_IN = 100;
    private RecyclerView recyclerView;
    private String msg;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupRecyclerView();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        auth = FirebaseAuth.getInstance();
        // Login Email example
//        auth.signInWithEmailAndPassword("jack@jack.com", "123456") //當你有登入成功 onstart 中的fun 會聽到 去執行
//            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                @Override
//                public void onComplete(@NonNull Task<AuthResult> task) {
//                    if (task.isSuccessful()) {
//                        Log.e(TAG, "onComplete : success");
//                    }else { // 在task.getException() 底下可以找到exception
//                        Log.e(TAG, "onComplete : success" + task.getException().getMessage());
//                    }
//                }
//            });
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerqq);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // adapter

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("groups")
                .child("3")
                .child("items");



        FirebaseRecyclerAdapter<Item, ItemViewHolder> adapter =
                new FirebaseRecyclerAdapter<Item, ItemViewHolder>(Item.class, R.layout.item_row, ItemViewHolder.class, ref) {
                    @Override
                    protected void populateViewHolder(ItemViewHolder viewHolder, Item model, int position) {
                        viewHolder.setModel(model);
                    }
                };
        recyclerView.setAdapter(adapter);

    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder{
        ImageView imageViewPhoto;
        TextView textViewName;
        TextView textViewPrice;
        public ItemViewHolder(View itemView) {
            super(itemView);
            imageViewPhoto = itemView.findViewById(R.id.row_photo);
            textViewName = itemView.findViewById(R.id.row_name);
            textViewPrice = itemView.findViewById(R.id.row_price);

        }

        public void setModel(final Item item){

            Glide.with(itemView.getContext())
                    .load(item.getPhotoUrl())
                    .apply(RequestOptions.overrideOf(300, 250))
                    .into(imageViewPhoto);


            textViewName.setText(item.getName());
            textViewPrice.setText(item.getPrice()+"");
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent detailIntent = new Intent(v.getContext(), DetailActivity.class);
                    detailIntent.putExtra("ITEM", item);
                    v.getContext().startActivity(detailIntent);
                }
            });
        }
    }

    private static final String TAG = MainActivity.class.getSimpleName();

    private FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            Log.e(TAG, "onAuthStateChanged :");
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) { // 有get到id
                Log.e(TAG, " uSER : " + user.getUid());
                FirebaseDatabase.getInstance().getReference("buyers")
                        .child(user.getUid())
                        .child("last")
                        .setValue(new Date());
            }else {
                //沒有get 到 id ，登入畫面 跳出註冊
//                Intent intent = new Intent(MainActivity.this, LoginAct.class);
//                startActivityForResult(intent, RC_SIGN_IN);
                startActivityForResult(AuthUI.getInstance()
                                .createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build() ,
                                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build()
                        )).build()
                        , RC_SIGN_IN);
            }
        }
    };



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (requestCode == RESULT_OK) {
                finish();
            }
        }
    }



    @Override
    protected void onStart() {
        super.onStart();


        //監聽有沒有登入過，如果有 可以跳過登入畫面直接進去，如果沒有要註冊會員
        auth.addAuthStateListener(authListener);

        Intent intent = getIntent();
        msg = intent.getStringExtra("msg");
        if (msg != null) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("This is a Title")
                    .setMessage(msg) // 將收到的文字顯示出來
                    .setPositiveButton("好! ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            msg = null;
                        }
                    })
                    .show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        auth.removeAuthStateListener(authListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_signout){
            auth.signOut();
        }



        return super.onOptionsItemSelected(item);
    }
}
