package com.spikingacacia.spikykaziemployee;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.preference.ListPreference;
import androidx.preference.Preference;

import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.spikingacacia.spikykaziemployee.database.UserAccount;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class USettingsActivity extends AppCompatActivity
        implements    PreferenceFragmentCompat.OnPreferenceStartFragmentCallback
{
    private static final String TITLE_TAG = "Settings";
    private static String url_update_account= LoginActivity.base_url+"update_user_account.php";
    //private String url_update_permissions= LoginActivity.base_url+"update_permissions.php";
    private static String url_delete_account= LoginActivity.base_url+"delete_user.php";
    private static String url_delete_profile= LoginActivity.base_url+"delete_user_profile.php";
    private static JSONParser jsonParser;
    private static String TAG_SUCCESS="success";
    private static String TAG_MESSAGE="message";
    private UpdateAccount updateTask;
   // private UpdatePermissions updatePermissions;
    public static boolean settingsChanged;
   // public static boolean permissionsChanged;
    static private Context context;
    public static UserAccount tempUserAccount;
    //public static String permissions;
    public static Bitmap profilePic;
    private Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        //preference
        preferences = new Preferences(getBaseContext());
        if(!preferences.isDark_theme_enabled())
        {
            findViewById(R.id.main).setBackgroundColor(getResources().getColor(R.color.main_background_light));
        }
        if (savedInstanceState == null)
        {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new HeaderFragment())
                    .commit();
        } else
        {
            setTitle(savedInstanceState.getCharSequence(TITLE_TAG));
        }
        getSupportFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener()
                {
                    @Override
                    public void onBackStackChanged()
                    {
                        if (getSupportFragmentManager().getBackStackEntryCount() == 0)
                        {
                            setTitle(R.string.title_activity_settings);
                        }
                    }
                });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //dark theme prefernce
        if(!preferences.isDark_theme_enabled())
        {
            setTheme(R.style.NonFullscreenSSettingsLight);
            Toolbar actionBarToolbar = (Toolbar)findViewById(R.id.action_bar);
            if (actionBarToolbar != null)
                actionBarToolbar.setTitleTextColor(getResources().getColor(R.color.text_light));
        }
        ///
        tempUserAccount=new UserAccount();
        tempUserAccount=LoginActivity.userAccount;
        //permissionsChanged=false;
        // permissions="";
        jsonParser=new JSONParser();
        updateTask=new UpdateAccount();
        // updatePermissions=new UpdatePermissions();
        settingsChanged=false;
        context=this;
        //get the profile pic
        String url= LoginActivity.base_url+"src/users/"+String.format("%s/profilepics/prof_pic",makeName(LoginActivity.userAccount.getId()))+".jpg";
        ImageRequest request=new ImageRequest(
                url,
                new Response.Listener<Bitmap>()
                {
                    @Override
                    public void onResponse(Bitmap response)
                    {
                        profilePic=response;
                        //imageView.setImageBitmap(response);
                        Log.d("volley","succesful");
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

    }
    @Override
    protected void onDestroy()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                if (settingsChanged)
                {
                    updateTask.execute((Void)null);
                   /* if(permissionsChanged)
                        updatePermissions.execute((Void)null);*/
                }
            }
        }).start();
        super.onDestroy();
    }
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        // Save current activity title so we can set it again after a configuration change
        outState.putCharSequence(TITLE_TAG, getTitle());
    }
    @Override
    public boolean onSupportNavigateUp()
    {
        if (getSupportFragmentManager().popBackStackImmediate())
        {
            return true;
        }
        return super.onSupportNavigateUp();
    }
    @Override
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, androidx.preference.Preference pref)
    {
        // Instantiate the new Fragment
        final Bundle args = pref.getExtras();
        final Fragment fragment = getSupportFragmentManager().getFragmentFactory().instantiate(
                getClassLoader(),
                pref.getFragment());
        fragment.setArguments(args);
        fragment.setTargetFragment(caller, 0);
        // Replace the existing Fragment with the new Fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settings, fragment)
                .addToBackStack(null)
                .commit();
        setTitle(pref.getTitle());
        return true;
    }
    public static class HeaderFragment extends PreferenceFragmentCompat
    {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
        {
            setPreferencesFromResource(R.xml.pref_uheaders, rootKey);
        }
    }


    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragmentCompat
    {
        private Preferences preferences;
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
        {
            setPreferencesFromResource(R.xml.pref_ugeneral, rootKey);

            //feedback preference click listener
            Preference preference_est=findPreference("username");
            preference_est.setSummary(LoginActivity.userAccount.getUsername());
            preference_est.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o)
                {
                    String name = o.toString();
                    tempUserAccount.setUsername(name);
                    settingsChanged=true;
                    preference.setSummary(name);
                    return false;
                }
            });
            //you cannot change the email
            Preference preference_est_type=findPreference("email");
            preference_est_type.setSummary(LoginActivity.userAccount.getEmail());

            //password change
            final Preference preference_password=findPreference("password");
            preference_password.setSummary(passwordStars(LoginActivity.userAccount.getPassword()));
            preference_password.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override
                public boolean onPreferenceClick(Preference preference)
                {
                    final AlertDialog dialog;
                    AlertDialog.Builder builderPass=new AlertDialog.Builder(context);
                    builderPass.setTitle("Change Password");
                    TextInputLayout lOld=new TextInputLayout(context);
                    lOld.setGravity(Gravity.CENTER);
                    final EditText oldPassword=new EditText(context);
                    oldPassword.setPadding(20,10,20,10);
                    oldPassword.setTextSize(14);
                    lOld.addView(oldPassword,0,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    TextInputLayout lNew=new TextInputLayout(context);
                    lNew.setGravity(Gravity.CENTER);
                    final EditText newPassword=new EditText(context);
                    newPassword.setPadding(20,10,20,10);
                    newPassword.setTextSize(14);
                    lNew.addView(newPassword,0,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    TextInputLayout lConfirm=new TextInputLayout(context);
                    lConfirm.setGravity(Gravity.CENTER);
                    final EditText confirmPassword=new EditText(context);
                    confirmPassword.setPadding(20,10,20,10);
                    confirmPassword.setTextSize(14);
                    lConfirm.addView(confirmPassword,0,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    oldPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    newPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    confirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    oldPassword.setHint("Old Password");
                    newPassword.setHint("New Password");
                    confirmPassword.setHint("Confirm Password");
                    oldPassword.setError(null);
                    newPassword.setError(null);
                    confirmPassword.setError(null);
                    LinearLayout layoutPassword=new LinearLayout(context);
                    layoutPassword.setOrientation(LinearLayout.VERTICAL);
                    layoutPassword.addView(lOld);
                    layoutPassword.addView(lNew);
                    layoutPassword.addView(lConfirm);
                    builderPass.setView(layoutPassword);
                    builderPass.setPositiveButton("Change", null);
                    builderPass.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            dialogInterface.dismiss();
                        }
                    });
                    dialog=builderPass.create();
                    dialog.setOnShowListener(new DialogInterface.OnShowListener()
                    {
                        @Override
                        public void onShow(DialogInterface dialogInterface)
                        {
                            Button button=((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                            button.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View view)
                                {
                                    String old=oldPassword.getText().toString();
                                    String newPass=newPassword.getText().toString();
                                    String confirm=confirmPassword.getText().toString();
                                    if(!old.contentEquals(LoginActivity.userAccount.getPassword()))
                                    {
                                        oldPassword.setError("Incorrect old password");
                                    }
                                    else if(newPass.length()<4)
                                    {
                                        newPassword.setError("New password too short");
                                    }
                                    else if(!newPass.contentEquals(confirm))
                                    {
                                        confirmPassword.setError("Password not the same");
                                    }
                                    else
                                    {
                                        tempUserAccount.setPassword(newPass);
                                        settingsChanged=true;
                                        preference_password.setSummary(passwordStars(newPass));
                                        dialog.dismiss();
                                    }
                                }
                            });
                        }
                    });
                    dialog.show();
                    return false;
                }
            });
            //preference
            preferences=new Preferences(context);
            final SwitchPreference preference_dark=findPreference("dark_theme");
            preference_dark.setChecked(preferences.isDark_theme_enabled());
            preference_dark.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue)
                {
                    boolean choice = (boolean) newValue;
                    preference_dark.setChecked(choice);
                    preferences.setDark_theme_enabled(choice);
                    return false;
                }
            });

        }


        private String passwordStars(String pass)
        {
            String name="";
            for(int count=0; count<pass.length(); count+=1)
                name+="*";
            return name;
        }
    }
    /**
     * This fragment shows location preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class LocationPreferenceFragment extends PreferenceFragmentCompat
    {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
        {
            setPreferencesFromResource(R.xml.pref_ulocation, rootKey);

            //countries
            String countryCode= LoginActivity.userAccount.getCountry();
            ListPreference pref_countries=(ListPreference) findPreference("countries");
            pref_countries.setEntries(getCountriesList());
            pref_countries.setEntryValues(getCountriesListValues());
            pref_countries.setSummary(countryCode.contentEquals("null")?"Please set the country":getCountryFromCode(countryCode));
            pref_countries.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o)
                {
                    String s=o.toString();
                    tempUserAccount.setCountry(s);
                    settingsChanged=true;
                    preference.setSummary(getCountryFromCode(s));
                    return false;
                }
            });
            //location
            String[] pos=LoginActivity.userAccount.getLocation().split(",");
            final Preference pref_location=findPreference("location");
            pref_location.setSummary(pos.length==3?pos[2]:"Please set your location");
            /*
            //visible online
            int online=Integer.parseInt(LoginActivity.accountSeller.getOnline());
            final SwitchPreference pref_visible_online= (SwitchPreference) findPreference("online_visibility");
            pref_visible_online.setChecked(online==1);
            pref_visible_online.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o)
                {
                    if(pref_visible_online.isChecked())
                    {
                        pref_visible_online.setChecked(false);
                        LoginActivity.accountSeller.setOnline("0");
                        settingsChanged=true;
                    }
                    else
                    {
                        pref_visible_online.setChecked(true);
                        LoginActivity.accountSeller.setOnline("1");
                        settingsChanged=true;
                    }
                    return false;
                }
            });
            //deliver
            int deliver=Integer.parseInt(LoginActivity.accountSeller.getDeliver());
            final SwitchPreference pref_deliver= (SwitchPreference) findPreference("online_delivery");
            //pref_deliver.setChecked(deliver==1);
            pref_deliver.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o)
                {
                    if(pref_deliver.isChecked())
                    {
                        pref_deliver.setChecked(false);
                        LoginActivity.accountSeller.setDeliver("0");
                        settingsChanged=true;
                    }
                    else
                    {
                        pref_deliver.setChecked(true);
                        LoginActivity.accountSeller.setDeliver("1");
                        settingsChanged=true;
                    }
                    return false;
                }
            });*/
        }

        private CharSequence[] getCountriesList()
        {
            CharSequence[] countriesList;
            String[] isoCountryCodes= Locale.getISOCountries();
            countriesList=new CharSequence[isoCountryCodes.length];
            for(int count=0; count<isoCountryCodes.length; count+=1)
            {
                String countryCode=isoCountryCodes[count];
                Locale locale=new Locale("",countryCode);
                String countryName=locale.getDisplayCountry();
                countriesList[count]=countryName;
            }
            return countriesList;
        }
        private  CharSequence[] getCountriesListValues()
        {
            CharSequence[] countriesListValues;
            String[] isoCountryCodes= Locale.getISOCountries();
            countriesListValues=new CharSequence[isoCountryCodes.length];
            for(int count=0; count<isoCountryCodes.length; count+=1)
            {
                String countryCode=isoCountryCodes[count];
                countriesListValues[count]=countryCode;
            }
            return countriesListValues;
        }
        private String getCountryFromCode(String code)
        {
            String[] isoCountryCodes= Locale.getISOCountries();
            for(int count=0; count<isoCountryCodes.length; count+=1)
            {
                String countryCode=isoCountryCodes[count];
                Locale locale=new Locale("",countryCode);
                String countryName=locale.getDisplayCountry();
                if (countryCode.contentEquals(code))
                    return countryName;
            }
            return "unknown";
        }
        private String getCodeFromCountry(String country)
        {
            String[] isoCountryCodes= Locale.getISOCountries();
            for(int count=0; count<isoCountryCodes.length; count+=1)
            {
                String countryCode=isoCountryCodes[count];
                Locale locale=new Locale("",countryCode);
                String countryName=locale.getDisplayCountry();
                if (countryName.contentEquals(country))
                    return countryCode;
            }
            return "unknown";
        }
    }
    /**
     * This fragment shows feedback preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class FeedBackPreferenceFragment extends PreferenceFragmentCompat
    {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
        {
            setPreferencesFromResource(R.xml.pref_ufeedback, rootKey);

            final Preference pref_ask= (Preference) findPreference("feedback_ask");
            final Preference pref_report= (Preference) findPreference("feedback_report");
            pref_ask.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override
                public boolean onPreferenceClick(Preference preference)
                {
                    composeEmail("New feature");
                    return true;
                }
            });
            pref_report.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override
                public boolean onPreferenceClick(Preference preference)
                {
                    composeEmail("Reporting a problem");
                    return true;
                }
            });

        }

        private void composeEmail(String subject)
        {
            Intent intent=new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));//only email apps should handle this
            intent.putExtra(Intent.EXTRA_EMAIL,new String[]{"spikingacacia@gmail.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT,subject);
            /*if(intent.resolveActivity(getPackageManager())!=null)
            {
                startActivity(intent);
            }*/
            startActivity(intent);
        }




    }
    /**
     * This fragment shows account preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class AccountPreferenceFragment extends PreferenceFragmentCompat
    {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
        {
            setPreferencesFromResource(R.xml.pref_uaccount, rootKey);

            final String company=LoginActivity.userAccount.getCompany();
            final Preference pref_del_profile= (Preference) findPreference("profile_delete");
            pref_del_profile.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override
                public boolean onPreferenceClick(Preference preference)
                {
                    if(company.contentEquals("null") || company.contentEquals(""))
                    {
                        Toast.makeText(context,"Your profile is not in any company",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        TextInputLayout textInputLayout=new TextInputLayout(context);
                        textInputLayout.setGravity(Gravity.CENTER);
                        final EditText password=new EditText(context);
                        password.setPadding(20,10,20,10);
                        password.setTextSize(14);
                        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        password.setHint("Password");
                        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        password.setError(null);
                        textInputLayout.addView(password,0,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                        new AlertDialog.Builder(context)
                                .setTitle("Enter the password")
                                .setView(textInputLayout)
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
                                        String pass=password.getText().toString();
                                        if(!pass.contentEquals(LoginActivity.userAccount.getPassword()))
                                        {
                                            password.setError("Incorrect password");
                                            Toast.makeText(context,"Incorrect password.",Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            String company_name=LoginActivity.contractorAccount.getUsername();
                                            new AlertDialog.Builder(context)
                                                    .setTitle("DELETE PROFILE")
                                                    .setMessage("Are you sure you want to remove your information from " +company_name+"?"+
                                                            "\nThis will permanently remove your information")
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
                                                            new DeleteProfile().execute((Void)null);
                                                        }
                                                    }).create().show();
                                        }
                                    }
                                }).create().show();


                    }

                    return true;
                }
            });
            final Preference pref_del= (Preference) findPreference("account_delete");
            pref_del.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override
                public boolean onPreferenceClick(Preference preference)
                {
                    new AlertDialog.Builder(context)
                            .setTitle("DELETE ACCOUNT")
                            .setMessage("Are you sure you want to delete this account?" +
                                    "\nThis will permanently remove your information")
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
                                    new DeleteAccount().execute((Void)null);
                                }
                            }).create().show();
                    return true;
                }
            });

        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item)
        {
            int id = item.getItemId();
            if (id == android.R.id.home)
            {
                startActivity(new Intent(getActivity(), USettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
        public class DeleteAccount extends AsyncTask<Void, Void, Boolean>
        {
            DeleteAccount()
            {
                Log.d("DELETINGACCOUNT","delete started started...");
                //setDialog(true);
            }
            @Override
            protected Boolean doInBackground(Void... params)
            {
                //building parameters
                List<NameValuePair>info=new ArrayList<NameValuePair>();
                info.add(new BasicNameValuePair("id",String.valueOf(LoginActivity.userAccount.getId())));
                info.add(new BasicNameValuePair("bossid",String.valueOf(LoginActivity.contractorAccount.getId())));
                JSONObject jsonObject= jsonParser.makeHttpRequest(url_delete_account,"POST",info);
                Log.d("jsonaccountdelete",jsonObject.toString());
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
            protected void onPostExecute(final Boolean success)
            {
                Log.d("settings permissions","finished");
                //setDialog(false);
                if(success)
                {
                    Toast.makeText(context,"Account deleted",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(context,LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    getActivity().finish();
                    /*Intent intent=new Intent(context,LoginActivity.class);
                    int mPendingIntentId=12356;
                    PendingIntent pendingIntent=PendingIntent.getActivity(context,mPendingIntentId,intent,PendingIntent.FLAG_CANCEL_CURRENT);
                    AlarmManager alarmManager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC,System.currentTimeMillis()+100,pendingIntent);
                    System.exit(0);*/
                }
                else
                {

                }

            }
        }
        public class DeleteProfile extends AsyncTask<Void, Void, Boolean>
        {
            DeleteProfile()
            {
                Log.d("DELETINGPROFILE","delete profile started started...");
                //setDialog(true);
            }
            @Override
            protected Boolean doInBackground(Void... params)
            {
                //building parameters
                List<NameValuePair>info=new ArrayList<NameValuePair>();
                info.add(new BasicNameValuePair("id",String.valueOf(LoginActivity.userAccount.getId())));
                info.add(new BasicNameValuePair("bossid",String.valueOf(LoginActivity.contractorAccount.getId())));
                JSONObject jsonObject= jsonParser.makeHttpRequest(url_delete_profile,"POST",info);
                Log.d("jsonprofiledelete",jsonObject.toString());
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
            protected void onPostExecute(final Boolean success)
            {
                Log.d("settings permissions","finished");
                //setDialog(false);
                if(success)
                {
                    Toast.makeText(context,"Profile deleted",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(context,LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    getActivity().finish();
                    /*Intent intent=new Intent(context,LoginActivity.class);
                    int mPendingIntentId=12356;
                    PendingIntent pendingIntent=PendingIntent.getActivity(context,mPendingIntentId,intent,PendingIntent.FLAG_CANCEL_CURRENT);
                    AlarmManager alarmManager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC,System.currentTimeMillis()+100,pendingIntent);
                    System.exit(0);*/
                }
                else
                {

                }

            }
        }




    }
    /**
     * This fragment shows help preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class HelpPreferenceFragment extends PreferenceFragmentCompat
    {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
        {
            setPreferencesFromResource(R.xml.pref_uhelp, rootKey);
        }
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class AboutPreferenceFragment extends PreferenceFragmentCompat
    {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
        {
            setPreferencesFromResource(R.xml.pref_about, rootKey);
        }

    }
    /**
     * This fragment shows privileges preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
   /* @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class PrivilegesPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_cprivileges);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            //bindPreferenceSummaryToValue(findPreference("online_visibility"));
            //bindPreferenceSummaryToValue(findPreference("online_delivery"));
            

        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item)
        {
            int id = item.getItemId();
            if (id == android.R.id.home)
            {
                startActivity(new Intent(getActivity(), USettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
        private CharSequence[] getCountriesList()
        {
            CharSequence[] countriesList;
            String[] isoCountryCodes= Locale.getISOCountries();
            countriesList=new CharSequence[isoCountryCodes.length];
            for(int count=0; count<isoCountryCodes.length; count+=1)
            {
                String countryCode=isoCountryCodes[count];
                Locale locale=new Locale("",countryCode);
                String countryName=locale.getDisplayCountry();
                countriesList[count]=countryName;
            }
            return countriesList;
        }
        private  CharSequence[] getCountriesListValues()
        {
            CharSequence[] countriesListValues;
            String[] isoCountryCodes= Locale.getISOCountries();
            countriesListValues=new CharSequence[isoCountryCodes.length];
            for(int count=0; count<isoCountryCodes.length; count+=1)
            {
                String countryCode=isoCountryCodes[count];
                countriesListValues[count]=countryCode;
            }
            return countriesListValues;
        }
        private String getCountryFromCode(String code)
        {
            String[] isoCountryCodes= Locale.getISOCountries();
            for(int count=0; count<isoCountryCodes.length; count+=1)
            {
                String countryCode=isoCountryCodes[count];
                Locale locale=new Locale("",countryCode);
                String countryName=locale.getDisplayCountry();
                if (countryCode.contentEquals(code))
                    return countryName;
            }
            return "unknown";
        }
        private String getCodeFromCountry(String country)
        {
            String[] isoCountryCodes= Locale.getISOCountries();
            for(int count=0; count<isoCountryCodes.length; count+=1)
            {
                String countryCode=isoCountryCodes[count];
                Locale locale=new Locale("",countryCode);
                String countryName=locale.getDisplayCountry();
                if (countryName.contentEquals(country))
                    return countryCode;
            }
            return "unknown";
        }
    }*/

    public class UpdateAccount extends AsyncTask<Void, Void, Boolean>
    {
        UpdateAccount()
        {
            Log.d("settings","update started...");
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //building parameters
            List<NameValuePair>info=new ArrayList<NameValuePair>();
            info.add(new BasicNameValuePair("id",Integer.toString(tempUserAccount.getId())));
            info.add(new BasicNameValuePair("password",tempUserAccount.getPassword()));
            info.add(new BasicNameValuePair("username",tempUserAccount.getUsername()));
            info.add(new BasicNameValuePair("company",tempUserAccount.getCompany()));
            info.add(new BasicNameValuePair("position",tempUserAccount.getPosition()));
            info.add(new BasicNameValuePair("country",tempUserAccount.getCountry()));
            info.add(new BasicNameValuePair("location",tempUserAccount.getLocation()));
            info.add(new BasicNameValuePair("permissions",tempUserAccount.getPermissions()));
            info.add(new BasicNameValuePair("notifications",tempUserAccount.getNotifications()));
            //getting all account details by making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_update_account,"POST",info);
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
        protected void onPostExecute(final Boolean success)
        {
            Log.d("settings","finished");
            if(success)
            {
                Log.d("settings", "update done");
                LoginActivity.userAccount=tempUserAccount;
                settingsChanged=false;
            }
            else
            {
                Log.e("settings", "error");
                Toast.makeText(context,"Your Account was not updated",Toast.LENGTH_LONG).show();
            }

        }
    }
   /* public class UpdatePermissions extends AsyncTask<Void, Void, Boolean>
    {
        UpdatePermissions()
        {
            Log.d("settings permissions","update started...");
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //building parameters
            List<NameValuePair>info=new ArrayList<NameValuePair>();
            info.add(new BasicNameValuePair("permissions",permissions));
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_update_permissions,"POST",info);
            Log.d("PERMISSIONS SEND: ",info.toString());
            Log.d("PERMISSIONS OBJECT: ",jsonObject.toString());
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
        protected void onPostExecute(final Boolean success)
        {
            Log.d("settings permissions","finished");
            if(success)
            {
                Log.d("settings permissions", "update done");
                permissionsChanged=false;
            }
            else
            {
                Log.e("settings permissions", "error");
                Toast.makeText(context,"Permissions Updating error",Toast.LENGTH_LONG).show();
            }

        }
    }*/

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
