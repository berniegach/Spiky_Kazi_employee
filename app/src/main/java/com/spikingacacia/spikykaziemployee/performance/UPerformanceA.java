package com.spikingacacia.spikykaziemployee.performance;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.spikingacacia.spikykaziemployee.R;


public class UPerformanceA extends AppCompatActivity
implements UPUserOverviewF.OnFragmentInteractionListener,
UPUserReviewsF.OnListFragmentInteractionListener{


    private String fragmentWhich="overview";
    public static  String reviewer="manager";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_uperformance);
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
