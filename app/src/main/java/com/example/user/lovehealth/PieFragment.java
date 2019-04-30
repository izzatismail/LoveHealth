package com.example.user.lovehealth;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
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
public class PieFragment extends Fragment {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference recordsRef;
    PieChart pieChartHR;
    PieChart pieChartGlucose;

    public PieFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_pie, container, false);

        //Get ID from list of patient activity
        String id = getActivity().getIntent().getStringExtra("Patient ID");

        //Check if id is null (User = Patient)
        if(id != null)
            recordsRef = db.collection("Patient Records").document(id).collection("Records");
        else
            recordsRef = db.collection("Patient Records").document(mAuth.getCurrentUser().getUid()).collection("Records");

        pieChartHR = view.findViewById(R.id.HRPie);
        pieChartGlucose = view.findViewById(R.id.GlucosePie);

        pieChartHR.setUsePercentValues(true);
        pieChartGlucose.setUsePercentValues(true);

        pieChartHR.getDescription().setEnabled(false);
        pieChartGlucose.getDescription().setEnabled(false);

        pieChartHR.setDragDecelerationFrictionCoef(0.95f);
        pieChartGlucose.setDragDecelerationFrictionCoef(0.95f);

        pieChartHR.setDrawHoleEnabled(true);
        pieChartHR.setHoleColor(Color.WHITE);
        pieChartHR.setTransparentCircleRadius(30f);
        pieChartHR.animateY(1500, Easing.EasingOption.EaseInOutCubic);

        pieChartGlucose.setDrawHoleEnabled(true);
        pieChartGlucose.setHoleColor(Color.WHITE);
        pieChartGlucose.setTransparentCircleRadius(30f);
        pieChartGlucose.animateY(1500, Easing.EasingOption.EaseInOutCubic);

        final ArrayList<PieEntry> yValuesHR = new ArrayList<>();
        final ArrayList<PieEntry> yValuesGlucose = new ArrayList<>();

        final int[] MY_COLORS = {Color.parseColor("#54c96d"),
                Color.parseColor("#ffbf00"),
                Color.parseColor("#ff3f3f"),
                Color.rgb(204,0,0),};

        final ArrayList<Integer> colors = new ArrayList<Integer>();

        for(int c: MY_COLORS) colors.add(c);

        recordsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override

            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                Log.v("Sizes", "" + queryDocumentSnapshots.size());
                int safeHR = 0;
                int warningHR = 0;
                int criticalHR = 0;

                int safeG = 0;
                int warningG = 0;
                int criticalG = 0;
                int DKA = 0;

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Records records = documentSnapshot.toObject(Records.class);

                    String heartrateLevel = records.getCriticalrate();
                    String glucoseLevel = records.getCriticalglucose();

                    if(heartrateLevel.equals("Safe"))
                        safeHR++;
                    else if(heartrateLevel.equals("Warning"))
                        warningHR++;
                    else
                        criticalHR++;

                    if(glucoseLevel.equals("Safe"))
                        safeG++;
                    else if(glucoseLevel.equals("Warning"))
                        warningG++;
                    else if(glucoseLevel.equals("Critical"))
                        criticalG++;
                    else
                        DKA++;
                }

                if(safeHR > 0)
                    yValuesHR.add(new PieEntry(safeHR, "Safe"));
                if(warningHR > 0)
                    yValuesHR.add(new PieEntry(warningHR, "Warning"));
                if(criticalHR > 0)
                    yValuesHR.add(new PieEntry(criticalHR, "Critical"));

                if(safeG > 0)
                    yValuesGlucose.add(new PieEntry(safeG, "Safe"));
                if(warningG > 0)
                    yValuesGlucose.add(new PieEntry(warningG, "Warning"));
                if(criticalG > 0)
                    yValuesGlucose.add(new PieEntry(criticalG, "Critical"));
                if(DKA > 0)
                    yValuesGlucose.add(new PieEntry(DKA, "DKA"));

                PieDataSet dataSetHR = new PieDataSet(yValuesHR, "Heartrate Levels");
                dataSetHR.setSliceSpace(3f);
                dataSetHR.setSelectionShift(5f);
                dataSetHR.setColors(colors);

                PieDataSet dataSetG = new PieDataSet(yValuesGlucose, "Glucose Levels");
                dataSetG.setSliceSpace(3f);
                dataSetG.setSelectionShift(5f);
                dataSetG.setColors(colors);

                PieData dataHR = new PieData(dataSetHR);
                dataHR.setValueTextSize(8f);
                dataHR.setValueTextColor(Color.WHITE);

                PieData dataG = new PieData(dataSetG);
                dataG.setValueTextSize(8f);
                dataG.setValueTextColor(Color.WHITE);

                pieChartHR.setData(dataHR);
                pieChartGlucose.setData(dataG);
            }
        });

        return view;
    }

}
