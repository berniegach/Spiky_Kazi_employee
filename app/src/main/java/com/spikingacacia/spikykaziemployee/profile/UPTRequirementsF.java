package com.spikingacacia.spikykaziemployee.profile;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.spikingacacia.spikykaziemployee.GetFilePathFromDevice;
import com.spikingacacia.spikykaziemployee.JSONParser;
import com.spikingacacia.spikykaziemployee.LoginActivity;
import com.spikingacacia.spikykaziemployee.Preferences;
import com.spikingacacia.spikykaziemployee.R;
import com.spikingacacia.spikykaziemployee.database.UserCertificates;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class UPTRequirementsF extends Fragment
{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
   // private OnFragmentInteractionListener mListener;
    private final int PICK_IMAGE_REQUEST=1;
    private final int PERMISSION_REQUEST_INTERNET=2;
    private LinearLayout layout;
    private String url_update= LoginActivity.base_url+"update_qualifications_matrix.php";
    private String url_upload_cert= LoginActivity.base_url+"upload_certs.php";
    private String url_update_cert= LoginActivity.base_url+"update_certs.php";
    private String url_get_certs= LoginActivity.base_url+"src/contractors/";//+".jpg";
    private JSONParser jsonParser;
    private String TAG_SUCCESS="success";
    private String TAG_MESSAGE="message";
    private int[]values;
    private int tradeId;
    private boolean certsChanged;
    private boolean requirementsChanged;
    private boolean datesIssueChanged;
    private boolean datesExpiryChanged;
    LinkedHashMap<Integer, UserCertificates>tempUsercertificates;
    LinkedHashMap<String,String>tempCertsIssueDate;
    LinkedHashMap<String,String>tempCertsExpiryDate;
    private TextView certficateTv;
    private String certName;
    private List<Boolean>compliance;
    private Preferences preferences;

    public UPTRequirementsF()
    {
        // Required empty public constructor
    }
    public static UPTRequirementsF newInstance(String param1, String param2)
    {
        UPTRequirementsF fragment = new UPTRequirementsF();
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
        View view= inflater.inflate(R.layout.f_crstrades_quali, container, false);
        preferences = new Preferences(getContext());
        ((Button)view.findViewById(R.id.update_button)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                new UpdateTask().execute((Void)null);
            }
        });
        certsChanged=false;
        requirementsChanged=false;
        datesIssueChanged=false;
        datesExpiryChanged=false;
        tempCertsIssueDate=new LinkedHashMap<>();
        tempCertsExpiryDate=new LinkedHashMap<>();
        compliance=new ArrayList<>();
        values=new int[LoginActivity.personnelColumnsList.size()];
        for(int c=0; c<values.length; c++)
        {
            values[c]=LoginActivity.onePersonnelMatrix[c];
        }
        //get trade id
        tradeId=getTradeId();
        //main layout
        layout=view.findViewById(R.id.base);
        getPersonnelHaveRequirements();
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
    }*/

    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onUpdate();
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
    private void addLayouts()
    {
        Typeface font= ResourcesCompat.getFont(getContext(),R.font.arima_madurai);
        //get the position matrix
        int[]requi=LoginActivity.personnelMatrix[tradeId];
        int pos=0,comp_index=0;
        Iterator iterator= LoginActivity.personnelColumnsList.entrySet().iterator();
        while (iterator.hasNext())
        {
            final int index=pos;
            LinkedHashMap.Entry<String,Character>set2=(LinkedHashMap.Entry<String, Character>) iterator.next();
            String username=set2.getKey();
            username=username.replace("_"," ");
            final String name=username;
            Character which=set2.getValue();
            if(which=='t')
                continue;
            //check if the qualification is required
            if(requi[pos]==0)
            {
                pos+=1;
                continue;
            }


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
            if(preferences.isDark_theme_enabled())
                tradeLayout.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.secondary_background));
            else
                tradeLayout.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.secondary_background_light));
            tradeLayout.setPadding(10,10,10,10);
            tradeLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if(mainLayout.getVisibility()==View.GONE)
                        expand(mainLayout);
                    else
                        collapse(mainLayout);
                }
            });
            //item number textview
            TextView textCount=new TextView(getContext());
            textCount.setLayoutParams(layoutParams);
            textCount.setText(String.valueOf(pos+1));
            textCount.setPadding(10,10,30,10);
            textCount.setTypeface(font);
            //content textview
            TextView textContent=new TextView(getContext());
            textContent.setLayoutParams(layoutParams);
            textContent.setText(name);
            textContent.setTypeface(font);
            //layout for count
            LinearLayout countLayout = new LinearLayout(getContext());
            countLayout.setOrientation(LinearLayout.HORIZONTAL);
            countLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,1));
            countLayout.setGravity(Gravity.END);
            //quals count textview
            TextView textQualiCount=new TextView(getContext());
            textQualiCount.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textQualiCount.setBackgroundResource(compliance.get(comp_index)?R.drawable.circle_compliant:R.drawable.circle_non_compliant);
            textQualiCount.setGravity(Gravity.CENTER);

            //add the qualifications layout
            //certificates
            LinearLayout.LayoutParams layoutParams3=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams3.setMargins(0,1,0,1);
            LinearLayout qualiLayout = new LinearLayout(getContext());
            qualiLayout.setOrientation(LinearLayout.HORIZONTAL);
            qualiLayout.setLayoutParams(layoutParams3);
            if(preferences.isDark_theme_enabled())
                qualiLayout.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.secondary_background));
            else
                qualiLayout.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.secondary_background_light));
            qualiLayout.setPadding(10,10,10,10);

            //content textview
            TextView textContent2=new TextView(getContext());
            textContent2.setLayoutParams(layoutParams);
            textContent2.setText("have it?");
            textContent2.setTypeface(font);
            //layout for switch
            LinearLayout switchLayout = new LinearLayout(getContext());
            switchLayout.setOrientation(LinearLayout.HORIZONTAL);
            switchLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,1));
            switchLayout.setGravity(Gravity.END);
            //switch
            Switch oneSwitch = new Switch(new ContextThemeWrapper(getContext(), R.style.switchTheme));
            oneSwitch.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            oneSwitch.setChecked(values[index]==1);
            oneSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b)
                {
                    if(b)
                        values[index]=1;
                    else
                        values[index]=0;
                    requirementsChanged=true;
                }
            });
            //certificate button
            TextView textView=new TextView(getContext());
            textView.setLayoutParams(layoutParams3);
            textView.setText(hasCertificate(name)?"Available":"Certificate");
            Log.d("cert",LoginActivity.userCertificatesNames[pos]+"---"+name+"\n");
            textView.setPadding(0,12,0,12);
            textView.setGravity(Gravity.CENTER);
            textView.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.button_normal));
            textView.setTypeface(font);
            textView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    final View v=view;
                    certName=name;
                    new AlertDialog.Builder(getContext())
                            .setItems(new String[]{"New", "View"}, new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    if(which==0)
                                        findCertficateImage(v);
                                    else
                                        showCertificate(name);
                                }
                            }).create().show();
                }
            });
            //layout for dates
            LinearLayout layoutDates=new LinearLayout(getContext());
            layoutDates.setLayoutParams(layoutParams3);
            layoutDates.setOrientation(LinearLayout.HORIZONTAL);
            layoutDates.setWeightSum(2);
            //textview issue
            TextView textViewIssue=new TextView(getContext());
            textViewIssue.setLayoutParams((new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,1)));
            String issueDate=LoginActivity.userCertsIssue.get(name);
            textViewIssue.setText(issueDate==null?"Issue":issueDate.contentEquals("0")?"Issue":"Issue: "+issueDate);
            textViewIssue.setPadding(0,12,0,12);
            textViewIssue.setGravity(Gravity.CENTER);
            if(preferences.isDark_theme_enabled())
                textViewIssue.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.tertiary_background));
            else
                textViewIssue.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.tertiary_background_light));
            textViewIssue.setTypeface(font);
            textViewIssue.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    setDatePicker("Set the issue date",view,name,1);
                }
            });
            //textView expiry
            TextView textViewExpiry=new TextView(getContext());
            textViewExpiry.setLayoutParams((new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,1)));
            String expiryDate=LoginActivity.userCertsExpiry.get(name);
            textViewExpiry.setText(expiryDate==null?"Expiry":expiryDate.contentEquals("0")?"Expiry":"Expiry: "+expiryDate);
            textViewExpiry.setGravity(Gravity.CENTER);
            textViewExpiry.setPadding(0,12,0,12);
            if(preferences.isDark_theme_enabled())
                textViewExpiry.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.tertiary_background));
            else
                textViewExpiry.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.tertiary_background_light));
            textViewExpiry.setTypeface(font);
            textViewExpiry.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    setDatePicker("Set the expiry date",view,name,2);
                }
            });
            //add date layouts
            layoutDates.addView(textViewIssue);
            layoutDates.addView(textViewExpiry);
            //add layouts
            switchLayout.addView(oneSwitch);
            qualiLayout.addView(textContent2);
            qualiLayout.addView(switchLayout);
            mainLayout.addView(qualiLayout);
            mainLayout.addView(textView);
            mainLayout.addView(layoutDates);
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
            comp_index+=1;
        }
    }
    private void getPersonnelHaveRequirements()
    {
        int[]values;
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
                have_requirement=true;
            }
            if(LoginActivity.userCertificatesNames[pos].contentEquals(name))
            {
                have_cert=true;
            }

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
                    ;//expiredCertificates += 1;
                else
                    valid_cert=true;
            }
            compliance.add(have_requirement && have_cert && valid_cert);
            pos+=1;
        }
    }
    private static void expand(final View v)
    {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight=v.getMeasuredHeight();
        //older versions of android  (pre api 21) cancel animations for view with a height of 0
        v.getLayoutParams().height=1;
        v.setVisibility(View.VISIBLE);
        Animation animation=new Animation()
        {
            @Override
            public boolean willChangeBounds()
            {
                return true;
            }

            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t)
            {
                v.getLayoutParams().height=interpolatedTime==1? LinearLayout.LayoutParams.WRAP_CONTENT:(int)(targetHeight*interpolatedTime);
                v.requestLayout();
            }
        };
        animation.setDuration((int)(targetHeight/v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(animation);
    }
    private static void collapse(final View v)
    {
        final int initialHeight=v.getMeasuredHeight();
        Animation animation=new Animation()
        {
            @Override
            public boolean willChangeBounds()
            {
                return true;
            }

            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t)
            {
                if(interpolatedTime==1)
                    v.setVisibility(View.GONE);
                else
                {
                    v.getLayoutParams().height=initialHeight-(int)(initialHeight*interpolatedTime);
                    v.requestLayout();
                }
            }
        };
        animation.setDuration((int)(initialHeight/v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(animation);
    }
    public class UpdateTask extends AsyncTask<Void, Void, Boolean>
    {

        public UpdateTask(){
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
            Boolean ok = true;
            //update the trades matrix one by one
            if(requirementsChanged)
            {
                final int end=values.length-1;
                int pos=0;
                String sqlcommand="UPDATE "+makeName(LoginActivity.contractorAccount.getId())+"_personnel_matrix"+" SET ";
                Iterator iterator_quals=LoginActivity.personnelColumnsList.entrySet().iterator();
                while(iterator_quals.hasNext())
                {
                    LinkedHashMap.Entry<String, Character> set2 = (LinkedHashMap.Entry<String, Character>) iterator_quals.next();
                    String name2 = set2.getKey();
                    Character which = set2.getValue();
                    if (which == 't')
                        continue;
                    sqlcommand+=name2+"="+String.valueOf(values[pos]);
                    if(pos!=end)
                        sqlcommand+=",";
                    pos+=1;
                    Log.d("values",String.format("pos2==%d end==%d",pos,end));
                }
                sqlcommand=sqlcommand.substring(0,sqlcommand.length()-1);
                sqlcommand+=" WHERE userid="+String.valueOf(LoginActivity.userAccount.getId());
                //buiild parameters
                List<NameValuePair> infoCommand=new ArrayList<NameValuePair>();
                infoCommand.add(new BasicNameValuePair("sqlcommand",sqlcommand));
                // making HTTP request
                JSONObject jsonObject= jsonParser.makeHttpRequest(url_update,"POST",infoCommand);
                Log.d("updating info",infoCommand.toString());
                Log.d("updating trades matrix",""+jsonObject.toString());
                try
                {
                    int success=jsonObject.getInt(TAG_SUCCESS);
                    if(success==1)
                    {
                        for(int c=0; c<values.length; c++)
                        {
                            LoginActivity.onePersonnelMatrix[c]=values[c];
                        }
                        requirementsChanged=false;
                    }
                    else
                    {
                        String message=jsonObject.getString(TAG_MESSAGE);
                        Log.e(TAG_MESSAGE,""+message);
                        ok= false;
                    }
                }
                catch (JSONException e)
                {
                    Log.e("JSON",""+e.getMessage());
                    ok= false;
                }
            }
            if(datesIssueChanged)
            {
                Iterator iterator=tempCertsIssueDate.entrySet().iterator();
                while(iterator.hasNext())
                {
                    LinkedHashMap.Entry<String,String>set=(LinkedHashMap.Entry<String, String>) iterator.next();
                    String qualification=set.getKey();
                    String date=set.getValue();
                    //buiild parameters
                    List<NameValuePair> info=new ArrayList<NameValuePair>();
                    info.add(new BasicNameValuePair("id",String.valueOf(LoginActivity.contractorAccount.getId())));
                    info.add(new BasicNameValuePair("userid",String.valueOf(LoginActivity.userAccount.getId())));
                    info.add(new BasicNameValuePair("whereis",qualification));
                    info.add(new BasicNameValuePair("issue",date));
                    info.add(new BasicNameValuePair("expiry","0"));
                    // making HTTP request
                    JSONObject jsonObject= jsonParser.makeHttpRequest(url_update_cert,"POST",info);
                    //Log.d("updating info",info.toString());
                    Log.d("updating issue dates",""+jsonObject.toString());
                    try
                    {
                        int success=jsonObject.getInt(TAG_SUCCESS);
                        if(success==1)
                        {
                            LoginActivity.userCertsIssue.put(qualification,date);
                        }
                        else
                        {
                            String message=jsonObject.getString(TAG_MESSAGE);
                            Log.e(TAG_MESSAGE,""+message);
                            ok= false;
                        }
                    }
                    catch (JSONException e)
                    {
                        Log.e("JSON",""+e.getMessage());
                        ok= false;
                    }
                }
                datesIssueChanged=false;
            }
            if(datesExpiryChanged)
            {
                Iterator iterator=tempCertsExpiryDate.entrySet().iterator();
                while(iterator.hasNext())
                {
                    LinkedHashMap.Entry<String,String>set=(LinkedHashMap.Entry<String, String>) iterator.next();
                    String qualification=set.getKey();
                    String date=set.getValue();
                    //buiild parameters
                    List<NameValuePair> info=new ArrayList<NameValuePair>();
                    info.add(new BasicNameValuePair("id",String.valueOf(LoginActivity.contractorAccount.getId())));
                    info.add(new BasicNameValuePair("userid",String.valueOf(LoginActivity.userAccount.getId())));
                    info.add(new BasicNameValuePair("whereis",qualification));
                    info.add(new BasicNameValuePair("issue","0"));
                    info.add(new BasicNameValuePair("expiry",date));
                    // making HTTP request
                    JSONObject jsonObject= jsonParser.makeHttpRequest(url_update_cert,"POST",info);
                    //Log.d("updating info",info.toString());
                    Log.d("updating expiry dates",""+jsonObject.toString());
                    try
                    {
                        int success=jsonObject.getInt(TAG_SUCCESS);
                        if(success==1)
                        {
                            LoginActivity.userCertsExpiry.put(qualification,date);
                        }
                        else
                        {
                            String message=jsonObject.getString(TAG_MESSAGE);
                            Log.e(TAG_MESSAGE,""+message);
                            ok= false;
                        }
                    }
                    catch (JSONException e)
                    {
                        Log.e("JSON",""+e.getMessage());
                        ok= false;
                    }
                }
                datesExpiryChanged=false;
            }


            return ok;
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
        private String makeName(int id)
        {
            String letters=String.valueOf(id);
            char[] array=letters.toCharArray();
            String name="";
            for(int count=0; count<array.length; count++)
            {
                switch (array[count])
                {
                    case '0':
                        name+="zero";
                        break;
                    case '1':
                        name+="one";
                        break;
                    case '2':
                        name+="two";
                        break;
                    case '3':
                        name+="three";
                        break;
                    case '4':
                        name+="four";
                        break;
                    case '5':
                        name+="five";
                        break;
                    case '6':
                        name+="six";
                        break;
                    case '7':
                        name+="seven";
                        break;
                    case '8':
                        name+="eight";
                        break;
                    case '9':
                        name+="nine";
                        break;
                    default :
                        name+="NON";
                }
            }
            return name;
        }

    }
    private void setDatePicker(String title,final View btn,String qualification,final int choice)
    {
        final String date;
        final String qualification_final=qualification.replace(" ","_");
        final AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        final DatePicker datePicker=new DatePicker(getContext());
        builder.setView(datePicker);
        builder.setTitle(title);
        builder.setPositiveButton("Set", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                int day; int month; final int year;
                day=datePicker.getDayOfMonth();
                month=datePicker.getMonth()+1;
                year=datePicker.getYear();
                String newDate=String.format("%d/%d/%d",day,month,year);
                //issue
                if(choice==1)
                {
                    ((TextView)btn).setText(String.format("Issue: %d/%d/%d",day,month,year));
                    tempCertsIssueDate.put(qualification_final,newDate);
                    datesIssueChanged=true;
                }
                else if(choice==2)
                {
                    ((TextView)btn).setText(String.format("Expiry: %d/%d/%d",day,month,year));
                    tempCertsExpiryDate.put(qualification_final,newDate);
                    datesExpiryChanged=true;
                }
            }
        });
        builder.create();
        builder.show();
    }
    private void  findCertficateImage(View txt)
    {
        certficateTv=(TextView) txt;
        Intent intent=new Intent();
        //show only images
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES,new String[]{"image/jpeg"});
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //Always show a chooser if there are mulitple options availabel
        startActivityForResult(Intent.createChooser(intent,"Select certificate Image in jpg format"),PICK_IMAGE_REQUEST);
        //layout.setBackground(new BitmapDrawable(getResources(),certificateBitmap));
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==PICK_IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            Uri uri=data.getData();
            try
            {
                certficateTv.setBackgroundColor(getResources().getColor(R.color.button_normal));

                String path = GetFilePathFromDevice.getPath(getContext(),uri);
                String[] token=path.split(".");
                String [] title=path.split("/");
                for(int count=0; count<token.length; count+=1)
                    Log.d("token",String.format("count: %d token: %s",count,token[count]));
                certficateTv.setText(title[title.length-1]);

               // certificates.get(certIndex).setCert(String.format("%d_%d",LoginActivity.userAccount.getId(),certIndex));
                Log.d("uri",GetFilePathFromDevice.getPath(getContext(),uri));
               // certUri[certIndex]=uri;
                uploadCert(1,path);

            }
            catch (Exception e)
            {
                Log.e("bitmap",""+e.getMessage());
            }
        }
    }
    /*
     * This is the method responsible for pdf upload
     * We need the full pdf path and the name for the pdf in this method
     * */

    private boolean uploadCert(int index,final String path) {
        boolean ok=true;
        //first check permission
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        {
            //getting name for the image
            String name=String.format("%d_%d",LoginActivity.userAccount.getId(),index);
            //getting the actual path of the image
            //String path = FilePath.getPath(this, filePath);
            // String path="";//=getPath(certUri[index]);

            if (path == null)
            {
                Log.e("upload cert","its null");

            }
            else
            {
                //Uploading code
                try
                {
                    certName=certName.replace(" ","_");
                    String uploadId = UUID.randomUUID().toString();
                    //Creating a multi part request
                    new MultipartUploadRequest(getContext(), uploadId, url_upload_cert)
                            .addFileToUpload(path, "jpg") //Adding file
                            .addParameter("name", certName) //Adding text parameter to the request
                            .addParameter("id",String.valueOf(LoginActivity.contractorAccount.getId()))
                            .addParameter("userid",String.valueOf(LoginActivity.userAccount.getId()))
                            .setNotificationConfig(new UploadNotificationConfig())
                            .setMaxRetries(2)
                            .setDelegate(new UploadStatusDelegate()
                            {
                                @Override
                                public void onProgress(Context context, UploadInfo uploadInfo)
                                {
                                    Log.d("GOTEV",uploadInfo.toString());
                                }

                                @Override
                                public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception)
                                {
                                    Log.e("GOTEV",uploadInfo.toString()+"\n"+exception.toString());
                                }

                                @Override
                                public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse)
                                {
                                    Log.d("GOTEV","completed "+uploadInfo.toString());
                                    certficateTv.setText("Available");
                                    certficateTv.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.button_normal));
                                    //JSONObject result = new JSONObject(serverResponse);
                                }

                                @Override
                                public void onCancelled(Context context, UploadInfo uploadInfo)
                                {
                                    Log.d("GOTEV","cancelled"+uploadInfo.toString());
                                }
                            })
                            .startUpload(); //Starting the upload
                }
                catch (Exception e)
                {
                    Log.e("image upload",""+e.getMessage());
                    e.printStackTrace();
                    ok=false;
                }
            }
        }
        //request the permission
        else
        {
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_REQUEST_INTERNET);
        }

        return ok;
    }
    private boolean hasCertificate(String name)
    {
        for(int c=0; c<LoginActivity.userCertificatesNames.length; c++)
        {
            if(LoginActivity.userCertificatesNames[c].contentEquals(name))
                return true;
        }
        return false;
    }
    private void showCertificate(String certName)
    {
        certName=certName.replace(" ","_");
        final String name=certName;
        String url=url_get_certs+makeName(LoginActivity.contractorAccount.getId())+"/certificates/"+makeName(LoginActivity.userAccount.getId())+'/';url+=name+".jpg";
        Log.d("Certificate name","  "+url);
        ImageRequest request=new ImageRequest(
                url,
                new Response.Listener<Bitmap>()
                {
                    @Override
                    public void onResponse(Bitmap response)
                    {
                        ImageView imageView=new ImageView(getContext());
                        imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        imageView.setImageBitmap(response);
                        Log.d("volley","succesful getting certificate:  "+name);
                        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                                builder.setTitle(name)
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        dialog.dismiss();
                                    }
                                });
                                AlertDialog dialog=builder.create();
                                dialog.setView(imageView);
                                dialog.show();
                    }
                }, 0, 0, null,
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError e)
                    {
                        Log.e("voley",""+e.getMessage()+e.toString());
                        Toast.makeText(getContext(), "No certificate", Toast.LENGTH_SHORT).show();
                    }
                });
        RequestQueue request2 = Volley.newRequestQueue(getContext());
        request2.add(request);
    }
    private String makeName(int id)
    {
        String letters=String.valueOf(id);
        char[] array=letters.toCharArray();
        String name="";
        for(int count=0; count<array.length; count++)
        {
            switch (array[count])
            {
                case '0':
                    name+="zero";
                    break;
                case '1':
                    name+="one";
                    break;
                case '2':
                    name+="two";
                    break;
                case '3':
                    name+="three";
                    break;
                case '4':
                    name+="four";
                    break;
                case '5':
                    name+="five";
                    break;
                case '6':
                    name+="six";
                    break;
                case '7':
                    name+="seven";
                    break;
                case '8':
                    name+="eight";
                    break;
                case '9':
                    name+="nine";
                    break;
                default :
                    name+="NON";
            }
        }
        return name;
    }

}
