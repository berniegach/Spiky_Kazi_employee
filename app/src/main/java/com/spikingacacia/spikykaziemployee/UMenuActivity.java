package com.spikingacacia.spikykaziemployee;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.spikingacacia.spikykaziemployee.notifications.UNMessageListActivity;
import com.spikingacacia.spikykaziemployee.performance.UPerformanceA;
import com.spikingacacia.spikykaziemployee.profile.UPProfileA;
import com.spikingacacia.spikykaziemployee.tasks.UTTasksA;


public class UMenuActivity extends AppCompatActivity
    implements UMenuFragment.OnFragmentInteractionListener
{
    private boolean runRate=true;
    private Preferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_umenu);
        preferences = new Preferences(getBaseContext());
        //set the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbarLayout=findViewById(R.id.collapsingToolbar);
        final Typeface tf= ResourcesCompat.getFont(this,R.font.amita);
        collapsingToolbarLayout.setCollapsedTitleTypeface(tf);
        collapsingToolbarLayout.setExpandedTitleTypeface(tf);
        setSupportActionBar(toolbar);
        setTitle("Menu");

        Fragment fragment=UMenuFragment.newInstance("","");
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.base,fragment,"menu");
        transaction.commit();
        if(!preferences.isDark_theme_enabled())
        {
            setTheme(R.style.AppThemeLight);
            findViewById(R.id.main).setBackgroundColor(getResources().getColor(R.color.main_background_light));
            findViewById(R.id.sec_main).setBackgroundColor(getResources().getColor(R.color.secondary_background_light));
            collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.text_light));
            collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.text_light));
            collapsingToolbarLayout.setBackgroundColor(getResources().getColor(R.color.main_background_light));
            ((TextView)findViewById(R.id.who)).setTextColor(getResources().getColor(R.color.text_light));
            ((TextView)findViewById(R.id.welcome)).setTextColor(getResources().getColor(R.color.text_light));
            toolbar.setTitleTextColor(getResources().getColor(R.color.text_light));
            toolbar.setPopupTheme(R.style.AppThemeLight_PopupOverlayLight);
            AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
            appBarLayout.getContext().setTheme(R.style.AppThemeLight_AppBarOverlayLight);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        //set the welcome text
        //we set it in onResume to factor in the possibility of the username changing in the settings
        try
        {
            if(LoginActivity.userAccount.getUsername().length()<2 || LoginActivity.userAccount.getUsername().contentEquals("null"))
            {
                ((TextView)findViewById(R.id.who)).setText("Please go to settings and set your name...");
            }
            else
                ((TextView)findViewById(R.id.who)).setText(LoginActivity.userAccount.getUsername());
        }
        catch (Exception e)
        {
            ((TextView)findViewById(R.id.who)).setText("Please go to settings and set your name...");
        }
        if(runRate)
        {
            AppRater.app_launched(this);
            runRate=false;
        }
    }
    @Override
    public void onBackPressed()
    {
        //super.onBackPressed();
        new AlertDialog.Builder(UMenuActivity.this)
                .setTitle("Quit")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        finishAffinity();
                        //Intent intent=new Intent(Intent.ACTION_MAIN);
                        // intent.addCategory(Intent.CATEGORY_HOME);
                        // startActivity(intent);
                    }
                }).create().show();
    }
    /**implementation of UMenuFragment.java**/
    @Override
    public void onMenuClicked(int id)
    {
        if(id==1)
        {
            //profile
            Intent intent=new Intent(this, UPProfileA.class);
            // intent.putExtra("NOTHING","nothing");
            startActivity(intent);
        }
        else if(id==4)
        {
            //tasks
            Intent intent=new Intent(UMenuActivity.this, UTTasksA.class);
            startActivity(intent);
        }
        else if(id==5)
        {
            //notifications
            Intent intent=new Intent(this, UNMessageListActivity.class);
            startActivity(intent);
        }
        else if(id==6)
        {
            //performance review
            Intent intent=new Intent(UMenuActivity.this, UPerformanceA.class);
            startActivity(intent);
        }
        else if(id==8)
        {
            //settings
            Intent intent=new Intent(this, USettingsActivity.class);
            // intent.putExtra("NOTHING","nothing");
            startActivity(intent);
        }
    }
    @Override
    public void onLogOut()
    {
        new AlertDialog.Builder(UMenuActivity.this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out?")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        SharedPreferences loginPreferences=getBaseContext().getSharedPreferences("loginPrefs",MODE_PRIVATE);
                        SharedPreferences.Editor loginPreferencesEditor =loginPreferences.edit();
                        loginPreferencesEditor.putBoolean("rememberme",false);
                        loginPreferencesEditor.commit();
                        finishAffinity();
                        //Intent intent=new Intent(Intent.ACTION_MAIN);
                        //intent.addCategory(Intent.CATEGORY_HOME);
                        // startActivity(intent);
                    }
                }).create().show();
    }

}
