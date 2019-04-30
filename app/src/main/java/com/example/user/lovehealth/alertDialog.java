package com.example.user.lovehealth;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

public class alertDialog extends AppCompatDialogFragment {
    private ImageView btn;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_alert, null);
        builder.setView(view);
        btn = view.findViewById(R.id.btnClose);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), MainPatientActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                dismiss();
            }
        });

        return builder.create();
    }
}
