package com.example.user.lovehealth;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PatientHomeMenu extends AppCompatActivity {

    GridLayout gridLayout;
    TextView menuTitle;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ProgressDialog progressDialog;
    FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    AlertDialog.Builder confirmationDialog;

    //For exit
    private long backPressedTime;
    private Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_home_menu);

        gridLayout = (GridLayout)findViewById(R.id.menuGrid);
        menuTitle = (TextView)findViewById(R.id.mainMenuTitle);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setTitle("Logging Out");
        progressDialog.setCanceledOnTouchOutside(false);

        final FirebaseUser user = mAuth.getCurrentUser();
        menuTitle.setText(user.getDisplayName());

        setEvent(gridLayout);
        checkInternet();
    }

    public void setEvent(GridLayout gridLayout){
        //Loop all child item in the grid
        confirmationDialog = new AlertDialog.Builder(PatientHomeMenu.this);
        for(int i=0;i<gridLayout.getChildCount();i++){
            CardView cardView = (CardView)gridLayout.getChildAt(i);
            final int index = i;
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(index == 0)
                        startActivity(new Intent(PatientHomeMenu.this, GuideActivity.class));
                    else if(index == 1)
                        startActivity(new Intent(PatientHomeMenu.this, MainPatientActivity.class));
                    else if(index == 2)
                        startActivity(new Intent(PatientHomeMenu.this, HistoryData.class));
                    else if(index == 3) {
                        AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(PatientHomeMenu.this);
                        confirmationDialog.setTitle("Confirm Logout");
                        confirmationDialog.setMessage("Are you sure you want to log out?");
                        confirmationDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                progressDialog.show();
                                final Map<String, Object> tokenRemoveMap = new HashMap<>();
                                tokenRemoveMap.put("token_id", FieldValue.delete());
                                mFirestore.collection("Patients").document(mAuth.getCurrentUser().getUid()).update(tokenRemoveMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                FirebaseAuth.getInstance().signOut();
                                                progressDialog.cancel();
                                                finish();
                                                Toast.makeText(PatientHomeMenu.this, "Logout Successful", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(PatientHomeMenu.this, LoginActivity.class));
                                            }
                                        });
                            }
                        });
                        confirmationDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        confirmationDialog.create().show();
                    }
                }
            });
        }
    }

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

    public void checkInternet(){
        boolean connected = false;
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
                || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED){
            connected = true;
        }else{
            connected = false;
        }

        if(connected) {
            Snackbar.make(gridLayout, "You are connected to the internet", Snackbar.LENGTH_SHORT).show();
        }else{
            //Snackbar.make(gridLayout,"You are not connected to the internet", Snackbar.LENGTH_SHORT).show();
            alert.setTitle("No Internet Detected");
            alert.setMessage("Your readings will be save locally, but your Caregiver would not be able to receive" +
                    " critical reading alerts. \n\nYour readings will be saved into the database once there is an " +
                    "internet connection.");
            alert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            alert.create().show();
        }
    }
}