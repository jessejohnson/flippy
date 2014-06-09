package com.jojo.flippy.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;


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

        //An Array of indicator image views to be manipulated with each page transition
        //depending on the current page
        final ArrayList<ImageView> indicators = new ArrayList<ImageView>();
        indicators.add((ImageView) findViewById(R.id.imageViewIndicatorOne));
        indicators.add((ImageView) findViewById(R.id.imageViewIndicatorTwo));
        indicators.add((ImageView) findViewById(R.id.imageViewIndicatorThree));
        indicators.add((ImageView) findViewById(R.id.imageViewIndicatorFour));

        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setPageIndicator(indicators, position);
            }
        });

        Button btnGetStarted = (Button) findViewById(R.id.buttonGetStarted);
        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OnboardingActivity.this, SignUpOptionsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setPageIndicator(ArrayList<ImageView> indicatorList, int position) {
        indicatorList.get(position).setImageResource(R.drawable.indicator_circle_active);
        for(int i = 0; i < indicatorList.size(); i++){
            if(i == position){
                continue;
            }
            indicatorList.get(i).setImageResource(R.drawable.indicator_circle);
        }
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

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
