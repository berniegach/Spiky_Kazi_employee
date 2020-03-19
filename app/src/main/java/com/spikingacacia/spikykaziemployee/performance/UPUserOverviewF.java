package com.spikingacacia.spikykaziemployee.performance;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.spikingacacia.spikykaziemployee.LoginActivity;
import com.spikingacacia.spikykaziemployee.Preferences;
import com.spikingacacia.spikykaziemployee.R;
import com.spikingacacia.spikykaziemployee.database.CReviews;
import com.spikingacacia.spikykaziemployee.pie_chart;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UPUserOverviewF.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UPUserOverviewF#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UPUserOverviewF extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    //private static final String ARG_USER_ID = "userid";

    private OnFragmentInteractionListener mListener;
    private Typeface font;
    private PieChart chart;
    private TextView t_p_count;
    private TextView t_a_count;
    private  RatingBar rating_bar;
    private TextView t_rating;
    private int rating=0;
    private int pending=0;
    private int all=0;
    private Preferences preferences;

    public UPUserOverviewF() {
        // Required empty public constructor
    }
    public static UPUserOverviewF newInstance() {
        UPUserOverviewF fragment = new UPUserOverviewF();
       // Bundle args = new Bundle();
       // args.putInt(ARG_USER_ID, userid);
       // fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
           // mUserid = getArguments().getInt(ARG_USER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.f_upuser_overview, container, false);
        preferences = new Preferences(getContext()) ;
        chart=view.findViewById(R.id.chart);
        t_p_count=view.findViewById(R.id.p_count);
        t_a_count=view.findViewById(R.id.a_count);
        rating_bar=view.findViewById(R.id.rating_bar);
        t_rating=view.findViewById(R.id.rating);

        /*((LinearLayout)view.findViewById(R.id.pending)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(mListener!=null)
                   mListener.onUserLayoutClicked(1,mUserid);
            }
        });*/
        ((LinearLayout)view.findViewById(R.id.all)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener!=null)
                    mListener.onUserLayoutClicked();
            }
        });
        //font
        font= ResourcesCompat.getFont(getContext(),R.font.arima_madurai);
        if(!preferences.isDark_theme_enabled())
        {
            view.findViewById(R.id.chart_back).setBackgroundColor(getResources().getColor(R.color.main_background_light));
            view.findViewById(R.id.pending).setBackgroundColor(getResources().getColor(R.color.tertiary_background_light));
            view.findViewById(R.id.all).setBackgroundColor(getResources().getColor(R.color.tertiary_background_light));
        }
        return view;
    }
    

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    @Override
    public void onResume() {
        super.onResume();
        pending=0;
        all=0;
        rating=0;
        setReviewsCount();
        pie_chart.init(chart,getContext());
        setRatingPie(chart);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.pie, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.actionToggleValues: {
                for (IDataSet<?> set : chart.getData().getDataSets())
                    set.setDrawValues(!set.isDrawValuesEnabled());
                item.setChecked(!item.isChecked());
                chart.invalidate();
                break;
            }
            case R.id.actionToggleHole: {
                if (chart.isDrawHoleEnabled())
                    chart.setDrawHoleEnabled(false);
                else
                    chart.setDrawHoleEnabled(true);
                item.setChecked(!item.isChecked());
                chart.invalidate();
                break;
            }
            case R.id.actionTogglePercent:
                chart.setUsePercentValues(!chart.isUsePercentValuesEnabled());
                item.setChecked(!item.isChecked());
                chart.invalidate();
                break;
        }
        return true;
    }
    
    public interface OnFragmentInteractionListener {
        void onUserLayoutClicked();
    }
    private void setRatingPie(PieChart pieChart)
    {
        List<PieEntry> entries=new ArrayList<>();
        //all
        entries.add(new PieEntry(rating));
        entries.add(new PieEntry(5-rating));

        pie_chart.add_data(entries,"Average Rating",chart);
    }

    private void setReviewsCount()
    {
        int total_rating=0;
        int average_rating=0;
        Iterator<CReviews> iterator= LoginActivity.uReviewsList.iterator();
        while(iterator.hasNext()) {
            CReviews cReviews = iterator.next();
            int classes=cReviews.getClasses();
            if(classes==0)
                pending+=1;
            else
            {
                all+=1;
                total_rating+=cReviews.getRating();
            }
        }
        if(total_rating!=0)
            average_rating=total_rating/all;
        else
            average_rating=0;
        rating=average_rating;
        t_rating.setText(String.format("%d/5",rating));
        rating_bar.setRating(rating);
        t_p_count.setText(String.format("%d",pending));
        t_a_count.setText(String.format("%d",all));
    }
}
