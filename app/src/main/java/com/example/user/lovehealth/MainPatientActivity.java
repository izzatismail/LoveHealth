package com.example.user.lovehealth;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.NetworkInfo;
import android.os.Vibrator;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.net.ConnectivityManager;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainPatientActivity extends AppCompatActivity implements recordDialog.dialogListener {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FloatingActionButton mFab;
    private Vibrator vibe;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference recordsRef = db.collection("Patient Records")
            .document(mAuth.getCurrentUser().getUid()).collection("Records");;
    private patientAdapter adapter;
    private ProgressDialog progressDialog;
    SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
    Calendar calendar = Calendar.getInstance();

    private String mPatId;
    private String mCareId;

    FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

    //Menu bar
    /*
    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_pat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logoutMenu:
                progressDialog.show();
                final Map<String, Object> tokenRemoveMap = new HashMap<>();
                tokenRemoveMap.put("token_id", FieldValue.delete());
                mFirestore.collection("Patients").document(mAuth.getCurrentUser().getUid()).update(tokenRemoveMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                FirebaseAuth.getInstance().signOut();
                                progressDialog.cancel();
                                Toast.makeText(getApplicationContext(), "Logout Successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainPatientActivity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//Clear activity on top of the stack
                                startActivity(intent);
                                finish();
                            }
                        });
                break;
        }
        return true;
    }
    //End of Menu Bar
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_patient);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setTitle("Logging Out");

        setUpRecyclerView();

        mFab = (FloatingActionButton) findViewById(R.id.fab);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity();
            }
        });
    }

    public void openActivity() {
        startActivity(new Intent(MainPatientActivity.this, Recording.class));
    }


    @Override
    public void apply(String name, String heartrate, String glucose, String notes, String curTime, String curDate,
                      String eaten, String criticalrate, String criticalglucose) {
        String dateNtime = sdf.format(calendar.getTime());
        //Critical Levels
        if(Integer.parseInt(heartrate) > 130 || (Double.parseDouble(glucose) < 15.0 && Double.parseDouble(glucose) >= 10.0) || Double.parseDouble(glucose) >= 15.0)
        {
            if(Integer.parseInt(heartrate) > 130)
                criticalrate = "Critical";
            else if(Integer.parseInt(heartrate) > 110)
                criticalrate = "Warning";
            else
                criticalrate = "Safe";

            if(Double.parseDouble(glucose) >= 15.0)
                criticalglucose = "DKA";
            else if(Double.parseDouble(glucose) < 15.0 && Double.parseDouble(glucose) >= 10.0)
                criticalglucose = "Critical";
            else if((Double.parseDouble(glucose) < 10.0 && Double.parseDouble(glucose) >= 7.8) || Double.parseDouble(glucose) < 4.2)
                criticalglucose = "Warning";
            else
                criticalglucose = "Safe";

            recordsRef.add(new Records(name, heartrate, curTime, curDate, glucose, notes, eaten, criticalrate, criticalglucose, dateNtime));
            sendAlert(heartrate, glucose); //Send alert notification if there's critical reading
            vibe = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
            vibe.vibrate(700);
        }

        //Warning Levels
        else if(Integer.parseInt(heartrate) > 110 || ((Double.parseDouble(glucose) < 10.0 && Double.parseDouble(glucose) >= 7.8) ||
                Double.parseDouble(glucose) < 4.2))
        {
            if(Integer.parseInt(heartrate) > 110)
                criticalrate = "Warning";
            else
                criticalrate = "Safe";

            if((Double.parseDouble(glucose) < 10.0 && Double.parseDouble(glucose) >= 7.8) || Double.parseDouble(glucose) < 4.2)
                criticalglucose = "Warning";
            else
                criticalglucose = "Safe";

            recordsRef.add(new Records(name, heartrate, curTime, curDate, glucose, notes, eaten, criticalrate, criticalglucose, dateNtime));
            Toast.makeText(getApplicationContext(), "Your readings are at Warning level",
                    Toast.LENGTH_SHORT).show();
        }

        //Safe Levels
        else{
            criticalrate = "Safe";
            criticalglucose = "Safe";

            recordsRef.add(new Records(name, heartrate, curTime, curDate, glucose, notes, eaten, criticalrate, criticalglucose, dateNtime));
            Toast.makeText(getApplicationContext(), "Readings Recorded Successfully",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void setUpRecyclerView(){
        Query query = recordsRef.orderBy("dateNtime", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Records> options = new FirestoreRecyclerOptions.Builder<Records>()
                .setQuery(query, Records.class)
                .build();

        adapter = new patientAdapter(options);

        final RecyclerView recyclerView = findViewById(R.id.rec_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    mFab.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 ||dy<0 && mFab.isShown())
                    mFab.hide();
            }
        });

        //Swipe to Delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.deleteItem(viewHolder.getAdapterPosition(), recyclerView);
            }
        }).attachToRecyclerView(recyclerView);
    }

    //To know when the app need to start or stop listening
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    //Notification codes
    public void sendAlert(final String a, final String b){
        final alertDialog alertDialog = new alertDialog();
        alertDialog.show(getSupportFragmentManager(), "Alert");

        mPatId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mFirestore.collection("Patients").document(mPatId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        mCareId = documentSnapshot.getString("Caregiver ID");
                        Map<String, Object>notificationMessage = new HashMap<>();
                        notificationMessage.put("Message", "LoveHealth detected critical readings");
                        notificationMessage.put("From", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                        notificationMessage.put("pat_id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        notificationMessage.put("Heartrate", a);
                        notificationMessage.put("Glucose", b);
                        mFirestore.collection("Caregivers/" + mCareId +"/Notifications").add(notificationMessage);
                    }
                });
    }
}