package com.spikingacacia.spikykaziemployee.profile;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.spikingacacia.spikykaziemployee.JSONParser;
import com.spikingacacia.spikykaziemployee.LoginActivity;
import com.spikingacacia.spikykaziemployee.Preferences;
import com.spikingacacia.spikykaziemployee.R;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class UPTEquipPropertiesF extends Fragment
{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
   // private OnFragmentInteractionListener mListener;
    private final int PICK_IMAGE_REQUEST=1;
    private LinearLayout layout;
    private String url_update= LoginActivity.base_url+"update_u_equipments_matrix.php";
    private String url_insert= LoginActivity.base_url+"insert_u_equipment.php";
    private String url_delete= LoginActivity.base_url+"delete_u_equipment.php";
    private String url_upload_cert= LoginActivity.base_url+"upload_certs_equipments.php";
    private String url_get_certs= LoginActivity.base_url+"src/contractors/";//+".jpg";
    private String url_update_cert= LoginActivity.base_url+"update_certs_equipments.php";


    private JSONParser jsonParser;
    private String TAG_SUCCESS="success";
    private String TAG_MESSAGE="message";
    private int[][]values;
    private int[][]personnelValues;
    private String TAG_THIS="UPTEquipPropertiesF";
    private TextView certficateTv;
    private String certName;
    LinkedHashMap<String,String>tempCertsIssueDate;
    LinkedHashMap<String,String>tempCertsExpiryDate;
    private boolean requirementsChanged;
    private boolean datesIssueChanged;
    private boolean datesExpiryChanged;
    private Preferences preferences;

    public UPTEquipPropertiesF()
    {
        // Required empty public constructor
    }
    public static UPTEquipPropertiesF newInstance(String param1, String param2)
    {
        UPTEquipPropertiesF fragment = new UPTEquipPropertiesF();
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
        View view= inflater.inflate(R.layout.f_uptequi_properties, container, false);
        preferences = new Preferences(getContext());
        ((Button)view.findViewById(R.id.update_button)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                new UpdateTask().execute((Void)null);
            }
        });
        requirementsChanged=false;
        datesIssueChanged=false;
        datesExpiryChanged=false;
        tempCertsIssueDate=new LinkedHashMap<>();
        tempCertsExpiryDate=new LinkedHashMap<>();
        values=new int[LoginActivity.equipmentsList.size()][LoginActivity.equipmentsColumnsList.size()];
        for(int c=0; c<values.length; c++)
        {
            for(int d=0; d<values[c].length; d++)
                values[c][d]=LoginActivity.equipmentsMatrix[c][d];
        }
       /* personnelValues=new int[LoginActivity.personnelEquipmentsMatrix.length][LoginActivity.equipmentsColumnsList.size()];
        for(int c=0; c<personnelValues.length; c++)
        {
            for(int d=0; d<personnelValues[c].length; d++)
                personnelValues[c][d]=LoginActivity.personnelEquipmentsMatrix[c][d];
        }*/
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
    }*/

    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onUpdate();
    }
    private void addLayouts()
    {
        Typeface font= ResourcesCompat.getFont(getContext(),R.font.arima_madurai);
        TypedArray array=getContext().getTheme().obtainStyledAttributes(new int[]{android.R.attr.listDivider});
        Drawable drawable=array.getDrawable(0);
        array.recycle();
        int pos=1;
        Iterator iterator= LoginActivity.equipmentsList.entrySet().iterator();
        while (iterator.hasNext())
        {
            final int index=pos;
            LinkedHashMap.Entry<String,Integer>set=(LinkedHashMap.Entry<String, Integer>) iterator.next();
            final String name=set.getKey();
            final Integer count=set.getValue();

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
            textCount.setText(String.valueOf(pos));
            textCount.setPadding(10,10,30,10);
            textCount.setTypeface(font);
            //content textview
            TextView textContent=new TextView(getContext());
            textContent.setLayoutParams(layoutParams);
            textContent.setText(name);
            textContent.setTypeface(font);
            //switch have it
            //switch
            final Switch haveItSwitch = new Switch(new ContextThemeWrapper(getContext(), R.style.switchTheme));
            haveItSwitch.setTextOff("");
            haveItSwitch.setTextOn("Have it");
            haveItSwitch.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            haveItSwitch.setPadding(10,10,30,10);
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
                            haveItSwitch.setChecked(true);
                            break;
                        }
                    }
                    columnIndex+=1;
                }
            }
            haveItSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    if(isChecked)
                    {
                        new AlertDialog.Builder(getContext())
                                .setMessage("Are you sure you want to add this equipment?")
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
                                        new InsertEquipmentTask(name).execute((Void) null);
                                    }
                                }).create().show();
                    }
                    else
                    {
                        new AlertDialog.Builder(getContext())
                                .setMessage("Are you sure you want to DELETE this equipment?")
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
                                        new DeleteEquipmentTask(name).execute((Void) null);
                                    }
                                }).create().show();

                    }
                }
            });
            //haveItSwitch.setChecked(values[index-1][index2-1]==1);
            //layout for count
            LinearLayout countLayout = new LinearLayout(getContext());
            countLayout.setOrientation(LinearLayout.HORIZONTAL);
            countLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,1));
            countLayout.setGravity(Gravity.END);
            //quals count textview
            int countOn=0;
            final TextView textQualiCount=new TextView(getContext());
            textQualiCount.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textQualiCount.setText(name);
            textQualiCount.setBackgroundResource(R.drawable.circle);
            textQualiCount.setText("0");
            textQualiCount.setGravity(Gravity.CENTER);
            textQualiCount.setTypeface(font);

            //add the qualifications layout
            //qualifications
            Iterator iterator_quals=LoginActivity.equipmentsColumnsList.entrySet().iterator();
            int columnsIndex=0;
            while(iterator_quals.hasNext())
            {
                final int columnsIndexFinal=columnsIndex;
                LinkedHashMap.Entry<String,Character>set2=(LinkedHashMap.Entry<String, Character>) iterator_quals.next();
                String name2=set2.getKey();
                Character which=set2.getValue();
                if(which=='t')
                    continue;
                if(tempPersonnel[columnsIndex]==1)
                    countOn+=1;
                LinearLayout.LayoutParams layoutParams3=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams3.setMargins(0,1,0,1);
                LinearLayout qualiLayout = new LinearLayout(getContext());
                qualiLayout.setOrientation(LinearLayout.HORIZONTAL);
                qualiLayout.setLayoutParams(layoutParams3);
                if(preferences.isDark_theme_enabled())
                    qualiLayout.setBackgroundColor(which=='m'?ContextCompat.getColor(getContext(),R.color.secondary_background):ContextCompat.getColor(getContext(),R.color.tertiary_background));
                else
                    qualiLayout.setBackgroundColor(which=='m'?ContextCompat.getColor(getContext(),R.color.secondary_background_light):ContextCompat.getColor(getContext(),R.color.tertiary_background_light));
                qualiLayout.setPadding(10,10,10,10);
                //item number textview
                TextView textCount2=new TextView(getContext());
                textCount2.setLayoutParams(layoutParams);
                textCount2.setText(String.valueOf(columnsIndex+1));
                textCount2.setPadding(10,10,30,10);
                textCount2.setTypeface(font);
                //content textview
                TextView textContent2=new TextView(getContext());
                textContent2.setLayoutParams(layoutParams);
                textContent2.setText(name2);
                textContent2.setTypeface(font);
                //layout for switch
                LinearLayout switchLayout = new LinearLayout(getContext());
                switchLayout.setOrientation(LinearLayout.HORIZONTAL);
                switchLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,1));
                switchLayout.setGravity(Gravity.END);
                //switch
                Switch oneSwitch = new Switch(new ContextThemeWrapper(getContext(), R.style.switchTheme));
                oneSwitch.setTextOff("Fail");
                oneSwitch.setTextOn("Pass");
                oneSwitch.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                oneSwitch.setChecked(false);
                oneSwitch.setChecked(tempPersonnel[columnsIndex]==1);
                final int newCountCountOnAdd=countOn+=1;
                final int newCountCountOnSub=countOn-=1;
                final int[]tempPersonnelRemove=tempPersonnel;
                oneSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
                {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b)
                    {
                        if(!haveItSwitch.isChecked())
                        {
                            Toast.makeText(getContext(),"Please add the equipment first before\nchanging the properties",Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(b)
                        {
                            tempPersonnelRemove[columnsIndexFinal] = 1;
                            textQualiCount.setText(String.valueOf(newCountCountOnAdd));
                            LoginActivity.personnelEquipmentsMatrix.put(name,tempPersonnelRemove);
                            requirementsChanged=true;
                        }
                        else
                        {
                            tempPersonnelRemove[columnsIndexFinal]= 0;
                            textQualiCount.setText(String.valueOf(newCountCountOnSub));
                            LoginActivity.personnelEquipmentsMatrix.put(name,tempPersonnelRemove);
                            requirementsChanged=true;
                        }
                    }
                });
                //add layouts
                switchLayout.addView(oneSwitch);
                qualiLayout.addView(textCount2);
                qualiLayout.addView(textContent2);
                qualiLayout.addView(switchLayout);
                mainLayout.addView(qualiLayout);
                mainLayout.setVisibility(View.GONE);
                columnsIndex+=1;
            }
            textQualiCount.setText(String.valueOf(countOn));
            LinearLayout.LayoutParams layoutParams4=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams4.setMargins(0,1,0,1);
            //certificate button
            TextView textView=new TextView(getContext());
            textView.setLayoutParams(layoutParams4);
            textView.setText(hasCertificate(name)?"Available":"Certificate");
            Log.d("cert",LoginActivity.userCertificatesEquipmentsNames[pos]+"---"+name+"\n");
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
            layoutDates.setLayoutParams(layoutParams4);
            layoutDates.setOrientation(LinearLayout.HORIZONTAL);
            layoutDates.setWeightSum(2);
            //textview issue
            TextView textViewIssue=new TextView(getContext());
            textViewIssue.setLayoutParams((new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,1)));
            String issueDate=LoginActivity.userCertsEquipmentsIssue.get(name);
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
            String expiryDate=LoginActivity.userCertsEquipmentsExpiry.get(name);
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
            mainLayout.addView(textView);
            mainLayout.addView(layoutDates);
            //add qualicount to its own layout
            countLayout.addView(haveItSwitch);
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
    private void setDatePicker(String title,final View btn,final String qualification,final int choice)
    {
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
                    tempCertsIssueDate.put(qualification,newDate);
                    datesIssueChanged=true;
                }
                else if(choice==2)
                {
                    ((TextView)btn).setText(String.format("Expiry: %d/%d/%d",day,month,year));
                    tempCertsExpiryDate.put(qualification,newDate);
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

                String path=getPath(uri);
                String[] token=path.split(".");
                String [] title=path.split("/");
                for(int count=0; count<token.length; count+=1)
                    Log.d("token",String.format("count: %d token: %s",count,token[count]));
                certficateTv.setText(title[title.length-1]);

                // certificates.get(certIndex).setCert(String.format("%d_%d",LoginActivity.userAccount.getId(),certIndex));
                Log.d("uri",getPath(uri));
                // certUri[certIndex]=uri;
                uploadCert(1,path);

            }
            catch (Exception e)
            {
                Log.e("bitmap",""+e.getMessage());
            }
        }
    }
    private String getPath(Uri uri)
    {
        if(uri==null)
            return null;
        String res=null;
        String[]proj={MediaStore.Images.Media.DATA};
        Cursor cursor=getContext().getContentResolver().query(uri,proj,null,null,null);
        if(cursor.moveToFirst())
        {
            int column_index=cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res=cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }
    /*
     * This is the method responsible for pdf upload
     * We need the full pdf path and the name for the pdf in this method
     * */

    private boolean uploadCert(int index,final String path) {
        boolean ok=true;
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
        return ok;
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
    private void showCertificate(final String name)
    {
        String url=url_get_certs+makeName(LoginActivity.contractorAccount.getId())+"/equipmentscertificates/"+makeName(LoginActivity.userAccount.getId())+'/';url+=name+".jpg";
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
            if(requirementsChanged)
            {
                //update the trades matrix one by one
                int pos=0;
                Iterator iterator=LoginActivity.personnelEquipmentsMatrix.entrySet().iterator();
                while(iterator.hasNext())
                {
                    String sqlcommand="UPDATE "+makeName(LoginActivity.contractorAccount.getId())+"_equipments_matrix"+" SET ";
                    LinkedHashMap.Entry<String, int[]> set = (LinkedHashMap.Entry<String, int[]>) iterator.next();
                    final String name = set.getKey();
                    final int[]tempArray=set.getValue();
                    int index=0;
                    final int end=tempArray.length-1;
                    Iterator iterator_quals=LoginActivity.equipmentsColumnsList.entrySet().iterator();
                    while(iterator_quals.hasNext())
                    {
                        LinkedHashMap.Entry<String, Character> set2 = (LinkedHashMap.Entry<String, Character>) iterator_quals.next();
                        String name2 = set2.getKey();
                        Character which = set2.getValue();
                        if (which == 't')
                            continue;
                        sqlcommand+=name2+"="+String.valueOf(tempArray[index]);
                        if(index!=end)
                            sqlcommand+=",";
                        index+=1;
                    }
                    sqlcommand=sqlcommand.substring(0,sqlcommand.length()-1);
                    sqlcommand+=" WHERE userid="+String.valueOf(LoginActivity.userAccount.getId())+" AND "+name+"=1";
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
                            ;
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
                requirementsChanged=false;
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
    public class InsertEquipmentTask extends AsyncTask<Void, Void, Boolean>
    {
        private String mEquipment;
        public InsertEquipmentTask(String equipment)
        {
            mEquipment=equipment;
        }
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            Toast.makeText(getContext(),"Adding equipment started. Please wait...",Toast.LENGTH_SHORT).show();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            List<NameValuePair> infoCommand=new ArrayList<NameValuePair>();
            infoCommand.add(new BasicNameValuePair("id",String.valueOf(LoginActivity.contractorAccount.getId())));
            infoCommand.add(new BasicNameValuePair("userid",String.valueOf(LoginActivity.userAccount.getId())));
            infoCommand.add(new BasicNameValuePair("equipment",mEquipment));
            // making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_insert,"POST",infoCommand);
            Log.d("inserting equipment",""+jsonObject.toString());
            try
            {
                int success=jsonObject.getInt(TAG_SUCCESS);
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
        protected void onPostExecute(final Boolean success) {

            if (success)
            {
                Toast.makeText(getContext(),"Succesfully added",Toast.LENGTH_SHORT).show();
                Iterator iterator=LoginActivity.equipmentsColumnsList.entrySet().iterator();
                int[]tempArray=new int[LoginActivity.equipmentsColumnsList.size()];
                int index=0;
                while(iterator.hasNext())
                {
                    LinkedHashMap.Entry<String, Character> set = (LinkedHashMap.Entry<String, Character>) iterator.next();
                    String name = set.getKey();
                    if(name.contentEquals(mEquipment))
                    {
                        tempArray[index] = 1;
                        break;
                    }
                    index+=1;
                }
                LoginActivity.personnelEquipmentsMatrix.put(mEquipment,tempArray);
            }
            else
                Toast.makeText(getContext(),"There was an error updating, Please try again",Toast.LENGTH_SHORT).show();

        }

    }
    public class DeleteEquipmentTask extends AsyncTask<Void, Void, Boolean>
    {
        private String mEquipment;
        public DeleteEquipmentTask(String equipment)
        {
            mEquipment=equipment;
        }
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            Toast.makeText(getContext(),"Deleting equipment started. Please wait...",Toast.LENGTH_SHORT).show();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            List<NameValuePair> infoCommand=new ArrayList<NameValuePair>();
            infoCommand.add(new BasicNameValuePair("id",String.valueOf(LoginActivity.contractorAccount.getId())));
            infoCommand.add(new BasicNameValuePair("userid",String.valueOf(LoginActivity.userAccount.getId())));
            infoCommand.add(new BasicNameValuePair("equipment",mEquipment));
            // making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_delete,"POST",infoCommand);
            Log.d("inserting equipment",""+jsonObject.toString());
            try
            {
                int success=jsonObject.getInt(TAG_SUCCESS);
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
        protected void onPostExecute(final Boolean success) {

            if (success)
            {
                Toast.makeText(getContext(),"Successfully deleted",Toast.LENGTH_SHORT).show();
                Iterator iterator= LoginActivity.personnelEquipmentsMatrix.entrySet().iterator();
                while (iterator.hasNext())
                {
                    LinkedHashMap.Entry<String,int[]>set=(LinkedHashMap.Entry<String, int[]>) iterator.next();
                    String equipment=set.getKey();
                    if(equipment.contentEquals(mEquipment))
                    {
                        iterator.remove();
                        break;
                    }
                }
            }
            else
                Toast.makeText(getContext(),"There was an error deleting, Please try again",Toast.LENGTH_SHORT).show();

        }

    }
}
