package com.example.user.lovehealth;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Set;

public class recordDialog extends AppCompatDialogFragment implements AdapterView.OnItemSelectedListener {
    private EditText mRate;
    private NumberPicker mPick;
    private NumberPicker mPick2;
    private CheckBox mCheck;
    private dialogListener listener;

    String notes;
    Boolean isListeningHeartRate = false;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a");
    SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("EEE, MMM d, ''yy");
    Calendar calendar = Calendar.getInstance();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null);

        builder.setView(view)
                .setTitle("Record Readings")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int a = mPick.getValue();
                        int b = mPick2.getValue();
                        String eaten;
                        if(mCheck.isChecked())
                            eaten = "Yes";
                        else
                            eaten = "No";

                        String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                        String heartrate = mRate.getText().toString();
                        String glucose = a + "." + b;
                        String currentTime = simpleDateFormat.format(calendar.getTime());
                        String date = simpleDateFormat2.format(calendar.getTime());
                        String criticalrate = "No";
                        String criticalglucose = "No";
                        listener.apply(name, heartrate, glucose,notes,currentTime, date, eaten, criticalrate, criticalglucose);
                    }
                });

        mRate = view.findViewById(R.id.edit_rate);
        mCheck = view.findViewById(R.id.check_eaten);
        mPick = view.findViewById(R.id.glucose_picker);
        mPick2 = view.findViewById(R.id.glucose_picker_dec);

        //Spinners
        Spinner spinnerNotes = view.findViewById(R.id.spin_notes);

        //ArrayAdapter to show the content of the spinner
        ArrayAdapter<CharSequence> adapterNotes = ArrayAdapter.createFromResource(view.getContext(),
                R.array.notes, android.R.layout.simple_spinner_item);
        adapterNotes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNotes.setAdapter(adapterNotes);
        spinnerNotes.setOnItemSelectedListener(this);
        //End for Spinners

        mPick.setMinValue(1);
        mPick.setMaxValue(20);
        mPick2.setMinValue(0);
        mPick2.setMaxValue(9);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (dialogListener)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Must implement dialogListener");
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        notes = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public interface dialogListener{
        void apply(String name, String heartrate, String glucose, String notes, String curTime, String curDate,
                   String eaten, String criticalrate, String criticalglucose);
    }
}
