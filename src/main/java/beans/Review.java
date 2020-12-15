package beans;

import java.util.Date;

public class Review {
    private String id;
    private Date date;
    private String title;
    private String text;
    private double rating;
    private boolean doRecommend;
    //Do purchase???

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public boolean isDoRecommend() {
        return doRecommend;
    }

    public void setDoRecommend(boolean doRecommend) {
        this.doRecommend = doRecommend;
    }

    public Review() {
    }
}
