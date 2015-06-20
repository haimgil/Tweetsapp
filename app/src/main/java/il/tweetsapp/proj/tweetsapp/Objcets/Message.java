package il.tweetsapp.proj.tweetsapp.Objcets;

import java.util.List;

/**
 * Created by Haim on 3/26/2015.
 */
public class Message {

    private String message_text;
    private String message_owner;
    private String time;
    private String date;
    private Boolean isGroupCreateMsg;
    private long messageId;
    private long ownerMessageId;

    private List<Comment> comments;


    public Message(String message_text, String message_owner, String time, String date,
                                        Boolean isGroupCreateMsg, long messageId, long ownerMessageId) {
        this.message_text = message_text;
        this.message_owner = message_owner;
        this.time = time;
        this.date = date;
        this.isGroupCreateMsg = isGroupCreateMsg;
        this.messageId = messageId;
        this.ownerMessageId = ownerMessageId;
        comments = null;
    }


    public String getMessage_text() {
        return message_text;
    }

    public String getMessage_owner() {
        return message_owner;
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

    public long getMessageId() {
        return messageId;
    }

    public long getOwnerMessageId() {
        return ownerMessageId;
    }

    public void setOwnerMessageId(long ownerMessageId) {
        this.ownerMessageId = ownerMessageId;
    }

    public String toString(){
        String msgVariables = "Text: " + this.message_text + "\r\nOwner: " + this.message_owner + "\r\nTime: " + this.time
                + "\r\nDate: " +  this.date;
        return  msgVariables;
    }

    public Boolean getIsGroupCreateMsg() {
        return isGroupCreateMsg;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}

