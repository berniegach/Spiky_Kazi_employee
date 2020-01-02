package com.spikingacacia.spikykaziemployee.tasks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.spikingacacia.spikykaziemployee.R;
import com.spikingacacia.spikykaziemployee.tasks.UTAllTasksContent.Task;
import com.spikingacacia.spikykaziemployee.tasks.UTAllTasksF.OnListFragmentInteractionListener;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Task} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class UTAllTasksTaskRecyclerViewAdapter extends RecyclerView.Adapter<UTAllTasksTaskRecyclerViewAdapter.ViewHolder>
{

    private final List<Task> mValues;
    private List<Task>itemsCopy;
    private final OnListFragmentInteractionListener mListener;
    private Context mContext;
    private  int mWhichTask=0;

    public UTAllTasksTaskRecyclerViewAdapter(List<Task> items, OnListFragmentInteractionListener listener, Context context, int whichTask)
    {
        mValues = items;
        mListener = listener;
        itemsCopy=new ArrayList<>();
        itemsCopy.addAll(items);
        mContext=context;
        mWhichTask=whichTask;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.f_ctall_tasks, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        holder.mItem = mValues.get(position);
        holder.countView.setText(mValues.get(position).count);
        holder.titleView.setText(mValues.get(position).title);

        holder.mView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (null != mListener)
                {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onTaskItemClicked(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount()
    {
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
            for(Task task:itemsCopy)
            {
                if(task.title.toLowerCase().contains(text))
                    mValues.add(task);
            }
        }
        notifyDataSetChanged();
    }
    public void notifyChange(int count, int id, String title, String description, String startings, String endings, int repetition, String location, String position, String geofence, String dateadded, String datechanged,
                             int pending, int inProgress, int completed, int overdue, int late, String pendingIds, String inProgressIds, String completedIds, String overdueIds, String lateIds)
    {
        UTAllTasksContent content=new UTAllTasksContent(mWhichTask);
        mValues.add(content.createItem(count,  id,title, description, startings, endings, repetition, location, position, geofence, dateadded, datechanged,
                pending, inProgress, completed, overdue, late, pendingIds, inProgressIds, completedIds, overdueIds, lateIds));
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public final View mView;
        public final LinearLayout lBase;
        public final TextView countView;
        public final TextView titleView;
        public Task mItem;

        public ViewHolder(View view)
        {
            super(view);
            mView = view;
            lBase=(LinearLayout)view.findViewById(R.id.base);
            countView = (TextView) view.findViewById(R.id.count);
            titleView = (TextView) view.findViewById(R.id.title);
            lBase.setBackgroundColor( ContextCompat.getColor(mContext, R.color.secondary_background));
        }

        @Override
        public String toString()
        {
            return super.toString() + " '" + titleView.getText() + "'";
        }
    }
}
