package com.spikingacacia.spikykaziemployee.database;

public class CReviews
{
    private int id;
    private int userid;
    private int classes;
    private String reviewer;
    private String review;
    private String toImprove;
    private int rating;
    private int themonth;
    private int theyear;
    private String dateAdded;
    private String dateChanged;

    public CReviews(int id, int userid, int classes, String reviewer, String review, String toImprove, int rating, int themonth, int theyear, String dateAdded, String dateChanged) {
        this.id = id;
        this.userid = userid;
        this.classes = classes;
        this.reviewer = reviewer;
        this.review = review;
        this.toImprove = toImprove;
        this.rating = rating;
        this.themonth = themonth;
        this.theyear = theyear;
        this.dateAdded = dateAdded;
        this.dateChanged = dateChanged;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getClasses() {
        return classes;
    }

    public void setClasses(int classes) {
        this.classes = classes;
    }

    public String getReviewer() {
        return reviewer;
    }

    public void setReviewer(String reviewer) {
        this.reviewer = reviewer;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getToImprove() {
        return toImprove;
    }

    public void setToImprove(String toImprove) {
        this.toImprove = toImprove;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getThemonth() {
        return themonth;
    }

    public void setThemonth(int themonth) {
        this.themonth = themonth;
    }

    public int getTheyear() {
        return theyear;
    }

    public void setTheyear(int theyear) {
        this.theyear = theyear;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getDateChanged() {
        return dateChanged;
    }

    public void setDateChanged(String dateChanged) {
        this.dateChanged = dateChanged;
    }




}
