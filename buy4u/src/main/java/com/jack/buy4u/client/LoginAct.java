package com.jack.buy4u.client;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginAct extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks{


    private EditText edUserid;
    private EditText edPassword;
    private Button submit , google;
    private FirebaseAuth auth;
    private GoogleApiClient apiClient;
    private int RC_GOOGLE_SING_IN = 100;
    private static final String TAG = LoginAct.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        edUserid = findViewById(R.id.edit_id);
        edPassword = findViewById(R.id.edit_pw);

        GoogleSignInOptions gso =  //先取得註冊完畢的憑證 TOKEN
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.google_app_client_id1))
                        .requestEmail()
                        .build();
        apiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        submit = findViewById(R.id.submit);
        google = findViewById(R.id.googleBtn);
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                google(v);
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(v);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        apiClient.connect();// 連線
        apiClient.registerConnectionCallbacks(this); // 連線完成時候呼叫apiCLient


    }

    @Override
    protected void onStop() {
        super.onStop();
        apiClient.disconnect();//當沒看到畫面就可以做斷線的動作
        apiClient.unregisterConnectionCallbacks(this);

    }

    public void login(View view){
        String userid = edUserid.getText().toString();
        String passwd = edPassword.getText().toString();
//        auth.createUserWithEmailAndPassword() //註冊一個新帳號
        auth.signInWithEmailAndPassword(userid, passwd)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(LoginAct.this, "Success", Toast.LENGTH_LONG).show();
                            setResult(RESULT_OK);
                            finish();
                        }else{
                            Toast.makeText(LoginAct.this, "Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void google(View view) {

        Intent singIntent = Auth.GoogleSignInApi.getSignInIntent(apiClient); //google 內建的登入Intent
        startActivityForResult(singIntent,RC_GOOGLE_SING_IN);// 回來後做甚麼事情
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GOOGLE_SING_IN){ // 剛剛登入畫面回來後 進來這裡

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data); // 取得剛剛登入的結果
            if (result.isSuccess()) {//如果登入有成功 做下列的事情
                GoogleSignInAccount account = result.getSignInAccount();
                Log.d(TAG, "onActivityResult: " + account.getEmail());
                Log.d(TAG, "onActivityResult: " + account.getIdToken());
                firebaseAuthWithGoogle(account); //把登入後的google token,驗證完成後 加入firebase database
            }else{ //沒有 登入失敗
                Log.d(TAG, "onActivityResult: " + result.getStatus().toString());
            }

        }

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){ //如果驗證成功
                    Log.d(TAG, "firebaseAuthWithGoogle: ");
                    Toast.makeText(LoginAct.this,
                            "Sign in Google success", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                }else{
                    Toast.makeText(LoginAct.this,
                            "Sign in Google failed", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

//        當你lonin完成後，最近到這個方法裡面
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
