package com.example.user.lovehealth;

import android.graphics.Typeface;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

public class HistoryData extends AppCompatActivity {

    private TextView mGraphLabel;
    private TextView mPieLabel;
    private TextView mSummaryLabel;

    private ViewPager mHistoryPager;

    private PagerViewAdapter mPagerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_data);

        mGraphLabel = (TextView) findViewById(R.id.GraphLabel);
        mPieLabel = (TextView) findViewById(R.id.PieLabel);
        mSummaryLabel = (TextView) findViewById(R.id.SummaryLabel);

        mHistoryPager = (ViewPager) findViewById(R.id.historyPager);
        mHistoryPager.setOffscreenPageLimit(3);

        mPagerViewAdapter = new PagerViewAdapter(getSupportFragmentManager());
        mHistoryPager.setAdapter(mPagerViewAdapter);

        mGraphLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHistoryPager.setCurrentItem(0);
            }
        });

        mPieLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHistoryPager.setCurrentItem(1);
            }
        });

        mSummaryLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHistoryPager.setCurrentItem(2);
            }
        });

        mHistoryPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
            mGraphLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            mGraphLabel.setTypeface(null, Typeface.BOLD);

            mPieLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            mPieLabel.setTypeface(null, Typeface.NORMAL);

            mSummaryLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            mSummaryLabel.setTypeface(null, Typeface.NORMAL);
        }

        if(position == 1){
            mGraphLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            mGraphLabel.setTypeface(null, Typeface.NORMAL);

            mPieLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            mPieLabel.setTypeface(null, Typeface.BOLD);

            mSummaryLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            mSummaryLabel.setTypeface(null, Typeface.NORMAL);
        }

        if(position == 2){
            mGraphLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            mGraphLabel.setTypeface(null, Typeface.NORMAL);

            mPieLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            mPieLabel.setTypeface(null, Typeface.NORMAL);

            mSummaryLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            mSummaryLabel.setTypeface(null, Typeface.BOLD);
        }
    }
}
