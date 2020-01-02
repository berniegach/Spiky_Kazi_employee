package com.spikingacacia.spikykaziemployee.database;

/**
 * Created by berniegach on 3/20/2018.
 */

public class ContractorAccount
{
    private int id;
    private String email;
    private String password;
    private String username;
    private String country;
    private String location;
    private String permissions;
    private String lengths;
    private String lengthEquipments;
    private String notifications;
    private String notificationsEquipments;
    private int reviewPeriod;
    private String dateadded;
    private String datechanged;
    private String dateToday;
    public ContractorAccount()
    { }
    public ContractorAccount(int id, String email, String password, String username, String country, String location, String permissions, String lengths, String lengthEquipments, String notifications, String notificationsEquipments, int reviewPeriod, String dateadded, String datechanged, String dateToday)
    {
        this.id=id;
        this.email=email;
        this.password=password;
        this.username=username;
        this.country=country;
        this.location=location;
        this.permissions=permissions;
        this.lengths=lengths;
        this.lengthEquipments=lengthEquipments;
        this.notifications=notifications;
        this.notificationsEquipments=notificationsEquipments;
        this.reviewPeriod=reviewPeriod;
        this.dateadded=dateadded;
        this.datechanged=datechanged;
        this.dateToday=dateToday;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public String getPermissions()
    {
        return permissions;
    }

    public void setPermissions(String permissions)
    {
        this.permissions = permissions;
    }

    public String getLengths()
    {
        return lengths;
    }

    public void setLengths(String lengths)
    {
        this.lengths = lengths;
    }
    public String getLengthEquipments() {
        return lengthEquipments;
    }

    public void setLengthEquipments(String lengthEquipments) {
        this.lengthEquipments = lengthEquipments;
    }
    public String getNotifications()
    {
        return notifications;
    }

    public void setNotifications(String notifications)
    {
        this.notifications = notifications;
    }

    public String getNotificationsEquipments()
    {
        return notificationsEquipments;
    }

    public void setNotificationsEquipments(String notificationsEquipments)
    {
        this.notificationsEquipments = notificationsEquipments;
    }
    public int getReviewPeriod() {
        return reviewPeriod;
    }

    public void setReviewPeriod(int reviewPeriod) {
        this.reviewPeriod = reviewPeriod;
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

    public String getDateToday() {
        return dateToday;
    }

    public void setDateToday(String dateToday) {
        this.dateToday = dateToday;
    }

}
