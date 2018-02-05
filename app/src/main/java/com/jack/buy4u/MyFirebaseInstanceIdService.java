package com.jack.buy4u;

import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Jack_Wang on 2018/1/28.
 */

//應該只會有第一次執行而已，理論上不會再次執行，以防萬一用
public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    private static final String TAG ="tokNE" ;

    @Override
    public void onTokenRefresh() {

        //取得token  ，要傳到server 裡面
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        Log.d(TAG, "Refreshed token: " + refreshedToken);
        FirebaseDatabase.getInstance().getReference("borker")
                .child("token")
                .setValue(refreshedToken);
    }
}
