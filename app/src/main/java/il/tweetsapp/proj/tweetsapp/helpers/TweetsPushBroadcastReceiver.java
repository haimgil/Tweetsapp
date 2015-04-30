package il.tweetsapp.proj.tweetsapp.helpers;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;

import com.parse.ParsePushBroadcastReceiver;

/**
 * Created by Haim on 4/30/2015.
 */
public class TweetsPushBroadcastReceiver extends ParsePushBroadcastReceiver{
    @Override
    protected Notification getNotification(Context context, Intent intent) {
        Notification notification = super.getNotification(context, intent);
        notification.notify();
        return notification;
    }
}
