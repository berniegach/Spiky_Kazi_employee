package com.spikingacacia.spikykaziemployee.performance;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.spikingacacia.spikykaziemployee.Preferences;
import com.spikingacacia.spikykaziemployee.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class UPReviewDetailF extends Fragment {
    private static final String ARG_REVIEW = "review";
    private static final String ARG_TO_IMPROVE = "to_improve";
    private static final String ARG_REVIEWER = "reviewer";
    private static final String ARG_RATING = "rating";
    private static final String ARG_DATE = "date";
    private String mReview;
    private String mToImprove;
    private String mReviewer;
    private int mRating;
    private String mDate;
    private Preferences preferences;

    public UPReviewDetailF() {
        // Required empty public constructor
    }
    public static UPReviewDetailF newInstance(String review, String toImprove, String reviewer, int rating, String date) {
        UPReviewDetailF fragment = new UPReviewDetailF();
        Bundle args = new Bundle();
        args.putString(ARG_REVIEW, review);
        args.putString(ARG_TO_IMPROVE, toImprove);
        args.putString(ARG_REVIEWER, reviewer);
        args.putInt(ARG_RATING, rating);
        args.putString(ARG_DATE, date);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mReview = getArguments().getString(ARG_REVIEW);
            mToImprove = getArguments().getString(ARG_TO_IMPROVE);
            mReviewer = getArguments().getString(ARG_REVIEWER);
            mRating = getArguments().getInt(ARG_RATING);
            mDate=getArguments().getString(ARG_DATE);
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.f_upreview_detail, container, false);
        preferences = new Preferences(getContext());
        ((TextView)view.findViewById(R.id.review)).setText(mReview);
        ((TextView)view.findViewById(R.id.to_improve)).setText(mToImprove);
        ((TextView)view.findViewById(R.id.reviewer)).setText("By " +mReviewer+" "+mDate);
        ((RatingBar)view.findViewById(R.id.rating_bar)).setRating(mRating);
        ((TextView)view.findViewById(R.id.rating)).setText(String.format("%d/5",mRating));
        if(!preferences.isDark_theme_enabled())
        {
            view.findViewById(R.id.main).setBackgroundColor(getResources().getColor(R.color.secondary_background_light));
            view.findViewById(R.id.review).setBackgroundColor(getResources().getColor(R.color.tertiary_background_light));
            view.findViewById(R.id.to_improve).setBackgroundColor(getResources().getColor(R.color.tertiary_background_light));
        }
        return view;
    }

}
