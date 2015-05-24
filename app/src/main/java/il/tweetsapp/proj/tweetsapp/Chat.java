package il.tweetsapp.proj.tweetsapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
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
import il.tweetsapp.proj.tweetsapp.helpers.Utils;

public class Chat extends ActionBarActivity{

    private static Chat INSTANCE = null;
    public static List <ParseUser> chatWith = null;
    public static String conversationName;
    private Button sendButton;
    private EditText msgEditText;
    private ScrollView msgsScrollView;
    private DataBL dataBL;
    private boolean isChatWithSingle;
    public static boolean onPauseCalled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        INSTANCE = this;
        dataBL = new DataBL(this);
        onPauseCalled = false;

        msgEditText = (EditText)findViewById(R.id.msgEditText);
        sendButton = (Button)findViewById(R.id.sendMsgButton);
        msgsScrollView = (ScrollView)findViewById(R.id.messagesScrollView);
        msgsScrollView.post(new Runnable() {
            @Override
            public void run() {
                msgsScrollView.fullScroll(View.FOCUS_DOWN);
            }
        });

        //Todo - Check why not working (make the button enabled when editing text
        msgEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(msgEditText.getText().toString().length() > 0)
                    sendButton.setEnabled(true);
                else
                    sendButton.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Intent intent = getIntent();

        isChatWithSingle = intent.hasExtra("Chat with single");

        if(intent.hasExtra("Conversation name")){
            conversationName = intent.getStringExtra("Conversation name");
            this.setTitle(conversationName);

            List<Message> messages = dataBL.getConversationMessages(conversationName);
            for(int i=0; i < messages.size(); i++){
                Utils.printMessage(INSTANCE, messages.get(i), messages.get(i).getIsGroupCreateMsg());
            }
            if(intent.hasExtra("Group created successfully"))
                Toast.makeText(this, "The group \"" + conversationName + "\" was created successfully!", Toast.LENGTH_LONG).show();
        }else {
            conversationName = null;
            Toast.makeText(this, "Some error occurred in conversation name assigning!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        onPauseCalled = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        onPauseCalled = true;
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
        EditText txtMessage = (EditText)findViewById(R.id.msgEditText);

        // Create new message object for insert to database
        final Message newMsg = new Message(txtMessage.getText().toString(), ParseUser.getCurrentUser().getUsername(),
                Utils.getCurrentTime(), Utils.getCurrentDate(), false);

        //Print the message to the sender screen.
        Utils.printMessage(INSTANCE, newMsg, newMsg.getIsGroupCreateMsg());
        for(ParseUser user : chatWith)
            dataBL.addMessageToDbTable(newMsg, user.getUsername());

        ParseQuery<ParseInstallation> destQuery = ParseQuery.getQuery(ParseInstallation.class);

        //Todo - handle in the destination (get all users in the current conversation);
        for(ParseUser user : chatWith) { // Every iteration send the message to one of the users group.
            ParseQuery<ParseInstallation> destination = destQuery.whereEqualTo("user", user);
            try {
                String conversationName;
                if(isChatWithSingle)
                    conversationName = ParseUser.getCurrentUser().getUsername();
                else
                    conversationName = this.conversationName;
                final JSONObject messageDetails = Utils.generateMessageJSONObject(newMsg);
                messageDetails.put("Conversation name", conversationName);
                ParsePush.sendDataInBackground(messageDetails, destination);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        txtMessage.setText("");
        sendButton.setEnabled(false);
    }
}











