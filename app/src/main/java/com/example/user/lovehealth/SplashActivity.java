package com.example.user.lovehealth;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_TIMEOUT = 1000;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //remove title & fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                if(mAuth.getCurrentUser() != null){
                    mFirestore.collection("Patients").document(mAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            finish();
                            String user_name = documentSnapshot.getString("Name");
                            if (user_name != null) {
                                startActivity(new Intent(SplashActivity.this, PatientMainMenu.class));
                            } else {
                                startActivity(new Intent(SplashActivity.this, MainCaregiverActivity.class));
                            }
                        }
                    });
                }else{
                    Intent homeIntent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(homeIntent);
                    finish();
                }
            }
        },SPLASH_TIMEOUT);
    }
}
