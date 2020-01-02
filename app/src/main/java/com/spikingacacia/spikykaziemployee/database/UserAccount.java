package com.spikingacacia.spikykaziemployee.database;

/**
 * Created by berniegach on 3/20/2018.
 */

public class UserAccount
{
    private int id;
    private String email;
    private String password;
    private String username;
    private String company;
    private String position;
    private String country;
    private String location;
    private String permissions;
    private String notifications;
    private String notificationsEquipments;
    private String dateadded;
    private String datechanged;


    public UserAccount()
    {
    }

    public UserAccount(int id, String email, String password, String username, String company, String position, String country, String location, String permissions, String notifications, String dateadded, String datechanged)
    {
        this.id = id;
        this.email = email;
        this.password = password;
        this.username = username;
        this.company=company;
        this.position=position;
        this.country = country;
        this.location = location;
        this.permissions = permissions;
        this.notifications=notifications;
        this.dateadded = dateadded;
        this.datechanged = datechanged;
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

    public String getCompany()
    {
        return company;
    }

    public void setCompany(String company)
    {
        this.company = company;
    }

    public String getPosition()
    {
        return position;
    }

    public void setPosition(String position)
    {
        this.position = position;
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

}
