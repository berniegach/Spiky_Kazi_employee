package com.spikingacacia.spikykaziemployee.tasks;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.appbar.AppBarLayout;
import com.spikingacacia.spikykaziemployee.Preferences;
import com.spikingacacia.spikykaziemployee.R;

public class UTTasksA extends AppCompatActivity
    implements UTOverviewF.OnFragmentInteractionListener,
        UTAllTasksF.OnListFragmentInteractionListener,
        UTTaskOverviewF.OnFragmentInteractionListener
{
    public static String fragmentWhich="overview";
    private int mWhichTask=0;
    private Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_uttasks);
        preferences = new Preferences(getBaseContext());
        //set actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Tasks");
        if(!preferences.isDark_theme_enabled())
        {
            setTheme(R.style.AppThemeLight);
            toolbar.setTitleTextColor(getResources().getColor(R.color.text_light));
            toolbar.setPopupTheme(R.style.AppThemeLight_PopupOverlayLight);
            AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
            appBarLayout.getContext().setTheme(R.style.AppThemeLight_AppBarOverlayLight);
            appBarLayout.setBackgroundColor(getResources().getColor(R.color.main_background_light));
            findViewById(R.id.main).setBackgroundColor(getResources().getColor(R.color.main_background_light));
        }
        //set the first base fragment
        Fragment fragment=UTOverviewF.newInstance("","");
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
                    setTitle("Tasks");
                else if(count==1)
                    setTitle(fragmentWhich);
            }
        });
    }

    @Override
    public void onTaskClicked(final int id)
    {
        mWhichTask=id;
        switch(id)
        {
            case 1:
                fragmentWhich="Pending";
                break;
            case 2:
                fragmentWhich="In Progress";
                break;
            case 3:
                fragmentWhich="Completed";
                break;
            case 4:
                fragmentWhich="Overdue";
                break;
            case 5:
                fragmentWhich="Late";
                break;
            case 6:
                fragmentWhich="All";
                break;
        }
        setTitle(fragmentWhich);
        Fragment fragment=UTAllTasksF.newInstance(1,id);
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.base,fragment,fragmentWhich);
        transaction.addToBackStack(fragmentWhich);
        transaction.commit();
    }
    /**implementation of UTAllTasksF.java**/
    @Override
    public void onCalenderClicked()
    {
        setTitle("Calender");
        Fragment fragment=UTCalenderF.newInstance(mWhichTask);
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.base,fragment,"calender");
        transaction.addToBackStack("calender");
        transaction.commit();
    }
    @Override
    public void onTaskItemClicked(UTAllTasksContent.Task item)
    {
        setTitle("Task Overview");
        Fragment fragment=UTTaskOverviewF.newInstance(item.id);
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.base,fragment,"task_overview");
        transaction.addToBackStack("task_overview");
        transaction.commit();
    }
    /**implementation of UTTaskOverviewF.java**/
    public void onUpdate(int id)
    {
        setTitle("Update Task");
        /*Fragment fragment=CTAddF.newInstance(1,id);
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.base,fragment,"add");
        transaction.addToBackStack("add");
        transaction.commit();*/
    }
    public void onDelete()
    {
        setTitle(fragmentWhich);
        onBackPressed();
    }
    @Override
    public void onStartClicked()
    {
        onBackPressed();
        fragmentWhich="In Progress";
        setTitle(fragmentWhich);
        Fragment fragment=UTAllTasksF.newInstance(1,2);
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.base,fragment,fragmentWhich);
        transaction.addToBackStack(fragmentWhich);
        transaction.commit();

    }
    @Override
    public void onEndClicked()
    {
        onBackPressed();
        fragmentWhich="Completed";
        setTitle(fragmentWhich);
        Fragment fragment=UTAllTasksF.newInstance(1,3 );
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.base,fragment,fragmentWhich);
        transaction.addToBackStack(fragmentWhich);
        transaction.commit();
    }
}
