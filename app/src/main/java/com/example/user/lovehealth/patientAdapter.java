/**
 * Author: Mohd Izzat bin Ismail Hashim
 */

package com.example.user.lovehealth;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class patientAdapter extends FirestoreRecyclerAdapter<Records, patientAdapter.recordsHolder> {

    Records records = new Records();
    public patientAdapter(@NonNull FirestoreRecyclerOptions<Records> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull recordsHolder holder, int position, @NonNull Records model) {
        holder.txtHeartrate.setText("Heartrate (bpm) : " + model.getHeartrate());
        holder.txtGlucose.setText("Glucose (mmol/L) : " + model.getGlucose());
        holder.txtTime.setText("Time : " + model.getTime());
        holder.txtDate.setText("Date : " + model.getDate());
        holder.txtEaten.setText("Measured : " + model.getEaten());
        holder.txtNotes.setText(model.getNotes());

        if(model.getCriticalrate().equals("Critical") || model.getCriticalglucose().equals("Critical") || model.getCriticalglucose().equals("DKA")){
            holder.mCard.setCardBackgroundColor(Color.parseColor("#ff3f3f"));

            //Heartrate
            if(model.getCriticalrate().equals("Critical")) {
                holder.txtHeartrateLevel.setText("Critical");
                holder.txtHeartrateLevel.setTextColor(Color.parseColor("#ff3f3f"));
            }else if(model.getCriticalrate().equals("Warning")){
                holder.txtHeartrateLevel.setText("Warning");
                holder.txtHeartrateLevel.setTextColor(Color.parseColor("#ffbf00"));
            }else{
                holder.txtHeartrateLevel.setText("Safe");
                holder.txtHeartrateLevel.setTextColor(Color.parseColor("#54c96d"));
            }

            //Glucose
            if(model.getCriticalglucose().equals("Critical") || model.getCriticalglucose().equals("DKA")) {
                if(model.getCriticalglucose().equals("DKA")) {
                    holder.txtGlucoseLevel.setText("DKA Alert");
                    holder.txtGlucoseLevel.setTextColor(Color.parseColor("#ff3f3f"));
                }else{
                    holder.txtGlucoseLevel.setText("Critical");
                    holder.txtGlucoseLevel.setTextColor(Color.parseColor("#ff3f3f"));
                }
            }else if(model.getCriticalglucose().equals("Warning")){
                holder.txtGlucoseLevel.setText("Warning");
                holder.txtGlucoseLevel.setTextColor(Color.parseColor("#ffbf00"));
            }else{
                holder.txtGlucoseLevel.setText("Safe");
                holder.txtGlucoseLevel.setTextColor(Color.parseColor("#54c96d"));
            }
        }else if (model.getCriticalrate().equals("Warning") || model.getCriticalglucose().equals("Warning")){
            holder.mCard.setCardBackgroundColor(Color.parseColor("#ffbf00"));

            //Heartrate
            if(model.getCriticalrate().equals("Warning")) {
                holder.txtHeartrateLevel.setText("Warning");
                holder.txtHeartrateLevel.setTextColor(Color.parseColor("#ffbf00"));
            }else{
                holder.txtHeartrateLevel.setText("Safe");
                holder.txtHeartrateLevel.setTextColor(Color.parseColor("#54c96d"));
            }
            //Glucose
            if(model.getCriticalglucose().equals("Warning")) {
                holder.txtGlucoseLevel.setText("Warning");
                holder.txtGlucoseLevel.setTextColor(Color.parseColor("#ffbf00"));
            }else{
                holder.txtGlucoseLevel.setText("Safe");
                holder.txtGlucoseLevel.setTextColor(Color.parseColor("#54c96d"));
            }
        }else{
            holder.mCard.setCardBackgroundColor(Color.parseColor("#54c96d"));

            holder.txtHeartrateLevel.setText("Safe");
            holder.txtHeartrateLevel.setTextColor(Color.parseColor("#54c96d"));

            holder.txtGlucoseLevel.setText("Safe");
            holder.txtGlucoseLevel.setTextColor(Color.parseColor("#54c96d"));
        }
    }

    @NonNull
    @Override
    public recordsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_record, parent, false);
        return new recordsHolder(v);
    }

    public void deleteItem(int position, final RecyclerView recyclerView){
        getSnapshots().getSnapshot(position).getReference().delete();
        Snackbar snackbar = Snackbar.make(recyclerView, "Record Removed", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    class recordsHolder extends RecyclerView.ViewHolder{
        TextView txtHeartrate;
        TextView txtGlucose;
        TextView txtTime;
        TextView txtDate;
        TextView txtNotes;
        TextView txtEaten;
        TextView txtHeartrateLevel;
        TextView txtGlucoseLevel;
        CardView mCard;

        public recordsHolder(View itemView) {
            super(itemView);
            txtHeartrate = itemView.findViewById(R.id.record_heartrate);
            txtGlucose = itemView.findViewById(R.id.record_glucose);
            txtTime = itemView.findViewById(R.id.record_time);
            txtDate = itemView.findViewById(R.id.record_date);
            txtNotes = itemView.findViewById(R.id.record_notes);
            txtEaten = itemView.findViewById(R.id.record_eaten);
            txtHeartrateLevel = itemView.findViewById(R.id.heartrate_level);
            txtGlucoseLevel = itemView.findViewById(R.id.glucose_level);
            mCard = itemView.findViewById(R.id.card_view);
        }
    }
}
