package com.spikingacacia.spikykaziemployee.tasks;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.spikingacacia.spikykaziemployee.CommonHelper;
import com.spikingacacia.spikykaziemployee.LoginActivity;
import com.spikingacacia.spikykaziemployee.Preferences;
import com.spikingacacia.spikykaziemployee.R;
import com.spikingacacia.spikykaziemployee.database.CTasks;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import static androidx.annotation.Dimension.SP;
import static com.spikingacacia.spikykaziemployee.CommonHelper.collapse;
import static com.spikingacacia.spikykaziemployee.CommonHelper.expand;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UTCalenderF.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UTCalenderF#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UTCalenderF extends Fragment
{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_WHICH_TASK = "which_task";

    private int  mWhichTask;
    private int mTaskColor;
    private View view;
    private  Handler handler;
    private LinearLayout lMonthCurrent;
    private LinearLayout lMonthPrevious;
    private LinearLayout lMonthNext;
    private LinearLayout l_months_view;
    private LinearLayout l_months;
    private static Calendar previous;
    private  static Calendar rightNow;
    private static Calendar currentCalender;
    private TextView tMonth;
    private TextView tToday;
    private TextView inHowLong;
    private TextView monthButton;
    private TextView weekButton;
    private TextView dayButton;
    private TextView goToButton;
    private LinearLayout monthView;
    private LinearLayout weekView;
    private LinearLayout dayView;
    private LinearLayout lAgendas;
    private TextView lastViewSelected;
    private TextView textViewNow;
    private TextView textViewWithTasks;
    //week view
    private ScrollView scrollWeek;
    private LinearLayout wDays;
    private LinearLayout wHours;
    private boolean daysChanged=false;
    private TextView tMonthWeek;
    private TextView tTodayWeek;
    //day view
    private ScrollView scrollDay;
    private LinearLayout dDays;
    private LinearLayout dHours;
    private TextView tMonthDay;
    private TextView tTodayDay;
    private Preferences preferences;

    private OnFragmentInteractionListener mListener;

    public UTCalenderF()
    {
        // Required empty public constructor
    }
    public static UTCalenderF newInstance(int whichTask)
    {
        UTCalenderF fragment = new UTCalenderF();
        Bundle args = new Bundle();
        args.putInt(ARG_WHICH_TASK, whichTask);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mWhichTask = getArguments().getInt(ARG_WHICH_TASK);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.f_ctcalender, container, false);
        preferences=new Preferences(getContext());
        handler=new Handler();
        //months view
        l_months_view=view.findViewById(R.id.month_view);
        l_months=view.findViewById(R.id.months);
        lAgendas=view.findViewById(R.id.agendas);
        tMonth=view.findViewById(R.id.t_month);
        tToday=view.findViewById(R.id.t_today);
        inHowLong=view.findViewById(R.id.in_how_long);
        monthButton=view.findViewById(R.id.month_button);
        weekButton=view.findViewById(R.id.week_button);
        dayButton=view.findViewById(R.id.day_button);
        goToButton=view.findViewById(R.id.goto_button);
        monthView=view.findViewById(R.id.month_view);
        weekView=view.findViewById(R.id.week_view);
        dayView=view.findViewById(R.id.day_view);
        //weeks view
        scrollWeek=view.findViewById(R.id.scroll_weeks);
        wDays=view.findViewById(R.id.w_days);
        wHours=view.findViewById(R.id.w_weeks);
        tMonthWeek=view.findViewById(R.id.t_month_week);
        tTodayWeek=view.findViewById(R.id.t_today_week);
        //days view
        scrollDay=view.findViewById(R.id.scroll_hours);
        dDays=view.findViewById(R.id.d_days);
        dHours=view.findViewById(R.id.d_hours);
        tMonthDay=view.findViewById(R.id.t_month_day);
        tTodayDay=view.findViewById(R.id.t_today_day);
         rightNow=Calendar.getInstance();
         currentCalender=(Calendar)rightNow.clone();
        //set the drawablelft programmatically coz of devices under api 21
        CommonHelper.setVectorDrawable(getContext(),monthButton,0,R.drawable.ic_ts1,0,0);
        CommonHelper.setVectorDrawable(getContext(),weekButton,0,R.drawable.ic_ts2,0,0);
        CommonHelper.setVectorDrawable(getContext(),dayButton,0,R.drawable.ic_ts3,0,0);
        CommonHelper.setVectorDrawable(getContext(),goToButton,0,R.drawable.ic_ts4,0,0);
         //get the color
        getTaskColor();

        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
         lMonthPrevious=new LinearLayout(getContext());
         lMonthPrevious.setLayoutParams(params);
         lMonthCurrent=new LinearLayout(getContext());
         lMonthCurrent.setLayoutParams(params);
         lMonthNext=new LinearLayout(getContext());
         lMonthNext.setLayoutParams(params);
        //add months to layouts
        tMonth.setText(getMonth(currentCalender));

        lMonthPrevious=addMonthsToLayout(getPreviousMonth(currentCalender));
        lMonthCurrent= addMonthsToLayout(currentCalender);
        lMonthNext=addMonthsToLayout( getNextMonth(currentCalender));
        l_months.addView(lMonthCurrent);
        tToday.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                currentCalender=(Calendar) rightNow.clone();
                l_months.removeView(lMonthCurrent);
                tMonth.setText(getMonth(rightNow));
                lMonthPrevious=addMonthsToLayout(getPreviousMonth(rightNow));
                lMonthCurrent= addMonthsToLayout(rightNow);
                lMonthNext=addMonthsToLayout( getNextMonth(rightNow));
                l_months.addView(lMonthCurrent);
            }
        });

        //changing the layout onclick listeners
        //changeLayoutButtonClicked(1);
        monthButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                changeLayoutButtonClicked(1);
            }
        });
        weekButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                changeLayoutButtonClicked(2);
            }
        });
        dayButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                changeLayoutButtonClicked(3);
            }
        });
        goToButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setDatePicker("Go to date");
            }
        });
        tTodayWeek.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
               // addDaysToWeeksHeader(rightNow,0);
                initializeCurrentWeek(rightNow);
            }
        });
        tTodayDay.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                addDaysToDaysHeader(rightNow,0);
                initializeCurrentWeekDay(rightNow);
            }
        });
        //weeks view
        //addDaysToWeeksHeader(currentCalender,0);
        //days view
        //addDaysToDaysHeader(currentCalender,0);
       // initializeCurrentWeek(currentCalender);
       // initializeCurrentWeekDay(currentCalender);
        if(!preferences.isDark_theme_enabled())
        {
            view.findViewById(R.id.footer).setBackgroundColor(getResources().getColor(R.color.tertiary_background_light));
            monthView.setBackgroundColor(getResources().getColor(R.color.secondary_background_light));
            weekView.setBackgroundColor(getResources().getColor(R.color.secondary_background_light));
            dayView.setBackgroundColor(getResources().getColor(R.color.secondary_background_light));
            view.findViewById(R.id.month_days).setBackgroundColor(getResources().getColor(R.color.tertiary_background_light));
            inHowLong.setBackgroundColor(getResources().getColor(R.color.tertiary_background_light));
            //week
            weekView.setBackgroundColor(getResources().getColor(R.color.secondary_background_light));
            view.findViewById(R.id.week_days).setBackgroundColor(getResources().getColor(R.color.tertiary_background_light));
            view.findViewById(R.id.week_view).setBackgroundColor(getResources().getColor(R.color.secondary_background_light));
            wDays.setBackgroundColor(getResources().getColor(R.color.tertiary_background_light));
            //day view
            dayView.setBackgroundColor(getResources().getColor(R.color.secondary_background_light));
            view.findViewById(R.id.day_view_days).setBackgroundColor(getResources().getColor(R.color.tertiary_background_light));
            view.findViewById(R.id.d_days).setBackgroundColor(getResources().getColor(R.color.tertiary_background_light));
        }

        return view;
    }
    private int  getFirstDayOfWeek(Calendar calendar)
    {
        Calendar cal=(Calendar) calendar.clone();
        int date=cal.get(Calendar.DAY_OF_MONTH);
        int day_of_week=calendar.get(Calendar.DAY_OF_WEEK);
        cal.add(Calendar.DAY_OF_MONTH,-1*day_of_week+1);

        return cal.get(Calendar.DAY_OF_MONTH);
    }
    private void changeLayoutButtonClicked(int id)
    {
        if (id==1)
        {
            //show month view
            expand(monthView);
            collapse(weekView);
            collapse(dayView);
        }
        else if(id==2)
        {
            //show week view
            collapse(monthView);
            expand(weekView);
            collapse(dayView);
            initializeCurrentWeek(currentCalender);
        }
        else if(id==3)
        {
            //show day view
            collapse(monthView);
            collapse(weekView);
            expand(dayView);
            initializeCurrentWeekDay(currentCalender);
        }
    }
    private String getMonth(Calendar calendar)
    {
        String month="";
        switch(calendar.get(Calendar.MONTH))
        {
            case Calendar.JANUARY:
                month="January";
                break;
            case Calendar.FEBRUARY:
                month="February";
                break;
            case Calendar.MARCH:
                month="March";
                break;
            case Calendar.APRIL:
                month="April";
                break;
            case Calendar.MAY:
                month="May";
                break;
            case Calendar.JUNE:
                month="June";
                break;
            case Calendar.JULY:
                month="July";
                break;
            case Calendar.AUGUST:
                month="August";
                break;
            case Calendar.SEPTEMBER:
                month="September";
                break;
            case Calendar.OCTOBER:
                month="October";
                break;
            case Calendar.NOVEMBER:
                month="November";
                break;
            case Calendar.DECEMBER:
                month="December";
                break;
        }
        return month+" "+calendar.get(Calendar.YEAR);
    }
    /**month views**/
    private LinearLayout addMonthsToLayout(final  Calendar calendar)
    {
        LinearLayout l_one_month=new LinearLayout(getContext());
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        l_one_month.setLayoutParams(params);
        l_one_month.setOrientation(LinearLayout.VERTICAL);
        //add the days
        addWeeksToMonthsLayout(l_one_month,calendar);
        return l_one_month;
    }
    private void addWeeksToMonthsLayout(final LinearLayout layout,final Calendar cal)
    {
        Calendar firstDayCal=(Calendar) cal.clone();
        firstDayCal.set(Calendar.DAY_OF_MONTH,1);
        // firstDayCal.set(Calendar.DAY_OF_MONTH,1);
        int s=firstDayCal.get(Calendar.DAY_OF_WEEK);
        int maxDays=cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        int dayCount=1;
        for(int c=1; c<=6; c+=1)
        {
            final LinearLayout l_one_week=new LinearLayout(getContext());
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            l_one_week.setLayoutParams(params);
            l_one_week.setWeightSum(7);
            l_one_week.setOrientation(LinearLayout.HORIZONTAL);
            l_one_week.setTag(String.format("week%d",c+1));
            //add days to layout
            for(int d=1; d<=7; d+=1)
            {
                final TextView textView=new TextView(getContext());
                LinearLayout.LayoutParams params_d=new LinearLayout.LayoutParams(0,dpTopx(50),1);
                textView.setLayoutParams(params_d);
                if(dayCount>maxDays)
                    break;
                if(c==1 && d<s)
                    textView.setText("");
                else
                {
                    final boolean now;
                    if(cal.get(Calendar.YEAR)==Calendar.getInstance().get(Calendar.YEAR) && cal.get(Calendar.MONTH)==Calendar.getInstance().get(Calendar.MONTH) && dayCount==Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
                        now=true;
                    else
                        now=false;
                    final int taskCount=getTasksMonthlyCount(cal,dayCount);
                    final LinkedHashMap<Integer,String>taskIds=getTasksMonthlyTitles(cal,dayCount);
                    textView.setText(String.format("%d", dayCount));
                    textView.setTag(String.format("m%d", dayCount));
                    if(now)
                    {
                        textView.setBackgroundResource(R.drawable.circle_date_now);
                        textViewNow=textView;
                    }
                    else if(taskCount>0)
                    {
                        //textView.setBackgroundColor(mTaskColor);
                        textView.setBackgroundResource(R.drawable.date_with_task);
                        textViewWithTasks=textView;
                    }

                    final int clickedDay=dayCount;
                    textView.setOnTouchListener(new View.OnTouchListener()
                    {
                        float downX, upX;
                        @Override
                        public boolean onTouch(final View v, MotionEvent event)
                        {
                            switch (event.getAction())
                            {
                                case MotionEvent.ACTION_DOWN:
                                    downX=event.getX();
                                    return true;
                                case MotionEvent.ACTION_UP:
                                    upX=event.getX();
                                    float deltaX=downX-upX;
                                    if(deltaX>100)
                                    {
                                        //swipe to left
                                        swippedMonthToNext();

                                    }
                                    else if(deltaX<-100)
                                    {
                                        //swipe to right
                                        swippedMonthToPrevious();
                                    }
                                    else
                                    {
                                        //clicked
                                        changeTextViewClickedColor(v,taskCount);
                                        new Thread(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                dayClicked(cal,clickedDay);
                                            }
                                        }).start();
                                        new Thread(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                addAgendasToMonthLayout(taskIds);
                                            }
                                        }).start();

                                    }

                                    return true;

                            }
                            return false;
                        }
                    });
                    dayCount += 1;
                }
                textView.setGravity(Gravity.CENTER);
                l_one_week.addView(textView);
            }
            layout.addView(l_one_week);
        }

    }
    private void getTaskColor()
    {
        //colors
        int color;
        if(preferences.isDark_theme_enabled())
            color=ContextCompat.getColor(getContext(), R.color.tertiary_background);
        else
            color=ContextCompat.getColor(getContext(), R.color.tertiary_background_light);
        /*switch(mWhichTask)
        {
            case 1:
                //pending
                color=ContextCompat.getColor(getContext(), R.color.a_mono3);
                break;
            case 2:
                //in progress
                color=ContextCompat.getColor(getContext(), R.color.a_mono5);
                break;
            case 3:
                //completed
                color=ContextCompat.getColor(getContext(), R.color.a_mono7);
                break;
            case 4:
                //overdue
                color=ContextCompat.getColor(getContext(), R.color.a_comp5);
                break;
            case 5:
                //late
                color=ContextCompat.getColor(getContext(), R.color.a_comp3);
                break;
            case 6:
                //all
                color=ContextCompat.getColor(getContext(), R.color.a_mono6);
                break;
        }*/
        mTaskColor=color;
    }
    private void changeTextViewClickedColor(final View v,final int taskCount)
    {
        if(((TextView)v).getText()=="")
            return;
        //the last and present view clicked have tasks
        if(taskCount>0 && lastViewSelected==textViewWithTasks)
        {
            lastViewSelected.setBackgroundResource(R.drawable.date_with_task);
            v.setBackgroundResource(R.drawable.circle_date_selected);
            textViewWithTasks=(TextView) v;
            lastViewSelected=(TextView) v;
        }
        else
        {
            if(taskCount>0)
                textViewWithTasks=(TextView)v;
            //the last view selected was today
            if(lastViewSelected==textViewNow && lastViewSelected!=null)
            {
                lastViewSelected.setBackgroundResource(R.drawable.circle_date_now);
                v.setBackgroundResource(R.drawable.circle_date_selected);
                lastViewSelected=(TextView) v;
            }
            else if(lastViewSelected==textViewWithTasks)
            {
                lastViewSelected.setBackgroundColor(mTaskColor);
                v.setBackgroundResource(R.drawable.circle_date_selected);
                lastViewSelected=(TextView) v;
            }
            else if(lastViewSelected!=null)
            {
                lastViewSelected.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.transparent));
                v.setBackgroundResource(R.drawable.circle_date_selected);
                lastViewSelected=(TextView) v;
            }
            else
            {
                v.setBackgroundResource(R.drawable.circle_date_selected);
                lastViewSelected=(TextView) v;
            }
        }
    }
    private void swippedMonthToNext()
    {
        Animation animation=AnimationUtils.loadAnimation(getContext(),R.anim.slide_right_to_left_remove);
        lMonthCurrent.startAnimation(animation);
        l_months.removeView(lMonthCurrent);
        currentCalender=getNextMonth(currentCalender);
        lMonthPrevious=addMonthsToLayout(getPreviousMonth(currentCalender));
        lMonthCurrent= addMonthsToLayout(currentCalender);
        lMonthNext=addMonthsToLayout(getNextMonth(currentCalender));
        l_months.addView(lMonthCurrent);
        Animation animation1=AnimationUtils.loadAnimation(getContext(),R.anim.slide_right_to_left_add);
        lMonthCurrent.startAnimation(animation1);
        tMonth.setText(getMonth(currentCalender));
    }
    private void swippedMonthToPrevious()
    {
        Animation animation=AnimationUtils.loadAnimation(getContext(),R.anim.slide_left_to_right_remove);
        lMonthCurrent.startAnimation(animation);
        l_months.removeView(lMonthCurrent);
        currentCalender=getPreviousMonth(currentCalender);
        lMonthPrevious=addMonthsToLayout(getPreviousMonth(currentCalender));
        lMonthCurrent= addMonthsToLayout(currentCalender);
        lMonthNext=addMonthsToLayout( getNextMonth(currentCalender));
        l_months.addView(lMonthCurrent);
        Animation animation1=AnimationUtils.loadAnimation(getContext(),R.anim.slide_left_to_right_add);
        lMonthCurrent.startAnimation(animation1);
        tMonth.setText(getMonth(currentCalender));
    }
    private Calendar getNextMonth(final Calendar calendar)
    {
        Calendar cal=(Calendar) calendar.clone();
        cal.add(Calendar.MONTH,1);
        return cal;
    }
    private Calendar getPreviousMonth(final Calendar calendar)
    {
        Calendar cal=(Calendar) calendar.clone();
        cal.add(Calendar.MONTH,-1);
        return cal;
    }
    //**should run in the background**/
    private void dayClicked(Calendar calendar,int day)
    {
        Calendar calWhen=Calendar.getInstance();
        calWhen.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),day);
        currentCalender=(Calendar)calWhen.clone();


        long days=(calWhen.getTimeInMillis()-Calendar.getInstance().getTimeInMillis())/(24*60*60*1000);
        final String howLong;
        if(days==0)
        {
            howLong="Today "+day+" "+getMonth(Calendar.getInstance());
        }
        else if(days==1)
        {
            howLong="Tomorrow "+day+" "+getMonth(Calendar.getInstance());
        }
        else if(days==2)
        {
            howLong="The day after tomorrow "+day+" "+getMonth(Calendar.getInstance());
        }
        else if(days>2)
        {
            howLong="In "+days+" days "+getMonth(Calendar.getInstance());
        }
        else if(days==-1)
        {
            howLong="Yesterday "+day+" "+getMonth(Calendar.getInstance());
        }
        else if(days==2)
        {
            howLong="The day before yesterday"+day+" "+getMonth(Calendar.getInstance());
        }
        else if(days<2)
        {
            howLong=Math.abs(days)+" days ago "+day+" "+getMonth(Calendar.getInstance());
        }
        else
            howLong="";
        Log.d("calender","days "+days);

        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                inHowLong.setText(howLong);
            }
        });
    }
    //**should run in the background**/
    private void addAgendasToMonthLayout(LinkedHashMap<Integer,String>agendas)
    {
        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                lAgendas.removeAllViews();
            }
        });
        final LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        Iterator iterator= agendas.entrySet().iterator();
        while (iterator.hasNext())
        {
            LinkedHashMap.Entry<Integer,String>set=(LinkedHashMap.Entry<Integer, String>) iterator.next();
            final int id=set.getKey();
            final String title=set.getValue();
            handler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    final TextView textView=new TextView(getContext());
                    textView.setLayoutParams(params);
                    textView.setTextSize(SP,12);
                    textView.setText(title);
                    lAgendas.addView(textView);
                }
            });
        }
    }
    /**week views**/
    private void initializeCurrentWeek(Calendar calendar)
    {
        //Animation animation=AnimationUtils.loadAnimation(getContext(),R.anim.slide_right_to_left_remove);
        wDays.removeAllViews();
        wHours.removeAllViews();
        Calendar currentCal=(Calendar)calendar.clone();
        //get next week
        //currentCal.add(Calendar.WEEK_OF_YEAR,1);
        addDaysToWeeksHeader(currentCal,0);
    }
    private void addDaysToWeeksHeader(Calendar calendar,int how)
    {
        //set the month textview
        tMonthWeek.setText(getMonth(calendar));
        if(wDays.getChildCount()>0)
        {
            wDays.removeAllViews();
        }
        Calendar cal=(Calendar) calendar.clone();
        Calendar calFirstDay=(Calendar) calendar.clone();
        int firstDay=getFirstDayOfWeek(cal);
        //cal.set(Calendar.DAY_OF_MONTH,firstDay);
        calFirstDay.set(Calendar.DAY_OF_MONTH,firstDay);
        for(int count=1; count<=8; count+=1)
        {
            int day=calFirstDay.get(Calendar.DAY_OF_MONTH);
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(0,dpTopx(30),1);
            TextView textView=new TextView(getContext());
            textView.setLayoutParams(params);
            if(count==1)
            {
                textView.setText("");
                Log.d("firstay", " "+firstDay);
            }
            else
            {
                //textView.setText(String.valueOf(firstDay));
                //firstDay+=1;
                textView.setText(String.valueOf(day));
                final boolean now;
                if(calFirstDay.get(Calendar.YEAR)==Calendar.getInstance().get(Calendar.YEAR) && calFirstDay.get(Calendar.MONTH)==Calendar.getInstance().get(Calendar.MONTH) && day==Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
                    now=true;
                else
                    now=false;
                final int taskCount=getTasksMonthlyCount(calFirstDay,day);
                final LinkedHashMap<Integer,String>taskIds=getTasksMonthlyTitles(cal,day);
                //textView.setText(String.format("%d", dayCount));
                textView.setTag(String.format("w%d", day));
                if(now)
                {
                    textView.setBackgroundResource(R.drawable.circle_date_now);
                    //textViewNow=textView;
                }
                else if(taskCount>0)
                {
                    textView.setBackgroundResource(R.drawable.date_with_task);
                   // textViewWithTasks=textView;
                }
                else if(lastViewSelected!=null && lastViewSelected.getText().equals(String.valueOf(day)))
                {
                    textView.setBackgroundResource(R.drawable.circle_date_selected);
                }
                calFirstDay.add(Calendar.DAY_OF_MONTH,1);
            }
            textView.setGravity(Gravity.CENTER);
            wDays.addView(textView);
            if(how==2)
            {
                Animation animation=AnimationUtils.loadAnimation(getContext(),R.anim.slide_right_to_left_add);
                textView.startAnimation(animation);
            }
            else if(how==1)
            {
                Animation animation=AnimationUtils.loadAnimation(getContext(),R.anim.slide_left_to_right_add);
                textView.startAnimation(animation);
            }
            addWeeksHours(count,cal,how,day);
        }
    }
    private void addWeeksHours(final int c, final Calendar calendar, final int how, final int day)
    {
        final LinearLayout layout=new LinearLayout(getContext());
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,1);
        layout.setLayoutParams(params);
        layout.setOrientation(LinearLayout.VERTICAL);
        if(c==1)
            if(preferences.isDark_theme_enabled())
                layout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.secondary_background));
            else
                layout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.secondary_background_light));
        //layout.setDividerPadding(5);
        layout.setShowDividers(LinearLayout.SHOW_DIVIDER_BEGINNING | LinearLayout.SHOW_DIVIDER_MIDDLE | LinearLayout.SHOW_DIVIDER_END);
        layout.setDividerDrawable(getResources().getDrawable(R.drawable.divider_line));
        for (int count=0; count<=23; count+=1)
        {
            final int time=count%12;
            final int time24=count;
            final String when=count>11?"pm":"am";
            final TextView textView=new TextView(getContext());
            LinearLayout.LayoutParams params1=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,dpTopx(50));
            textView.setLayoutParams(params1);
            textView.setPadding(5,5,5,5);
            if(c==1)
            {
                textView.setText(String.format("%d:00 %s", time, when));
            }
            else
            {
                if(getTasksHourlyColor(calendar,time24,0,day))
                {
                    //textView.setBackgroundColor(mTaskColor);
                    textView.setBackgroundResource(R.drawable.date_with_task);
                    Log.d("textview"," "+String.format("%d:00 %s",time,when));
                    //textView.setText("T");
                    String titles=getTasksHourlyTitles(calendar,time24,0,day);
                    textView.setText(titles);
                    textView.setGravity(Gravity.CENTER);
                    textView.setTextSize(SP,10);
                }


            }
            textView.setTextSize(SP,10);
            scrollWeek.setOnTouchListener(new View.OnTouchListener()
            {
                float downX, upX;
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    switch (event.getAction())
                    {
                        case MotionEvent.ACTION_DOWN:
                            downX=event.getX();
                            return true;
                        case MotionEvent.ACTION_UP:
                            upX=event.getX();
                            float deltaX=downX-upX;
                            if(deltaX>100)
                            {
                                //swipe to left
                                swippedToNextWeek(calendar);

                            }
                            else if(deltaX<-100)
                            {
                                //swipe to right
                                swippedToPreviousWeek(calendar);
                            }

                            return true;

                    }
                    return false;
                }
            });
            layout.addView(textView);
        }
        wHours.addView(layout);
        if(how==2)
        {
            Animation animation=AnimationUtils.loadAnimation(getContext(),R.anim.slide_right_to_left_add);
            layout.startAnimation(animation);
        }
        else if(how==1)
        {
            Animation animation=AnimationUtils.loadAnimation(getContext(),R.anim.slide_left_to_right_add);
            layout.startAnimation(animation);
        }


    }
    private void swippedToNextWeek(Calendar calendar)
    {
        Animation animation=AnimationUtils.loadAnimation(getContext(),R.anim.slide_right_to_left_remove);
        int wDaysCount=wDays.getChildCount();
        for(int count=wDaysCount-1; count>=0; count-=1)
        {
            View view=wDays.getChildAt(count);
            view.startAnimation(animation);
            wDays.removeView(view);

        }
        int wHourCount=wHours.getChildCount();
        for(int count=wHourCount-1; count>=0; count-=1)
        {
            View view=wHours.getChildAt(count);
            view.startAnimation(animation);
            wHours.removeView(view);

        }
        Calendar currentCal=(Calendar)calendar.clone();
        //get next week
        currentCal.add(Calendar.WEEK_OF_YEAR,1);
        currentCalender=(Calendar)currentCal.clone();
        addDaysToWeeksHeader(currentCal,2);
    }
    private void swippedToPreviousWeek(Calendar calendar)
    {
        Animation animation=AnimationUtils.loadAnimation(getContext(),R.anim.slide_left_to_right_remove);
        int wDaysCount=wDays.getChildCount();
        for(int count=wDaysCount-1; count>=0; count-=1)
        {
            View view=wDays.getChildAt(count);
            view.startAnimation(animation);
            wDays.removeView(view);

        }
        int wHourCount=wHours.getChildCount();
        for(int count=wHourCount-1; count>=0; count-=1)
        {
            View view=wHours.getChildAt(count);
            view.startAnimation(animation);
            wHours.removeView(view);

        }
        Calendar currentCal=(Calendar)calendar.clone();
        //get next week
        currentCal.add(Calendar.WEEK_OF_YEAR,-1);
        currentCalender=(Calendar)currentCal.clone();
        addDaysToWeeksHeader(currentCal,1);
    }
    /**day views functions**/
    private void initializeCurrentWeekDay(Calendar calendar)
    {
        dDays.removeAllViews();
        dHours.removeAllViews();
        Calendar currentCal=(Calendar)calendar.clone();
        addDaysToDaysHeader(currentCal,0);
    }
    private void addDaysToDaysHeader(final Calendar calendar,int how)
    {
        //set the month textview
        tMonthDay.setText(getMonth(calendar));
        if(dDays.getChildCount()>0)
            dDays.removeAllViews();
        Calendar cal=(Calendar) calendar.clone();
        Calendar calFirstDay=(Calendar) calendar.clone();
        int firstDay=getFirstDayOfWeek(cal);
        calFirstDay.set(Calendar.DAY_OF_MONTH,firstDay);
        for(int count=1; count<=8; count+=1)
        {
            int day=calFirstDay.get(Calendar.DAY_OF_MONTH);
            boolean now=false;
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(0,dpTopx(40),1);
            TextView textView=new TextView(getContext());
            textView.setLayoutParams(params);
            if(count==1)
                textView.setText("");
            else
            {
                textView.setText(String.valueOf(day));
                if(calFirstDay.get(Calendar.YEAR)==Calendar.getInstance().get(Calendar.YEAR) && calFirstDay.get(Calendar.MONTH)==Calendar.getInstance().get(Calendar.MONTH) && day==Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
                    now=true;
                else
                    now=false;
                final int taskCount=getTasksMonthlyCount(calFirstDay,day);
                final LinkedHashMap<Integer,String>taskIds=getTasksMonthlyTitles(cal,day);
                //textView.setText(String.format("%d", dayCount));
                textView.setTag(String.format("w%d", day));
                if(now)
                {
                    textView.setBackgroundResource(R.drawable.circle_date_now);
                    //textViewNow=textView;
                }
                else if(taskCount>0)
                {
                    textView.setBackgroundResource(R.drawable.date_with_task);
                   // textViewWithTasks=textView;
                }
                else if(lastViewSelected!=null && lastViewSelected.getText().equals(String.valueOf(day)))
                {
                    textView.setBackgroundResource(R.drawable.circle_date_selected);
                    changeTextViewClickedColor(textView,taskCount);
                }
                textView.setGravity(Gravity.CENTER);
                textView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        changeTextViewClickedColor(v,taskCount);

                    }
                });
                calFirstDay.add(Calendar.DAY_OF_MONTH,1);
            }

            dDays.addView(textView);
            if(how==2)
            {
                Animation animation=AnimationUtils.loadAnimation(getContext(),R.anim.slide_right_to_left_add);
                textView.startAnimation(animation);
            }
            else if(how==1)
            {
                Animation animation=AnimationUtils.loadAnimation(getContext(),R.anim.slide_left_to_right_add);
                textView.startAnimation(animation);
            }
            //addDayHours(count,cal,how,day);
        }
        //add the hours markings
        addDayHours(1,cal,how);
        //add the hours
        addDayHours(2,calendar,how);
    }
    private void addDayHours(final int c, final Calendar calendar,final int how)
    {
        LinearLayout layout=new LinearLayout(getContext());
        LinearLayout.LayoutParams params;
        if(c==1)
        {
            params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            if(preferences.isDark_theme_enabled())
                layout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.secondary_background));
            else
                layout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.secondary_background_light));
        }
        else
            params=new LinearLayout.LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,7);
        layout.setLayoutParams(params);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setShowDividers(LinearLayout.SHOW_DIVIDER_BEGINNING | LinearLayout.SHOW_DIVIDER_MIDDLE | LinearLayout.SHOW_DIVIDER_END);
        layout.setDividerDrawable(getResources().getDrawable(R.drawable.divider_line));
        //layout.setDividerPadding(5);
        for (int count=0; count<=23; count+=1)
        {
            final int time=count%12;
            final int time24=count;
            final String when=count>11?"pm":"am";
            final TextView textView=new TextView(getContext());
            LinearLayout.LayoutParams params1=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,dpTopx(50));
            textView.setLayoutParams(params1);
            textView.setPadding(5,5,5,5);
            if(c==1)
                textView.setText(String.format("%d:00 %s",time,when));
            else
            {
                if(getTasksHourlyColor(calendar,time24,0,calendar.get(Calendar.DAY_OF_MONTH)))
                {
                    //textView.setBackgroundColor(mTaskColor);
                    textView.setBackgroundResource(R.drawable.date_with_task);
                    Log.d("textview"," "+String.format("%d:00 %s",time,when));
                    //textView.setText("T");
                    String titles=getTasksHourlyTitles(calendar,time24,0,calendar.get(Calendar.DAY_OF_MONTH));
                    textView.setText(titles);
                    textView.setGravity(Gravity.CENTER);
                    textView.setTextSize(SP,10);
                }
                /*new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        final int color=ContextCompat.getColor(getContext(), R.color.a_comp4);
                        if(getTasksHourlyColor(calendar,time24,0,calendar.get(Calendar.DAY_OF_MONTH)))
                        {
                            handler.post(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    textView.setBackgroundColor(color);
                                    Log.d("textview"," "+String.format("%d:00 %s",time,when));
                                    //textView.setText("T");
                                }
                            });
                        }
                    }
                }).start();*/
            }
            textView.setTextSize(SP,10);
            scrollDay.setOnTouchListener(new View.OnTouchListener()
            {
                float downX, upX;
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    switch (event.getAction())
                    {
                        case MotionEvent.ACTION_DOWN:
                            downX=event.getX();
                            return true;
                        case MotionEvent.ACTION_UP:
                            upX=event.getX();
                            float deltaX=downX-upX;
                            if(deltaX>100)
                            {
                                //swipe to left
                                swippedToNextWeekDay(calendar);

                            }
                            else if(deltaX<-100)
                            {
                                //swipe to right
                                swippedToPreviousWeekDay(calendar);
                            }
                            else
                            {
                                //clicked
                                // Toast.makeText(getContext(),"swipped",Toast.LENGTH_SHORT).show();
                                //clicked

                            }

                            return true;

                    }
                    return false;
                }
            });

            layout.addView(textView);
        }
        dHours.addView(layout);
        if(how==2)
        {
            Animation animation=AnimationUtils.loadAnimation(getContext(),R.anim.slide_right_to_left_add);
            layout.startAnimation(animation);
        }
        else if(how==1)
        {
            Animation animation=AnimationUtils.loadAnimation(getContext(),R.anim.slide_left_to_right_add);
            layout.startAnimation(animation);
        }

    }
    private void swippedToNextWeekDay(Calendar calendar)
    {
        Animation animation=AnimationUtils.loadAnimation(getContext(),R.anim.slide_right_to_left_remove);
        int dDaysCount=dDays.getChildCount();
        for(int count=dDaysCount-1; count>=0; count-=1)
        {
            View view=dDays.getChildAt(count);
            view.startAnimation(animation);
            dDays.removeView(view);

        }
        int dHourCount=dHours.getChildCount();
        for(int count=dHourCount-1; count>=0; count-=1)
        {
            View view=dHours.getChildAt(count);
            view.startAnimation(animation);
            dHours.removeView(view);

        }
        Calendar currentCal=(Calendar)calendar.clone();
        //get next week
        currentCal.add(Calendar.WEEK_OF_YEAR,1);
        currentCalender=(Calendar)currentCal.clone();
        addDaysToDaysHeader(currentCal,2);
    }
    private void swippedToPreviousWeekDay(Calendar calendar)
    {
        Animation animation=AnimationUtils.loadAnimation(getContext(),R.anim.slide_left_to_right_remove);
        int dDaysCount=dDays.getChildCount();
        for(int count=dDaysCount-1; count>=0; count-=1)
        {
            View view=dDays.getChildAt(count);
            view.startAnimation(animation);
            dDays.removeView(view);

        }
        int dHourCount=dHours.getChildCount();
        for(int count=dHourCount-1; count>=0; count-=1)
        {
            View view=dHours.getChildAt(count);
            view.startAnimation(animation);
            dHours.removeView(view);

        }
        Calendar currentCal=(Calendar)calendar.clone();
        //get next week
        currentCal.add(Calendar.WEEK_OF_YEAR,-1);
        currentCalender=(Calendar)currentCal.clone();
        addDaysToDaysHeader(currentCal,1);
    }

    private int getTasksMonthlyCount( Calendar calendar,int day)
    {
        int count=0;
        List<String>nowTasks=new ArrayList<>();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH)+1;
        int fHour;
        int fMin;
        int index=0;
        String dt=String.format("%d/%d/%d",day,month,year);
        Iterator<CTasks> iterator= LoginActivity.uTasksList.iterator();
        while(iterator.hasNext())
        {
            CTasks cTasks=iterator.next();
            int id=cTasks.getId();
            String title=cTasks.getTitle();
            String description=cTasks.getDescription();
            String startings=cTasks.getStartings();
            String endings=cTasks.getEndings();
            int repetition=cTasks.getRepetition();
            String location=cTasks.getLocation();
            String position=cTasks.getPosition();
            String geofence=cTasks.getGeofence();
            String dateadded=cTasks.getDateadded();
            String datechanged=cTasks.getDatechanged();
            //get the starting time
            String start[]=startings.split("s");
            String end[]=endings.split("s");
            Log.d("time"," "+start[0]);
            Log.d("time"," "+end[0]);
            if(dt.contentEquals(start[0]))
                count+=1;
            if(dt.contentEquals(end[0]))
                count+=1;

            index+=1;
        }
        return count%5;
    }
    private LinkedHashMap<Integer,String> getTasksMonthlyTitles(Calendar calendar, int day)
    {
        LinkedHashMap<Integer,String>tasks=new LinkedHashMap<>();
        List<String>nowTasks=new ArrayList<>();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH)+1;
        int fHour;
        int fMin;
        int index=0;
        String dt=String.format("%d/%d/%d",day,month,year);
        Iterator<CTasks> iterator=LoginActivity.uTasksList.iterator();
        while(iterator.hasNext())
        {
            CTasks cTasks=iterator.next();
            int id=cTasks.getId();
            String title=cTasks.getTitle();
            String description=cTasks.getDescription();
            String startings=cTasks.getStartings();
            String endings=cTasks.getEndings();
            int repetition=cTasks.getRepetition();
            String location=cTasks.getLocation();
            String position=cTasks.getPosition();
            String geofence=cTasks.getGeofence();
            String dateadded=cTasks.getDateadded();
            String datechanged=cTasks.getDatechanged();
            //get the starting time
            String start[]=startings.split("s");
            String end[]=endings.split("s");
            Log.d("time"," "+start[0]);
            Log.d("time"," "+end[0]);
            if(dt.contentEquals(start[0]))
            {
                tasks.put(id,title);
            }
            if(dt.contentEquals(end[0]))
            {
                tasks.put(id,title);
            }

            index+=1;
        }
        return tasks;
    }
    /**should run in the background**/
    private boolean getTasksHourlyColor( Calendar calendar, int hour, int mins, int day)
    {
        Calendar calPresent=(Calendar)calendar.clone();
        calPresent.set(Calendar.HOUR_OF_DAY,hour);
        calPresent.set(Calendar.MINUTE,mins);
        calPresent.set(Calendar.DAY_OF_MONTH,day);
        int count=0;
        List<String>nowTasks=new ArrayList<>();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH)+1;
        int index=0;
        String dt=String.format("%d/%d/%d",day,month,year);
       // Log.d("gdgsgsa"," "+dt);
        Iterator<CTasks> iterator=LoginActivity.uTasksList.iterator();
        while(iterator.hasNext())
        {
            CTasks cTasks=iterator.next();
            int id=cTasks.getId();
            String title=cTasks.getTitle();
            String description=cTasks.getDescription();
            String startings=cTasks.getStartings();
            String endings=cTasks.getEndings();
            int repetition=cTasks.getRepetition();
            String location=cTasks.getLocation();
            String position=cTasks.getPosition();
            String geofence=cTasks.getGeofence();
            String dateadded=cTasks.getDateadded();
            String datechanged=cTasks.getDatechanged();
            int pending=cTasks.getPending();
            int inProgress=cTasks.getInProgress();
            int completed=cTasks.getCompleted();
            int overdue=cTasks.getOverdue();
            int late=cTasks.getLate();
            String pendingIds=cTasks.getPendingIds();
            String inProgressIds=cTasks.getInProgressIds();
            String completedIds=cTasks.getCompletedIds();
            String overdueIds=cTasks.getOverdueIds();
            String lateIds=cTasks.getLateIds();
            //get only the required
            /*switch(mWhichTask)
            {
                case 1:
                    //pending
                    if(pending==0)
                        continue;
                    break;
                case 2:
                    //in progress
                    if(inProgress==0)
                        continue;
                    break;
                case 3:
                    //completed
                    if (completed==0)
                        continue;
                    break;
                case 4:
                    //overdue
                    if(overdue==0)
                        continue;
                    break;
                case 5:
                    //late
                    if(late==0)
                        continue;
                    break;
                case 6:
                    //all
                    break;
            }*/
            //get the date
            //get the starting time
            String start[]=startings.split("s");
            String start2[]=start[0].split("/");
            String end[]=endings.split("s");
            Date dtStart;
            Date dtEnd;
            Calendar calStart=Calendar.getInstance();
            Calendar calEnd=Calendar.getInstance();
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy hh:mm a");
            try
            {
                dtStart = simpleDateFormat.parse(startings.replace("s", " "));
                dtEnd = simpleDateFormat.parse(endings.replace("s", " "));
                calStart.setTime(dtStart);
                calEnd.setTime(dtEnd);
            }
            catch (ParseException e)
            {
                Log.e("parsing", " "+e.getMessage());
            }
            long startDifference=calStart.getTimeInMillis()-calPresent.getTimeInMillis();
            long endDifference=calEnd.getTimeInMillis()-calPresent.getTimeInMillis();
            //get the date
            /*
            String end2[]=end[0].split("/");
            Calendar calStart=Calendar.getInstance();
            Calendar calEnd=Calendar.getInstance();
            String tm[]=start[1].split(":");
            String etm[]=end[1].split(":");
            int tmH=Integer.parseInt(tm[0]);
            int etmH=Integer.parseInt(etm[0]);
            tm=tm[1].split(" ");
            etm=etm[1].split(" ");
            int tmM=Integer.parseInt(tm[0]);
            int etmM=Integer.parseInt(etm[0]);
            if(tm[1].contentEquals("PM"))
                tmH+=12;
            if(etm[1].contentEquals("PM"))
                etmH+=12;
            calStart.set(Integer.parseInt(start2[2]),Integer.parseInt(start2[1]),Integer.parseInt(start2[0]),tmH,tmM);
            calEnd.set(Integer.parseInt(end2[2]),Integer.parseInt(end2[1]),Integer.parseInt(end2[0]),etmH,etmM);*/

            if(startDifference<=0 && endDifference>=0)
            {
                Log.d(" START", " "+startDifference);
                Log.d(" END", " "+endDifference);
                Log.d("startSTART", startings);
                Log.d("endEND", endings);
                return true;
            }


            index+=1;
        }

        return false;
    }
    private String getTasksHourlyTitles( Calendar calendar, int hour, int mins, int day)
    {
        String titles="";
        Calendar calPresent=(Calendar)calendar.clone();
        calPresent.set(Calendar.HOUR_OF_DAY,hour);
        calPresent.set(Calendar.MINUTE,mins);
        calPresent.set(Calendar.DAY_OF_MONTH,day);
        int count=0;
        List<String>nowTasks=new ArrayList<>();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH)+1;
        int index=0;
        String dt=String.format("%d/%d/%d",day,month,year);
        // Log.d("gdgsgsa"," "+dt);
        Iterator<CTasks> iterator=LoginActivity.uTasksList.iterator();
        while(iterator.hasNext())
        {
            CTasks cTasks=iterator.next();
            int id=cTasks.getId();
            String title=cTasks.getTitle();
            String description=cTasks.getDescription();
            String startings=cTasks.getStartings();
            String endings=cTasks.getEndings();
            int repetition=cTasks.getRepetition();
            String location=cTasks.getLocation();
            String position=cTasks.getPosition();
            String geofence=cTasks.getGeofence();
            String dateadded=cTasks.getDateadded();
            String datechanged=cTasks.getDatechanged();
            int pending=cTasks.getPending();
            int inProgress=cTasks.getInProgress();
            int completed=cTasks.getCompleted();
            int overdue=cTasks.getOverdue();
            int late=cTasks.getLate();
            String pendingIds=cTasks.getPendingIds();
            String inProgressIds=cTasks.getInProgressIds();
            String completedIds=cTasks.getCompletedIds();
            String overdueIds=cTasks.getOverdueIds();
            String lateIds=cTasks.getLateIds();
            //get the date
            //get the starting time
            String start[]=startings.split("s");
            String start2[]=start[0].split("/");
            String end[]=endings.split("s");
            Date dtStart;
            Date dtEnd;
            Calendar calStart=Calendar.getInstance();
            Calendar calEnd=Calendar.getInstance();
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy hh:mm a");
            try
            {
                dtStart = simpleDateFormat.parse(startings.replace("s", " "));
                dtEnd = simpleDateFormat.parse(endings.replace("s", " "));
                calStart.setTime(dtStart);
                calEnd.setTime(dtEnd);
            }
            catch (ParseException e)
            {
                Log.e("parsing", " "+e.getMessage());
            }
            long startDifference=calStart.getTimeInMillis()-calPresent.getTimeInMillis();
            long endDifference=calEnd.getTimeInMillis()-calPresent.getTimeInMillis();
            //get the date
            /*
            String end2[]=end[0].split("/");
            Calendar calStart=Calendar.getInstance();
            Calendar calEnd=Calendar.getInstance();
            String tm[]=start[1].split(":");
            String etm[]=end[1].split(":");
            int tmH=Integer.parseInt(tm[0]);
            int etmH=Integer.parseInt(etm[0]);
            tm=tm[1].split(" ");
            etm=etm[1].split(" ");
            int tmM=Integer.parseInt(tm[0]);
            int etmM=Integer.parseInt(etm[0]);
            if(tm[1].contentEquals("PM"))
                tmH+=12;
            if(etm[1].contentEquals("PM"))
                etmH+=12;
            calStart.set(Integer.parseInt(start2[2]),Integer.parseInt(start2[1]),Integer.parseInt(start2[0]),tmH,tmM);
            calEnd.set(Integer.parseInt(end2[2]),Integer.parseInt(end2[1]),Integer.parseInt(end2[0]),etmH,etmM);*/

            if(startDifference<=0 && endDifference>=0)
            {
                Log.d(" START", " "+startDifference);
                Log.d(" END", " "+endDifference);
                Log.d("startSTART", startings);
                Log.d("endEND", endings);
                titles+=title+"\n";
            }


            index+=1;
        }

        return titles;
    }

    private int dpTopx(int dp)
    {
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,getContext().getResources().getDisplayMetrics());
    }

    private void setDatePicker(String title)
    {
        final int date[];
        final AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        final DatePicker datePicker=new DatePicker(getContext());
       // datePicker.setSpinnersShown(true);
        //datePicker.setCalendarViewShown(false);
        builder.setView(datePicker);
        builder.setTitle(title);
        builder.setPositiveButton("Set", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                int day; int month; final int year;
                day=datePicker.getDayOfMonth();
                month=datePicker.getMonth();
                year=datePicker.getYear();
                currentCalender.set(year,month,day);
                l_months.removeView(lMonthCurrent);
                tMonth.setText(getMonth(currentCalender));
                lMonthPrevious=addMonthsToLayout(getPreviousMonth(currentCalender));
                lMonthCurrent= addMonthsToLayout(currentCalender);
                lMonthNext=addMonthsToLayout( getNextMonth(currentCalender));
                l_months.addView(lMonthCurrent);
                changeLayoutButtonClicked(1);
                addDaysToWeeksHeader(currentCalender,0);
                addDaysToDaysHeader(currentCalender,0);
                TextView textView=view.findViewWithTag(String.format("m%d", day));
                //clicked
                final boolean now;
                if(currentCalender.get(Calendar.YEAR)==Calendar.getInstance().get(Calendar.YEAR) && currentCalender.get(Calendar.MONTH)==Calendar.getInstance().get(Calendar.MONTH) && day==Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
                    now=true;
                else
                    now=false;
                if(lastViewSelected!=null && !now)
                    lastViewSelected.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.transparent));
                if(!now)
                    lastViewSelected=textView;
                if(!now)
                    textView.setBackgroundResource(R.drawable.circle_date_selected);
                dayClicked(currentCalender,day);

            }
        });
        builder.create();
        builder.show();
    }
/*
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
    }*/

    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
