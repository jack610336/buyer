package com.jack.buy4u;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by Jack_Wang on 2018/1/28.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("FCM", "onMessageReceived: " + remoteMessage.getFrom() +"/" +
                remoteMessage.getMessageType());

        if (remoteMessage.getData() != null){
            Map<String, String> map = remoteMessage.getData();
            Log.d(TAG, "onMessageReceived: " + map.get("orderKey"));
        }
    }
}
