package com.jojo.flippy.app;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by odette on 6/2/14.
 */
public class OnboardingTextFragment extends Fragment {

    public static final String ARG_PAGE = "page";
    private int mPageNumber;
    private static ArrayList<Integer> onboardingStringIds;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_onboarding_text_layout, container, false);

        onboardingStringIds = new ArrayList<Integer>();
        onboardingStringIds.add(R.string.onboarding_slide_1);
        onboardingStringIds.add(R.string.onboarding_slide_2);
        onboardingStringIds.add(R.string.onboarding_slide_3);
        onboardingStringIds.add(R.string.onboarding_slide_4);

        TextView onboardingText = (TextView) rootView.findViewById(R.id.textViewOnboardingPageText);
        onboardingText.setText(getString(onboardingStringIds.get(mPageNumber)));


        return rootView;
    }

    public static OnboardingTextFragment create(int pageNumber){
        OnboardingTextFragment fragment = new OnboardingTextFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
    }
}
