package com.example.user.lovehealth;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class ListPatientActivity extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference patientCollection = db.collection("Caregivers")
            .document(mAuth.getCurrentUser().getUid()).collection("Patients List");

    private ListView patientListView;
    ArrayList<Map<String,String>> patientListArrayList = new ArrayList<>();
    String[] patient_id = new String[20];
    String[] patient_name = new String[20];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_patient);

        patientListView = (ListView)findViewById(R.id.patientList);
        SimpleAdapter adapter = new SimpleAdapter(this, patientListArrayList,
                R.layout.custom_list,new String[]{"name","ageNgender"},new int[]{R.id.text_1,R.id.text_2});
        patientListView.setAdapter(adapter);
        patientListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String patient_ident = patient_id[position];
                String patient_nama = patient_name[position];

                Intent intent = new Intent(ListPatientActivity.this, ViewRecords.class);
                intent.putExtra("Patient ID", patient_ident);
                intent.putExtra("Patient Name", patient_nama);

                startActivity(intent);
            }
        });

        patientCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            Map<String, String> data;
            int index = 0;

            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    data = new HashMap<>();
                    PatientList patientList = documentSnapshot.toObject(PatientList.class);
                    Log.v("Patient Name", "" + patientList.getPat_name());
                    Log.v("Patient Gender", "" + patientList.getPat_gender());
                    Log.v("Patient Age", "" + patientList.getPat_age());

                    data.put("name", patientList.getPat_name());
                    data.put("ageNgender", "Gender : " + patientList.getPat_gender() + "\nAge : " + patientList.getPat_age());
                    patientListArrayList.add(data);
                    patient_id[index] = patientList.getPat_id();
                    patient_name[index] = patientList.getPat_name();
                    index++;
                }
            }
        });
    }
}
