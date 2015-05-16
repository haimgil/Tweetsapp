package il.tweetsapp.proj.tweetsapp.helpers;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import il.tweetsapp.proj.tweetsapp.Objcets.Message;
import il.tweetsapp.proj.tweetsapp.R;

/**
 * Created by Haim on 5/16/2015.
 */
public class NotifyHelper {

    public static void printMessage(Activity activity, Message msg){
        LinearLayout messages = (LinearLayout)activity.findViewById(R.id.messages);
        LinearLayout inflatedView;
        if(msg.getMessage_owner().equals(ParseUser.getCurrentUser().getUsername()))
            //inflate view for current user messages
            inflatedView = (LinearLayout) View.inflate(activity.getApplicationContext(), R.layout.current_user_message_layout, null);
        else
            //inflate view for other users messages
            inflatedView = (LinearLayout)View.inflate(activity.getApplicationContext(), R.layout.users_message_layout, null);
        //View newMsgLayout = inflater.inflate(R.layout.current_user_message_layout, messages, true);
        TextView msgTxtV = (TextView)inflatedView.findViewById(R.id.msgTextView);
        TextView timeTxtV = (TextView)inflatedView.findViewById(R.id.msgTimeText);
        msgTxtV.setText(msg.getMessage_owner() + ": " + msg.getMessage_text());
        timeTxtV.setText(msg.getTime());
        //TextView message = new TextView(this);
        //message.setText(msg);

        messages.addView(inflatedView);
    }


    public static JSONObject generateMessageJSONObject(Message msg, boolean isMsg) throws JSONException {
        JSONObject object = new JSONObject();
        if(isMsg)
            object.put("msgAlert", msg.getMessage_text());
        else //
            object.put("gCreateAlert", msg.getMessage_text());
        object.put("msg_owner", msg.getMessage_owner());
        object.put("msg_time", msg.getTime());
        object.put("msg_date", msg.getDate());
        object.put("msg_rating", msg.getRating());
        object.put("msg_ratings", msg.getNumber_of_ratings());

        return object;
    }


    public static String getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String date = sdf.format(cal.getTime());
        return date;
    }


    public static String getCurrentTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String time = sdf.format(cal.getTime());
        return time;
    }
}
