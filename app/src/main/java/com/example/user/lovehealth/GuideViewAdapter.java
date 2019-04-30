package com.example.user.lovehealth;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

class GuideViewAdapter extends FragmentPagerAdapter {

    public GuideViewAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                Step1Fragment step1Fragment = new Step1Fragment();
                return  step1Fragment;

            case 1:
                Step2Fragment step2Fragment = new Step2Fragment();
                return  step2Fragment;

            case 2:
                Step3Fragment step3Fragment = new Step3Fragment();
                return  step3Fragment;

            case 3:
                Step4Fragment step4Fragment = new Step4Fragment();
                return  step4Fragment;

            case 4:
                AlertFragment alertFragment = new AlertFragment();
                return  alertFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 5;
    }
}
