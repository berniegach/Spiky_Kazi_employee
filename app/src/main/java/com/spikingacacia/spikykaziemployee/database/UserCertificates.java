package com.spikingacacia.spikykaziemployee.database;

/**
 * Created by $USER_NAME on 10/8/2018.
 **/
public class UserCertificates
{
    private int id;
    private int userid;
    private String whereis;
    private int verified;
    private String issue;
    private String expiry;
    private String dateAdded;
    private String dateChanged;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getUserid()
    {
        return userid;
    }

    public void setUserid(int userid)
    {
        this.userid = userid;
    }

    public String getWhereis()
    {
        return whereis;
    }

    public void setWhereis(String whereis)
    {
        this.whereis = whereis;
    }

    public int getVerified()
    {
        return verified;
    }

    public void setVerified(int verified)
    {
        this.verified = verified;
    }

    public String getIssue()
    {
        return issue;
    }

    public void setIssue(String issue)
    {
        this.issue = issue;
    }

    public String getExpiry()
    {
        return expiry;
    }

    public void setExpiry(String expiry)
    {
        this.expiry = expiry;
    }

    public String getDateAdded()
    {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded)
    {
        this.dateAdded = dateAdded;
    }

    public String getDateChanged()
    {
        return dateChanged;
    }

    public void setDateChanged(String dateChanged)
    {
        this.dateChanged = dateChanged;
    }

    public UserCertificates()
    {
    }

    public UserCertificates(int id, int userid, String whereis, int verified, String issue, String expiry, String dateAdded, String dateChanged)
    {
        this.id = id;
        this.userid = userid;
        this.whereis = whereis;
        this.verified = verified;
        this.issue = issue;
        this.expiry = expiry;
        this.dateAdded = dateAdded;
        this.dateChanged = dateChanged;
    }
}
