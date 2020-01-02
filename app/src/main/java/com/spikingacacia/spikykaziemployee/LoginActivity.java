package com.spikingacacia.spikykaziemployee;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.spikingacacia.spikykaziemployee.database.CReviews;
import com.spikingacacia.spikykaziemployee.database.CTasks;
import com.spikingacacia.spikykaziemployee.database.ContractorAccount;
import com.spikingacacia.spikykaziemployee.database.UGlobalInfo;
import com.spikingacacia.spikykaziemployee.database.UNotifications;
import com.spikingacacia.spikykaziemployee.database.UserAccount;


import net.gotev.uploadservice.Logger;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements
CreateAccountF.OnFragmentInteractionListener,
        SignInFragment.OnFragmentInteractionListener
{
    private static final int OVERLAY_PERMISSION_CODE=543;
    //REMEMBER TO CHANGE THIS WHEN CHANGING BETWEEN ONLINE AND LOCALHOST
    public static final String base_url="https://www.spikingacacia.com/kazi_project/android/"; //online
    //public static final String base_url="http://10.0.2.2/kazi_project/android/"; //localhost no connection for testing user accounts coz it doesnt require subscription checking
    //public static final String base_url="http://192.168.0.10/kazi_project/android/"; //localhost
    //public static final String base_url="http://192.168.43.228/kazi_project/android/"; //localhost tablet
    private String TAG_SUCCESS="success";
    private String TAG_MESSAGE="message";
    private String TAG="LoginActivity";
    private JSONParser jsonParser;
    private Intent intentLoginProgress;
    private static int loginProgress;
    public static boolean AppRunningInThisActivity=true;//check if the app is running the in this activity
    //whenever you add a background asynctask make sure to update the finalprogress variables accordingly
    private static int uFinalProgress=17;

    private String url_get_trades_list=base_url+"get_all_trades_from_table.php";
    private String url_get_equipments_list=base_url+"get_all_equipments_from_table.php";
    private String url_get_staff_count_in_trades=base_url+"get_staff_count_from_tradename.php";
    private String url_get_staff_count_in_equipment=base_url+"get_staff_count_from_equipmentname.php";
    private String url_get_personnel_table_columns=base_url+"get_personnel_table_columns.php";
    private String url_get_equipments_table_columns=base_url+"get_equipments_table_columns.php";
    private String url_get_personnel_matrix=base_url+"get_personnel_matrix.php";
    private String url_get_equipments_matrix=base_url+"get_equipments_matrix.php";

    //user
    private String url_get_account_contractor_u=base_url+"get_contractor_account_u.php";
    private String url_get_personnel_matrix_u=base_url+"u_get_personnel_matrix.php";
    private String url_get_one_personnel_compliance=base_url+"get_one_personnel_compliance.php";
    private String url_get_certs= LoginActivity.base_url+"src/contractors/";//+".jpg";
    private String url_get_certs_info= LoginActivity.base_url+"get_certs.php";
    private String url_get_certs_equi_info= LoginActivity.base_url+"get_certs_equipments.php";
    private String url_get_personnel_equipments_matrix=base_url+"get_personnel_equipments_matrix.php";
    private String url_get_u_notifications=base_url+"get_u_notifications.php";
    private String url_get_tasks_u=base_url+"get_tasks_u.php";
    private String url_get_u_reviews=base_url+"get_user_performance_review.php";
    //contractor
    public static UserAccount userAccount;
    public static ContractorAccount contractorAccount;
    public static LinkedHashMap<String,Integer>tradesList;
    public static LinkedHashMap<String,Character>personnelColumnsList;
    public static int[][]personnelMatrix;
    public static LinkedHashMap<String,Integer>equipmentsList;
    public static LinkedHashMap<String,Character>equipmentsColumnsList;
    public static int[][]equipmentsMatrix;
    //users
    public static int[]onePersonnelMatrix;
    public static String[]userCertificatesNames;
    public static UGlobalInfo uGlobalInfo;
    public static LinkedHashMap<String,String>userCertsIssue;
    public static LinkedHashMap<String,String>userCertsExpiry;
    public static LinkedHashMap<String,String>userCertsEquipmentsIssue;
    public static LinkedHashMap<String,String>userCertsEquipmentsExpiry;
    public static LinkedHashMap<String,int[]>personnelEquipmentsMatrix;
    public static String[]userCertificatesEquipmentsNames;
    public static LinkedHashMap<String,UNotifications>uNotificationsList;
    public static List<CTasks>uTasksList;
    public static List<CReviews>uReviewsList;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPreferencesEditor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //preference
        loginPreferences=getBaseContext().getSharedPreferences("loginPrefs",MODE_PRIVATE);
        loginPreferencesEditor=loginPreferences.edit();
        //background intent
        intentLoginProgress=new Intent(LoginActivity.this,ProgressView.class);
        loginProgress=0;
        //accounts
        contractorAccount=new ContractorAccount();
        userAccount =new UserAccount();
        tradesList=new LinkedHashMap<>();
        personnelColumnsList=new LinkedHashMap<>();
        equipmentsList=new LinkedHashMap<>();
        equipmentsColumnsList=new LinkedHashMap<>();
        personnelEquipmentsMatrix=new LinkedHashMap<>();
        //user variables
        userCertsIssue=new LinkedHashMap<>();
        userCertsExpiry=new LinkedHashMap<>();
        userCertsEquipmentsIssue=new LinkedHashMap<>();
        userCertsEquipmentsExpiry=new LinkedHashMap<>();
        uGlobalInfo=new UGlobalInfo();
        uNotificationsList=new LinkedHashMap<>();
        uReviewsList=new ArrayList<>();
        //global variables
        uTasksList=new ArrayList<>();
        jsonParser=new JSONParser();
        //firebase links
        if((loginPreferences.getBoolean("verify",false)==true) || (loginPreferences.getBoolean("reset_password",false)==true))
        {
            Toast.makeText(getBaseContext(),"Please wait",Toast.LENGTH_SHORT).show();
            FirebaseDynamicLinks.getInstance()
                    .getDynamicLink(getIntent())
                    .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                        @Override
                        public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                            // Get deep link from result (may be null if no link is found)
                            Uri deepLink = null;
                            if (pendingDynamicLinkData != null)
                            {
                                deepLink = pendingDynamicLinkData.getLink();
                                if(loginPreferences.getBoolean("verify",false)==true)
                                {
                                    setTitle("Sign Up");
                                    Fragment fragment=CreateAccountF.newInstance(1,loginPreferences.getString("email_verify",""));
                                    FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                                    transaction.replace(R.id.loginbase,fragment,"createnewaccount");
                                    transaction.addToBackStack("createaccount");
                                    transaction.commit();
                                }
                                else if(loginPreferences.getBoolean("reset_password",false)==true)
                                {
                                    setTitle("Reset Password");
                                    Fragment fragment=CreateAccountF.newInstance(2,loginPreferences.getString("email_reset_password",""));
                                    FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                                    transaction.replace(R.id.loginbase,fragment,"createnewaccount");
                                    transaction.addToBackStack("createaccount");
                                    transaction.commit();
                                }

                            }


                            // Handle the deep link. For example, open the linked
                            // content, or apply promotional credit to the user's
                            // account.
                            // ...

                            // ...
                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "getDynamicLink:onFailure", e);
                        }
                    });
        }

        //fragment manager
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener()
        {
            @Override
            public void onBackStackChanged()
            {
                int count=getSupportFragmentManager().getBackStackEntryCount();
                if(count==0)
                    setTitle("Sign In");
            }
        });
        setTitle("Sign In");
        Fragment fragment=SignInFragment.newInstance("","");
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.loginbase,fragment,"signin");
        transaction.commit();
    }
    @Override
    protected void onDestroy()
    {
        //super.onDestroy();
        if(intentLoginProgress!=null)
            stopService(intentLoginProgress);
        super.onDestroy();
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        //clear the variables . if not done youll find some list contents add up on top of the previous ones
        loginProgress=0;
        if(!tradesList.isEmpty())tradesList.clear();
        if(!personnelColumnsList.isEmpty())personnelColumnsList.clear();
        if(!equipmentsList.isEmpty())equipmentsList.clear();
        if(!equipmentsColumnsList.isEmpty())equipmentsColumnsList.clear();
        if(!personnelEquipmentsMatrix.isEmpty())personnelEquipmentsMatrix.clear();
        //user variables
        if(!userCertsIssue.isEmpty())userCertsIssue.clear();
        if(!userCertsExpiry.isEmpty())userCertsExpiry.clear();
        if(!userCertsEquipmentsIssue.isEmpty())userCertsEquipmentsIssue.clear();
        if(!userCertsEquipmentsExpiry.isEmpty())userCertsEquipmentsExpiry.clear();
        if(!uNotificationsList.isEmpty())uNotificationsList.clear();
        //global variables
        if(uTasksList.isEmpty())uTasksList.clear();
        if(!uReviewsList.isEmpty())uReviewsList.clear();
        AppRunningInThisActivity=true;
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==OVERLAY_PERMISSION_CODE)
        {
            if(Settings.canDrawOverlays(this))
            {
                startService(intentLoginProgress);
            }
           startBackgroundTasks();
        }
    }
    /** Implementation of SignInFragment.java**/
   @Override
   public void onSuccesfull()
   {
       //start the floating service
       if(Build.VERSION.SDK_INT>=23)
       {
           if(!Settings.canDrawOverlays(this))
           {
               //open permissions page
               Intent intent=new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,  Uri.parse("package:"+getPackageName()));
               startActivityForResult(intent,OVERLAY_PERMISSION_CODE);
               //return;
           }
           else
               startBackgroundTasks();
       }
       else
           startBackgroundTasks();
   }
   private void startBackgroundTasks()
   {
       startService(intentLoginProgress);
       new UContractorAccountTask().execute((Void)null);
       new CTradesListTask().execute((Void)null);
       new CPersonnelMatrixTask().execute((Void)null);
       new CPersonnelColumnsListTask().execute((Void)null);
       new UPersonnelMatrixTask().execute((Void)null);
       new UCertificatesInfoTask().execute((Void)null);
       new CEquipmentsListTask().execute((Void)null);
       new CEquipmentsColumnsListTask().execute((Void)null);
       new CEquipmentsMatrixTask().execute((Void)null);
       new UPersonnelEquipmentsMatrixTask().execute((Void)null);
       new UCertificatesEquipmentsInfoTask().execute((Void)null);
       new UNotificationsTask().execute((Void)null);
       new UTasksTask().execute((Void)null);
       new UReviewsTask().execute((Void)null);
       Intent intent=new Intent(this, UMenuActivity.class);
       // intent.putExtra("NOTHING","nothing");
       startActivity(intent);
   }


    /** Implementation of CreateAccountF.java**/
    @Override
    public  void onRegisterFinished()
    {
        setTitle("Sign In");
        onBackPressed();
    }
    @Override
    public void createAccount()
    {
        setTitle("Sign Up");
        Fragment fragment=CreateAccountF.newInstance(0,"");
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.loginbase,fragment,"createnewaccount");
        transaction.addToBackStack("createaccount");
        transaction.commit();
    }

    /**
     * The following code will get a list of positions or trades defined in the bosses personnel matrix table
     * * Arguments are:
     * id==boss id.
     * Returns are:
     * success==1 successful get
     * success==0 for id argument missing
     **/
    private class CTradesListTask extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected void onPreExecute()
        {
            Log.d("TRADESLIST: ","getting tradeslist....");
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            boolean ok=true;
            //getting trades list
            List<NameValuePair>infoTrades=new ArrayList<NameValuePair>(); //info for staff count
            infoTrades.add(new BasicNameValuePair("id",Integer.toString(contractorAccount.getId())));
            // making HTTP request
            JSONObject jsonObjectTrades= jsonParser.makeHttpRequest(url_get_trades_list,"POST",infoTrades);
            Log.d("tradesContent",""+jsonObjectTrades.toString());
            try
            {
                JSONArray tradesArrayList=null;
                int success=jsonObjectTrades.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    tradesArrayList=jsonObjectTrades.getJSONArray("tradeslist");
                    //JSONObject tradesArrayObject=tradesArrayList.getJSONObject(0);
                    tradesArrayList=tradesArrayList.getJSONArray(0);
                    for(int count=0; count<tradesArrayList.length(); count+=1)
                    {
                        List<NameValuePair>infoStaffCount=new ArrayList<NameValuePair>(); //info for staff count
                        infoStaffCount.add(new BasicNameValuePair("id",Integer.toString(contractorAccount.getId())));
                        infoStaffCount.add(new BasicNameValuePair("tradename",tradesArrayList.getString(count)));
                        // making HTTP request
                        JSONObject jsonObjectStaffCount= jsonParser.makeHttpRequest(url_get_staff_count_in_trades,"POST",infoStaffCount);
                        Log.d("compliancy",""+jsonObjectStaffCount.toString());
                        try
                        {
                            int successcount=jsonObjectStaffCount.getInt(TAG_SUCCESS);
                            if(successcount==1)
                            {
                                int staffcount=jsonObjectStaffCount.getInt("staffcount");
                                tradesList.put(tradesArrayList.getString(count),staffcount);
                               // addItem(createTradesItem(count+1,tradesArrayList.getString(count).replace("_"," "),Integer.toString(staffcount)));
                            }
                            else
                            {
                                String message=jsonObjectStaffCount.getString(TAG_MESSAGE);
                                Log.e(TAG_MESSAGE,""+message);
                                ok=false;
                            }
                        }
                        catch (JSONException e)
                        {
                            Log.e("JSON",""+e.getMessage());
                            ok= false;
                        }
                    }
                }
                else
                {
                    String message=jsonObjectTrades.getString(TAG_MESSAGE);
                    Log.e(TAG_MESSAGE,""+message);
                    ok= false;
                }
            }
            catch (JSONException e)
            {
                Log.e("JSON",""+e.getMessage());
                ok=false;
            }
            return ok;
        }
        @Override
        protected void onPostExecute(final Boolean successful) {
            Log.d("TRADESLIST: ","finished...."+"progress: "+String.valueOf(loginProgress));
            loginProgress+=1;
            if (loginProgress == uFinalProgress)
                stopService(intentLoginProgress);

            if (successful)
            {

            }
            else
            {

            }
        }
    }
    /**
     * Following code will return a list of equipments.
     * The columns returned are from the first index of equipments to the last index of equipment
     * Arguments are:
     * id==boss id.
     * Returns are:
     * success==1 successful get
     * success==0 for id argument missing
     **/
    private class CEquipmentsListTask extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected void onPreExecute()
        {
            Log.d("EQUIPMENTSLIST: ","getting equipmentslist....");
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            boolean ok=true;
            //getting trades list
            List<NameValuePair>infoTrades=new ArrayList<NameValuePair>(); //info for staff count
            infoTrades.add(new BasicNameValuePair("id",Integer.toString(contractorAccount.getId())));
            // making HTTP request
            JSONObject jsonObjectEquipments= jsonParser.makeHttpRequest(url_get_equipments_list,"POST",infoTrades);
            Log.d("equipmentsContent",""+jsonObjectEquipments.toString());
            try
            {
                JSONArray equipmentsArrayList=null;
                int success=jsonObjectEquipments.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    equipmentsArrayList=jsonObjectEquipments.getJSONArray("equipmentslist");
                    //JSONObject tradesArrayObject=tradesArrayList.getJSONObject(0);
                    equipmentsArrayList=equipmentsArrayList.getJSONArray(0);
                    for(int count=0; count<equipmentsArrayList.length(); count+=1)
                    {
                        List<NameValuePair>infoStaffCount=new ArrayList<NameValuePair>(); //info for staff count
                        infoStaffCount.add(new BasicNameValuePair("id",Integer.toString(contractorAccount.getId())));
                        infoStaffCount.add(new BasicNameValuePair("equipmentsname",equipmentsArrayList.getString(count)));
                        // making HTTP request
                        JSONObject jsonObjectStaffCount= jsonParser.makeHttpRequest(url_get_staff_count_in_equipment,"POST",infoStaffCount);
                        Log.d("compliancy",""+jsonObjectStaffCount.toString());
                        try
                        {
                            int successcount=jsonObjectStaffCount.getInt(TAG_SUCCESS);
                            if(successcount==1)
                            {
                                int staffcount=jsonObjectStaffCount.getInt("staffcount");
                                equipmentsList.put(equipmentsArrayList.getString(count),staffcount);
                                // addItem(createTradesItem(count+1,tradesArrayList.getString(count).replace("_"," "),Integer.toString(staffcount)));
                            }
                            else
                            {
                                String message=jsonObjectStaffCount.getString(TAG_MESSAGE);
                                Log.e(TAG_MESSAGE,""+message);
                                ok=false;
                            }
                        }
                        catch (JSONException e)
                        {
                            Log.e("JSON",""+e.getMessage());
                            ok= false;
                        }
                    }
                }
                else
                {
                    String message=jsonObjectEquipments.getString(TAG_MESSAGE);
                    Log.e(TAG_MESSAGE,""+message);
                    ok= false;
                }
            }
            catch (JSONException e)
            {
                Log.e("JSON",""+e.getMessage());
                ok=false;
            }
            return ok;
        }
        @Override
        protected void onPostExecute(final Boolean successful) {
            Log.d("EQUIPMENTSLIST: ","finished...."+"progress: "+String.valueOf(loginProgress));
            loginProgress+=1;
            if (loginProgress == uFinalProgress)
                stopService(intentLoginProgress);

            if (successful)
            {

            }
            else
            {

            }
        }
    }
    /**
     * Following code will get the boss personnel matrix table columns
     * the columns got start from index 2 all the way to the last position/trade.
     * Arguments are:
     * id==boss id.
     * Returns are:
     * success==1 successful get
     * success==0 for id argument missing
     **/
    private class CPersonnelColumnsListTask extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected void onPreExecute()
        {
            Log.d("CPERSONNELCOLUMNSLIST: ","getting columns list....");
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //getting columns list
            List<NameValuePair>info=new ArrayList<NameValuePair>(); //info for staff count
            info.add(new BasicNameValuePair("id",Integer.toString(contractorAccount.getId())));
            // making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_personnel_table_columns,"POST",info);
            Log.d("cPColumnsContent",""+jsonObject.toString());
            try
            {
                JSONArray columnssArrayList=null;
                int success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    columnssArrayList=jsonObject.getJSONArray("columns");
                    columnssArrayList=columnssArrayList.getJSONArray(0);
                    for(int count=0; count<columnssArrayList.length(); count+=1)
                    {
                        String name=columnssArrayList.getString(count);
                        String[] namePieces=name.split(":");
                        personnelColumnsList.put(namePieces[0],namePieces[1].charAt(0));

                    }
                    userCertificatesNames=new String[personnelColumnsList.size()];
                    Arrays.fill(userCertificatesNames,"");
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
            Log.d("CPERSONNELCOLUMNS: ","finished...."+"progress: "+String.valueOf(loginProgress));
            loginProgress+=1;
            if (loginProgress == uFinalProgress)
                stopService(intentLoginProgress);

            if (successful)
            {
                new UCertificatesTask().execute((Void)null);
            }
            else
            {

            }
        }
    }
    /**
     * Following code will get the boss equipments matrix table columns
     * the columns got start from index 2 all the way to the last equipment.
     * Arguments are:
     * id==boss id.
     * Returns are:
     * success==1 successful get
     * success==0 for id argument missing
     **/
    private class CEquipmentsColumnsListTask extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected void onPreExecute()
        {
            Log.d("CEQUIPMENTSCOLUMNS: ","getting columns list....");
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //getting columns list
            List<NameValuePair>info=new ArrayList<NameValuePair>(); //info for staff count
            info.add(new BasicNameValuePair("id",Integer.toString(contractorAccount.getId())));
            // making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_equipments_table_columns,"POST",info);
            Log.d("cEColumnsContent",""+jsonObject.toString());
            try
            {
                JSONArray columnssArrayList=null;
                int success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    columnssArrayList=jsonObject.getJSONArray("columns");
                    columnssArrayList=columnssArrayList.getJSONArray(0);
                    for(int count=0; count<columnssArrayList.length(); count+=1)
                    {
                        String name=columnssArrayList.getString(count);
                        String[] namePieces=name.split(":");
                        equipmentsColumnsList.put(namePieces[0],namePieces[1].charAt(0));

                    }
                    userCertificatesEquipmentsNames=new String[equipmentsColumnsList.size()];
                    Arrays.fill(userCertificatesEquipmentsNames,"");
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
            Log.d("CEQUIPMENTSCOLUMNS: ","finished...."+"progress: "+String.valueOf(loginProgress));
            loginProgress+=1;
            if (loginProgress == uFinalProgress)
                stopService(intentLoginProgress);

            if (successful)
                new UCertificatesEquipmentsTask().execute((Void)null);
            else
            {

            }
        }
    }
    /**
     * Following code will get all the rows in the boss personnel matrix table.
     * The columns returned are from index 2 marking the first requirement to the last, position.
     * The rows returned are from index 1 to the last. From the position definitions to all the personnel requirements
     * Arguments are:
     * id==boss id.
     * Returns are:
     * success==1 successful get
     * success==0 for id argument missing
     **/
    private class CPersonnelMatrixTask extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected void onPreExecute()
        {
            Log.d("CPERSONNELMATRIXSLIST: ","getting matrix....");
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //getting columns list
            List<NameValuePair>info=new ArrayList<NameValuePair>(); //info for staff count
            info.add(new BasicNameValuePair("id",Integer.toString(contractorAccount.getId())));
            // making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_personnel_matrix,"POST",info);
            Log.d("cPMatrix",""+jsonObject.toString());
            try
            {
                int success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    JSONArray matrixArray=jsonObject.getJSONArray("matrix");
                    matrixArray=matrixArray.getJSONArray(0);
                    personnelMatrix=new int[matrixArray.length()][];
                    for(int count=0; count<matrixArray.length(); count++)
                    {
                        JSONArray personnelArray=matrixArray.getJSONArray(count);
                        personnelMatrix[count]=new int[personnelArray.length()];
                        for(int c=0; c<personnelArray.length(); c++)
                        {
                            if(personnelArray.get(c).equals(null))
                                personnelMatrix[count][c]=0;
                            else
                                personnelMatrix[count][c]=personnelArray.getInt(c);
                        }
                    }
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
            Log.d("CPMATRIX: ","finished...."+"progress: "+String.valueOf(loginProgress));
            loginProgress+=1;
            if (loginProgress == uFinalProgress)
                stopService(intentLoginProgress);

            if (successful)
            {

            }
            else
            {

            }
        }
    }
    /**
     * Following code will get all the rows in the boss equipments matrix table.
     * The columns returned are from index 2 marking the first property to the last, equipment.
     * The rows returned are from index 1 to the last. From the equipments definitions to all the personnel equipments properties
     * Arguments are:
     * id==boss id.
     * Returns are:
     * success==1 successful get
     * success==0 for id argument missing
     **/
    private class CEquipmentsMatrixTask extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected void onPreExecute()
        {
            Log.d("CEQUIPMATRIXSLIST: ","getting matrix....");
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //getting columns list
            List<NameValuePair>info=new ArrayList<NameValuePair>(); //info for staff count
            info.add(new BasicNameValuePair("id",Integer.toString(contractorAccount.getId())));
            // making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_equipments_matrix,"POST",info);
            Log.d("cEMatrix",""+jsonObject.toString());
            try
            {
                int success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    JSONArray matrixArray=jsonObject.getJSONArray("matrix");
                    matrixArray=matrixArray.getJSONArray(0);
                    equipmentsMatrix=new int[matrixArray.length()][];
                    for(int count=0; count<matrixArray.length(); count++)
                    {
                        JSONArray personnelArray=matrixArray.getJSONArray(count);
                        equipmentsMatrix[count]=new int[personnelArray.length()];
                        for(int c=0; c<personnelArray.length(); c++)
                        {
                            if(personnelArray.get(c).equals(null))
                                equipmentsMatrix[count][c]=0;
                            else
                                equipmentsMatrix[count][c]=personnelArray.getInt(c);
                        }
                    }
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
            Log.d("CEMATRIX: ","finished...."+"progress: "+String.valueOf(loginProgress));
            loginProgress+=1;
            if (loginProgress == uFinalProgress)
                stopService(intentLoginProgress);

            if (successful)
            {

            }
            else
            {

            }
        }
    }

    /**
     * Following code will get single contractor account details for the user. The following boss account details are returned
     * id,email,username, country, location, permissions, lengths, notifications, dateadded, datechanged
     * Arguments are:
     * id==boss id.
     * Returns are:
     * sucess==-2 for no account with that id found
     * success==-3 for id argument missing
     **/
    public class UContractorAccountTask extends AsyncTask<Void, Void, Boolean>
    {

        private int success=0;

        UContractorAccountTask() {
        }
        @Override
        protected void onPreExecute()
        {
            Log.d("UCACCOUNT","starting....");
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // logIn=handler.LogInContractor(mEmail,mPassword);

            //building parameters
            List<NameValuePair>info=new ArrayList<NameValuePair>();
            info.add(new BasicNameValuePair("id",userAccount.getCompany()));
            //getting all account details by making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_account_contractor_u,"POST",info);
            try
            {
                success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    //seccesful
                    JSONArray accountArray=jsonObject.getJSONArray("account");
                    JSONObject accountObject=accountArray.getJSONObject(0);

                    contractorAccount.setId(accountObject.getInt("id"));
                    contractorAccount.setEmail(accountObject.getString("email"));
                    contractorAccount.setUsername(accountObject.getString("username"));
                    contractorAccount.setCountry(accountObject.getString("country"));
                    contractorAccount.setLocation(accountObject.getString("location"));
                    contractorAccount.setPermissions(accountObject.getString("permissions"));
                    contractorAccount.setLengths(accountObject.getString("lengths"));
                    contractorAccount.setLengthEquipments(accountObject.getString("lengthsequipments"));
                    contractorAccount.setNotifications(accountObject.getString("notifications"));
                    contractorAccount.setDateadded(accountObject.getString("dateadded"));
                    contractorAccount.setDatechanged(accountObject.getString("datechanged"));
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
        protected void onPostExecute(final Boolean successfull) {
            Log.d("UCACCOUNT","finished...."+"progress: "+String.valueOf(loginProgress));
            loginProgress+=1;
            if (loginProgress == uFinalProgress)
                stopService(intentLoginProgress);
            if (successfull)
            {
                String lengths=contractorAccount.getLengthEquipments();
                if(lengths.contentEquals("NULL") || lengths.contentEquals("null") || lengths.contentEquals(""))
                    uFinalProgress-=3;
                new UPersonnelComplianceTask().execute((Void)null);
            }
            else
            {

            }
        }

    }
    /**
     * Following code will a single personnel row from boss personnel matrix table.
     * The column index are starting with 2, the first requirement to the last position/trade.
     * Arguments are:
     * id==boss id.
     * Returns are:
     * success==1 successful get
     * success==0 for id argument missing
     **/
    private class UPersonnelMatrixTask extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected void onPreExecute()
        {
            Log.d("UPERSONNELMATRIXSLIST: ","getting matrix....");
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //getting columns list
            List<NameValuePair>info=new ArrayList<NameValuePair>(); //info for staff count
            info.add(new BasicNameValuePair("id",Integer.toString(contractorAccount.getId())));
            info.add(new BasicNameValuePair("userid",Integer.toString(userAccount.getId())));

            // making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_personnel_matrix_u,"POST",info);
            Log.d("uPMatrix",""+jsonObject.toString());
            try
            {
                int success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    JSONArray matrixArray=jsonObject.getJSONArray("matrix");
                    matrixArray=matrixArray.getJSONArray(0);
                    onePersonnelMatrix=new int[matrixArray.length()];
                    for(int count=0; count<matrixArray.length(); count++)
                    {
                        if(matrixArray.get(count).equals(null))
                            onePersonnelMatrix[count]=0;
                        else
                            onePersonnelMatrix[count]=matrixArray.getInt(count);
                    }
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
            Log.d("CPMATRIX: ","finished...."+"progress: "+String.valueOf(loginProgress));
            loginProgress+=1;
            if (loginProgress == uFinalProgress)
                stopService(intentLoginProgress);


            if (successful)
            {

            }
            else
            {

            }
        }
    }
    private class UCertificatesTask extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected void onPreExecute()
        {
            Logger.setLogLevel(Logger.LogLevel.DEBUG);
            Log.d("UCERTIFICATES: ","getting certificates....");
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            Iterator iterator= LoginActivity.personnelColumnsList.entrySet().iterator();
            int count=0;
            while (iterator.hasNext())
            {
                final int index=count;
                LinkedHashMap.Entry<String, Character> set2 = (LinkedHashMap.Entry<String, Character>) iterator.next();
                final String name = set2.getKey();
                Character which = set2.getValue();
                if (which == 't')
                {
                    count+=1;
                    continue;
                }
                String url=url_get_certs+makeName(contractorAccount.getId())+"/certificates/"+makeName(userAccount.getId())+'/';url+=name+".jpg";
                Log.d("Certificate name","  "+url);
                ImageRequest request=new ImageRequest(
                        url,
                        new Response.Listener<Bitmap>()
                        {
                            @Override
                            public void onResponse(Bitmap response)
                            {
                                //profilePic=response;
                                userCertificatesNames[index]=name;
                                //imageView.setImageBitmap(response);
                                Log.d("volley","succesful getting certificate:  "+name);
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
                RequestQueue request2 = Volley.newRequestQueue(getBaseContext());
                request2.add(request);
                count+=1;
            }
            return true;
        }
        @Override
        protected void onPostExecute(final Boolean successful) {
            Log.d("UCERTIFICATES: ","finished...."+"progress: "+String.valueOf(loginProgress));
            loginProgress+=1;
            if (loginProgress == uFinalProgress)
                stopService(intentLoginProgress);


            if (successful)
            {

            }
            else
            {

            }
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
    private class UCertificatesEquipmentsTask extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected void onPreExecute()
        {
            Logger.setLogLevel(Logger.LogLevel.DEBUG);
            Log.d("UCERTIFICATESEQUIP: ","getting certificates....");
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            Iterator iterator= LoginActivity.equipmentsColumnsList.entrySet().iterator();
            int count=0;
            while (iterator.hasNext())
            {
                final int index=count;
                LinkedHashMap.Entry<String, Character> set2 = (LinkedHashMap.Entry<String, Character>) iterator.next();
                final String name = set2.getKey();
                Character which = set2.getValue();
                if (which == 'm'|| which=='j')
                {
                    count+=1;
                    continue;
                }
                String url=url_get_certs+makeName(contractorAccount.getId())+"/equipmentscertificates/"+makeName(userAccount.getId())+'/';url+=name+".jpg";
                Log.d("Certificate name","  "+url);
                ImageRequest request=new ImageRequest(
                        url,
                        new Response.Listener<Bitmap>()
                        {
                            @Override
                            public void onResponse(Bitmap response)
                            {
                                //profilePic=response;
                                userCertificatesEquipmentsNames[index]=name;
                                //imageView.setImageBitmap(response);
                                Log.d("volley","succesful getting certificate:  "+name);
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
                RequestQueue request2 = Volley.newRequestQueue(getBaseContext());
                request2.add(request);
                count+=1;
            }
            return true;
        }
        @Override
        protected void onPostExecute(final Boolean successful) {
            Log.d("UCERTIFICATESEQUIP: ","finished...."+"progress: "+String.valueOf(loginProgress));
            loginProgress+=1;
            if (loginProgress == uFinalProgress)
                stopService(intentLoginProgress);


            if (successful)
            {

            }
            else
            {

            }
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

    /**
     * Following code will a single personnel certificates info from boss certificates table.
     * The returned columns are id, userid, whereis, verified, issue, expiry, dateadded, datechanged.
     * Arguments are:
     * id==boss id.
     * userid== personnel id.
     * Returns are:
     * success==1 successful get
     * success==0 for missing certificates info
     * success==0 for id argument missing
     **/
    private class UCertificatesInfoTask extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected void onPreExecute()
        {
            Logger.setLogLevel(Logger.LogLevel.DEBUG);
            Log.d("UCERTIFICATESINFO: ","getting certificates....");
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //get the certs;
            List<NameValuePair> info=new ArrayList<NameValuePair>(); //info for schemas
            info.add(new BasicNameValuePair("id",Integer.toString(LoginActivity.contractorAccount.getId())));
            info.add(new BasicNameValuePair("userid",Integer.toString(LoginActivity.userAccount.getId())));
            JSONObject jsonObjectCerts= jsonParser.makeHttpRequest(url_get_certs_info,"POST",info);
            Log.d("getting certs",""+jsonObjectCerts.toString());
            try
            {
                int success=jsonObjectCerts.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    JSONArray certsList=jsonObjectCerts.getJSONArray("certs");
                    for(int count=0; count<certsList.length(); count+=1)
                    {
                        JSONObject certs=certsList.getJSONObject(count);
                        userCertsIssue.put(certs.getString("whereis"),certs.getString("issue"));
                        userCertsExpiry.put(certs.getString("whereis"),certs.getString("expiry"));
                       /* Certificates cert=new Certificates();
                        final int index=Integer.parseInt(certs.getString("whereis"));
                        certificates.get(index).setId(Integer.parseInt(certs.getString("id")));
                        certificates.get(index).setStaffAccountId(Integer.parseInt(certs.getString("staffaccountid")));
                        certificates.get(index).setWhere(Integer.parseInt(certs.getString("whereis")));
                        certificates.get(index).setVerified(Integer.parseInt(certs.getString("verified")));
                        certificates.get(index).setIssue(certs.getString("issue"));
                        certificates.get(index).setExpiry(certs.getString("expiry"));
                        certificates.get(index).setCert(certs.getString("cert"));*/
                    }
                }
                else
                {
                    String message=jsonObjectCerts.getString(TAG_MESSAGE);
                    Log.e(TAG_MESSAGE,""+message);
                    return false;
                }
            }
            catch (JSONException e)
            {
                Log.e("JSON",""+e.getMessage());
                return false;
            }
            return true;
        }
        @Override
        protected void onPostExecute(final Boolean successful) {
            Log.d("UCERTIFICATESINFO: ","finished...."+"progress: "+String.valueOf(loginProgress));
            loginProgress+=1;
            if (loginProgress == uFinalProgress)
                stopService(intentLoginProgress);


            if (successful)
            {

            }
            else
            {

            }
        }
    }
    private class UCertificatesEquipmentsInfoTask extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected void onPreExecute()
        {
            Logger.setLogLevel(Logger.LogLevel.DEBUG);
            Log.d("UCERTIFICATESEQUI: ","getting certificates....");
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //get the certs;
            List<NameValuePair> info=new ArrayList<NameValuePair>(); //info for schemas
            info.add(new BasicNameValuePair("id",Integer.toString(LoginActivity.contractorAccount.getId())));
            info.add(new BasicNameValuePair("userid",Integer.toString(LoginActivity.userAccount.getId())));
            JSONObject jsonObjectCerts= jsonParser.makeHttpRequest(url_get_certs_equi_info,"POST",info);
            Log.d("getting certs",""+jsonObjectCerts.toString());
            try
            {
                int success=jsonObjectCerts.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    JSONArray certsList=jsonObjectCerts.getJSONArray("certs");
                    for(int count=0; count<certsList.length(); count+=1)
                    {
                        JSONObject certs=certsList.getJSONObject(count);
                        userCertsEquipmentsIssue.put(certs.getString("whereis"),certs.getString("issue"));
                        userCertsEquipmentsExpiry.put(certs.getString("whereis"),certs.getString("expiry"));
                       /* Certificates cert=new Certificates();
                        final int index=Integer.parseInt(certs.getString("whereis"));
                        certificates.get(index).setId(Integer.parseInt(certs.getString("id")));
                        certificates.get(index).setStaffAccountId(Integer.parseInt(certs.getString("staffaccountid")));
                        certificates.get(index).setWhere(Integer.parseInt(certs.getString("whereis")));
                        certificates.get(index).setVerified(Integer.parseInt(certs.getString("verified")));
                        certificates.get(index).setIssue(certs.getString("issue"));
                        certificates.get(index).setExpiry(certs.getString("expiry"));
                        certificates.get(index).setCert(certs.getString("cert"));*/
                    }
                }
                else
                {
                    String message=jsonObjectCerts.getString(TAG_MESSAGE);
                    Log.e(TAG_MESSAGE,""+message);
                    return false;
                }
            }
            catch (JSONException e)
            {
                Log.e("JSON",""+e.getMessage());
                return false;
            }
            return true;
        }
        @Override
        protected void onPostExecute(final Boolean successful) {
            Log.d("UCERTIFICATESEQUI: ","finished...."+"progress: "+String.valueOf(loginProgress));
            loginProgress+=1;
            if (loginProgress == uFinalProgress)
                stopService(intentLoginProgress);

            if (successful)
            {

            }
            else
            {

            }
        }
    }
    private class UPersonnelEquipmentsMatrixTask extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected void onPreExecute()
        {
            Log.d("UPEQUIPMATRIXSLIST: ","getting matrix....");
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //getting columns list
            List<NameValuePair>info=new ArrayList<NameValuePair>(); //info for staff count
            info.add(new BasicNameValuePair("id",Integer.toString(contractorAccount.getId())));
            info.add(new BasicNameValuePair("userid",Integer.toString(userAccount.getId())));
            // making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_personnel_equipments_matrix,"POST",info);
            Log.d("uEMatrix",""+jsonObject.toString());
            try
            {
                int success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    JSONArray columnssArrayList=jsonObject.getJSONArray("columns");
                    columnssArrayList=columnssArrayList.getJSONArray(0);
                    String[]tempColumnsNames=new String[columnssArrayList.length()];
                    for(int count=0; count<columnssArrayList.length(); count+=1)
                    {
                        String name=columnssArrayList.getString(count);
                        String[] namePieces=name.split(":");
                        tempColumnsNames[count]=namePieces[0];

                    }
                    JSONArray matrixArray=jsonObject.getJSONArray("matrix");
                    matrixArray=matrixArray.getJSONArray(0);
                    //personnelEquipmentsMatrix=new int[matrixArray.length()][];
                    for(int count=0; count<matrixArray.length(); count++)
                    {
                        JSONArray personnelArray=matrixArray.getJSONArray(count);
                        int[]tempArray=new int[personnelArray.length()];
                        //personnelEquipmentsMatrix[count]=new int[personnelArray.length()];
                        int indexFinal=-1;
                        for(int c=0; c<personnelArray.length(); c++)
                        {
                            if(personnelArray.get(c).equals(null))
                                tempArray[c]=0;
                                //personnelEquipmentsMatrix[count][c]=0;
                            else
                            {
                                tempArray[c] = personnelArray.getInt(c);
                                indexFinal=c;
                            }
                                //personnelEquipmentsMatrix[count][c]=personnelArray.getInt(c);
                        }
                        personnelEquipmentsMatrix.put(tempColumnsNames[indexFinal],tempArray);
                    }
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
            Log.d("UPEMATRIX: ","finished...."+"progress: "+String.valueOf(loginProgress));
            loginProgress+=1;
            if (loginProgress == uFinalProgress)
                stopService(intentLoginProgress);

            if (successful)
            {

            }
            else
            {

            }
        }
    }
    private class UPersonnelComplianceTask extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected void onPreExecute()
        {
            Log.d("UPERSONNELCOMPLIANCE:","getting compliance....");
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //getting columns list
            List<NameValuePair>info=new ArrayList<NameValuePair>(); //info for staff count
            info.add(new BasicNameValuePair("id",Integer.toString(contractorAccount.getId())));
            info.add(new BasicNameValuePair("userid",Integer.toString(userAccount.getId())));
            // making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_one_personnel_compliance,"POST",info);
            Log.d("OnePComplaince",""+jsonObject.toString());
            try
            {
                int success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    uGlobalInfo.setStaffCount(jsonObject.getInt("staffcount"));
                    uGlobalInfo.setCompliant(jsonObject.getInt("compliant"));
                    uGlobalInfo.setNoncompliant(jsonObject.getInt("noncompliant"));
                    JSONArray compArray=jsonObject.getJSONArray("compliantstaff");
                    LinkedHashMap<String,Character>tempCompliantStaff=new LinkedHashMap<>();
                    for(int count=0; count<compArray.length(); count+=1)
                    {
                        //add the staff member to the recylcer
                        String staff=compArray.getString(count);
                        String[] token=staff.split(":");
                        final String id=token[0];
                        final String userid=token[1];
                        final String trade=token[2];
                        final String names=token[3];
                        tempCompliantStaff.put(staff,'C');
                    }
                    uGlobalInfo.setComplaintStaff(tempCompliantStaff);
                    JSONArray noncompArray=jsonObject.getJSONArray("noncompliantstaff");
                    LinkedHashMap<String,Character>tempNonCompliantStaff=new LinkedHashMap<>();
                    for(int count=0; count<noncompArray.length(); count+=1)
                    {
                        //add the staff member to the recylcer
                        String staff=noncompArray.getString(count);
                        String[] token=staff.split(":");
                        final String id=token[0];
                        final String userid=token[1];
                        final String trade=token[2];
                        final String names=token[3];
                        tempNonCompliantStaff.put(staff,'N');
                    }
                    uGlobalInfo.setNonComplaintStaff(tempNonCompliantStaff);
                    uGlobalInfo.setMissingQualifications(jsonObject.getInt("missingqualifications"));
                    uGlobalInfo.setMissingCertificates(jsonObject.getInt("missingcertificates"));
                    uGlobalInfo.setExpiredCertificates(jsonObject.getInt("expiredcertificates"));
                    uGlobalInfo.setUsersWithMissingQualifications(jsonObject.getInt("staffwithmissingqualifications"));
                    uGlobalInfo.setUsersWithMissingCertificates(jsonObject.getInt("staffwithmissingcertificates"));
                    uGlobalInfo.setUserWithExpiredCertificates(jsonObject.getInt("staffwithexpiredcertificates"));
                    uGlobalInfo.setUsersWithCase(jsonObject.getInt("staffwith_case"));
                    uGlobalInfo.setUsersWithQCCase(jsonObject.getInt("staffwith_q_c_case"));
                    uGlobalInfo.setUsersWithQECCase(jsonObject.getInt("staffwith_q_e_case"));
                    uGlobalInfo.setUsersWithCECCase(jsonObject.getInt("staffwith_c_ec_case"));
                    uGlobalInfo.setUsersWithQCECCase(jsonObject.getInt("staffwith_q_c_ec_case"));
                    JSONArray eachQualMissingArray=jsonObject.getJSONArray("each_qual_missing_count");
                    int []each_qual_missing_count=new int[eachQualMissingArray.length()];
                    for(int count=0; count<eachQualMissingArray.length(); count+=1)
                    {
                        each_qual_missing_count[count]=eachQualMissingArray.getInt(count);
                    }
                    uGlobalInfo.setEachQualMissingCount(each_qual_missing_count);
                    JSONArray tableColumnsArray=jsonObject.getJSONArray("columnnames");
                    String[]tableColumns=new String[tableColumnsArray.length()];
                    for(int count=0; count<tableColumnsArray.length(); count+=1)
                    {
                        tableColumns[count]=tableColumnsArray.getString(count);
                    }
                    uGlobalInfo.setTableColumns(tableColumns);
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
            Log.d("UPCOMPLIANCE: ","finished...."+"progress: "+String.valueOf(loginProgress));
            loginProgress+=1;
            if (loginProgress == uFinalProgress)
                stopService(intentLoginProgress);

            if (successful)
            {

            }
            else
            {

            }
        }
    }
    /**
     * Following code will get the user notifications
     * The returned infos are id, classes, messages, dateadded.
     * Arguments are:
     * id==user id.
     * Returns are:
     * success==1 successful get
     * success==0 for id argument missing
     **/
    private class UNotificationsTask extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected void onPreExecute()
        {
            Log.d("UNOTIFICATIONS: ","starting....");
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //getting columns list
            List<NameValuePair>info=new ArrayList<NameValuePair>(); //info for staff count
            info.add(new BasicNameValuePair("id",Integer.toString(userAccount.getId())));
            // making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_u_notifications,"POST",info);
            Log.d("uNotis",""+jsonObject.toString());
            try
            {
                JSONArray notisArrayList=null;
                int success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    notisArrayList=jsonObject.getJSONArray("notis");
                    for(int count=0; count<notisArrayList.length(); count+=1)
                    {
                        JSONObject jsonObjectNotis=notisArrayList.getJSONObject(count);
                        int id=jsonObjectNotis.getInt("id");
                        int classes=jsonObjectNotis.getInt("classes");
                        String message=jsonObjectNotis.getString("messages");
                        String date=jsonObjectNotis.getString("dateadded");
                        UNotifications oneUNotifications=new UNotifications(id,classes,message,date);
                        uNotificationsList.put(String.valueOf(id),oneUNotifications);
                    }
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
            Log.d("UNOTIFICATIONS: ","finished...."+"progress: "+String.valueOf(loginProgress));
            loginProgress+=1;
            if (loginProgress == uFinalProgress)
                stopService(intentLoginProgress);

            if (successful)
            {

            }
            else
            {

            }
        }
    }
    /**
     * Following code will get one personnel tasks info from boss tasks table.
     * The returned columns are id, titles, descriptions, startings, endings, repetitions, locations, positions, geofence dateadded, datechanged.
     * Arguments are:
     * id==boss id.
     * Returns are:
     * tasks rows
     * success==1 successful get
     * success==0 for missing certificates info
     * success==0 for id argument missing
     **/
    private class UTasksTask extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected void onPreExecute()
        {
            Log.d("CTASKS: ","starting....");
            if(!uTasksList.isEmpty())
                uTasksList.clear();
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //getting columns list
            List<NameValuePair>info=new ArrayList<NameValuePair>(); //info for staff count
            info.add(new BasicNameValuePair("bossid",Integer.toString(contractorAccount.getId())));
            info.add(new BasicNameValuePair("userid",Integer.toString(userAccount.getId())));
            info.add(new BasicNameValuePair("position",userAccount.getPosition()));
            // making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_tasks_u,"POST",info);
            Log.d("uTasks",""+jsonObject.toString());
            try
            {
                JSONArray notisArrayList=null;
                int success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    notisArrayList=jsonObject.getJSONArray("tasks");
                    for(int count=0; count<notisArrayList.length(); count+=1)
                    {
                        JSONObject jsonObjectNotis=notisArrayList.getJSONObject(count);
                        int id=jsonObjectNotis.getInt("id");
                        String title=jsonObjectNotis.getString("title");
                        String description=jsonObjectNotis.getString("description");
                        String starting=jsonObjectNotis.getString("starting");
                        String ending=jsonObjectNotis.getString("ending");
                        int repetition=jsonObjectNotis.getInt("repetition");
                        String location=jsonObjectNotis.getString("location");
                        String position=jsonObjectNotis.getString("position");
                        String geofence=jsonObjectNotis.getString("geofence");
                        String dateadded=jsonObjectNotis.getString("dateadded");
                        String datechanged=jsonObjectNotis.getString("datechanged");
                        int pending=jsonObjectNotis.getInt("p");
                        int inProgress=jsonObjectNotis.getInt("i");
                        int completed=jsonObjectNotis.getInt("c");
                        int overdue=jsonObjectNotis.getInt("o");
                        int late=jsonObjectNotis.getInt("l");
                        String pendingIds=jsonObjectNotis.getString("pids");
                        String inProgressIds=jsonObjectNotis.getString("inids");
                        String completedIds=jsonObjectNotis.getString("cids");
                        String overdueIds=jsonObjectNotis.getString("oids");
                        String lateIds=jsonObjectNotis.getString("lids");


                        CTasks uTasks=new CTasks(id,title,description,starting,ending,repetition,location,position,geofence,dateadded,datechanged,pending,inProgress,completed,overdue,late,pendingIds,inProgressIds,completedIds,overdueIds,lateIds);
                        uTasksList.add(uTasks);
                    }
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
            Log.d("CTASKS: ","finished...."+"progress: "+String.valueOf(loginProgress));
            loginProgress+=1;
            if (loginProgress == uFinalProgress)
                stopService(intentLoginProgress);
            if (successful)
            {

            }
            else
            {

            }
        }
    }
    private class UReviewsTask extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected void onPreExecute()
        {
            Log.d("UREVIEWS: ","starting....");
            if(!uReviewsList.isEmpty())
                uReviewsList.clear();
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //getting columns list
            List<NameValuePair>info=new ArrayList<NameValuePair>(); //info for staff count
            info.add(new BasicNameValuePair("id",Integer.toString(contractorAccount.getId())));
            info.add(new BasicNameValuePair("userid",Integer.toString(userAccount.getId())));
            // making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_u_reviews,"POST",info);
            Log.d("cReviews",""+jsonObject.toString());
            try
            {
                JSONArray reviewsArrayList=null;
                int success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    reviewsArrayList=jsonObject.getJSONArray("reviews");
                    for(int count=0; count<reviewsArrayList.length(); count+=1)
                    {
                        JSONObject jsonObjectReviews=reviewsArrayList.getJSONObject(count);

                        int id=jsonObjectReviews.getInt("id");
                        int userid=jsonObjectReviews.getInt("userid");
                        int classes=jsonObjectReviews.getInt("classes");
                        String reviewer=jsonObjectReviews.getString("reviewer");
                        String review=jsonObjectReviews.getString("review");
                        String toimprove=jsonObjectReviews.getString("toimprove");
                        String s_rating=jsonObjectReviews.getString("rating");
                        int rating=0;
                        if(!(s_rating.contentEquals("NULL") ||s_rating.contentEquals("null") || s_rating.contentEquals("") ))
                            rating=Integer.parseInt(s_rating);
                        int themonth=jsonObjectReviews.getInt("themonth");
                        int theyear=jsonObjectReviews.getInt("theyear");
                        String dateadded=jsonObjectReviews.getString("dateadded");
                        String datechanged=jsonObjectReviews.getString("datechanged");

                        CReviews cReviews=new CReviews(id,userid,classes,reviewer,review,toimprove,rating,themonth,theyear,dateadded,datechanged);
                        uReviewsList.add(cReviews);
                    }
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
            Log.d("UREVIEWS: ","finished...."+"progress: "+String.valueOf(loginProgress));
            loginProgress+=1;
            if (loginProgress == uFinalProgress)
                stopService(intentLoginProgress);

            if (successful)
            {

            }
            else
            {

            }
        }
    }

}

