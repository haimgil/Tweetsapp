package com.example.haim.tweetsapp.Objcets;

/**
 * Created by Haim on 3/26/2015.
 */
public class Message {

    private String msg_text;
    private String time;
    private int rating;
    private double average_rating;
    private int number_of_ratings;

    public Message(String msg_text, String time) {
        this.msg_text = msg_text;
        this.time = time;
        rating = 0;
        average_rating = 0;
        number_of_ratings = 0;
    }

    public double getAverage_rating() {
        return average_rating;
    }

    public void setAverage_rating(double userRate) {
        this.rating += userRate;
        this.number_of_ratings++;
        double tmpAverage = this.rating / this.number_of_ratings;
        this.average_rating = tmpAverage;
    }
}
