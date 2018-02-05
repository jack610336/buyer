package com.jack.buy4u;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemDetailActivity extends AppCompatActivity {

    private ImageView photo;
    private TextView name;
    private TextView info;
    private TextView startTime;
    private TextView endTime;
    private TextView price;
    private TextView qty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itemdetail);

        findViews();

    }

    private void findViews() {
        photo = findViewById(R.id.prev_photo);
        name = findViewById(R.id.prev_name);
        info = findViewById(R.id.prev_info);
        startTime = findViewById(R.id.startTime);
        endTime = findViewById(R.id.endTime);
        price = findViewById(R.id.price);
        qty = findViewById(R.id.qty);
    }


}
