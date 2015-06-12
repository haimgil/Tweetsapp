package il.tweetsapp.proj.tweetsapp.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseUser;

import il.tweetsapp.proj.tweetsapp.R;
import il.tweetsapp.proj.tweetsapp.helpers.Utils;


public class Registration extends Activity {

    private EditText username;
    private EditText name;
    private EditText email;
    private EditText password;
    private EditText confirm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        username = (EditText)findViewById(R.id.userField);
        name = (EditText)findViewById(R.id.nameField);
        email = (EditText)findViewById(R.id.emailField);
        password = (EditText)findViewById(R.id.passwordField);
        confirm = (EditText)findViewById(R.id.confirmField);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registration, menu);
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

    public void onRegisterClick(View view){
        String username = this.username.getText().toString().trim();
        String name = this.name.getText().toString().trim();
        String email = this.email.getText().toString().trim();
        String password = this.password.getText().toString();
        String confirmPass = this.confirm.getText().toString();

        String[] login_details = {username, password};

        // Checks that username field contains username
        if(username.length() < 2){
            this.name.setError("Please enter username");
            return;
        }

        // Checks that Name field contains a real name
        if(name.length() < 2){
            this.name.setError("Please enter your full name");
            return;
        }

        // Checks that email is legal
        if(TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            this.email.setError("Illegal email address");
            return;
        }

        // check that password contains at least 8 characters
        if(password.length() < 8){
            this.password.setError("Password must contains at least 8 characters");
            return;
        }

        // confirm passwords quality
        if(!password.equals(confirmPass)){
            this.confirm.setError("Passwords don't match");
            return;
        }

        // create new user
        ParseUser user = new ParseUser();
        user.put("name", name);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);

        // save the new user
        try {
            user.signUp();
        } catch (ParseException | IllegalArgumentException e){
            if(e.getMessage().equals("email_in_use")) {
                Utils.alert(this, "Registration", "Registration Failed. Email address already in use.");
            }
            else if(e.getMessage().equals("username " + username + " already taken")){
                Utils.alert(this, "Registration", "Registration Failed. Username already in use.");
            }
            else {
                Utils.alert(this,"Registration", "Registration Failed. Check your internet connection.");
            }
            return;
        }

        Utils.alert(this,"Registration","You have successfully registered. Check your mail box for verification email");

        Intent intent = new Intent(this, Login.class);
        intent.putExtra("login_details", login_details);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }
}
