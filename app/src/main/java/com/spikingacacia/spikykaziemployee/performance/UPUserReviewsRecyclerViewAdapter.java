package com.spikingacacia.spikykaziemployee.performance;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.spikingacacia.spikykaziemployee.Preferences;
import com.spikingacacia.spikykaziemployee.R;
import com.spikingacacia.spikykaziemployee.performance.UPUserReviewsContent.ReviewItem;
import com.spikingacacia.spikykaziemployee.performance.UPUserReviewsF.OnListFragmentInteractionListener;

import java.util.ArrayList;
import java.util.List;


public class UPUserReviewsRecyclerViewAdapter extends RecyclerView.Adapter<UPUserReviewsRecyclerViewAdapter.ViewHolder> {
    private final List<ReviewItem> mValues;
    private final OnListFragmentInteractionListener mListener;
    private List<ReviewItem>itemsCopy;
    private Context mContext;
    private Preferences preferences;

    public UPUserReviewsRecyclerViewAdapter(List<ReviewItem> items, OnListFragmentInteractionListener listener, Context context) {
        mValues = items;
        mListener = listener;
        itemsCopy=new ArrayList<>();
        itemsCopy.addAll(items);
        mContext=context;
        preferences = new Preferences(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.f_upuser_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        if(!preferences.isDark_theme_enabled())
        {
            holder.mView.setBackgroundColor(mContext.getResources().getColor(R.color.secondary_background_light));
        }
        holder.mItem = mValues.get(position);
        holder.mCountView.setText(mValues.get(position).count);
        holder.mDateView.setText(mValues.get(position).dateAdded);
        holder.mRatingView.setText(String.valueOf(mValues.get(position).rating)+"/5");
        String review="";
        if(isXLargeTablet(mContext))
        {
            if((mValues.get(position).review).length()>120)
                review=(mValues.get(position).review).substring(0,120)+"...";
            else
                review=mValues.get(position).review;
        }
        else if(isLargeTablet(mContext))
        {
            if((mValues.get(position).review).length()>80)
                review=(mValues.get(position).review).substring(0,80)+"...";
            else
                review=mValues.get(position).review;
        }
        else
        {
            if((mValues.get(position).review).length()>40)
                review=(mValues.get(position).review).substring(0,40)+"...";
            else
                review=mValues.get(position).review;
        }
        holder.mReviewView.setText(review);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onUserReviewClicked(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
    public void filter(String text)
    {
        mValues.clear();
        if(text.isEmpty())
            mValues.addAll(itemsCopy);
        else
        {
            text=text.toLowerCase();
            for(ReviewItem task:itemsCopy)
            {
                if(task.dateAdded.toLowerCase().contains(text))
                    mValues.add(task);
            }
        }
        notifyDataSetChanged();
    }
    public void itemAdded()
    {
        notifyDataSetChanged();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mCountView;
        public final TextView mDateView;
        public final TextView mRatingView;
        public final TextView mReviewView;
        public final LinearLayout lBase;
        public ReviewItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            lBase=(LinearLayout)view.findViewById(R.id.base);
            mCountView = (TextView) view.findViewById(R.id.count);
            mDateView=(TextView) view.findViewById(R.id.date);
            mRatingView=view.findViewById(R.id.rating);
            mReviewView=view.findViewById(R.id.review);
            lBase.setBackgroundColor( ContextCompat.getColor(mContext, R.color.secondary_background));
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mDateView.getText() + "'";
        }
    }
    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context)
    {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }
    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 7" tablets are extra-large.
     */
    private static boolean isLargeTablet(Context context)
    {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}
