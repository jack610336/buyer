package com.jack.buy4u;

import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jack.buy4u.databinding.ActivityAddItemBinding;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoActivity;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.model.TakePhotoOptions;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class AddItemActivity extends TakePhotoActivity { //繼承 takephoto 的 activity

    private Button btn_selectImage,btn_submit;
    private EditText itemName,startTime,endTime,price,quantity;
    private TextView prevName,prevInfo;
    private ImageView photo;

    ActivityAddItemBinding binding;
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_item);
        Item item = new Item();
        item.setName("北海道巧克力");
        item.setPriceString("300");
        binding.setItem(item);


        findView();
    }

    private void findView() {
        photo = findViewById(R.id.prev_photo);
        prevName = findViewById(R.id.prev_name);
        prevInfo = findViewById(R.id.prev_info);
        btn_selectImage = findViewById(R.id.btn_selectImage);
        itemName = findViewById(R.id.add_itemName);
        startTime = findViewById(R.id.add_startTime);
        endTime = findViewById(R.id.add_endTime);
        price = findViewById(R.id.add_price);
        quantity = findViewById(R.id.add_quantity);
        btn_submit = findViewById(R.id.btn_submit);

        btn_selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickPhoto();

            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                final Item item = new Item();
                item.setName(itemName.getText().toString());
                try{
                    item.setStart(sdf.parse(startTime.getText().toString()));
                    item.setEnd(sdf.parse(endTime.getText().toString()));

                }catch (ParseException e) {
                    e.printStackTrace();
                }
                item.setPrice(Integer.parseInt(price.getText().toString()));
                item.setQty(Integer.parseInt(quantity.getText().toString()));
                item.setPhotoPath(imagePath);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ItemDataBase.getDatabase(AddItemActivity.this).itemDao().insert(item);
                        setResult(RESULT_OK);
                    }
                }).start();
                Log.e("TRR","onClick" + binding.getItem());
                finish();
            }
        });
    }



    private void pickPhoto(){
        File file = new File(Environment.getExternalStorageDirectory(),
                "/buy4u/" + System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        Uri uri = Uri.fromFile(file);
        TakePhoto takePhoto = getTakePhoto();
        takePhoto.setTakePhotoOptions(new TakePhotoOptions.Builder()
                .setWithOwnGallery(true)
                .create());
//        takePhoto.onPickFromCaptureWithCrop(); 拍完照或選完照片 可以設定圖片大小 存在設定的路徑中
        takePhoto.onPickFromGalleryWithCrop(uri,getCropOptions());
    }

    //設定一個 圖片的option 有 resize 功能
    private CropOptions getCropOptions() {
        int width = 720;
        int height = 600;
        CropOptions.Builder builder = new CropOptions.Builder()
                .setAspectX(width)
                .setAspectY(height)
                .setWithOwnCrop(true);
        return builder.create();
    }

    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        imagePath = result.getImage().getOriginalPath();
        Glide.with(this).load(imagePath)
                .into(photo);
    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
    }

    @Override
    public void takeCancel() {
        super.takeCancel();
    }
}
