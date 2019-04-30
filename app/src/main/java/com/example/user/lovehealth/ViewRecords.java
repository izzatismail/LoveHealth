package com.example.user.lovehealth;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ViewRecords extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference recordsRef;
    private recordsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_records);
        String patient_id = getIntent().getStringExtra("Patient ID");
        Log.v("Patient ID", "" + patient_id);

        String id = getIntent().getStringExtra("Patient ID");
        String name = getIntent().getStringExtra("Patient Name");
        setTitle("" + name + " Records");

        recordsRef = db.collection("Patient Records").document(id).collection("Records");
        setUpRecyclerView();
    }


    public void setUpRecyclerView(){
        Query query = recordsRef.orderBy("dateNtime", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Records> options = new FirestoreRecyclerOptions.Builder<Records>()
                .setQuery(query, Records.class)
                .build();

        adapter = new recordsAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.record_views);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
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
}
