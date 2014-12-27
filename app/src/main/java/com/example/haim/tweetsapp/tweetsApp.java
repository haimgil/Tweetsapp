package com.example.haim.tweetsapp;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParsePush;
import com.parse.SaveCallback;


public class TweetsApp extends Application {

    private static final String PARSE_APP_ID = "SmqqaJ358gtvsoD1wAli03oQ1xGXw7atn8DEfq3O";
    private static final String PARSE_CLIENT_KEY = "Itxa39Y8LIgIcBc587GZCkvqeKlGpQke3BoXPBJm";

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(this, PARSE_APP_ID, PARSE_CLIENT_KEY);
        ParseFacebookUtils.initialize(getResources().getString(R.string.facebook_app_id));

        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });
    }
}
