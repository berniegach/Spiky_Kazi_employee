package com.spikingacacia.spikykaziemployee.performance;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.appbar.AppBarLayout;
import com.spikingacacia.spikykaziemployee.Preferences;
import com.spikingacacia.spikykaziemployee.R;


public class UPerformanceA extends AppCompatActivity
implements UPUserOverviewF.OnFragmentInteractionListener,
UPUserReviewsF.OnListFragmentInteractionListener
{


    private String fragmentWhich="overview";
    public static  String reviewer="manager";
    private Preferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_uperformance);
        preferences = new Preferences(getBaseContext());
        Log.d("checking","in cperactivity create");
        //set actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Overview");
        //set the first base fragment
        Fragment fragment=UPUserOverviewF.newInstance();
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.base,fragment,"");
        transaction.commit();
        //fragment manager
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener()
        {
            @Override
            public void onBackStackChanged()
            {
                int count=getSupportFragmentManager().getBackStackEntryCount();
                if(count==0)
                    setTitle("Overview");
                else if(count==1)
                    setTitle(fragmentWhich);
            }
        });
        if(!preferences.isDark_theme_enabled())
        {
            setTheme(R.style.AppThemeLight_NoActionBarLight);
            toolbar.setTitleTextColor(getResources().getColor(R.color.text_light));
            toolbar.setPopupTheme(R.style.AppThemeLight_PopupOverlayLight);
            AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
            appBarLayout.getContext().setTheme(R.style.AppThemeLight_AppBarOverlayLight);
            appBarLayout.setBackgroundColor(getResources().getColor(R.color.main_background_light));
            findViewById(R.id.main).setBackgroundColor(getResources().getColor(R.color.main_background_light));
        }

    }
    /**
     * implementation of CPOverview.java*/
    @Override
    public void onUserLayoutClicked()
    {
         fragmentWhich="Reviews";
        setTitle(fragmentWhich);
        Fragment fragment=UPUserReviewsF.newInstance();
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.base,fragment,fragmentWhich);
        transaction.addToBackStack(fragmentWhich);
        transaction.commit();
    }
    /**
     * implementation of UPUserReviewsF.java*/
    @Override
    public void onUserReviewClicked(UPUserReviewsContent.ReviewItem item)
    {
        // fragmentWhich="";
        //setTitle(fragmentWhich);
        Fragment fragment= UPReviewDetailF.newInstance(item.review,item.toImprove,item.reviewer,item.rating,item.dateAdded);
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.base,fragment,fragmentWhich);
        transaction.addToBackStack(fragmentWhich);
        transaction.commit();
    }
}
