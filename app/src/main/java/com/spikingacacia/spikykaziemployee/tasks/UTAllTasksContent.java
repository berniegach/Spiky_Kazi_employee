package com.spikingacacia.spikykaziemployee.tasks;

import com.spikingacacia.spikykaziemployee.LoginActivity;
import com.spikingacacia.spikykaziemployee.database.CTasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class UTAllTasksContent
{
    public final List<Task> ITEMS = new ArrayList<Task>();
    public final Map<String, Task> ITEM_MAP = new HashMap<String, Task>();
    private  int mWhichTask=0;

    public UTAllTasksContent(int whichTask)
    {
        mWhichTask=whichTask;
        int pos=1;
        Iterator<CTasks>iterator= LoginActivity.uTasksList.iterator();
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
            switch(mWhichTask)
            {
                case 1:
                    //pending
                    if(pending>0)
                        addItem(createItem(pos, id, title, description, startings, endings, repetition, location, position, geofence, dateadded, datechanged,
                                pending, inProgress, completed, overdue, late, pendingIds, inProgressIds, completedIds, overdueIds, lateIds));
                    pos+=1;
                    break;
                case 2:
                    //in progress
                    if(inProgress>0)
                        addItem(createItem(pos, id, title, description, startings, endings, repetition, location, position, geofence, dateadded, datechanged,
                                pending, inProgress, completed, overdue, late, pendingIds, inProgressIds, completedIds, overdueIds, lateIds));
                    pos+=1;
                    break;
                case 3:
                    //completed
                    if(completed>0)
                        addItem(createItem(pos, id, title, description, startings, endings, repetition, location, position, geofence, dateadded, datechanged,
                                pending, inProgress, completed, overdue, late, pendingIds, inProgressIds, completedIds, overdueIds, lateIds));
                    pos+=1;
                    break;
                case 4:
                    //overdue
                    if(overdue>0)
                        addItem(createItem(pos, id, title, description, startings, endings, repetition, location, position, geofence, dateadded, datechanged,
                                pending, inProgress, completed, overdue, late, pendingIds, inProgressIds, completedIds, overdueIds, lateIds));
                    pos+=1;
                    break;
                case 5:
                    //late
                    if(late>0)
                        addItem(createItem(pos, id, title, description, startings, endings, repetition, location, position, geofence, dateadded, datechanged,
                                pending, inProgress, completed, overdue, late, pendingIds, inProgressIds, completedIds, overdueIds, lateIds));
                    pos+=1;
                    break;
                case 6:
                    //all
                    addItem(createItem(pos, id, title, description, startings, endings, repetition, location, position, geofence, dateadded, datechanged,
                            pending, inProgress, completed, overdue, late, pendingIds, inProgressIds, completedIds, overdueIds, lateIds));
                    pos+=1;
                    break;
            }

        }
    }


    private  void addItem(Task item)
    {
        ITEMS.add(item);
        ITEM_MAP.put(item.count, item);
    }

    public  Task createItem(int count, int id, String title, String description, String startings, String endings, int repetition, String location, String position, String geofence, String dateadded, String datechanged,
                            int pending, int inProgress, int completed, int overdue, int late, String pendingIds, String inProgressIds, String completedIds, String overdueIds, String lateIds)
    {
        return new Task(String.valueOf(count), id, title, description, startings, endings, repetition, location, position, geofence, dateadded, datechanged,
                pending, inProgress, completed, overdue, late, pendingIds, inProgressIds, completedIds, overdueIds, lateIds);
    }
    public  class Task
    {
        public final String count;
        public final int id;
        public final String title;
        public final String description;
        private String startings;
        private String endings;
        private int repetition;
        private String location;
        private String position;
        private String geofence;
        private String dateadded;
        private String datechanged;
        private int pending;
        private int inProgress;
        private int completed;
        private int overdue;
        private int late;
        private String pendingIds;
        private String inProgressIds;
        private String completedIds;
        private String overdueIds;
        private String lateIds;

        public Task(String count, int id, String title, String description, String startings, String endings, int repetition, String location, String position, String geofence, String dateadded, String datechanged, int pending, int inProgress, int completed, int overdue, int late, String pendingIds, String inProgressIds, String completedIds, String overdueIds, String lateIds)
        {
            this.count = count;
            this.id=id;
            this.title = title;
            this.description = description;
            this.startings = startings;
            this.endings = endings;
            this.repetition = repetition;
            this.location = location;
            this.position = position;
            this.geofence = geofence;
            this.dateadded = dateadded;
            this.datechanged = datechanged;
            this.pending = pending;
            this.inProgress = inProgress;
            this.completed = completed;
            this.overdue = overdue;
            this.late = late;
            this.pendingIds = pendingIds;
            this.inProgressIds = inProgressIds;
            this.completedIds = completedIds;
            this.overdueIds = overdueIds;
            this.lateIds = lateIds;
        }

                @Override
        public String toString()
        {
            return title;
        }
    }
}
