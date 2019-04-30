/**
 * Author: Mohd Izzat bin Ismail Hashim
 */

package com.example.user.lovehealth;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class recordsAdapter extends FirestoreRecyclerAdapter<Records, recordsAdapter.recordsHolder> {

    public recordsAdapter(@NonNull FirestoreRecyclerOptions<Records> options) {
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
                holder.txtHeartrate.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                holder.txtHeartrate.setTypeface(null, Typeface.BOLD);
                holder.txtHeartrate.setTextColor(Color.parseColor("#ff3f3f"));
            }else if(model.getCriticalrate().equals("Warning")){
                holder.txtHeartrate.setTextColor(Color.parseColor("#ffbf00"));
                holder.txtHeartrate.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                holder.txtHeartrate.setTypeface(null, Typeface.BOLD);
            }else{
                holder.txtHeartrate.setTextColor(Color.parseColor("#FF000000"));
                holder.txtHeartrate.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                holder.txtHeartrate.setTypeface(null, Typeface.NORMAL);
            }

            //Glucose
            if(model.getCriticalglucose().equals("Critical") || model.getCriticalglucose().equals("DKA")) {
                if(model.getCriticalglucose().equals("DKA"))
                    holder.txtGlucose.setText("Glucose (mmol/L) : " + model.getGlucose() + " (DKA Alert!)");
                holder.txtGlucose.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                holder.txtGlucose.setTypeface(null, Typeface.BOLD);
                holder.txtGlucose.setTextColor(Color.parseColor("#ff3f3f"));
                holder.txtEaten.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                holder.txtEaten.setTypeface(null, Typeface.BOLD);
                holder.txtEaten.setTextColor(Color.parseColor("#ff3f3f"));
            }else if(model.getCriticalglucose().equals("Warning")){
                holder.txtGlucose.setTextColor(Color.parseColor("#ffbf00"));
                holder.txtEaten.setTextColor(Color.parseColor("#ffbf00"));
                holder.txtGlucose.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                holder.txtGlucose.setTypeface(null, Typeface.BOLD);
                holder.txtEaten.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                holder.txtEaten.setTypeface(null, Typeface.BOLD);
            }else{
                holder.txtGlucose.setTextColor(Color.parseColor("#FF000000"));
                holder.txtEaten.setTextColor(Color.parseColor("#FF000000"));
                holder.txtGlucose.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                holder.txtGlucose.setTypeface(null, Typeface.NORMAL);
                holder.txtEaten.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                holder.txtEaten.setTypeface(null, Typeface.NORMAL);
            }
        }else if (model.getCriticalrate().equals("Warning") || model.getCriticalglucose().equals("Warning")){
            holder.mCard.setCardBackgroundColor(Color.parseColor("#ffbf00"));
            holder.txtHeartrate.setTextColor(Color.parseColor("#ffbf00"));
            holder.txtGlucose.setTextColor(Color.parseColor("#ffbf00"));
            holder.txtEaten.setTextColor(Color.parseColor("#ffbf00"));

            //Heartrate
            if(model.getCriticalrate().equals("Warning")) {
                holder.txtHeartrate.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                holder.txtHeartrate.setTypeface(null, Typeface.BOLD);
            }else{
                holder.txtHeartrate.setTextColor(Color.parseColor("#FF000000"));
                holder.txtHeartrate.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                holder.txtHeartrate.setTypeface(null, Typeface.NORMAL);
            }
            //Glucose
            if(model.getCriticalglucose().equals("Warning")) {
                holder.txtGlucose.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                holder.txtGlucose.setTypeface(null, Typeface.BOLD);
                holder.txtEaten.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                holder.txtEaten.setTypeface(null, Typeface.BOLD);
            }else{
                holder.txtGlucose.setTextColor(Color.parseColor("#FF000000"));
                holder.txtEaten.setTextColor(Color.parseColor("#FF000000"));
                holder.txtGlucose.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                holder.txtGlucose.setTypeface(null, Typeface.NORMAL);
                holder.txtEaten.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                holder.txtEaten.setTypeface(null, Typeface.NORMAL);
            }
        }else{
            holder.mCard.setCardBackgroundColor(Color.parseColor("#54c96d"));

            holder.txtHeartrate.setTextColor(Color.parseColor("#FF000000"));
            holder.txtGlucose.setTextColor(Color.parseColor("#FF000000"));
            holder.txtEaten.setTextColor(Color.parseColor("#FF000000"));

            holder.txtGlucose.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            holder.txtGlucose.setTypeface(null, Typeface.NORMAL);
            holder.txtEaten.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            holder.txtEaten.setTypeface(null, Typeface.NORMAL);
            holder.txtHeartrate.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            holder.txtHeartrate.setTypeface(null, Typeface.NORMAL);
        }
    }

    @NonNull
    @Override
    public recordsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_item, parent, false);
        return new recordsHolder(v);
    }

    class recordsHolder extends RecyclerView.ViewHolder{
        TextView txtHeartrate;
        TextView txtGlucose;
        TextView txtTime;
        TextView txtDate;
        TextView txtNotes;
        TextView txtEaten;
        CardView mCard;

        public recordsHolder(View itemView) {
            super(itemView);
            txtHeartrate = itemView.findViewById(R.id.record_heartrate);
            txtGlucose = itemView.findViewById(R.id.record_glucose);
            txtTime = itemView.findViewById(R.id.record_time);
            txtDate = itemView.findViewById(R.id.record_date);
            txtNotes = itemView.findViewById(R.id.record_notes);
            txtEaten = itemView.findViewById(R.id.record_eaten);
            mCard = itemView.findViewById(R.id.card_view);
        }
    }
}
