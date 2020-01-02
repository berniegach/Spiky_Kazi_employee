package com.spikingacacia.spikykaziemployee.tasks;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.spikingacacia.spikykaziemployee.JSONParser;
import com.spikingacacia.spikykaziemployee.LoginActivity;
import com.spikingacacia.spikykaziemployee.R;
import com.spikingacacia.spikykaziemployee.database.CTasks;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import static com.spikingacacia.spikykaziemployee.LoginActivity.base_url;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UTTaskOverviewF.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UTTaskOverviewF#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UTTaskOverviewF extends Fragment
{
    private static final String ARG_ID = "id";
    private int mId;
    private int taskListIndex=0;
    private String url_update_task=base_url+"update_task_u.php";
    private String TAG_SUCCESS="success";
    private String TAG_MESSAGE="message";
    private JSONParser jsonParser;
    private OnFragmentInteractionListener mListener;
    private ImageButton startStopButton;
    private String whichFragment;
    //task variables
    private String title;
    private String description;
    private String startings[];
    private String endings[];
    private String repetition;
    private String location[];
    private String position[];
    private String geofence;
    private String dateadded;
    private String datechanged;

    public UTTaskOverviewF()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     */
    // TODO: Rename and change types and number of parameters
    public static UTTaskOverviewF newInstance(int id)
    {
        UTTaskOverviewF fragment = new UTTaskOverviewF();
        Bundle args = new Bundle();
        args.putInt(ARG_ID, id);
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
            mId = getArguments().getInt(ARG_ID);
        }
        jsonParser=new JSONParser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.f_uttask_overview, container, false);
        //layouts
        startStopButton=view.findViewById(R.id.start_stop);
        //get the task using the id
        int index=0;
        Iterator<CTasks> iterator= LoginActivity.uTasksList.iterator();
        while(iterator.hasNext())
        {
            CTasks cTasks=iterator.next();
            int id=cTasks.getId();
            if(id==mId)
            {
                //set the values
                title=cTasks.getTitle();
                description=cTasks.getDescription();
                startings=cTasks.getStartings().split(",");
                endings=cTasks.getEndings().split(",");
                repetition=getResources().getStringArray(R.array.tasks_repeatition)[cTasks.getRepetition()];
                location=cTasks.getLocation().split(",");
                position=cTasks.getPosition().split(":");
                geofence=cTasks.getGeofence();
                dateadded=cTasks.getDateadded();
                datechanged=cTasks.getDatechanged();
                ((TextView)view.findViewById(R.id.title)).setText(title);
                ((TextView)view.findViewById(R.id.description)).setText(description);
                ((TextView)view.findViewById(R.id.start)).setText(startings[0].replace("s","  "));
                ((TextView)view.findViewById(R.id.end)).setText(endings[0].replace("s","  "));
                ((TextView)view.findViewById(R.id.repetition)).setText(repetition);
                ((TextView)view.findViewById(R.id.location)).setText(location[2]);
                ((TextView)view.findViewById(R.id.position)).setText(position[0]);
                ((TextView)view.findViewById(R.id.date_added)).setText(dateadded);
                ((TextView)view.findViewById(R.id.date_changed)).setText(datechanged);
                taskListIndex=index;
            }
            index+=1;
        }
        //modify the start stop button
        whichFragment=UTTasksA.fragmentWhich;
        if (whichFragment.contentEquals("Completed") || whichFragment.contentEquals("Overdue") || whichFragment.contentEquals("late") || whichFragment.contentEquals("All"))
            startStopButton.setVisibility(View.GONE);
        else if(whichFragment.contentEquals("In Progress"))
            startStopButton.setImageResource(R.drawable.ic_stop);
        //onclick listenr
        startStopButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (whichFragment.contentEquals("Pending"))
                    startClicked(1);
                else
                    startClicked(2);
            }
        });

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
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener
    {
        void onUpdate(int id);
        void onDelete();
        void onStartClicked();
        void onEndClicked();
    }
    private void startClicked(int which )
    {
        String title="";
        if(which==1)
            title="Are you sure you want to start the task?";
        else
            title="Are you sure you want to end the task?";
        new AlertDialog.Builder(getContext())
                .setTitle("Carry out this task")
                .setMessage(title)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Proceed", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        //get time now
                        if(which==1)
                            new StartTask().execute((Void)null);
                        else
                            new EndTask().execute((Void)null);

                    }
                }).create().show();
    }
    public class StartTask extends AsyncTask<Void, Void, Boolean>
    {
        private int success=0;
        private String start="";
        private String end="";

        StartTask()
        {
            Toast.makeText(getContext(),"Updating started. Please wait...",Toast.LENGTH_SHORT).show();
            Log.d("UPDATING TASK: ","updating....");
            //get time now
            Calendar calendar=Calendar.getInstance();
            int year=calendar.get(Calendar.YEAR);
            int month=calendar.get(Calendar.MONTH)+1;
            int day=calendar.get(Calendar.DAY_OF_MONTH);
            int hour=calendar.get(Calendar.HOUR_OF_DAY);
            int mins=calendar.get(Calendar.MINUTE);
            String when=hour<12?"AM":"PM";
            hour=hour<=12?hour:hour-12;
            for(int c=0; c<startings.length; c++)
                start+=startings[c]+',';
            start+=String.valueOf(LoginActivity.userAccount.getId())+"id"+String.format("%d/%d/%d",day,month,year)+"s"+String.format("%d:%02d %s",hour,mins,when);
            for(int c=0; c<endings.length; c++)
            {
                end+=endings[c];
                if(c!=endings.length-1)
                    end+=',';
            }

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //logIn=handler.LogInStaff(mEmail,mPassword);
            //building parameters
            List<NameValuePair> info=new ArrayList<NameValuePair>();
            info.add(new BasicNameValuePair("bossid",Integer.toString(LoginActivity.contractorAccount.getId())));
            info.add(new BasicNameValuePair("id",String.valueOf(mId)));
            info.add(new BasicNameValuePair("starting",start));
            info.add(new BasicNameValuePair("ending",end));
            //getting all account details by making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_update_task,"POST",info);
            try
            {
                success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
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
        protected void onPostExecute(final Boolean successful) {
            Log.d("UPDATING TASK: ","finished....");
            if (successful)
            {
                Toast.makeText(getContext(),"Updating successful",Toast.LENGTH_SHORT).show();
                //set the task to inprogress
                CTasks cTasks=LoginActivity.uTasksList.get(taskListIndex);
                cTasks.setStartings(start);
                cTasks.setEndings(end);
                cTasks.setPending(cTasks.getPending()-1);
                cTasks.setInProgress(cTasks.getInProgress()+1);
                //cTasks.setPendingIds(cTasks.getPendingIds()+":"+String.valueOf(LoginActivity.userAccount.getId()));
                cTasks.setInProgressIds(cTasks.getInProgressIds()+":"+String.valueOf(LoginActivity.userAccount.getId()));
                LoginActivity.uTasksList.set(taskListIndex,cTasks);
                if(mListener!=null)
                    mListener.onStartClicked();
            }
            else
            {
                Toast.makeText(getContext(),"Error, Please try again.",Toast.LENGTH_SHORT).show();
            }
        }

    }
    public class EndTask extends AsyncTask<Void, Void, Boolean>
    {
        private int success=0;
        private String start="";
        private String end="";

        EndTask()
        {
            Toast.makeText(getContext(),"Updating started. Please wait...",Toast.LENGTH_SHORT).show();
            Log.d("UPDATING TASK: ","updating....");
            //get time now
            Calendar calendar=Calendar.getInstance();
            int year=calendar.get(Calendar.YEAR);
            int month=calendar.get(Calendar.MONTH)+1;
            int day=calendar.get(Calendar.DAY_OF_MONTH);
            int hour=calendar.get(Calendar.HOUR_OF_DAY);
            int mins=calendar.get(Calendar.MINUTE);
            String when=hour<12?"AM":"PM";
            hour=hour<=12?hour:hour-12;
            for(int c=0; c<endings.length; c++)
                end+=endings[c]+',';
            end+=String.valueOf(LoginActivity.userAccount.getId())+"id"+String.format("%d/%d/%d",day,month,year)+"s"+String.format("%d:%02d %s",hour,mins,when);
            for(int c=0; c<startings.length; c++)
            {
                start+=startings[c];
                if(c!=startings.length-1)
                    start+=',';
            }

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //logIn=handler.LogInStaff(mEmail,mPassword);
            //building parameters
            List<NameValuePair> info=new ArrayList<NameValuePair>();
            info.add(new BasicNameValuePair("bossid",Integer.toString(LoginActivity.contractorAccount.getId())));
            info.add(new BasicNameValuePair("id",String.valueOf(mId)));
            info.add(new BasicNameValuePair("starting",start));
            info.add(new BasicNameValuePair("ending",end));
            //getting all account details by making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_update_task,"POST",info);
            try
            {
                success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
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
        protected void onPostExecute(final Boolean successful) {
            Log.d("UPDATING TASK: ","finished....");
            if (successful)
            {
                Toast.makeText(getContext(),"Updating succesful",Toast.LENGTH_SHORT).show();
                //set the task to inprogress
                CTasks cTasks=LoginActivity.uTasksList.get(taskListIndex);
                cTasks.setStartings(start);
                cTasks.setEndings(end);
                cTasks.setInProgress(cTasks.getInProgress()-1);
                cTasks.setCompleted(cTasks.getCompleted()+1);
                //cTasks.setPendingIds(cTasks.getPendingIds()+":"+String.valueOf(LoginActivity.userAccount.getId()));
                cTasks.setCompletedIds(cTasks.getCompletedIds()+":"+String.valueOf(LoginActivity.userAccount.getId()));
                LoginActivity.uTasksList.set(taskListIndex,cTasks);
                if(mListener!=null)
                    mListener.onEndClicked();
            }
            else
            {
                Toast.makeText(getContext(),"Error, Please try again.",Toast.LENGTH_SHORT).show();
            }
        }

    }


}
