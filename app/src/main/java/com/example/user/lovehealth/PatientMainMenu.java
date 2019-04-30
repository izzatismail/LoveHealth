package com.example.user.lovehealth;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class PatientMainMenu extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    AlertDialog.Builder confirmationDialog;
    private DrawerLayout mDrawerLayout;
    private ProgressDialog progressDialog;
    TextView drawerTitle;
    private FloatingActionButton mFab;
    private CollectionReference recordsRef;
    private LineChart mChartGlucose;

    //Average TextViews
    TextView mAvgAwake;
    TextView mBBreak;
    TextView mABreak;
    TextView mBLunch;
    TextView mALunch;
    TextView mBDinner;
    TextView mADinner;
    TextView mAvgBed;

    //Average variable;
    DecimalFormat df = new DecimalFormat("#.#");
    Double avgAfterAwake = 0.0;
    Double avgBeforeB = 0.0;
    Double avgAfterB = 0.0;
    Double avgBeforeL = 0.0;
    Double avgAfterL = 0.0;
    Double avgBeforeD = 0.0;
    Double avgAfterD = 0.0;
    Double avgBeforeBed = 0.0;

    int countAfterAwake = 0;
    int countBeforeB = 0;
    int countAfterB = 0;
    int countBeforeL = 0;
    int countAfterL = 0;
    int countBeforeD = 0;
    int countAfterD = 0;
    int countBeforeBed = 0;

    //For exit
    private long backPressedTime;
    private Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_main_menu);

        recordsRef = mFirestore.collection("Patient Records").document(mAuth.getCurrentUser().getUid()).collection("Records");

        mFab = (FloatingActionButton) findViewById(R.id.fab2);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PatientMainMenu.this, Recording.class));
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setTitle("Logging Out");
        progressDialog.setCanceledOnTouchOutside(false);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        drawerTitle = (TextView) headerView.findViewById(R.id.nav_pat_name);
        FirebaseUser user = mAuth.getCurrentUser();
        drawerTitle.setText(user.getDisplayName());

        mAvgAwake = (TextView)findViewById(R.id.avgAwake);
        mBBreak = (TextView)findViewById(R.id.avgBBreakfast);
        mABreak = (TextView)findViewById(R.id.avgABreakfast);
        mBLunch = (TextView)findViewById(R.id.avgBLunch);
        mALunch = (TextView)findViewById(R.id.avgALunch);
        mBDinner = (TextView)findViewById(R.id.avgBDinner);
        mADinner = (TextView)findViewById(R.id.avgADinner);
        mAvgBed = (TextView)findViewById(R.id.avgBed);

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        switch (menuItem.getItemId()){
                            case R.id.nav_records:
                                startActivity(new Intent(PatientMainMenu.this, MainPatientActivity.class));
                                break;
                            case R.id.nav_history:
                                startActivity(new Intent(PatientMainMenu.this, HistoryData.class));
                                break;
                            case R.id.nav_guide:
                                startActivity(new Intent(PatientMainMenu.this, GuideActivity.class));
                                break;
                            case R.id.nav_logout:
                                logout();
                                break;
                        }

                        return true;
                    }
                });

        //Chart
        mChartGlucose = findViewById(R.id.summaryGraph);
        mChartGlucose.setDragEnabled(true);
        mChartGlucose.setScaleEnabled(true);
        mChartGlucose.getDescription().setEnabled(false);
        final ArrayList<Entry> yValuesGlucose = new ArrayList<>();

        recordsRef.orderBy("dateNtime",Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override

            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                Log.v("Sizes", "" + queryDocumentSnapshots.size());
                int index = 0;

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Records records = documentSnapshot.toObject(Records.class);

                    if(records.getEaten().equals("After awake")){
                        avgAfterAwake+=Double.parseDouble(records.getGlucose());
                        countAfterAwake++;
                    }else if(records.getEaten().equals("Before breakfast")){
                        avgBeforeB+=Double.parseDouble(records.getGlucose());
                        countBeforeB++;
                    }else if(records.getEaten().equals("After breakfast")) {
                        avgAfterB += Double.parseDouble(records.getGlucose());
                        countAfterB++;
                    }else if(records.getEaten().equals("Before lunch")) {
                        avgBeforeL += Double.parseDouble(records.getGlucose());
                        countBeforeL++;
                    }else if(records.getEaten().equals("After lunch")) {
                        avgAfterL += Double.parseDouble(records.getGlucose());
                        countAfterL++;
                    }else if(records.getEaten().equals("Before dinner")) {
                        avgBeforeD += Double.parseDouble(records.getGlucose());
                        countBeforeD++;
                    }else if(records.getEaten().equals("After dinner")) {
                        avgAfterD += Double.parseDouble(records.getGlucose());
                        countAfterD++;
                    }else if(records.getEaten().equals("Before bed")){
                        avgBeforeBed+=Double.parseDouble(records.getGlucose());
                        countBeforeBed++;
                    }

                    Integer heartrate = Integer.parseInt(records.getHeartrate());
                    Float glucose = Float.parseFloat(records.getGlucose());

                    yValuesGlucose.add(new Entry(index,glucose));
                    index++;
                }
                LineDataSet set2 = new LineDataSet(yValuesGlucose, "Glucose (mmol/L)");

                set2.setFillAlpha(110);
                set2.setColor(Color.BLUE);
                set2.setCircleColor(Color.BLUE);
                set2.setLineWidth(2f);

                ArrayList<ILineDataSet> dataSetsGlucose = new ArrayList<>();
                dataSetsGlucose.add(set2);

                LineData dataGlucose = new LineData(dataSetsGlucose);

                mChartGlucose.setData(dataGlucose);

                //Average
                avgAfterAwake = avgAfterAwake / countAfterAwake;
                avgBeforeB = avgBeforeB / countBeforeB;
                avgAfterB = avgAfterB / countAfterB;
                avgBeforeL = avgBeforeL / countBeforeL;
                avgAfterL = avgAfterL / countAfterL;
                avgBeforeD = avgBeforeD / countBeforeD;
                avgAfterD = avgAfterD / countAfterD;
                avgBeforeBed = avgBeforeBed / countBeforeBed;

                //setText
                if(!avgAfterAwake.isNaN())
                    mAvgAwake.setText(df.format(avgAfterAwake));
                if(!avgBeforeB.isNaN())
                    mBBreak.setText(df.format(avgBeforeB));
                if(!avgAfterB.isNaN())
                    mABreak.setText(df.format(avgAfterB));
                if(!avgBeforeL.isNaN())
                    mBLunch.setText(df.format(avgBeforeL));
                if(!avgAfterL.isNaN())
                    mALunch.setText(df.format(avgAfterL));
                if(!avgBeforeD.isNaN())
                    mBDinner.setText(df.format(avgBeforeD));
                if(!avgAfterD.isNaN())
                    mADinner.setText(df.format(avgAfterD));
                if(!avgBeforeBed.isNaN())
                    mAvgBed.setText(df.format(avgBeforeBed));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(this.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.mDrawerLayout.closeDrawer(GravityCompat.START);
        }else {
            if (backPressedTime + 2000 > System.currentTimeMillis()) {
                backToast.cancel();
                super.onBackPressed();
                return;
            } else {
                backToast = Toast.makeText(getBaseContext(), "Press again to exit", Toast.LENGTH_SHORT);
                backToast.show();
            }
            backPressedTime = System.currentTimeMillis();
        }
    }

    public void logout(){
        confirmationDialog = new AlertDialog.Builder(PatientMainMenu.this);
        AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(PatientMainMenu.this);
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
                                Toast.makeText(PatientMainMenu.this, "Logout Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(PatientMainMenu.this, LoginActivity.class));
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
