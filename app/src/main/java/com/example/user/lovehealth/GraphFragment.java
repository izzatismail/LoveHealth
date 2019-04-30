package com.example.user.lovehealth;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

/**
 * A simple {@link Fragment} subclass.
 */
public class GraphFragment extends Fragment {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference recordsRef;
    private LineChart mChartHR;
    private LineChart mChartGlucose;

    public GraphFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_graph, container, false);

        //Get ID from list of patient activity
        String id = getActivity().getIntent().getStringExtra("Patient ID");

        //Check if id is null (User = Patient)
        if(id != null)
            recordsRef = db.collection("Patient Records").document(id).collection("Records");
        else
            recordsRef = db.collection("Patient Records").document(mAuth.getCurrentUser().getUid()).collection("Records");


        mChartHR = view.findViewById(R.id.graphHR);
        mChartGlucose = view.findViewById(R.id.graphGlucose);

        mChartHR.setDragEnabled(true);
        mChartHR.setScaleEnabled(true);
        mChartHR.getDescription().setEnabled(false);
        mChartHR.animateY(1500,Easing.EasingOption.EaseInOutCubic);

        mChartGlucose.setDragEnabled(true);
        mChartGlucose.setScaleEnabled(true);
        mChartGlucose.getDescription().setEnabled(false);
        mChartGlucose.animateY(1500,Easing.EasingOption.EaseInOutCubic);

        final ArrayList<Entry> yValuesHR = new ArrayList<>();
        final ArrayList<Entry> yValuesGlucose = new ArrayList<>();



        recordsRef.orderBy("dateNtime",Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override

            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                Log.v("Sizes", "" + queryDocumentSnapshots.size());
                int index = 0;

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Records records = documentSnapshot.toObject(Records.class);

                    Integer heartrate = Integer.parseInt(records.getHeartrate());
                    Float glucose = Float.parseFloat(records.getGlucose());

                    yValuesHR.add(new Entry(index,heartrate));
                    yValuesGlucose.add(new Entry(index,glucose));
                    index++;
                }
                LineDataSet set1 = new LineDataSet(yValuesHR, "Heartrate (bpm)");
                LineDataSet set2 = new LineDataSet(yValuesGlucose, "Glucose (mmol/L)");

                set1.setFillAlpha(110);
                set1.setColor(Color.BLUE);
                set1.setCircleColor(Color.BLUE);
                set1.setLineWidth(2f);

                set2.setFillAlpha(110);
                set2.setColor(Color.BLUE);
                set2.setCircleColor(Color.BLUE);
                set2.setLineWidth(2f);

                ArrayList<ILineDataSet> dataSetsHR = new ArrayList<>();
                dataSetsHR.add(set1);

                ArrayList<ILineDataSet> dataSetsGlucose = new ArrayList<>();
                dataSetsGlucose.add(set2);

                LineData dataHR = new LineData(dataSetsHR);
                LineData dataGlucose = new LineData(dataSetsGlucose);

                mChartHR.setData(dataHR);
                mChartGlucose.setData(dataGlucose);
            }
        });
        return view;
    }

}
