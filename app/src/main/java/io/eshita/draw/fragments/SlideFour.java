package io.eshita.draw.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.lottie.LottieAnimationView;

import io.eshita.draw.R;


/**
 * Created by Aniruddh on 18-10-2017.
 */

public class SlideFour extends Fragment {
    private static final String ARG_LAYOUT_RES_ID = "layoutResId";
    private int layoutResId;

    public SlideFour() {
    }

    public static SlideFour newInstance(int layoutResId) {
        SlideFour sampleSlide = new SlideFour();

        Bundle bundleArgs = new Bundle();
        bundleArgs.putInt(ARG_LAYOUT_RES_ID, layoutResId);
        sampleSlide.setArguments(bundleArgs);

        return sampleSlide;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(ARG_LAYOUT_RES_ID))
            layoutResId = getArguments().getInt(ARG_LAYOUT_RES_ID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.slide_four, container, false);
        LottieAnimationView animationView = (LottieAnimationView) view.findViewById(R.id.slideFourAnimation);
        animationView.setAnimation("animations/shock.json");
        animationView.loop(true);
        animationView.playAnimation();
        return view;
    }

}