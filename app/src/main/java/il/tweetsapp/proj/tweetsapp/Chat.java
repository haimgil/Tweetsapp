package il.tweetsapp.proj.tweetsapp;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import il.tweetsapp.proj.tweetsapp.Database.DataBL;
import il.tweetsapp.proj.tweetsapp.Objcets.Message;

public class Chat extends ActionBarActivity{

    private static Chat INSTANCE = null;
    public  static ParseUser chatWith = null;
    private DataBL dataBL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);

        INSTANCE = this;
        dataBL = new DataBL(this);

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

        // Create new message object for insert to database
        final Message newMsg = new Message(txtMessage.getText().toString(), ParseUser.getCurrentUser().getUsername(),
                getCurrentTime(), getCurrentDate());

        dataBL.addMessageToDbTable(newMsg, chatWith.getUsername());


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

        ParsePush.sendMessageInBackground(newMsg.getMessage_owner() + ": " + newMsg.getMessage_text(), destination);
        try {
            final JSONObject messageDetails =  generateMessageJSONObject(newMsg);
            ParsePush.sendDataInBackground(messageDetails, destination);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        txtMessage.setText("");
    }

    private JSONObject generateMessageJSONObject(Message msg) throws JSONException {
        JSONObject object = new JSONObject();
        object.put("msg_txt", msg.getMessage_text());
        object.put("msg_owner", msg.getMessage_owner());
        object.put("msg_time", msg.getTime());
        object.put("msg_date", msg.getDate());
        object.put("msg_rating", msg.getRating());
        object.put("msg_ratings", msg.getNumber_of_ratings());
        Toast.makeText(this, object.toString(),Toast.LENGTH_LONG).show();

        return object;
    }


    //TODO - Last time I stop here! need to check pulling messages from db

    private String getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String date = sdf.format(cal.getTime());
        return date;
    }

    private String getCurrentTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String time = sdf.format(cal.getTime());
        return time;
    }

    public void printMessage(String msg){
        LinearLayout messages = (LinearLayout)findViewById(R.id.messages);
        TextView message = new TextView(this);
        message.setText(msg);

        messages.addView(message);
    }

}
