package il.tweetsapp.proj.tweetsapp.Objcets;

/**
 * Created by Haim on 3/26/2015.
 */
public class Message {

    private String message_text;
    private String message_owner;
    private String time;
    private String date;
    private Boolean isGroupCreateMsg;

    public Message(String message_text, String message_owner, String time, String date, Boolean isGroupCreateMsg) {
        this.message_text = message_text;
        this.message_owner = message_owner;
        this.time = time;
        this.date = date;
        this.isGroupCreateMsg = isGroupCreateMsg;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String toString(){
        String msgVariables = "Text: " + this.message_text + "\r\nOwner: " + this.message_owner + "\r\nTime: " + this.time
                + "\r\nDate: " +  this.date;
        return  msgVariables;
    }

    public Boolean getIsGroupCreateMsg() {
        return isGroupCreateMsg;
    }
}

