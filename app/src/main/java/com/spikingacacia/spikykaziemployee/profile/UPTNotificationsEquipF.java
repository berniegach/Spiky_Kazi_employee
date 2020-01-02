package com.spikingacacia.spikykaziemployee.profile;

import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.spikingacacia.spikykaziemployee.CommonHelper;
import com.spikingacacia.spikykaziemployee.JSONParser;
import com.spikingacacia.spikykaziemployee.LoginActivity;
import com.spikingacacia.spikykaziemployee.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class UPTNotificationsEquipF extends Fragment
{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    //private OnFragmentInteractionListener mListener;
    private LinearLayout layout;
    private String url_update= LoginActivity.base_url+"update_u_notifications_equipments.php";
    private JSONParser jsonParser;
    private String TAG_SUCCESS="success";
    private String TAG_MESSAGE="message";
    private boolean[][]tempNotis;
    private float textViewPadding=16;

    public UPTNotificationsEquipF()
    {
        // Required empty public constructor
    }
    public static UPTNotificationsEquipF newInstance(String param1, String param2)
    {
        UPTNotificationsEquipF fragment = new UPTNotificationsEquipF();
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
        jsonParser=new JSONParser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.f_uptnotifications_equip, container, false);
        ((Button)view.findViewById(R.id.update_button)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String notifications="";
                for(int c=0; c<tempNotis.length; c++)
                {
                    boolean first=true;
                    for(int d=0; d<tempNotis[c].length; d++)
                    {
                        if(first)
                        {
                            boolean value=tempNotis[c][d];
                            notifications += (value == true ? String.valueOf(d) : "");
                            if(value)
                                first=false;
                        }
                        else
                            notifications+=(tempNotis[c][d]==true?","+String.valueOf(d):"");
                    }
                    if(c!=tempNotis.length-1)
                        notifications+=":";
                }
                Log.d("notifications",notifications);
                new UpdateTask(notifications).execute((Void)null);
            }
        });
        textViewPadding= CommonHelper.convertDpToPixels(16,getContext());
        //main layout
        layout=view.findViewById(R.id.base);
        addLayouts();
        return view;
    }

   /* @Override
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
        void onUpdate();
    }*/
    private void addLayouts()
    {
        final String[]dates=getResources().getStringArray(R.array.notifications);
        String notifications=LoginActivity.userAccount.getNotificationsEquipments();
        if(notifications.contentEquals("null") ||notifications.contentEquals("") )
        {
            if(notifications.contentEquals("null"))
                notifications="";
            for(int c=1; c<LoginActivity.equipmentsColumnsList.size(); c++)
                notifications += ":";
        }
        String[] qualNotification=notifications.split(":");
        List<String[]>notis=new ArrayList<>();
        for(int c=0; c<qualNotification.length; c++)
        {
            String[]pieces=qualNotification[c].split(",");
            notis.add(pieces);
        }
        //count fields
        int countQuals=0;
        Iterator iteratorCount=LoginActivity.equipmentsColumnsList.entrySet().iterator();
        while(iteratorCount.hasNext())
        {
            LinkedHashMap.Entry<String,Character>set=(LinkedHashMap.Entry<String, Character>) iteratorCount.next();
            String name=set.getKey();
            Character which=set.getValue();
            if(which=='t')
                continue;
            countQuals+=1;
        }
        tempNotis=new boolean[countQuals][dates.length];
        //initialize the tempNotis
        for(int c=0; c<tempNotis.length; c++)
        {
            for(int d=0; d<tempNotis[c].length; d++)
                tempNotis[c][d]=false;
        }
        for(int c=0; c<notis.size(); c++)
        {
            String[]pieces=notis.get(c);
            for(int d=0; d<pieces.length; d++)
            {
                if(pieces[d].contentEquals(""))
                    continue;
                tempNotis[c][Integer.parseInt(pieces[d])]=true;
            }
        }
        final Typeface font= ResourcesCompat.getFont(getContext(),R.font.arima_madurai);
        final Typeface font2= ResourcesCompat.getFont(getContext(),R.font.amita);
        TypedArray array=getContext().getTheme().obtainStyledAttributes(new int[]{android.R.attr.listDivider});
        Drawable drawable=array.getDrawable(0);
        array.recycle();
        int pos=1;
        Iterator iterator= LoginActivity.equipmentsColumnsList.entrySet().iterator();
        while (iterator.hasNext())
        {
            final int index=pos;
            LinkedHashMap.Entry<String,Character>set=(LinkedHashMap.Entry<String, Character>) iterator.next();
            String name=set.getKey();
            name=name.replace("_"," ");
            Character which=set.getValue();
            if(which=='t')
                continue;
            //add the main quaifiations layout
            LinearLayout.LayoutParams layoutParams2=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams2.setMargins(50,0,50,0);
            final LinearLayout mainLayout = new LinearLayout(getContext());
            mainLayout.setOrientation(LinearLayout.VERTICAL);
            mainLayout.setLayoutParams(layoutParams2);

            //layout params;
            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            //trade layout
            LinearLayout tradeLayout = new LinearLayout(getContext());
            tradeLayout.setOrientation(LinearLayout.HORIZONTAL);
            tradeLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tradeLayout.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.secondary_background));
           // tradeLayout.setPadding(10,10,10,10);
            tradeLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if(mainLayout.getVisibility()==View.GONE)
                        CommonHelper.expand(mainLayout);
                    else
                        CommonHelper.collapse(mainLayout);
                }
            });
            //item number textview
            TextView textCount=new TextView(getContext());
            textCount.setLayoutParams(layoutParams);
            textCount.setText(String.valueOf(pos));
            textCount.setPadding((int) textViewPadding,(int) textViewPadding,(int) textViewPadding,(int) textViewPadding);
            textCount.setTypeface(font);
            //content textview
            TextView textContent=new TextView(getContext());
            textContent.setLayoutParams(layoutParams);
            textContent.setPadding((int) textViewPadding,(int) textViewPadding,(int) textViewPadding,(int) textViewPadding);
            textContent.setText(name);
            textContent.setTypeface(font);
            //layout for count
            LinearLayout countLayout = new LinearLayout(getContext());
            countLayout.setOrientation(LinearLayout.HORIZONTAL);
            countLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,1));
            countLayout.setGravity(Gravity.END);
            //quals count textview
            TextView textQualiCount=new TextView(getContext());
            LinearLayout.LayoutParams paramsTextQualiCount=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            paramsTextQualiCount.setMargins((int) textViewPadding,(int) textViewPadding,(int) textViewPadding,(int) textViewPadding);
            textQualiCount.setLayoutParams(paramsTextQualiCount);
            textQualiCount.setText(name);
            textQualiCount.setBackgroundResource(R.drawable.circle);
            textQualiCount.setGravity(Gravity.CENTER);
            textQualiCount.setTypeface(font);

            //add the notifications layout
            //notifications
            String[]notisPresent=new String[0];
            if(notis.size()>0)
            {
                if(notis.size()>pos-1 )
                    notisPresent = notis.get(pos - 1);
            }
            if(notisPresent.length==1 && notisPresent[0].contentEquals(""))
                textQualiCount.setText(String.valueOf(0));
            else
                textQualiCount.setText(String.valueOf(notisPresent.length));
            int c;
            for( c=0; c<tempNotis[index-1].length; c++)
            {
                if(!tempNotis[index-1][c])
                    continue;
                final int index2=c;
                String name2=dates[c];
                LinearLayout.LayoutParams layoutParams3=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams3.setMargins(0,1,0,1);
                final LinearLayout qualiLayout = new LinearLayout(getContext());
                qualiLayout.setOrientation(LinearLayout.HORIZONTAL);
                qualiLayout.setLayoutParams(layoutParams3);
                qualiLayout.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.tertiary_background));
                qualiLayout.setPadding(10,10,10,10);
                //item number textview
                TextView textCount2=new TextView(getContext());
                textCount2.setLayoutParams(layoutParams);
                textCount2.setText(String.valueOf(c+1));
                textCount2.setPadding((int) textViewPadding,(int) textViewPadding,(int) textViewPadding,(int) textViewPadding);
                textCount2.setTypeface(font);
                //content textview
                TextView textContent2=new TextView(getContext());
                textContent2.setLayoutParams(layoutParams);
                textContent2.setText(name2);
                textContent2.setPadding((int) textViewPadding,(int) textViewPadding,(int) textViewPadding,(int) textViewPadding);
                textContent2.setTypeface(font);
                //layout for switch
                LinearLayout switchLayout = new LinearLayout(getContext());
                switchLayout.setOrientation(LinearLayout.HORIZONTAL);
                switchLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,1));
                switchLayout.setGravity(Gravity.END);
                //switch
                TextView textViewRemove = new TextView(getContext());
                textViewRemove.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_remove,0,0,0);
                textViewRemove.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                textViewRemove.setPadding((int) textViewPadding,(int) textViewPadding,(int) textViewPadding,(int) textViewPadding);
                textViewRemove.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        //remove this layout
                        mainLayout.removeView(qualiLayout);
                        tempNotis[index-1][index2]=false;
                    }
                });



                //add layouts
                switchLayout.addView(textViewRemove);
                qualiLayout.addView(textCount2);
                qualiLayout.addView(textContent2);
                qualiLayout.addView(switchLayout);
                mainLayout.addView(qualiLayout);
                mainLayout.setVisibility(View.GONE);
            }
            TextView textViewAdd=new TextView(getContext());
            textViewAdd.setGravity(Gravity.CENTER);
            textViewAdd.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textViewAdd.setPadding(0,(int)CommonHelper.convertDpToPixels(10,getContext()),0,(int)CommonHelper.convertDpToPixels(10,getContext()));
            textViewAdd.setText("Add");
            textViewAdd.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.button_normal));
            textViewAdd.setTypeface(font2);
            textViewAdd.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    new AlertDialog.Builder(getContext())
                            .setItems(dates, new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i)
                                {
                                    addNewLayout(tempNotis,index-1,i,font,dates[i],mainLayout);
                                    tempNotis[index-1][i]=true;
                                }
                            }).create().show();
                }
            });
            //add the view
            mainLayout.addView(textViewAdd);
            mainLayout.setVisibility(View.GONE);


            //add qualicount to its own layout
            countLayout.addView(textQualiCount);
            //add views to tradelayout
            tradeLayout.addView(textCount);
            tradeLayout.addView(textContent);
            tradeLayout.addView(countLayout);
            //add layout to main
            layout.addView(tradeLayout);
            layout.addView(mainLayout);

            pos+=1;
        }
    }
    private void addNewLayout(final boolean[][]tempNotis, final int c,final int d, final Typeface font, final String name, final LinearLayout mainLayout )
    {
        LinearLayout.LayoutParams layoutParams3=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams3.setMargins(0,1,0,1);
        final LinearLayout qualiLayout = new LinearLayout(getContext());
        qualiLayout.setOrientation(LinearLayout.HORIZONTAL);
        qualiLayout.setLayoutParams(layoutParams3);
        qualiLayout.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.tertiary_background));
        qualiLayout.setPadding(10,10,10,10);
        //item number textview
        TextView textCount2=new TextView(getContext());
        textCount2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textCount2.setText("*");
        textCount2.setPadding((int) textViewPadding,(int) textViewPadding,(int) textViewPadding,(int) textViewPadding);
        textCount2.setTypeface(font);
        //content textview
        TextView textContent2=new TextView(getContext());
        textContent2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textContent2.setPadding((int) textViewPadding,(int) textViewPadding,(int) textViewPadding,(int) textViewPadding);
        textContent2.setText(name);
        textContent2.setTypeface(font);
        //layout for switch
        LinearLayout switchLayout = new LinearLayout(getContext());
        switchLayout.setOrientation(LinearLayout.HORIZONTAL);
        switchLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,1));
        switchLayout.setGravity(Gravity.END);
        //switch
        TextView textViewRemove = new TextView(getContext());
        textViewRemove.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_remove,0,0,0);
        textViewRemove.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textViewRemove.setPadding((int) textViewPadding,(int) textViewPadding,(int) textViewPadding,(int) textViewPadding);
        textViewRemove.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //remove this layout
                mainLayout.removeView(qualiLayout);
                tempNotis[c][d]=false;
            }
        });



        //add layouts
        switchLayout.addView(textViewRemove);
        qualiLayout.addView(textCount2);
        qualiLayout.addView(textContent2);
        qualiLayout.addView(switchLayout);
        int position=mainLayout.getChildCount()-1;
        mainLayout.addView(qualiLayout,position);
    }

    public class UpdateTask extends AsyncTask<Void, Void, Boolean>
    {
         final String notifications;

        public UpdateTask(final String note)
        {
           notifications=note;
        }
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            Toast.makeText(getContext(),"Updating started. Please wait...",Toast.LENGTH_SHORT).show();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            List<NameValuePair> infoCommand=new ArrayList<NameValuePair>();
            infoCommand.add(new BasicNameValuePair("id",Integer.toString(LoginActivity.userAccount.getId())));
            infoCommand.add(new BasicNameValuePair("notifications",notifications));
            // making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_update,"POST",infoCommand);
            Log.d("updating info",infoCommand.toString());
            try
            {
                int success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    LoginActivity.userAccount.setNotificationsEquipments(notifications);
                   return true;
                }
                else
                {
                    String message=jsonObject.getString(TAG_MESSAGE);
                    Log.e(TAG_MESSAGE,""+message);
                    return false;
                }
            }
            catch (JSONException e)
            {
                Log.e("JSON",""+e.getMessage());
                return false;
            }
        }
        @Override
        protected void onPostExecute(final Boolean success) {

            if (success)
            {
                Toast.makeText(getContext(),"Succesfully updated",Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(getContext(),"There was an error updating, Please try again",Toast.LENGTH_SHORT).show();

        }

    }
}
