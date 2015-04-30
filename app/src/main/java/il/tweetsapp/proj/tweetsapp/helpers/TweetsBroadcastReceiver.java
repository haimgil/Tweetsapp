package il.tweetsapp.proj.tweetsapp.helpers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.parse.ParseBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import il.tweetsapp.proj.tweetsapp.Chat;
import il.tweetsapp.proj.tweetsapp.Database.DataBL;
import il.tweetsapp.proj.tweetsapp.Objcets.Message;

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

        /*if(msg.contains("alert")) {
            try {

                JSONObject data = new JSONObject(msg);
                if (Chat.getInstance() != null)
                    Chat.getInstance().printMessage(data.getString("alert"));

            } catch (JSONException e) {
                // TODO remove after debug
                Toast.makeText(context, "Invalid JSON received", Toast.LENGTH_LONG).show();
            }
        }
        else {*/
            try {
                JSONObject data = new JSONObject(msg);
                Message msgToDb = new Message(data.getString("alert"),
                        data.getString("msg_owner"),
                        data.getString("msg_time"),
                        data.getString("msg_date"),
                        data.getInt("msg_rating"),
                        data.getInt("msg_ratings"));
                if (Chat.getInstance() != null)
                    Chat.getInstance().printMessage(msgToDb);
                msgToDb.calculateAverageRating();
                pushCurrentMessageToDb(context, msgToDb);
                Toast.makeText(context, msgToDb.toString(), Toast.LENGTH_LONG).show();
            } catch (JSONException je) {
                Log.e("ParseBroadcastReceiver", je.getMessage());
                return;
            }
        //}
    }

    private void pushCurrentMessageToDb(Context context, Message message) {
        DataBL dataBL = new DataBL(context);
        dataBL.addMessageToDbTable(message, message.getMessage_owner());
    }
}













