package il.tweetsapp.proj.tweetsapp.helpers;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.parse.ParseBroadcastReceiver;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.List;

import il.tweetsapp.proj.tweetsapp.Activities.Chat;
import il.tweetsapp.proj.tweetsapp.Activities.Conversations;
import il.tweetsapp.proj.tweetsapp.Database.DataBL;
import il.tweetsapp.proj.tweetsapp.Objcets.Message;
import il.tweetsapp.proj.tweetsapp.R;

/**
 * Created by Haim on 12/27/2014.
 */
public class TweetsBroadcastReceiver extends ParseBroadcastReceiver {

    private DataBL dataBL;
    public TweetsBroadcastReceiver(){
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        dataBL = new DataBL(context);
        String conversationName;
        Log.i("ParseBroadcastReceiver", intent.getExtras().getString("com.parse.Data"));
        String msg = intent.getExtras().getString("com.parse.Data");
        Message msgToDb;
        String groupID = null;

        try {
            JSONObject data = new JSONObject(msg);

            msgToDb = new Message(data.getString("alert"),
                    data.getString("msg_owner"), // In case that the message is notify about group create this value is the Group Name.
                    data.getString("msg_time"),
                    data.getString("msg_date"),
                    data.getBoolean("msg_gCreate"),0);

            // In case that the message is notify about group create
            if(msgToDb.getIsGroupCreateMsg()) {
                groupID = data.getString("groupID");
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Group");

                try {// Get the group details for inserting to local db.
                    ParseObject group = query.get(groupID);
                    conversationName = (String) group.get("name");
                    ParseRelation<ParseUser> groupUsers = group.getRelation("users");
                    ParseQuery<ParseUser> usersQuery = groupUsers.getQuery();
                    // Remove the current user from the group users list (doesn't needed for local db).
                    usersQuery = usersQuery.whereNotEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
                    List<ParseUser> gUsersList = usersQuery.find();
                    dataBL.addConversation(conversationName); // push the conversation to local db
                    for (ParseUser user : gUsersList) // push the users to local db according to conversation.
                        dataBL.addUserToDbTable(conversationName, user.getUsername());
                    if(Chat.getInstance() != null) // In any case the user isn't in group chat screen, so has to notify him about it
                        sendNotification(context, msgToDb, conversationName, msgToDb.getIsGroupCreateMsg());

                } catch (ParseException pe) {
                    Log.e("ParseException", "Get group details failed");
                    // Let the user know that some user have been added him to a group but error occurred.

                    AlertDialog alertDialog = new AlertDialog.Builder(context.getApplicationContext()).create();
                    alertDialog.setTitle("New group error");
                    alertDialog.setMessage(msgToDb.getMessage_owner() +
                            " have been added you to his Group but error occurred in try to fetch the group details.");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                    return;
                }
            }
            else // In this case, the conversation name is the username of the user that chatting with
                conversationName = data.getString("Conversation name");

            // Handle in case that the conversation is with one user and the conversation not exist in the local db.
            if(dataBL.getConversation(conversationName) == null) {
                dataBL.addConversation(conversationName);
                dataBL.addUserToDbTable(conversationName, msgToDb.getMessage_owner());
            }

            // Push the message to the local db
            dataBL.addMessageToDbTable(msgToDb, conversationName);

            //todo - remove debug
            boolean isConvOpen = false;
            if(Conversations.isConvsOpen != null){
                if(Conversations.isConvsOpen.get(conversationName) != null)
                    isConvOpen = Conversations.isConvsOpen.get(conversationName);
            }
            else{// In case that the Hashmap for conversations not initialized before, initializing it here.
                List<String> convsNames = dataBL.getConversationsNames();
                Conversations.isConvsOpen = new LinkedHashMap<String, Boolean>();
                for(String convName : convsNames) {
                    Conversations.isConvsOpen.put(convName, false);
                }
            }

            //Print the message or send notification.
            if(Chat.getInstance() != null && isConvOpen && !Chat.onPauseCalled)
                Utils.printMessage(Chat.getInstance(), msgToDb, msgToDb.getIsGroupCreateMsg(), conversationName);
            else
                sendNotification(context, msgToDb, conversationName, msgToDb.getIsGroupCreateMsg());

            //Todo - delete code below (1 Line for debug)
            //Toast.makeText(context, msgToDb.toString(), Toast.LENGTH_LONG).show();
        } catch (JSONException je) {
            Log.e("ParseBroadcastReceiver", je.getMessage());
            return;
        }
    }

    private void sendNotification(Context context, Message message, String conversationName, boolean isGroupCreate) {
        String notifyMsg;
        if(isGroupCreate)
            notifyMsg = "TweetsApp-New group created!";
        else
            notifyMsg = "New tweetApp from " + message.getMessage_owner();
        NotificationCompat.Builder notification=
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_tweetsapp)
                        .setContentTitle(notifyMsg)
                        .setContentText(message.getMessage_text());
        notification.setAutoCancel(true);

        Intent resultIntent = new Intent(context, Chat.class);
        resultIntent.putExtra("Conversation name", conversationName);
        resultIntent.putExtra("Open conversation", 0);

        // Because clicking the notification opens a new ("special") activity, there's no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(context, 0, resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        notification.setContentIntent(resultPendingIntent);
        SharedPreferences preferences = context.getSharedPreferences("PrefsFile", Context.MODE_PRIVATE);
        // Sets an ID for the notification
        int notificationId = preferences.getInt("notificationId", 001);

        Log.d("Notification ID","Notification ID is " + notificationId);
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(notificationId, notification.build());
        playRingNotification(context);

        // Save the notificationId in Preferences file.
        preferences = context.getSharedPreferences("PrefsFile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("notificationId", ++notificationId);
        editor.commit();
    }

    private void playRingNotification(Context context) {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(context.getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}













