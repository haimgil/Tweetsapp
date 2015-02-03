package com.example.haim.tweetsapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class Chat extends Activity {

    private static Chat INSTANCE = null;
    public  static ParseUser chatWith = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        INSTANCE = this;
        setContentView(R.layout.activity_chat);
        TextView userName = (TextView)findViewById(R.id.chatting_with);
        if(chatWith != null)
            userName.setText(chatWith.getUsername());
        else
            userName.setText("Everyone");
    }

    static public Chat getInstance(){
        return INSTANCE;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
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

    public void onSendButtonClick(View view){
        EditText txtMessage = (EditText)findViewById(R.id.txtMessage);
        printMessage(txtMessage.getText().toString());

        ParseQuery<ParseInstallation> destination;
        destination = ParseQuery.getQuery(ParseInstallation.class);
        if(chatWith == null) {
            // Query that get all users from installation list except the current user.
            destination = destination.whereNotEqualTo("objectId", ParseInstallation.getCurrentInstallation().getObjectId());
        }
        else{
            destination = destination.whereEqualTo("user", chatWith);
        }
        ParsePush.sendMessageInBackground("PUSH: " + txtMessage.getText(), destination);
        txtMessage.setText("");
    }

    public void onUsersClick(View view){
        chatWith = null;
        Intent iUsersList = new Intent(this, Users_list.class);
        startActivity(iUsersList);
    }

    public void printMessage(String msg){
        LinearLayout messages = (LinearLayout)findViewById(R.id.messages);
        TextView message = new TextView(this);
        message.setText(msg);

        messages.addView(message);
    }

}
