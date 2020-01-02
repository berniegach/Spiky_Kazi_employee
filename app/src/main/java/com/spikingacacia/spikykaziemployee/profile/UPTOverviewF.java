package com.spikingacacia.spikykaziemployee.profile;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.spikingacacia.spikykaziemployee.LoginActivity;
import com.spikingacacia.spikykaziemployee.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;


public class UPTOverviewF extends Fragment
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //private OnFragmentInteractionListener mListener;
    private TextView rCount;
    private TextView cCount;
    private TextView ecCount;
    private PieChart chart;
    private Typeface font;
    private int[]havesRequirements;
    private int[]haveCertificates;
    private int expiredCertificates=0;
    private int compliant=0;
    private int nonCompliant=0;

    public UPTOverviewF()
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
    public static UPTOverviewF newInstance(String param1, String param2)
    {
        UPTOverviewF fragment = new UPTOverviewF();
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
        View view= inflater.inflate(R.layout.f_uptoverview, container, false);
        //textviews
        rCount=view.findViewById(R.id.rCount);
        cCount=view.findViewById(R.id.cCount);
        ecCount=view.findViewById(R.id.ecCount);

        //pCount.setText(String.valueOf(LoginActivity.cGlobalInfo.getStaffCount()));
       // cCount.setText(String.valueOf(LoginActivity.cGlobalInfo.getCompliant()));
       // ncCount.setText(String.valueOf(LoginActivity.cGlobalInfo.getNoncompliant()));
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
        setPieChart(chart);
        //selector textview
        final TextView selector=view.findViewById(R.id.selector);
        String position= LoginActivity.userAccount.getPosition();
        position=position.replace("_"," ");
        selector.setText(position);

        return view;
    }
    @Override
    public void onResume()
    {
        super.onResume();
        //when you swipe to the last tab and come back the counters must be re set to 0
        //otherwise they will add extra counts ono themselves
        getPersonnelHaveRequirements();
        setCompliacePie(chart);
        //set the counts
        rCount.setText(String.format("%d/%d",havesRequirements[0],havesRequirements[0]+havesRequirements[1]));
        cCount.setText(String.format("%d/%d",haveCertificates[0],haveCertificates[0]+haveCertificates[1]));
        ecCount.setText(String.format("%d",expiredCertificates));
    }
    private void setPieChart(PieChart pieChart)
    {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,10,5,5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(false);
        //pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);
       // pieChart.setHoleRadius(90f);
        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.setEntryLabelColor(Color.WHITE);
       // pieChart.setEntryLabelTypeface(getResources().getFont(R.font.arima_madurai));

        Legend legend=pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(false);
        legend.setXEntrySpace(7f);
        legend.setYEntrySpace(0f);
        legend.setYOffset(0f);
        legend.setTextSize(13);
        legend.setTextColor(Color.WHITE);
        legend.setTypeface(font);
        //entry label
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.setEntryLabelTypeface(font);
        pieChart.setEntryLabelTextSize(12);


        pieChart.invalidate();

    }
    private void setCompliacePie(PieChart pieChart)
    {
        List<PieEntry>entries=new ArrayList<>();
        //colors
        List<Integer>colors=new ArrayList<>();
        List<Integer>tempColors=new ArrayList<>();
        //int comp=havesRequirements[0]+haveCertificates[0]-expiredCertificates;
        //int nonComp=havesRequirements[0]+haveCertificates[0]-comp;
        if(compliant==0 && nonCompliant==0)
        {
            entries.add(new PieEntry(1,"Empty"));
            colors= ColorTemplate.createColors(getResources(),new int[]{R.color.graph_1});
        }
        else
        {
            if(compliant>0)
            {
                entries.add(new PieEntry(compliant,compliant>0?"Compliance":""));
                tempColors.add(R.color.graph_14);
                //ColorTemplate.createColors(getResources(),new int[]{R.color.a_mono3});
            }
            if(nonCompliant>0)
            {
                entries.add(new PieEntry(nonCompliant,nonCompliant>0?"Missing":""));
                tempColors.add(R.color.graph_13);
                //ColorTemplate.createColors(getResources(),new int[]{R.color.a_mono3});
            }
            int[]tempTempColors=new int[tempColors.size()];
            for(int count=0; count<tempColors.size(); count+=1 )
                tempTempColors[count]=tempColors.get(count);
            colors= ColorTemplate.createColors(getResources(),tempTempColors);

        }
        PieDataSet set=new PieDataSet(entries,"Compliance");
        set.setSliceSpace(0f);
        //colors
        set.setColors(colors);
        PieData data=new PieData(set);
        data.setValueTextColor(Color.WHITE);
        data.setValueTypeface(font);
        data.setValueFormatter(new PercentFormatter());
        pieChart.setData(data);
        pieChart.highlightValues(null);
        pieChart.invalidate();
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
    private int getTradeId()
    {
        //get the trade index in the columns list to get its requiremnts from the personnel matrix
        int id=-1;
        Iterator iterator= LoginActivity.personnelColumnsList.entrySet().iterator();
        while (iterator.hasNext())
        {
            //final int index = pos;
            LinkedHashMap.Entry<String, Character> set2 = (LinkedHashMap.Entry<String, Character>) iterator.next();
            String name = set2.getKey();
            Character which = set2.getValue();
            if (which == 'm' || which=='j')
                continue;
            id+=1;
            if(LoginActivity.userAccount.getPosition().contentEquals(name))
                break;
        }

        return id;
    }
    private void getPersonnelHaveRequirements()
    {
        int[]values;
        havesRequirements=new int[2];
        haveCertificates=new int[2];
        expiredCertificates=0;
        int have=0, missing=0;
        int haveCert=0,missingCert=0;
        int[]requi=LoginActivity.personnelMatrix[getTradeId()];
        values=new int[LoginActivity.personnelColumnsList.size()];
        for(int c=0; c<values.length; c++)
        {
            values[c]=LoginActivity.onePersonnelMatrix[c];
        }
        int pos=0;
        Iterator iterator= LoginActivity.personnelColumnsList.entrySet().iterator();
        while (iterator.hasNext())
        {
            boolean have_requirement=false,have_cert=false, valid_cert=false;
            final int index = pos;
            LinkedHashMap.Entry<String, Character> set2 = (LinkedHashMap.Entry<String, Character>) iterator.next();
            final String name = set2.getKey();
            Character which = set2.getValue();
            if (which == 't')
                continue;
            //check if the qualification is required
            if (requi[pos] == 0)
            {
                pos += 1;
                continue;
            }
            if(values[index]==1)
            {
                have+=1;
                have_requirement=true;
            }

            else
                missing+=1;
            if(LoginActivity.userCertificatesNames[pos].contentEquals(name))
            {
                haveCert+=1;
                have_cert=true;
            }
            else
                missingCert+=1;
            String expiryDate=LoginActivity.userCertsExpiry.get(name);
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
                else
                    valid_cert=true;
            }
            if(have_requirement && have_cert && valid_cert)
                compliant+=1;
            else
                nonCompliant+=1;

            pos+=1;
        }

        havesRequirements[0]=have;
        havesRequirements[1]=missing;
        haveCertificates[0]=haveCert;
        haveCertificates[1]=missingCert;
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
