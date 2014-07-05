package com.jojo.flippy.core;

import android.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;

import com.jojo.flippy.app.R;

import java.util.ArrayList;

public class HelpActivity extends FragmentActivity {

    private static final int NUMBER_OF_PAGES = 4;
    private ViewPager pager;
    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setSubtitle("Create channel");

        pager = (ViewPager) findViewById(R.id.pagerHelp);
        pagerAdapter = new HelpPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);

        final ArrayList<ImageView> indicators = new ArrayList<ImageView>();
        indicators.add((ImageView) findViewById(R.id.imageViewHelpIndicatorOne));
        indicators.add((ImageView) findViewById(R.id.imageViewHelpIndicatorTwo));
        indicators.add((ImageView) findViewById(R.id.imageViewHelpIndicatorThree));
        indicators.add((ImageView) findViewById(R.id.imageViewHelpIndicatorFour));

        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setPageIndicator(indicators, position);
            }
        });
    }

    private void setPageIndicator(ArrayList<ImageView> indicatorList, int position) {
        indicatorList.get(position).setImageResource(R.drawable.indicator_circle_active);
        for (int i = 0; i < indicatorList.size(); i++) {
            if (i == position) {
                continue;
            }
            indicatorList.get(i).setImageResource(R.drawable.indicator_circle);
        }
    }

    private class HelpPagerAdapter extends FragmentStatePagerAdapter {
        public HelpPagerAdapter(FragmentManager supportFragmentManager) {
            super(supportFragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            return HelpTextFragment.create(position);
        }

        @Override
        public int getCount() {
            return NUMBER_OF_PAGES;
        }
    }
}
