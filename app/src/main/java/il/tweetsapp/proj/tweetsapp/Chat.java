package il.tweetsapp.proj.tweetsapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import il.tweetsapp.proj.tweetsapp.Database.DataBL;
import il.tweetsapp.proj.tweetsapp.Objcets.Message;
import il.tweetsapp.proj.tweetsapp.helpers.NotifyHelper;

public class Chat extends ActionBarActivity{

    private static Chat INSTANCE = null;
    public static ParseUser chatWith = null;
    //public static String
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
        //TODO - Remove this code after debug
        Intent intent = getIntent();
        if(intent.hasExtra("Conversation name")){
           String conversationName = intent.getStringExtra("Conversation name");
            List<Message> messages = dataBL.getConversationMessages(conversationName);
            for(int i=0; i < messages.size(); i++){
                NotifyHelper.printMessage(INSTANCE, messages.get(i));
            }
        }
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
                NotifyHelper.getCurrentTime(), NotifyHelper.getCurrentDate());

        dataBL.addMessageToDbTable(newMsg, chatWith.getUsername());


        NotifyHelper.printMessage(INSTANCE, newMsg);

        ParseQuery<ParseInstallation> destination;
        destination = ParseQuery.getQuery(ParseInstallation.class);
        if(chatWith == null) {
            // Query that get all users from installation list except the current user.
            destination = destination.whereNotEqualTo("objectId", ParseInstallation.getCurrentInstallation().getObjectId());
        }
        else{
            destination = destination.whereEqualTo("user", chatWith);
        }

        //ParsePush.sendMessageInBackground(newMsg.getMessage_owner() + ": " + newMsg.getMessage_text(), destination);
        try {
            final JSONObject messageDetails =  NotifyHelper.generateMessageJSONObject(newMsg, true);
            ParsePush.sendDataInBackground(messageDetails, destination);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        txtMessage.setText("");
    }

    /*private JSONObject generateMessageJSONObject(Message msg) throws JSONException {
        JSONObject object = new JSONObject();
        object.put("alert", msg.getMessage_text());
        object.put("msg_owner", msg.getMessage_owner());
        object.put("msg_time", msg.getTime());
        object.put("msg_date", msg.getDate());
        object.put("msg_rating", msg.getRating());
        object.put("msg_ratings", msg.getNumber_of_ratings());

        return object;
    }*/

    /*private String getCurrentDate() {
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

    public void printMessage(Message msg){
        LinearLayout messages = (LinearLayout)findViewById(R.id.messages);
        LinearLayout inflatedView;
        if(msg.getMessage_owner().equals(ParseUser.getCurrentUser().getUsername()))
            //inflate view for current user messages
            inflatedView = (LinearLayout)View.inflate(this, R.layout.current_user_message_layout, null);
        else
            //inflate view for other users messages
            inflatedView = (LinearLayout)View.inflate(this, R.layout.users_message_layout, null);
        //View newMsgLayout = inflater.inflate(R.layout.current_user_message_layout, messages, true);
        TextView msgTxtV = (TextView)inflatedView.findViewById(R.id.msgTextView);
        TextView timeTxtV = (TextView)inflatedView.findViewById(R.id.msgTimeText);
        msgTxtV.setText(msg.getMessage_owner() + ": " + msg.getMessage_text());
        timeTxtV.setText(msg.getTime());
        //TextView message = new TextView(this);
        //message.setText(msg);

        messages.addView(inflatedView);
    }*/

}
