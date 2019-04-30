package com.example.user.lovehealth;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;

import javax.annotation.Nullable;


/**
 * A simple {@link Fragment} subclass.
 */
public class SummaryFragment extends Fragment {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference recordsRef;

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

    public SummaryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_summary, container, false);

        //Get ID from list of patient activity
        String id = getActivity().getIntent().getStringExtra("Patient ID");

        //Check if id is null (User = Patient)
        if(id != null)
            recordsRef = db.collection("Patient Records").document(id).collection("Records");
        else
            recordsRef = db.collection("Patient Records").document(mAuth.getCurrentUser().getUid()).collection("Records");

        mAvgAwake = (TextView)view.findViewById(R.id.aa);
        mBBreak = (TextView)view.findViewById(R.id.bb);
        mABreak = (TextView)view.findViewById(R.id.ab);
        mBLunch = (TextView)view.findViewById(R.id.bl);
        mALunch = (TextView)view.findViewById(R.id.al);
        mBDinner = (TextView)view.findViewById(R.id.bd);
        mADinner = (TextView)view.findViewById(R.id.ad);
        mAvgBed = (TextView)view.findViewById(R.id.bbed);

        recordsRef.orderBy("dateNtime",Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override

            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                Log.v("Sizes", "" + queryDocumentSnapshots.size());
                int index = 0;

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Records records = documentSnapshot.toObject(Records.class);

                    if (records.getEaten().equals("After awake")) {
                        avgAfterAwake += Double.parseDouble(records.getGlucose());
                        countAfterAwake++;
                    } else if (records.getEaten().equals("Before breakfast")) {
                        avgBeforeB += Double.parseDouble(records.getGlucose());
                        countBeforeB++;
                    } else if (records.getEaten().equals("After breakfast")) {
                        avgAfterB += Double.parseDouble(records.getGlucose());
                        countAfterB++;
                    } else if (records.getEaten().equals("Before lunch")) {
                        avgBeforeL += Double.parseDouble(records.getGlucose());
                        countBeforeL++;
                    } else if (records.getEaten().equals("After lunch")) {
                        avgAfterL += Double.parseDouble(records.getGlucose());
                        countAfterL++;
                    } else if (records.getEaten().equals("Before dinner")) {
                        avgBeforeD += Double.parseDouble(records.getGlucose());
                        countBeforeD++;
                    } else if (records.getEaten().equals("After dinner")) {
                        avgAfterD += Double.parseDouble(records.getGlucose());
                        countAfterD++;
                    } else if (records.getEaten().equals("Before bed")) {
                        avgBeforeBed += Double.parseDouble(records.getGlucose());
                        countBeforeBed++;
                    }
                }

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

        return view;
    }

}
