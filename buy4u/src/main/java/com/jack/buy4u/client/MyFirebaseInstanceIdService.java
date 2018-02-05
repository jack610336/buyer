package com.jack.buy4u.client;

import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Jack_Wang on 2018/2/4.
 */

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
