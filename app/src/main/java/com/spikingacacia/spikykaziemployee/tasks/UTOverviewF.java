package com.spikingacacia.spikykaziemployee.tasks;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.spikingacacia.spikykaziemployee.LoginActivity;
import com.spikingacacia.spikykaziemployee.Preferences;
import com.spikingacacia.spikykaziemployee.R;
import com.spikingacacia.spikykaziemployee.database.CTasks;
import com.spikingacacia.spikykaziemployee.pie_chart;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UTOverviewF extends Fragment
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private OnFragmentInteractionListener mListener;
    private String mParam1;
    private String mParam2;
    private Typeface font;
    //Textviews
    private TextView pCount;
    private TextView iCount;
    private TextView cCount;
    private TextView oCount;
    private TextView uCount;
    private TextView aCount;
    private PieChart chart;
    //counts
    private int pendingCount=0;
    private int inProgressCount=0;
    private int completedCount=0;
    private int overdueCount=0;
    private int lateCount=0;
    private Preferences preferences;


    public UTOverviewF()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CTOverviewF.
     */
    // TODO: Rename and change types and number of parameters
    public static UTOverviewF newInstance(String param1, String param2)
    {
        UTOverviewF fragment = new UTOverviewF();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        /*if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.f_utoverview, container, false);
        preferences=new Preferences(getContext());
        //textviews
        pCount=view.findViewById(R.id.p_count);
        iCount=view.findViewById(R.id.i_count);
        cCount=view.findViewById(R.id.c_count);
        oCount=view.findViewById(R.id.o_count);
        uCount=view.findViewById(R.id.u_count);
        aCount=view.findViewById(R.id.a_count);
        chart=view.findViewById(R.id.chart);

        //onclick listener
        ((LinearLayout)view.findViewById(R.id.pending)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (pendingCount==0)
                {
                    Toast.makeText(getContext(),"Empty",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(mListener!=null)
                    mListener.onTaskClicked(1);
            }
        });
        ((LinearLayout)view.findViewById(R.id.inprogress)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (inProgressCount==0)
                {
                    Toast.makeText(getContext(),"Empty",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(mListener!=null)
                    mListener.onTaskClicked(2);
            }
        });
        ((LinearLayout)view.findViewById(R.id.completed)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (completedCount==0)
                {
                    Toast.makeText(getContext(),"Empty",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(mListener!=null)
                    mListener.onTaskClicked(3);
            }
        });
        ((LinearLayout)view.findViewById(R.id.overdue)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (overdueCount==0)
                {
                    Toast.makeText(getContext(),"Empty",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(mListener!=null)
                    mListener.onTaskClicked(4);
            }
        });
        ((LinearLayout)view.findViewById(R.id.unfinished)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (lateCount==0)
                {
                    Toast.makeText(getContext(),"Empty",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(mListener!=null)
                    mListener.onTaskClicked(5);
            }
        });
        ((LinearLayout)view.findViewById(R.id.all)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if ((pendingCount+inProgressCount+completedCount+overdueCount+lateCount)==0)
                {
                    Toast.makeText(getContext(),"Empty",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(mListener!=null)
                    mListener.onTaskClicked(6);
            }
        });


        //font
        font= ResourcesCompat.getFont(getContext(),R.font.arima_madurai);
        if(!preferences.isDark_theme_enabled())
        {
            ((LinearLayout)view.findViewById(R.id.chart_back)).setBackgroundColor(getResources().getColor(R.color.main_background_light));
            ((LinearLayout)view.findViewById(R.id.pending)).setBackgroundColor(getResources().getColor(R.color.tertiary_background_light));
            ((LinearLayout)view.findViewById(R.id.inprogress)).setBackgroundColor(getResources().getColor(R.color.tertiary_background_light));
            ((LinearLayout)view.findViewById(R.id.completed)).setBackgroundColor(getResources().getColor(R.color.tertiary_background_light));
            ((LinearLayout)view.findViewById(R.id.overdue)).setBackgroundColor(getResources().getColor(R.color.tertiary_background_light));
            ((LinearLayout)view.findViewById(R.id.unfinished)).setBackgroundColor(getResources().getColor(R.color.tertiary_background_light));
            ((LinearLayout)view.findViewById(R.id.all)).setBackgroundColor(getResources().getColor(R.color.tertiary_background_light));
        }

        return view;
    }
    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener)
        {
            mListener = (OnFragmentInteractionListener) context;
        } else
        {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onResume()
    {
        //we set the following variables because of the following
        //1. so that every time we enter a task fragment and then get back to the overview the variables are set to
        //correct values otherwise they will just add to the before values
        //2. so we can set the texviews after setting the values. if not done here the texviews will show 0 during the initial run
        //3 so we can set the piechart with correct values during the initial run as above 2
        super.onResume();
        pendingCount=0;
        inProgressCount=0;
        completedCount=0;
        overdueCount=0;
        lateCount=0;
        setCounts();
        //set the counts
        pCount.setText(String.valueOf(pendingCount));
        iCount.setText(String.valueOf(inProgressCount));
        cCount.setText(String.valueOf(completedCount));
        oCount.setText(String.valueOf(overdueCount));
        uCount.setText(String.valueOf(lateCount));
        aCount.setText(String.valueOf(pendingCount+inProgressCount+completedCount+overdueCount+ lateCount));
        //piechart
        pie_chart.init(chart,getContext());
        setTasksPie(chart);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
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
    public interface OnFragmentInteractionListener
    {
        void onTaskClicked(int id);
    }
    private void setTasksPie(PieChart pieChart)
    {
        List<PieEntry> entries=new ArrayList<>();

        if(pendingCount==0 && inProgressCount==0 && completedCount==0 && overdueCount==0 && lateCount ==0)
        {
            entries.add(new PieEntry(1,"Empty"));
        }
        else
        {
            if(pendingCount>0)
            {
                entries.add(new PieEntry(pendingCount, pendingCount>0?"Pending":""));
            }
            if(inProgressCount>0)
            {
                entries.add(new PieEntry(inProgressCount, "In Progress"));
            }
            if(completedCount>0)
            {
                entries.add(new PieEntry(completedCount, "Completed"));
            }
            if(overdueCount>0)
            {
                entries.add(new PieEntry(overdueCount, "Overdue"));
            }
            if(lateCount >0)
            {
                entries.add(new PieEntry(lateCount, "Unfinished"));
            }

        }

        pie_chart.add_data(entries,"Count",chart);

    }
    private void setCounts()
    {
        Iterator<CTasks> iterator= LoginActivity.uTasksList.iterator();
        while(iterator.hasNext())
        {
            CTasks cTasks=iterator.next();
            pendingCount+=cTasks.getPending();
            inProgressCount+=cTasks.getInProgress();
            completedCount+=cTasks.getCompleted();
            overdueCount+=cTasks.getOverdue();
            lateCount+=cTasks.getLate();
        }
    }

}
