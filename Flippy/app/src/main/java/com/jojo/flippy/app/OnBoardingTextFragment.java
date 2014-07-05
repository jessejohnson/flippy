package com.jojo.flippy.app;

import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by odette on 6/2/14.
 */
public class OnBoardingTextFragment extends Fragment {

    public static final String ARG_PAGE = "page";
    private int mPageNumber;
    private static ArrayList<Integer> onBoardingStringIds;
    private static ArrayList<Integer> onBoardingImages;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_onboarding_text_layout, container, false);

        onBoardingStringIds = new ArrayList<Integer>();
        onBoardingStringIds.add(R.string.on_boarding_slide_1);
        onBoardingStringIds.add(R.string.on_boarding_slide_2);
        onBoardingStringIds.add(R.string.on_boarding_slide_3);
        onBoardingStringIds.add(R.string.on_boarding_slide_4);


        onBoardingImages = new ArrayList<Integer>();
        onBoardingImages.add(R.drawable.share);
        onBoardingImages.add(R.drawable.connect);
        onBoardingImages.add(R.drawable.receive);
        onBoardingImages.add(R.drawable.start);

        TextView onBoardingText = (TextView) rootView.findViewById(R.id.textViewOnboardingPageText);
        ImageView imageViewOnBoardingImage = (ImageView)rootView.findViewById(R.id.imageViewOnBoardingImage);
        onBoardingText.setText(getString(onBoardingStringIds.get(mPageNumber)));
        imageViewOnBoardingImage.setImageBitmap(BitmapFactory.decodeResource(getResources(),onBoardingImages.get(mPageNumber)));


        return rootView;
    }

    public static OnBoardingTextFragment create(int pageNumber){
        OnBoardingTextFragment fragment = new OnBoardingTextFragment();
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
