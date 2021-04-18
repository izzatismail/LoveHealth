package com.example.user.lovehealth;

import android.app.AlertDialog;
import android.app.Dialog;
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
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Recording extends AppCompatActivity{

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference recordsRef = db.collection("Patient Records")
            .document(mAuth.getCurrentUser().getUid()).collection("Records");
    private Vibrator vibe;


    FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

    Boolean isListeningHeartRate = false;
    Boolean checkedHR = false;
    Boolean connected = false;

    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    BluetoothGatt bluetoothGatt;
    BluetoothDevice bluetoothDevice;

    Spinner spinnerNotes;
    Spinner spinnerMeasure;
    FrameLayout mFrame;
    FrameLayout mFrame2;
    Button btnStartConnecting, mGetHR, btnSubmit, btnCancel;
    TextView txtState, txtDot, txtRecGlu, txtHow, txtHeartrate, txtBPM, txtMeasured;
    NumberPicker mPick;
    NumberPicker mPick2;
    private ProgressDialog progressDialog;

    String notes;
    String measures;
    String heartrateBPM;
    private String mPatId;
    private String mCareId;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a");
    SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("EEE, MMM d, ''yy");
    SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
    Calendar calendar = Calendar.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);

        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }

        initilaizeComponents();
        initializeEvents();

    }

    void initilaizeComponents() {
        btnStartConnecting = (Button) findViewById(R.id.btnStartConnecting);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        mGetHR = (Button) findViewById(R.id.btnGetHeartrate);
        txtState = (TextView) findViewById(R.id.txtState);
        txtBPM = (TextView) findViewById(R.id.txtRealHR);
        spinnerMeasure = (Spinner) findViewById(R.id.check_eaten);
        mPick = (NumberPicker) findViewById(R.id.glucose_picker);
        mPick2 = (NumberPicker) findViewById(R.id.glucose_picker_dec);
        mFrame = (FrameLayout) findViewById(R.id.frameRecord);
        mFrame2 = (FrameLayout) findViewById(R.id.frameRecord2);
        txtDot = (TextView) findViewById(R.id.justADot);
        txtRecGlu = (TextView) findViewById(R.id.txtGlucose);
        txtHow = (TextView) findViewById(R.id.txtNotes);
        txtHeartrate = (TextView) findViewById(R.id.txtHR);
        txtMeasured = (TextView) findViewById(R.id.txtMeasure);
        spinnerNotes = (Spinner) findViewById(R.id.spin_notes);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please stay still..");
        progressDialog.setTitle("Reading Heartrate");
        progressDialog.setCanceledOnTouchOutside(false);

//        btnSubmit.setVisibility(View.INVISIBLE);
//        btnCancel.setVisibility(View.INVISIBLE);
//        mGetHR.setVisibility(View.INVISIBLE);
//        txtBPM.setVisibility(View.INVISIBLE);
//        spinnerMeasure.setVisibility(View.INVISIBLE);
//        mPick2.setVisibility(View.INVISIBLE);
//        mPick.setVisibility(View.INVISIBLE);
//        mFrame.setVisibility(View.INVISIBLE);
//        mFrame2.setVisibility(View.INVISIBLE);
//        txtDot.setVisibility(View.INVISIBLE);
//        txtRecGlu.setVisibility(View.INVISIBLE);
//        txtHow.setVisibility(View.INVISIBLE);
//        txtHeartrate.setVisibility(View.INVISIBLE);
//        txtMeasured.setVisibility(View.INVISIBLE);
//        spinnerNotes.setVisibility(View.INVISIBLE);

        mPick.setMinValue(1);
        mPick.setMaxValue(20);
        mPick2.setMinValue(0);
        mPick2.setMaxValue(9);

        //ArrayAdapter to show the content of the spinner
        ArrayAdapter<CharSequence> adapterNotes = ArrayAdapter.createFromResource(this,
                R.array.notes, android.R.layout.simple_spinner_item);
        adapterNotes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNotes.setAdapter(adapterNotes);
        spinnerNotes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                notes = spinnerNotes.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<CharSequence> adapterMeasure = ArrayAdapter.createFromResource(this,
                R.array.measure, android.R.layout.simple_spinner_item);
        adapterMeasure.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMeasure.setAdapter(adapterMeasure);
        spinnerMeasure.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                measures = spinnerMeasure.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //End for Spinners
    }

    void initializeEvents() {
        btnStartConnecting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startConnecting();
            }
        });
        mGetHR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!connected)
                    Toast.makeText(v.getContext(), "You are not connected to smartwatch. Please wait until State is Connected and try again",Toast.LENGTH_SHORT).show();
                else
                    startScanHeartRate();

            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkedHR)
                    Toast.makeText(v.getContext(), "You have not checked your heartrate",Toast.LENGTH_SHORT).show();
                else{
                    submitReadings();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    void startConnecting() {

        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not turned on. Please turn it on and try again",Toast.LENGTH_SHORT).show();
            return;
        }

        if(bluetoothAdapter != null && !bluetoothAdapter.isEnabled()){
            AlertDialog.Builder startBT = new AlertDialog.Builder(Recording.this);
            startBT.setTitle("Bluetooth Is Not Enabled");
            startBT.setMessage("Bluetooth is currently disabled. Please Enable Bluetooth to connect with smartwatch");
            startBT.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (!bluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, 1);
                    }
                }
            });
            startBT.create().show();
        }else{
            //String address = txtPhysicalAddress.getText().toString();
            String address = "C9:3F:B2:2E:BB:F9";
            bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);

            Log.v("test", "Connecting to " + address);
            Log.v("test", "Device name " + bluetoothDevice.getName());

            bluetoothGatt = bluetoothDevice.connectGatt(this, true, bluetoothGattCallback);

            txtHeartrate.setVisibility(View.VISIBLE);
            btnCancel.setVisibility(View.VISIBLE);
            mGetHR.setVisibility(View.VISIBLE);
            txtBPM.setVisibility(View.VISIBLE);
            mFrame.setVisibility(View.VISIBLE);
            btnSubmit.setVisibility(View.VISIBLE);
            spinnerMeasure.setVisibility(View.VISIBLE);
            mPick2.setVisibility(View.VISIBLE);
            mPick.setVisibility(View.VISIBLE);
            mFrame2.setVisibility(View.VISIBLE);
            txtDot.setVisibility(View.VISIBLE);
            txtRecGlu.setVisibility(View.VISIBLE);
            txtHow.setVisibility(View.VISIBLE);
            txtMeasured.setVisibility(View.VISIBLE);
            spinnerNotes.setVisibility(View.VISIBLE);
        }
    }

    void stateConnected() {
        bluetoothGatt.discoverServices();
        txtState.setText("Connected");
    }

    void stateDisconnected() {
        bluetoothGatt.disconnect();
        txtState.setText("Disconnected");
    }

    void startScanHeartRate() {
        txtBPM.setText("Reading...");
        progressDialog.show();
        checkedHR = true;
        BluetoothGattCharacteristic bchar = bluetoothGatt.getService(UUIDs.HeartRate.service)
                .getCharacteristic(UUIDs.HeartRate.controlCharacteristic);
        bchar.setValue(new byte[]{21, 2, 1});
        bluetoothGatt.writeCharacteristic(bchar);
    }

    void listenHeartRate() {
        BluetoothGattCharacteristic bchar = bluetoothGatt.getService(UUIDs.HeartRate.service)
                .getCharacteristic(UUIDs.HeartRate.measurementCharacteristic);
        bluetoothGatt.setCharacteristicNotification(bchar, true);
        BluetoothGattDescriptor descriptor = bchar.getDescriptor(UUIDs.HeartRate.descriptor);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        bluetoothGatt.writeDescriptor(descriptor);
        isListeningHeartRate = true;
    }

    final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.v("test", "onConnectionStateChange");

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                connected = true;
                stateConnected();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                stateDisconnected();
            }

        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            Log.v("test", "onServicesDiscovered");
            listenHeartRate();
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Log.v("test", "onCharacteristicRead");
            byte[] data = characteristic.getValue();
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.v("test", "onCharacteristicWrite");
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            Log.v("test", "onCharacteristicChanged");
            progressDialog.cancel();
            byte[] data = characteristic.getValue();
            heartrateBPM = ("" + data[1]);
            if(Integer.parseInt(heartrateBPM) > 130)
                txtBPM.setText(heartrateBPM + " (Critical)");
            else if(Integer.parseInt(heartrateBPM) > 110)
                txtBPM.setText(heartrateBPM + " (Warning)");
            else
                txtBPM.setText(heartrateBPM + " (Safe)");
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            Log.v("test", "onDescriptorRead");
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            Log.v("test", "onDescriptorWrite");
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
            Log.v("test", "onReliableWriteCompleted");
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            Log.v("test", "onReadRemoteRssi");
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            Log.v("test", "onMtuChanged");
        }

    };

    public void submitReadings(){
        int a = mPick.getValue();
        int b = mPick2.getValue();
        String heartrate = heartrateBPM;
        String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String glucose = a + "." + b;
        String currentTime = simpleDateFormat.format(calendar.getTime());
        String date = simpleDateFormat2.format(calendar.getTime());
        String criticalrate = "No";
        String criticalglucose = "No";
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

            recordsRef.add(new Records(name, heartrate, currentTime, date, glucose, notes, measures, criticalrate, criticalglucose, dateNtime));
            sendAlert(heartrate, glucose, currentTime, date); //Send alert notification if there's critical reading
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

            recordsRef.add(new Records(name, heartrate, currentTime, date, glucose, notes, measures, criticalrate, criticalglucose, dateNtime));
            Toast.makeText(getApplicationContext(), "Your readings are at Warning level",
                    Toast.LENGTH_SHORT).show();
            finish();
        }

        //Safe Levels
        else{
            criticalrate = "Safe";
            criticalglucose = "Safe";

            recordsRef.add(new Records(name, heartrate, currentTime, date, glucose, notes, measures, criticalrate, criticalglucose, dateNtime));
            Toast.makeText(getApplicationContext(), "Readings Recorded Successfully",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void sendAlert(final String a, final String b, final String time, final String date){
        final alertDialog alertDialog = new alertDialog();
        alertDialog.show(getSupportFragmentManager(), "Alert");

        mPatId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mFirestore.collection("Patients").document(mPatId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        mCareId = documentSnapshot.getString("Caregiver ID");
                        Map<String, Object> notificationMessage = new HashMap<>();
                        notificationMessage.put("Message", "LoveHealth detected critical readings");
                        notificationMessage.put("From", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                        notificationMessage.put("pat_id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        notificationMessage.put("Heartrate", a);
                        notificationMessage.put("Glucose", b);
                        notificationMessage.put("Time", time);
                        notificationMessage.put("Date", date);
                        mFirestore.collection("Caregivers/" + mCareId +"/Notifications").add(notificationMessage);
                    }
                });
    }
}
