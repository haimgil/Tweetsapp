package com.example.haim.tweetsapp;

import android.app.Activity;
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

public class Chat extends Activity {

    private static Chat INSTANCE = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        INSTANCE = this;

        setContentView(R.layout.activity_chat);
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
        // Query that get all users from installation list except the current user.
        ParseQuery<ParseInstallation> allOthers = ParseQuery.getQuery(ParseInstallation.class);
        allOthers = allOthers.whereNotEqualTo("objectId", ParseInstallation.getCurrentInstallation().getObjectId());

        ParsePush.sendMessageInBackground("PUSH: " + txtMessage.getText(), allOthers);

        txtMessage.setText("");
    }

    public void printMessage(String msg){
        LinearLayout messages = (LinearLayout)findViewById(R.id.messages);
        TextView message = new TextView(this);
        message.setText(msg);

        messages.addView(message);
    }

}
