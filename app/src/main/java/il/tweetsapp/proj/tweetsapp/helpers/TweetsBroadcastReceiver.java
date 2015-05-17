package il.tweetsapp.proj.tweetsapp.helpers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.parse.ParseBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import il.tweetsapp.proj.tweetsapp.Chat;
import il.tweetsapp.proj.tweetsapp.Database.DataBL;
import il.tweetsapp.proj.tweetsapp.Objcets.Message;
import il.tweetsapp.proj.tweetsapp.R;

/**
 * Created by Haim on 12/27/2014.
 */
public class TweetsBroadcastReceiver extends ParseBroadcastReceiver {
    public TweetsBroadcastReceiver(){
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("ParseBroadcastReceiver", intent.getExtras().getString("com.parse.Data"));
        String msg = intent.getExtras().getString("com.parse.Data");
        Message msgToDb;
        String groupID;

        boolean isGroupCreate = false;
            try {
                JSONObject data = new JSONObject(msg);
                if(data.getString("gCreateAlert") != null){
                    msgToDb = new Message(data.getString("gCreateAlert"),
                            data.getString("msg_owner"),
                            data.getString("msg_time"),
                            data.getString("msg_date"),
                            data.getInt("msg_rating"),
                            data.getInt("msg_ratings"));
                    groupID = data.getString("groupID");
                    isGroupCreate = true;
                }
                else {
                    msgToDb = new Message(data.getString("msgAlert"),
                            data.getString("msg_owner"),
                            data.getString("msg_time"),
                            data.getString("msg_date"),
                            data.getInt("msg_rating"),
                            data.getInt("msg_ratings"));
                }
                if(Chat.getInstance() == null){
                   NotificationCompat.Builder notification = createNotification(context, msgToDb, isGroupCreate);

                }
                else if (Chat.getInstance() != null)
                    NotifyHelper.printMessage(Chat.getInstance(), msgToDb);
                msgToDb.calculateAverageRating();
                pushCurrentMessageToDb(context, msgToDb);
                //Todo - delete code below (1 Line for debug)
                //Toast.makeText(context, msgToDb.toString(), Toast.LENGTH_LONG).show();
            } catch (JSONException je) {
                Log.e("ParseBroadcastReceiver", je.getMessage());
                return;
            }
        //}
    }

    private NotificationCompat.Builder createNotification(Context context, Message message, boolean isGroupCreate) {
        String notifyMsg;
        if(isGroupCreate)
            notifyMsg = "New group created by ";
        else
            notifyMsg = "New tweetApp from ";
        NotificationCompat.Builder notification=
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(notifyMsg + message.getMessage_owner())
                        .setContentText(message.getMessage_text().substring(0, 10).concat("..."));

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













