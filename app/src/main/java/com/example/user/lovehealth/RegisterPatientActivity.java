package com.example.user.lovehealth;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class RegisterPatientActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private EditText mName;
    private EditText mPhone;
    private EditText mAge;
    private EditText mEmail;
    private EditText mPass;
    private EditText mConPass;
    private Button mSignUp;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private ProgressBar mBar;
    String gender;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_patient);

        mName = (EditText)findViewById(R.id.patName);
        mAge = (EditText)findViewById(R.id.patAge);
        mPhone = (EditText)findViewById(R.id.patPhone);
        mEmail = (EditText)findViewById(R.id.patEmail);
        mPass = (EditText)findViewById(R.id.patPass);
        mConPass = (EditText)findViewById(R.id.patConPass);
        mSignUp = (Button)findViewById(R.id.newPat);
        mBar = (ProgressBar)findViewById(R.id.progressBar2);
        mFirestore = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();

        //Spinners
        Spinner spinnerGender = findViewById(R.id.patGender);
        final Spinner spinnerType = findViewById(R.id.patDiabetesType);

        //ArrayAdapter to show the content of the spinner
        ArrayAdapter<CharSequence> adapterGender = ArrayAdapter.createFromResource(this,
                R.array.gender, android.R.layout.simple_spinner_item);
        adapterGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapterGender);
        spinnerGender.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> adapterType = ArrayAdapter.createFromResource(this,
                R.array.type, android.R.layout.simple_spinner_item);
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapterType);
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = spinnerType.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //End for Spinners

        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerPatient();
            }
        });
    }

    //Override for Gender
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        gender = parent.getItemAtPosition(position).toString();
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void registerPatient(){
        final String name = mName.getText().toString().trim();
        final String phone = mPhone.getText().toString().trim();
        final String age = mAge.getText().toString().trim();
        String email = mEmail.getText().toString().trim();
        String password = mPass.getText().toString().trim();
        String confirmPass = mConPass.getText().toString().trim();
        final String careName = mAuth.getCurrentUser().getDisplayName();
        final String careId = mAuth.getCurrentUser().getUid();

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

        if(age.isEmpty()){
            mAge.setError("Age is not specified");
            mAge.requestFocus();
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

                    //To store more than 1 object in the database, use HashMap
                    HashMap<String, String> dataMap = new HashMap<String, String>();
                    dataMap.put("Name",name);
                    dataMap.put("Age",age);
                    dataMap.put("Gender",gender);
                    dataMap.put("Diabetes Type",type);
                    dataMap.put("Phone",phone);
                    dataMap.put("Caregiver Name",careName);
                    dataMap.put("Caregiver ID",careId);

                    HashMap<String, String> caregiverMap = new HashMap<String, String>();
                    caregiverMap.put("pat_name",name);
                    caregiverMap.put("pat_id",user_id);
                    caregiverMap.put("pat_age",age);
                    caregiverMap.put("pat_gender",gender);

                    mFirestore.collection("Patients").document(user_id).set(dataMap);
                    mFirestore.collection("Caregivers/" + careId +"/Patients List").add(caregiverMap);

                    if(user != null){
                        UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder().setDisplayName(name)
                                .build();

                        user.updateProfile(profile);
                    }
                    mBar.setVisibility(View.GONE);
                    finish();
                    Toast.makeText(getApplicationContext(),"Patient Registration Successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterPatientActivity.this, MainCaregiverActivity.class);
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
