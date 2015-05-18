package il.tweetsapp.proj.tweetsapp.helpers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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

import java.util.List;

import il.tweetsapp.proj.tweetsapp.Chat;
import il.tweetsapp.proj.tweetsapp.Database.DataBL;
import il.tweetsapp.proj.tweetsapp.Objcets.Message;
import il.tweetsapp.proj.tweetsapp.R;

/**
 * Created by Haim on 12/27/2014.
 */
public class TweetsBroadcastReceiver extends ParseBroadcastReceiver {

    DataBL dataBL;

    public TweetsBroadcastReceiver(){
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        dataBL = new DataBL(context);
        Log.i("ParseBroadcastReceiver", intent.getExtras().getString("com.parse.Data"));
        String msg = intent.getExtras().getString("com.parse.Data");
        Message msgToDb;
        String groupID;

        try {
            JSONObject data = new JSONObject(msg);

            msgToDb = new Message(data.getString("alert"),
                    data.getString("msg_owner"), // In case that the message is notify about group create this value is the Group Name.
                    data.getString("msg_time"),
                    data.getString("msg_date"),
                    data.getInt("msg_rating"),
                    data.getInt("msg_ratings"),
                    data.getBoolean("msg_gCreate"));
            groupID = data.getString("groupID");
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Group");
            try{// Get the group details for inserting to local db.
                ParseObject group = query.get(groupID);
                String groupName = (String)group.get("name");
                ParseRelation<ParseUser> groupUsers = group.getRelation("users");
                ParseQuery<ParseUser> usersQuery = groupUsers.getQuery();
                List<ParseUser> gUsersList = usersQuery.find();
                dataBL.addConversation(groupName);
                for(ParseUser user : gUsersList)
                    dataBL.addUserToDbTable(groupName, user.getUsername());

            }catch (ParseException pe){
                Log.e("ParseException", "Get group details failed");
                return;
            }

            if(Chat.getInstance() == null){
               NotificationCompat.Builder notification = createNotification(context, msgToDb, msgToDb.getIsGroupCreateMsg());

            }
            else if (Chat.getInstance() != null)
                NotifyHelper.printMessage(Chat.getInstance(), msgToDb, msgToDb.getIsGroupCreateMsg());
            msgToDb.calculateAverageRating();
            pushCurrentMessageToDb(context, msgToDb);
            //Todo - delete code below (1 Line for debug)
            //Toast.makeText(context, msgToDb.toString(), Toast.LENGTH_LONG).show();
        } catch (JSONException je) {
            Log.e("ParseBroadcastReceiver", je.getMessage());
            return;
        }
    }

    private NotificationCompat.Builder createNotification(Context context, Message message, boolean isGroupCreate) {
        String notifyMsg;
        if(isGroupCreate)
            notifyMsg = "TweetsAppNew-group created ";
        else
            notifyMsg = "TweetsApp-New tweetApp (`･⊝･´)";
        NotificationCompat.Builder notification=
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(notifyMsg)
                        .setContentText(message.getMessage_text());

        Intent resultIntent = new Intent(context, Chat.class);

        // Because clicking the notification opens a new ("special") activity, there's no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(context, 0, resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(resultPendingIntent);

        // Sets an ID for the notification
        int mNotificationId = 001;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, notification.build());
        return notification;
    }

    private void pushCurrentMessageToDb(Context context, Message message) {
        DataBL dataBL = new DataBL(context);
        dataBL.addMessageToDbTable(message, message.getMessage_owner());
    }
}













