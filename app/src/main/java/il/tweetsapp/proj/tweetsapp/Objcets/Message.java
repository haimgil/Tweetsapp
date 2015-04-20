package il.tweetsapp.proj.tweetsapp.Objcets;

/**
 * Created by Haim on 3/26/2015.
 */
public class Message {

    private String message_text;
    private String message_owner;
    private String time;
    private int rating;
    private int number_of_ratings;
    private double average_rating;

    public Message(String message_text, String message_owner, String time) {
        this.message_text = message_text;
        this.message_owner = message_owner;
        this.time = time;
        rating = 0;
        average_rating = 0;
        number_of_ratings = 0;
    }

    public Message(String message_text, String message_owner, String msgTime, int msgRating, int number_of_ratings){
        this.message_text = message_text;
        this.message_owner = message_owner;
        this.time = msgTime;
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
        double tmpAverage = this.rating / this.number_of_ratings;
        this.average_rating = tmpAverage;
    }

}
