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
import android.widget.Toast;

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
    public static List <ParseUser> chatWith = null;
    public static String conversationName;
    private DataBL dataBL;
    private boolean isChatWithSingle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);

        INSTANCE = this;
        dataBL = new DataBL(this);

        setContentView(R.layout.activity_chat);
        TextView cNameTextV = (TextView)findViewById(R.id.chatting_with);

        Intent intent = getIntent();

        isChatWithSingle = intent.hasExtra("Chat with single");

        if(intent.hasExtra("Conversation name")){
            conversationName = intent.getStringExtra("Conversation name");
            cNameTextV.setText(conversationName);
            List<Message> messages = dataBL.getConversationMessages(conversationName);
            for(int i=0; i < messages.size(); i++){
                NotifyHelper.printMessage(INSTANCE, messages.get(i), messages.get(i).getIsGroupCreateMsg());
            }
            if(intent.hasExtra("Group created successfully"))
                Toast.makeText(this, "The group \"" + conversationName + "\" was created successfully!", Toast.LENGTH_LONG).show();
        }else {
            conversationName = null;
            Toast.makeText(this, "Some error occurred in conversation name assigning!", Toast.LENGTH_LONG).show();
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
                NotifyHelper.getCurrentTime(), NotifyHelper.getCurrentDate(), false);
        //Todo - check the problem here
        for(ParseUser user : chatWith)
            dataBL.addMessageToDbTable(newMsg, user.getUsername());


        NotifyHelper.printMessage(INSTANCE, newMsg, newMsg.getIsGroupCreateMsg());

        ParseQuery<ParseInstallation> destQuery = ParseQuery.getQuery(ParseInstallation.class);

        //Todo - handle in the destination (get all the user in the current conversation);
        for(ParseUser user : chatWith) { // Every iteration send the message to one of the users group.
            ParseQuery<ParseInstallation> destination = destQuery.whereEqualTo("user", user);
            try {
                String conversationName;
                if(isChatWithSingle)
                    conversationName = ParseUser.getCurrentUser().getUsername();
                else
                    conversationName = this.conversationName;
                final JSONObject messageDetails = NotifyHelper.generateMessageJSONObject(newMsg);
                messageDetails.put("Conversation name", conversationName);
                ParsePush.sendDataInBackground(messageDetails, destination);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        txtMessage.setText("");
    }
}
