package il.tweetsapp.proj.tweetsapp;

import android.app.Application;
import android.test.ApplicationTestCase;

import org.json.JSONException;
import org.json.JSONObject;

import il.tweetsapp.proj.tweetsapp.Activities.Login;
import il.tweetsapp.proj.tweetsapp.Objcets.Comment;
import il.tweetsapp.proj.tweetsapp.Objcets.Message;
import il.tweetsapp.proj.tweetsapp.helpers.Utils;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {

    public ApplicationTest() {
        super(Application.class);
    }

    public void testGenerateCommentJsonObject(){
        Comment comment = new Comment("New comment", "somebody", "19/6/14", "12:30", "Positive");
        Message message = new Message("New message", "somebody other", "18/6/14", "15:20", false, 11223344, 12345678);
        try {
            JSONObject testObj = Utils.generateCommentJSONObject(comment, message);
            if(testObj != null){
                assertEquals("Comment text", "New comment", testObj.getString("comment_alert"));
                assertEquals("Comment owner", "somebody", testObj.getString("comment_owner"));
                assertEquals("Comment date", "19/6/14", testObj.getString("comment_date"));
                assertEquals("Comment time", "12:30", testObj.getString("comment_time"));
                assertEquals("Comment classification", "Positive", testObj.getString("comment_classification"));
                assertEquals("Message owner", "somebody other", testObj.getString("msg_owner"));
                assertEquals("Message ID", 12345678, testObj.getInt("msgId_comment"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void testGenerateMessageJsonObject(){
        Message message = new Message("Message text", "Message owner", "10:30", "1/4/13", false, 11223344, 12345678);

        try {
            JSONObject testObj = Utils.generateMessageJSONObject(message);

            if(testObj != null){
                assertEquals("Message text", "Message text", testObj.getString("alert"));
                assertEquals("Message owner", "Message owner", testObj.getString("msg_owner"));
                assertEquals("Message date", "1/4/13", testObj.getString("msg_date"));
                assertEquals("Message time", "10:30", testObj.getString("msg_time"));
                assertFalse("Message isGroupCreate", testObj.getBoolean("msg_gCreate"));
                assertEquals("Message owner ID", 12345678, testObj.getInt("msg_ownerMsgId"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void testIsUsernameExist(){
        boolean isUsernameExist = Login.usernameExist("/NoName/");
        assertFalse(isUsernameExist);
    }
}