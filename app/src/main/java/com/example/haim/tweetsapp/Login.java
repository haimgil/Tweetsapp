package com.example.haim.tweetsapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.util.Arrays;


public class Login extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
    }

    public void onFacebookClick(View view){
        final ProgressDialog pd = ProgressDialog.show(this, "", "Logging in...", true);
        ParseFacebookUtils.logIn(Arrays.asList("public_profile", "email"), this, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                pd.dismiss();

                if(parseUser == null || e!=null) {
                    Utils.alert(Login.this,"","Login with Facebook failed");
                }else{
                    // TODO: get full name from facebook profile
                    Intent i = new Intent(Login.this, Chat.class);
                    startActivity(i);
                }
            }
        });
    }
}
