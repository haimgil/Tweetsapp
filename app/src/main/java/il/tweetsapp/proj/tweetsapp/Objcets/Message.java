package il.tweetsapp.proj.tweetsapp.Objcets;

/**
 * Created by Haim on 3/26/2015.
 */
public class Message {

    private String message_text;
    private String message_owner;
    private String time;
    private String date;
    private int rating;
    private int number_of_ratings;
    private double average_rating;

    public Message(String message_text, String message_owner, String time, String date) {
        this.message_text = message_text;
        this.message_owner = message_owner;
        this.time = time;
        this.date = date;
        rating = 0;
        average_rating = 0;
        number_of_ratings = 0;
    }

    public Message(String message_text, String message_owner, String msgTime, String msgDate, int msgRating, int number_of_ratings){
        this.message_text = message_text;
        this.message_owner = message_owner;
        this.time = msgTime;
        this.date = msgDate;
        this.rating = msgRating;
        this.number_of_ratings = number_of_ratings;
    }

    public String getMessage_text() {
        return message_text;
    }

    public void setMessage_text(String message_text) {
        this.message_text = message_text;
    }

    public String getMessage_owner() {
        return message_owner;
    }

    public void setMessage_owner(String message_owner) {
        this.message_owner = message_owner;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public double getAverage_rating() {
        return average_rating;
    }

    public void setAverage_rating(double userRate) {
        this.rating += userRate;
        this.number_of_ratings++;
        this.average_rating = this.rating / this.number_of_ratings;
    }

    public double calculateAverageRating(){
        if(this.number_of_ratings == 0)
            return 0;
        else
            return this.average_rating = this.rating / this.number_of_ratings;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getNumber_of_ratings() {
        return number_of_ratings;
    }

    public int getRating() {
        return rating;
    }

    public String toString(){
        String msgVariables = "Text: " + this.message_text + "\r\nOwner: " + this.message_owner + "\r\nTime: " + this.time
                + "\r\nDate: " +  this.date + "\r\nRating: " + this.rating + "\r\nRatings: " + this.number_of_ratings + "\r\nAverage: " + this.average_rating;
        return  msgVariables;
    }
}

