package com.example.user.lovehealth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private TextView mSignUp;
    private EditText mEmail;
    private EditText mPass;
    private Button mSignIn;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

    private String mUserId;
    private String token_id;

    //For exit
    private long backPressedTime;
    private Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        mSignUp = (TextView) findViewById(R.id.sign_up);
        mEmail = (EditText)findViewById(R.id.email);
        mPass = (EditText)findViewById(R.id.password);
        mSignIn = (Button)findViewById(R.id.signIn);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setTitle("Logging in");

        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUp.class);
                startActivity(intent);
            }
        });
    }

    private void login() {
        String email = mEmail.getText().toString().trim();
        String password = mPass.getText().toString().trim();

        if(email.isEmpty()){
            mEmail.setError("Email is required");
            mEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            mEmail.setError("Please Enter a Valid Email");
            mEmail.requestFocus();
            return;
        }

        if(password.isEmpty()){
            mPass.setError("Password is required");
            mPass.requestFocus();
            return;
        }

        if(password.length() < 6){ //In Firebase, the minimum length of password must be 6
            mPass.setError("Minimum length of password must be 6");
            mPass.requestFocus();
            return;
        }

        progressDialog.show();

        //Sign in
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressDialog.cancel();
                    mUserId = mAuth.getCurrentUser().getUid();
                    final FirebaseUser user = mAuth.getCurrentUser();

                            token_id = FirebaseInstanceId.getInstance().getToken();

                            final Map<String, Object> tokenMap = new HashMap<>();
                            tokenMap.put("token_id", token_id);

                            mFirestore.collection("Patients").document(mUserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    String user_name = documentSnapshot.getString("Name");
                                    if(user_name != null) {
                                        finish();
                                        mFirestore.collection("Patients").document(mUserId).update(tokenMap);
                                        Toast.makeText(getApplicationContext(), "Login Successful. Welcome " + user.getDisplayName(),
                                                Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(LoginActivity.this, PatientMainMenu.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//Clear activity on top of the stack
                                        startActivity(intent);
                                    }else{
                                        finish();
                                        mFirestore.collection("Caregivers").document(mUserId).update(tokenMap);
                                        Toast.makeText(getApplicationContext(), "Login Successful. Welcome " + user.getDisplayName(),
                                                Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(LoginActivity.this, MainCaregiverActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//Clear activity on top of the stack
                                        startActivity(intent);
                                    }
                                }
                    });
                } else {
                    progressDialog.cancel();
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Press back twice to exit
    @Override
    public void onBackPressed() {
        if(backPressedTime + 2000 > System.currentTimeMillis()){
            backToast.cancel();
            super.onBackPressed();
            return;
        }else{
            backToast = Toast.makeText(getBaseContext(), "Press again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }

        backPressedTime = System.currentTimeMillis();
    }
}
