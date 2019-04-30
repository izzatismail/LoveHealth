package com.example.user.lovehealth;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;


import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
//import com.jjoe64.graphview.GraphView;
//import com.jjoe64.graphview.series.DataPoint;
//import com.jjoe64.graphview.series.LineGraphSeries;


import javax.annotation.Nullable;

public class HistoryActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference recordsRef = db.collection("Records");

    /*GraphView graph, graph2;
    LineGraphSeries<DataPoint> series;
    LineGraphSeries<DataPoint> series2;*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        /*
        graph = (GraphView) findViewById(R.id.graph);
        graph2 = (GraphView) findViewById(R.id.graph2);

        series = new LineGraphSeries<>();
        series2 = new LineGraphSeries<>();

        graph.getGridLabelRenderer().setNumHorizontalLabels(3);
        graph2.getGridLabelRenderer().setNumHorizontalLabels(3);

        graph.addSeries(series);
        graph2.addSeries(series2);


        graph.getGridLabelRenderer().setVerticalAxisTitle("Heartrate (bpm)");
        graph2.getGridLabelRenderer().setVerticalAxisTitle("Glucose (mmol/L)");

        series.setDrawDataPoints(true);
        series.setDataPointsRadius(7f);
        series2.setDrawDataPoints(true);
        series2.setDataPointsRadius(7f);

        Testing
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });*/


        recordsRef.orderBy("dateNtime",Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                //DataPoint[] dp = new DataPoint[queryDocumentSnapshots.size()];
                //DataPoint[] dp2 = new DataPoint[queryDocumentSnapshots.size()];
                Log.v("Sizes", "" + queryDocumentSnapshots.size());
                int index = 0;

                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    Records records = documentSnapshot.toObject(Records.class);

                    //dp[index] = new DataPoint(index,Integer.parseInt(records.getHeartrate()));
                    //dp2[index] = new DataPoint(index,Double.parseDouble(records.getGlucose()));
                    //Log.v("Index " + index, ""+dp[index]);
                    index++;
                }
                //series.resetData(dp);
                //series2.resetData(dp2);
            }
        });
    }
}
