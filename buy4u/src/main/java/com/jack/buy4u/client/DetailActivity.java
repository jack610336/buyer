package com.jack.buy4u.client;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DetailActivity extends AppCompatActivity {

    private Item item;
    private String uid;
    private TextView tv_startTime,tv_endTime,tv_preName,tv_Price,tv_qty;
    private ImageView imgv_PreProduct;
    private Button btn_purchase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        item = (Item) getIntent().getSerializableExtra("ITEM");
        //取得驗證 再取得UID;
        FirebaseAuth auth = FirebaseAuth.getInstance();
        uid = auth.getCurrentUser().getUid();

        findViews();


        btn_purchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        tv_qty.setText(String.valueOf(item.getQty()));
        tv_Price.setText(String.valueOf(item.getPrice()));
        tv_preName.setText(String.valueOf(item.getName()));
        tv_startTime.setText(String.valueOf(item.getStarFormatted()));
        tv_endTime.setText(String.valueOf(item.getEndFormatted()));
    }

    private void findViews() {
        tv_endTime = findViewById(R.id.endTime);
        tv_startTime = findViewById(R.id.startTime);
        tv_preName = findViewById(R.id.prev_name);
        tv_Price = findViewById(R.id.price);
        tv_qty = findViewById(R.id.qty);
        imgv_PreProduct = findViewById(R.id.prev_photo);
        btn_purchase = findViewById(R.id.btnBuy);
    }

    public void buy(View view) {
        Order order = new Order(uid, item.getId(), item.getGroupId(), 1);
        DatabaseReference orders = FirebaseDatabase.getInstance()
                .getReference("buyers")
                .child(uid)
                .child("orders").push();
        orders.setValue(order);


    }
}
