package com.spikingacacia.spikykaziemployee.database;

/**
 * Created by $USER_NAME on 12/23/2018.
 **/
public class CTasks
{
    private int id;
    private String title;
    private String description;
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

    public CTasks(int id, String title, String description, String startings, String endings, int repetition, String location, String position, String geofence, String dateadded, String datechanged, int pending, int inProgress, int completed, int overdue, int late, String pendingIds, String inProgressIds, String completedIds, String overdueIds, String lateIds)
    {
        this.id = id;
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



    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getStartings()
    {
        return startings;
    }

    public void setStartings(String startings)
    {
        this.startings = startings;
    }

    public String getEndings()
    {
        return endings;
    }

    public void setEndings(String endings)
    {
        this.endings = endings;
    }

    public int getRepetition()
    {
        return repetition;
    }

    public void setRepetition(int repetition)
    {
        this.repetition = repetition;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public String getPosition()
    {
        return position;
    }

    public void setPosition(String position)
    {
        this.position = position;
    }

    public String getGeofence()
    {
        return geofence;
    }

    public void setGeofence(String geofence)
    {
        this.geofence = geofence;
    }

    public String getDateadded()
    {
        return dateadded;
    }

    public void setDateadded(String dateadded)
    {
        this.dateadded = dateadded;
    }

    public String getDatechanged()
    {
        return datechanged;
    }

    public void setDatechanged(String datechanged)
    {
        this.datechanged = datechanged;
    }


    public int getPending()
    {
        return pending;
    }

    public void setPending(int pending)
    {
        this.pending = pending;
    }

    public int getInProgress()
    {
        return inProgress;
    }

    public void setInProgress(int inProgress)
    {
        this.inProgress = inProgress;
    }

    public int getCompleted()
    {
        return completed;
    }

    public void setCompleted(int completed)
    {
        this.completed = completed;
    }

    public int getOverdue()
    {
        return overdue;
    }

    public void setOverdue(int overdue)
    {
        this.overdue = overdue;
    }

    public int getLate()
    {
        return late;
    }

    public void setLate(int late)
    {
        this.late = late;
    }

    public String getPendingIds()
    {
        return pendingIds;
    }

    public void setPendingIds(String pendingIds)
    {
        this.pendingIds = pendingIds;
    }

    public String getInProgressIds()
    {
        return inProgressIds;
    }

    public void setInProgressIds(String inProgressIds)
    {
        this.inProgressIds = inProgressIds;
    }

    public String getCompletedIds()
    {
        return completedIds;
    }

    public void setCompletedIds(String completedIds)
    {
        this.completedIds = completedIds;
    }

    public String getOverdueIds()
    {
        return overdueIds;
    }

    public void setOverdueIds(String overdueIds)
    {
        this.overdueIds = overdueIds;
    }

    public String getLateIds()
    {
        return lateIds;
    }

    public void setLateIds(String lateIds)
    {
        this.lateIds = lateIds;
    }


}
