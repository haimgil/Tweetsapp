package com.example.haim.tweetsapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.haim.tweetsapp.helpers.Utils;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import java.util.Arrays;


public class Login extends Activity {

    private EditText mail;
    private EditText password;
    private String[] loginDetails;
    private SharedPreferences settings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mail = (EditText)findViewById(R.id.mailLogin);
        password = (EditText)findViewById(R.id.passwordLogin);

        Bundle extras = getIntent().getExtras();
        // When Login activity powered by Registration activity then gets the login details from Registration activity.
        Intent iFromReg = getIntent();
        if(iFromReg != null && iFromReg.hasExtra("login_details")){
            loginDetails = extras.getStringArray("login_details");
            mail.setText(loginDetails[0]);
            password.setText(loginDetails[1]);
        }
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
                    Utils.alert(Login.this, "", "Login with Facebook failed");
                }else{
                    makeMeRequest();

                    settings = getSharedPreferences("PrefsFile", MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("login",true);
                    editor.commit();

                    pairingUserToInstallationId();
                    Intent i = new Intent(Login.this, Users_list.class);
                    startActivity(i);
                }
            }
        });
    }

    public void makeMeRequest() {
        if (ParseFacebookUtils.getSession().isOpened()) {
            Request.newMeRequest(ParseFacebookUtils.getSession(), new Request.GraphUserCallback() {

                @Override
                public void onCompleted(GraphUser facebookUser, Response response) {
                    if (facebookUser != null) {
                        ParseUser.getCurrentUser().put("name", facebookUser.getFirstName());
                        ParseUser.getCurrentUser().setUsername(facebookUser.getName());
                        ParseUser.getCurrentUser().saveInBackground();
                    }
                }
            }).executeAsync();
        }
    }

    public void onSignUpClick(View view){
        Intent iReg = new Intent(this, Registration.class);
        startActivity(iReg);
    }

    public void onLoginClick(View view){
        boolean isUserExist = false;
        String userMail = mail.getText().toString();
        String userPassword = password.getText().toString();
        ParseUser.logInInBackground(userMail, userPassword, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    // If user exist and authenticated, send user to Welcome.class
                    Intent intent = new Intent(
                            Login.this,
                            Users_list.class);

                    settings = getSharedPreferences("PrefsFile", MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("login",true);
                    editor.commit();

                    // TODO here => Save the currentUser to Extra for using in Chat.class
                    pairingUserToInstallationId();

                    intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(),
                            "Successfully Logged in",
                            Toast.LENGTH_LONG).show();
                    finish();
                }
                else{
                    Toast.makeText(
                            getApplicationContext(),
                            "E-mail address or password was incorrect. Try again or Sign Up now",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void pairingUserToInstallationId() {
        ParseInstallation pInstallation = ParseInstallation.getCurrentInstallation();
        pInstallation.put("user", ParseUser.getCurrentUser());
        pInstallation.saveInBackground();
    }
}
