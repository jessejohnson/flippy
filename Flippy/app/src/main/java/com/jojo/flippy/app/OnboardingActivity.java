package com.jojo.flippy.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;



public class OnboardingActivity extends FragmentActivity {

    private static final int NUMBER_OF_PAGES = 4;
    private ViewPager pager;
    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new OnboardingPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private class OnboardingPagerAdapter extends FragmentStatePagerAdapter {
        public OnboardingPagerAdapter(FragmentManager supportFragmentManager) {
            super(supportFragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            return OnboardingTextFragment.create(position);
        }

        @Override
        public int getCount() {
            return NUMBER_OF_PAGES;
        }
    }
}
