package com.spikingacacia.spikykaziemployee.performance;

import com.spikingacacia.spikykaziemployee.LoginActivity;
import com.spikingacacia.spikykaziemployee.database.CReviews;

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
public class UPUserReviewsContent {
    public final List<ReviewItem> ITEMS = new ArrayList<ReviewItem>();
    public final Map<String, ReviewItem> ITEM_MAP = new HashMap<String, ReviewItem>();

    public UPUserReviewsContent()
    {
        int pos=1;
        Iterator<CReviews> iterator= LoginActivity.uReviewsList.iterator();
        while(iterator.hasNext())
        {
            CReviews cReviews = iterator.next();
            int id=cReviews.getId();
            int userid=cReviews.getUserid();
            int classes=cReviews.getClasses();
            String reviewer=cReviews.getReviewer();
            String review=cReviews.getReview();
            String toImprove=cReviews.getToImprove();
            int rating=cReviews.getRating();
            int themonth=cReviews.getThemonth();
            int theyear=cReviews.getTheyear();
            String dateAdded=cReviews.getDateAdded();
            String dateChanged=cReviews.getDateChanged();

            addItem(createItem(pos, id, LoginActivity.userAccount.getUsername(),userid, classes,  reviewer, review, toImprove, rating, themonth, theyear, dateAdded, dateChanged, LoginActivity.userAccount.getPosition(),rating));
            pos+=1;
        }
    }

    private void addItem(ReviewItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.count, item);
    }

    private ReviewItem createItem(int count, int id, String names, int userid, int classes, String reviewer, String review, String toImprove, int rating, int themonth, int theyear, String dateAdded, String dateChanged, String position,int avarageRating) {
        return new ReviewItem(String.valueOf(count), id, names, userid, classes,  reviewer, review, toImprove, rating, themonth, theyear, dateAdded, dateChanged, position,avarageRating);
    }
    public class ReviewItem
    {
        public final String count;
        public final int id;
        public final String names;
        public int userid;
        public int classes;
        public String reviewer;
        public String review;
        public String toImprove;
        public int rating;
        public int themonth;
        public int theyear;
        public String dateAdded;
        public String dateChanged;
        public final String position;
        public final int averageRating;

        public ReviewItem(String count, int id, String names, int userid, int classes, String reviewer, String review, String toImprove, int rating, int themonth, int theyear, String dateAdded, String dateChanged, String position, int averageRating) {
            this.count=count;
            this.id = id;
            this.names=names;
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
            this.position=position;
            this.averageRating=averageRating;
        }

        @Override
        public String toString() {
            return names;
        }
    }
}
