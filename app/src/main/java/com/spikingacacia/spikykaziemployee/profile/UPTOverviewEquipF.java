package com.spikingacacia.spikykaziemployee.profile;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.spikingacacia.spikykaziemployee.pie_chart;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;


public class UPTOverviewEquipF extends Fragment
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //private OnFragmentInteractionListener mListener;
    private PieChart chart;
    private Typeface font;
    private int[]haveEquipments;
    private int[]haveCertificates;
    private int expiredCertificates=0;
    private Preferences preferences;

    public UPTOverviewEquipF()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CRSOverviewF.
     */
    // TODO: Rename and change types and number of parameters
    public static UPTOverviewEquipF newInstance(String param1, String param2)
    {
        UPTOverviewEquipF fragment = new UPTOverviewEquipF();
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
        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.f_uptoverviewequip, container, false);
        preferences = new Preferences(getContext());
        //textviews
        TextView eCount=view.findViewById(R.id.eCount);
        TextView cCount=view.findViewById(R.id.cCount);
        TextView ecCount=view.findViewById(R.id.ecCount);
        getEquipmentsInformation();
        //set the counts
        eCount.setText(String.format("%d",haveEquipments[0]));
        cCount.setText(String.format("%d/%d",haveCertificates[0],haveCertificates[0]+haveCertificates[1]));
        ecCount.setText(String.format("%d",expiredCertificates));
        //count fields
        int countQualsM=0;
        int countQualsJ=0;
        int countT=0;
        /*Iterator iteratorCount=LoginActivity.personnelColumnsList.entrySet().iterator();
        while(iteratorCount.hasNext())
        {
            LinkedHashMap.Entry<String,Character>set=(LinkedHashMap.Entry<String, Character>) iteratorCount.next();
            String name=set.getKey();
            Character which=set.getValue();
            if(which=='t')
                countT+=1;
            else if(which=='m')
                countQualsM+=1;
            else if(which=='j')
                countQualsJ+=1;
        }
        tCount.setText(String.valueOf(countT));
        mCount.setText(String.valueOf(countQualsM));
        jCount.setText(String.valueOf(countQualsJ));*/

        //font
        font= ResourcesCompat.getFont(getContext(),R.font.arima_madurai);
        //chart
        chart=view.findViewById(R.id.chart);
        pie_chart.init(chart,getContext());
        setCompliacePie(chart);
        //selector textview
        final TextView selector=view.findViewById(R.id.selector);
        selector.setText(LoginActivity.userAccount.getPosition());
        if(!preferences.isDark_theme_enabled())
        {
            ((LinearLayout)view.findViewById(R.id.chart_back)).setBackgroundColor(getResources().getColor(R.color.main_background_light));
            ((LinearLayout)view.findViewById(R.id.sec_main)).setBackgroundColor(getResources().getColor(R.color.tertiary_background_light));
        }

        return view;
    }
    @Override
    public void onResume()
    {
        super.onResume();
        //when you swipe to the last tab and come back the counters must be re set to 0
        //otherwise they will add extra counts ono themselves
        getEquipmentsInformation();
        setCompliacePie(chart);
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
    private void setCompliacePie(PieChart pieChart)
    {
        List<PieEntry>entries=new ArrayList<>();
        int comp=haveCertificates[0]-expiredCertificates;
        int nonComp=haveCertificates[0]-comp;
        if(comp==0 && nonComp==0)
        {
            entries.add(new PieEntry(1,"Empty"));
        }
        else
        {
            if(comp>0)
            {
                entries.add(new PieEntry(comp,comp>0?"Compliance":""));
            }
            if(nonComp>0)
            {
                entries.add(new PieEntry(nonComp,nonComp>0?"Missing":""));
            }

        }

        pie_chart.add_data(entries,"Compliance",chart);

    }
    private void setFieldsPie(PieChart pieChart)
    {
        List<PieEntry>entries=new ArrayList<>();
        entries.add(new PieEntry(30.7f,"Mandatory"));
        entries.add(new PieEntry(65.7f,"Job Specific"));
        PieDataSet set=new PieDataSet(entries,"Fields");
        set.setSliceSpace(0f);
        //colors
        List<Integer>colors= ColorTemplate.createColors(getResources(),new int[]{R.color.graph_11,R.color.graph_12});
        set.setColors(colors);
        PieData data=new PieData(set);
        data.setValueTextColor(Color.WHITE);
        data.setValueTypeface(font);
        data.setValueFormatter(new PercentFormatter());
        pieChart.setData(data);
        pieChart.highlightValues(null);
        pieChart.invalidate();
    }
    private void setTradesPie(PieChart pieChart)
    {
        List<PieEntry>entries=new ArrayList<>();
        entries.add(new PieEntry(30.7f,"trade1"));
        entries.add(new PieEntry(65.7f,"trade2"));
        PieDataSet set=new PieDataSet(entries,"Trades");
        set.setSliceSpace(0f);
        //colors
        List<Integer>colors= ColorTemplate.createColors(getResources(),new int[]{R.color.graph_11,R.color.graph_12});
        set.setColors(colors);
        PieData data=new PieData(set);
        data.setValueTextColor(Color.WHITE);
        data.setValueTypeface(font);
        data.setValueFormatter(new PercentFormatter());
        pieChart.setData(data);
        pieChart.highlightValues(null);
        pieChart.invalidate();
    }
    private void getEquipmentsInformation()
    {
        haveEquipments=new int[2];
        haveCertificates=new int[2];
        expiredCertificates=0;
        int hasEquips=0,missingEquips=0;
        int haveCert=0,missingCert=0;
        int pos = 1;
        Iterator iterator = LoginActivity.equipmentsList.entrySet().iterator();
        while (iterator.hasNext())
        {
            final int index = pos;
            LinkedHashMap.Entry<String, Integer> set = (LinkedHashMap.Entry<String, Integer>) iterator.next();
            final String name = set.getKey();
            final Integer count = set.getValue();
            //get the specific row representing this equipment from the personnel matrix
            int[]tempPersonnel=new int[LoginActivity.equipmentsColumnsList.size()];
            if(LoginActivity.personnelEquipmentsMatrix.containsKey(name))
            {
                tempPersonnel=LoginActivity.personnelEquipmentsMatrix.get(name);
                int columnIndex=0;
                Iterator iteratorColumns= LoginActivity.equipmentsColumnsList.entrySet().iterator();
                while (iteratorColumns.hasNext())
                {
                    LinkedHashMap.Entry<String, Character> setColumn = (LinkedHashMap.Entry<String, Character>) iteratorColumns.next();
                    final String nameColumn = setColumn.getKey();
                    if(nameColumn.contentEquals(name))
                    {
                        if(tempPersonnel[columnIndex]==1)
                        {
                            //haveItSwitch.setChecked(true);
                            hasEquips+=1;
                            if(hasCertificate(name))
                                haveCert+=1;
                            else
                                missingCert+=1;
                            String expiryDate=LoginActivity.userCertsEquipmentsExpiry.get(name);
                            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yy");
                            Date dateExp=null;
                            Date dateNow=null;
                            if(expiryDate!=null)
                            {
                                try
                                {
                                    dateExp = simpleDateFormat.parse(expiryDate);
                                    dateNow = simpleDateFormat.parse(new SimpleDateFormat("dd/MM/yy").format(Calendar.getInstance().getTime()));
                                }
                                catch (ParseException e)
                                {
                                    Log.e("Date", "" + e.getMessage());
                                }
                                long difference = dateExp.getTime() - dateNow.getTime();
                                Log.d("Date diff",String.format("%d%n",difference));
                                if (difference <= 0)
                                    expiredCertificates += 1;
                            }
                            break;
                        }
                        else
                            missingEquips+=1;
                    }
                    columnIndex+=1;
                }
            }

            pos+=1;
        }
        haveEquipments[0]=hasEquips;
        haveEquipments[1]=missingEquips;
        haveCertificates[0]=haveCert;
        haveCertificates[1]=missingCert;
    }
    private boolean hasCertificate(String name)
    {
        for(int c=0; c<LoginActivity.userCertificatesEquipmentsNames.length; c++)
        {
            if(LoginActivity.userCertificatesEquipmentsNames[c].contentEquals(name))
                return true;
        }
        return false;
    }
    /*
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
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    } */
}
