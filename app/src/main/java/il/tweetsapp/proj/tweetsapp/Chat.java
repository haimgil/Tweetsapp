package il.tweetsapp.proj.tweetsapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import il.tweetsapp.proj.tweetsapp.Objcets.Message;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Chat extends ActionBarActivity{

    private static Chat INSTANCE = null;
    public  static ParseUser chatWith = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
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

        Message message = createNewMessage(txtMessage.getText().toString());
        //TODO - Handle the message object(insert to db?!?)

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

    // Save the message details for db storage.
    private Message createNewMessage(String msg) {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy\r\nHH:mm");
        Calendar calendar = Calendar.getInstance();
        String time = dateFormat.format(calendar.getTime());
        Message message = new Message(msg, time);

        return message;
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
