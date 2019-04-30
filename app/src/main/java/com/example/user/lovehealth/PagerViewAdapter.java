package com.example.user.lovehealth;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

class PagerViewAdapter extends FragmentPagerAdapter {

    public PagerViewAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                GraphFragment graphFragment = new GraphFragment();
                return  graphFragment;

            case 1:
                PieFragment pieFragment = new PieFragment();
                return pieFragment;

            case 2:
                SummaryFragment summaryFragment = new SummaryFragment();
                return summaryFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
