package com.example.haim.tweetsapp.helpers;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.haim.tweetsapp.Chat;
import com.parse.ParseBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Haim on 12/27/2014.
 */
public class TweetsBroadcastReceiver extends ParseBroadcastReceiver {
    public TweetsBroadcastReceiver(){
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            JSONObject data = new JSONObject(intent.getExtras().getString("com.parse.Data"));
            if(Chat.getInstance()!=null)
                Chat.getInstance().printMessage(data.getString("alert"));
        }catch(JSONException e){
            Toast.makeText(context, "Invalid JSON received", Toast.LENGTH_LONG).show();
        }
    }
}