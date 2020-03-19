package com.spikingacacia.spikykaziemployee.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.spikingacacia.spikykaziemployee.LoginActivity;
import com.spikingacacia.spikykaziemployee.Preferences;
import com.spikingacacia.spikykaziemployee.R;


public class UPProfileA extends AppCompatActivity
    implements UPAddF.OnFragmentInteractionListener
{
    private Preferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_upprofile);
        preferences = new Preferences(getBaseContext());
        //set the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbarLayout=findViewById(R.id.collapsingToolbar);
        final Typeface tf= ResourcesCompat.getFont(this,R.font.amita);
        collapsingToolbarLayout.setCollapsedTitleTypeface(tf);
        collapsingToolbarLayout.setExpandedTitleTypeface(tf);
        setSupportActionBar(toolbar);
        if(!preferences.isDark_theme_enabled())
        {
            setTheme(R.style.AppThemeLight);
            collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.text_light));
            collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.text_light));
            collapsingToolbarLayout.setBackgroundColor(getResources().getColor(R.color.main_background_light));
            toolbar.setTitleTextColor(getResources().getColor(R.color.text_light));
            toolbar.setPopupTheme(R.style.AppThemeLight_PopupOverlayLight);
            AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
            appBarLayout.getContext().setTheme(R.style.AppThemeLight_AppBarOverlayLight);
            appBarLayout.setBackgroundColor(getResources().getColor(R.color.main_background_light));
            findViewById(R.id.main).setBackgroundColor(getResources().getColor(R.color.main_background_light));
            findViewById(R.id.sec_main).setBackgroundColor(getResources().getColor(R.color.secondary_background_light));
            ((TextView)findViewById(R.id.welcome)).setTextColor(getResources().getColor(R.color.text_light));
        }
        //textviews
        TextView t_insert=findViewById(R.id.insert);
        TextView t_company=findViewById(R.id.company);
        if(LoginActivity.userAccount.getCompany().contentEquals("null") || LoginActivity.userAccount.getCompany().contentEquals(""))
        {
            setTitle("Add");
            Fragment fragment=UPAddF.newInstance("add","");
            FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.base,fragment,"add");
            transaction.commit();
        }
        else
        {
            setTitle("Profile");
            t_insert.setText("Your profile in");
            t_company.setText(LoginActivity.contractorAccount.getUsername()+"...");
            Fragment fragment=UPAddF.newInstance("edit","");
            FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.base,fragment,"add");
            transaction.commit();
            //imageview
            final ImageView imageView=findViewById(R.id.imagepic);
            //get the profile pic
            String url= LoginActivity.base_url+"src/contractors/"+String.format("%s/profilepics/prof_pic",makeName(LoginActivity.contractorAccount.getId()))+".jpg";
            ImageRequest request=new ImageRequest(
                    url,
                    new Response.Listener<Bitmap>()
                    {
                        @Override
                        public void onResponse(Bitmap response)
                        {
                            imageView.setImageBitmap(response);
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

    }
    /**implementation of UPAddF.java**/
    @Override
    public  void onMenuClicked(final int id)
    {
        if(id==1)
        {
            //we need to check if the personnel matrix is empty
            //that is it has position columns
            if(LoginActivity.personnelMatrix==null)
            {
                Toast.makeText(getBaseContext(), "Information unavailable", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, UPTabbedProfileA.class);
            // intent.putExtra("NOTHING","nothing");
            startActivity(intent);
        }
        else
        {
            //we need to check if the equipments matrix is empty
            //that is it has position columns
            if(LoginActivity.equipmentsMatrix==null)
            {
                Toast.makeText(getBaseContext(), "Information unavailable", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent=new Intent(this,UPTabbedProfileEquipA.class);
            // intent.putExtra("NOTHING","nothing");
            startActivity(intent);
        }
    }
    @Override
    public void onProfileAdded()
    {
        Intent intent=new Intent(UPProfileA.this,LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        //int mPendingIntentId=12356;
        //PendingIntent pendingIntent=PendingIntent.getActivity(getContext(),mPendingIntentId,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        // AlarmManager alarmManager=(AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
        // alarmManager.set(AlarmManager.RTC,System.currentTimeMillis()+100,pendingIntent);
        // System.exit(0);
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
