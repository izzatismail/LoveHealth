package com.example.user.lovehealth;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignUp extends AppCompatActivity {

    private EditText mName;
    private EditText mPhone;
    private EditText mEmail;
    private EditText mPass;
    private EditText mConPass;
    private Button mSignUp;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private ProgressBar mBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mName = (EditText)findViewById(R.id.newName);
        mPhone = (EditText)findViewById(R.id.newPhone);
        mEmail = (EditText)findViewById(R.id.newEmail);
        mPass = (EditText)findViewById(R.id.newPass);
        mConPass = (EditText)findViewById(R.id.confirmPass);
        mSignUp = (Button)findViewById(R.id.newsignUp);
        mBar = (ProgressBar)findViewById(R.id.progressBar);

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerCaregiver();
            }
        });
    }

    private void registerCaregiver() {
        final String name = mName.getText().toString().trim();
        String email = mEmail.getText().toString().trim();
        final String phone = mPhone.getText().toString().trim();
        String password = mPass.getText().toString().trim();
        String confirmPass = mConPass.getText().toString().trim();

        if(name.isEmpty()){
            mName.setError("Name is missing");
            mName.requestFocus();
            return;
        }

        if(phone.isEmpty()){
            mPhone.setError("Phone number is missing");
            mPhone.requestFocus();
            return;
        }

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

        if(!password.equals(confirmPass)){
            mConPass.setError("Password confirmation is incorrect");
            mConPass.requestFocus();
            return;
        }

        mBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //If successful
                    FirebaseUser user = mAuth.getCurrentUser();
                    String user_id =  mAuth.getCurrentUser().getUid();
                    HashMap<String, String> dataMap = new HashMap<String, String>();
                    dataMap.put("Name",name);
                    dataMap.put("Phone",phone);

                    mFirestore.collection("Caregivers").document(user_id).set(dataMap);
                    if(user != null){
                        UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder().setDisplayName(name)
                                .build();

                        user.updateProfile(profile);
                    }
                    mBar.setVisibility(View.GONE);
                    finish();
                    Toast.makeText(getApplicationContext(),"User Registration Successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUp.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }else{
                    //If there's already an email registered
                    if(task.getException() instanceof FirebaseAuthUserCollisionException){
                        mBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(),"This Email is already used", Toast.LENGTH_LONG).show();
                    }else {
                        mBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
}
