package com.example.user.lovehealth;

import android.graphics.Typeface;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

public class GuideActivity extends AppCompatActivity {

    private TextView mStep1Label;
    private TextView mStep2Label;
    private TextView mStep3Label;
    private TextView mStep4Label;
    private TextView mAlertLabel;

    private ViewPager mGuidePager;

    private GuideViewAdapter mGuideViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        mStep1Label = (TextView) findViewById(R.id.Step1Label);
        mStep2Label = (TextView) findViewById(R.id.Step2Label);
        mStep3Label = (TextView) findViewById(R.id.Step3Label);
        mStep4Label = (TextView) findViewById(R.id.Step4Label);
        mAlertLabel = (TextView) findViewById(R.id.AlertLabel);

        mGuidePager = (ViewPager) findViewById(R.id.guidePager);

        mGuideViewAdapter = new GuideViewAdapter(getSupportFragmentManager());
        mGuidePager.setAdapter(mGuideViewAdapter);

        mStep1Label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGuidePager.setCurrentItem(0);
            }
        });

        mStep2Label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGuidePager.setCurrentItem(1);
            }
        });

        mStep3Label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGuidePager.setCurrentItem(2);
            }
        });

        mStep4Label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGuidePager.setCurrentItem(3);
            }
        });

        mAlertLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGuidePager.setCurrentItem(4);
            }
        });

        mGuidePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                changeTabs(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void changeTabs(int position) {
        if(position == 0 ){
            mStep1Label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            mStep1Label.setTypeface(null, Typeface.BOLD);

            mStep2Label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            mStep2Label.setTypeface(null, Typeface.NORMAL);

            mStep3Label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            mStep3Label.setTypeface(null, Typeface.NORMAL);

            mStep4Label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            mStep4Label.setTypeface(null, Typeface.NORMAL);

            mAlertLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            mAlertLabel.setTypeface(null, Typeface.NORMAL);
        }

        if(position == 1){
            mStep1Label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            mStep1Label.setTypeface(null, Typeface.NORMAL);

            mStep2Label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            mStep2Label.setTypeface(null, Typeface.BOLD);

            mStep3Label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            mStep3Label.setTypeface(null, Typeface.NORMAL);

            mStep4Label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            mStep4Label.setTypeface(null, Typeface.NORMAL);

            mAlertLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            mAlertLabel.setTypeface(null, Typeface.NORMAL);
        }

        if(position == 2){
            mStep1Label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            mStep1Label.setTypeface(null, Typeface.NORMAL);

            mStep2Label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            mStep2Label.setTypeface(null, Typeface.NORMAL);

            mStep3Label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            mStep3Label.setTypeface(null, Typeface.BOLD);

            mStep4Label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            mStep4Label.setTypeface(null, Typeface.NORMAL);

            mAlertLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            mAlertLabel.setTypeface(null, Typeface.NORMAL);
        }

        if(position == 3){
            mStep1Label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            mStep1Label.setTypeface(null, Typeface.NORMAL);

            mStep2Label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            mStep2Label.setTypeface(null, Typeface.NORMAL);

            mStep3Label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            mStep3Label.setTypeface(null, Typeface.NORMAL);

            mStep4Label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            mStep4Label.setTypeface(null, Typeface.BOLD);

            mAlertLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            mAlertLabel.setTypeface(null, Typeface.NORMAL);
        }

        if(position == 4){
            mStep1Label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            mStep1Label.setTypeface(null, Typeface.NORMAL);

            mStep2Label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            mStep2Label.setTypeface(null, Typeface.NORMAL);

            mStep3Label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            mStep3Label.setTypeface(null, Typeface.NORMAL);

            mStep4Label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            mStep4Label.setTypeface(null, Typeface.NORMAL);

            mAlertLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            mAlertLabel.setTypeface(null, Typeface.BOLD);
        }
    }
}
