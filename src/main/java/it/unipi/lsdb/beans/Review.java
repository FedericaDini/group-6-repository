package it.unipi.lsdb.beans;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Review {
    private Date date;
    private String title;
    private String text;
    private double rating;
    private boolean doRecommend;
    private String prodId;
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
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

    public Review(String title, String text, double rating, boolean doRecommend, String prodId, String userId) {
        this.title = title;
        this.text = text;
        this.rating = rating;
        this.doRecommend = doRecommend;
        this.prodId = prodId;
        this.userId = userId;
        this.date = new Date();
    }

    public Review(String title, String text, double rating, boolean doRecommend, String prodId, String userId, Date date) {
        this.title = title;
        this.text = text;
        this.rating = rating;
        this.doRecommend = doRecommend;
        this.prodId = prodId;
        this.userId = userId;
        this.date = date;
    }

    @Override
    public String toString() {
        String pattern = "dd/MM/yyyy HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        String s = "Date: " + simpleDateFormat.format(date) + "\n";

        if (userId != null) {
            s = s.concat("User: " + userId + "\n");
        }

        s = s.concat("Title: " + getTitle() + "\n" +
                "Text: " + text + "\n" +
                "Rate: " + rating + "\n");

        return s;
    }
}
