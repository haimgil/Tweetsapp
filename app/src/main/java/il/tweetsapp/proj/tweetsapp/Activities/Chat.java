package il.tweetsapp.proj.tweetsapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import il.tweetsapp.proj.tweetsapp.Database.DataBL;
import il.tweetsapp.proj.tweetsapp.Objcets.Message;
import il.tweetsapp.proj.tweetsapp.R;
import il.tweetsapp.proj.tweetsapp.helpers.Utils;

public class Chat extends ActionBarActivity{

    private static Chat INSTANCE = null;
    public static List <ParseUser> chatWith = null;
    public static String conversationName;
    public static ScrollView msgsScrollView;
    private Button sendButton;
    private EditText msgEditText;
    private TextView numOfCharacters;
    private DataBL dataBL;

    private static String lastMsgOwner;
    private static String lastMsgDate;
    private static String lastMsgTime;

    public static boolean isChatWithSingle;

    public static boolean onPauseCalled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        lastMsgOwner = "";
        lastMsgDate = "";
        lastMsgTime = "";

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        INSTANCE = this;
        dataBL = new DataBL(this);
        onPauseCalled = false;

        numOfCharacters = (TextView)findViewById(R.id.numOfCharacters);
        msgEditText = (EditText)findViewById(R.id.msgEditText);
        sendButton = (Button)findViewById(R.id.sendMsgButton);
        msgsScrollView = (ScrollView)findViewById(R.id.messagesScrollView);
        msgsScrollView.post(new Runnable() {
            @Override
            public void run() {
                msgsScrollView.fullScroll(View.FOCUS_DOWN);
            }
        });

        msgEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(msgEditText.getText().toString().length() > 0 && Utils.isNetworkAvailable(INSTANCE))
                    sendButton.setEnabled(true);
                else
                    sendButton.setEnabled(false);
                String text = msgEditText.getText().toString();
                int numOfChars = Utils.MAX_CHARACTERS_IN_MESSAGE - text.length();
                numOfCharacters.setText(String.valueOf(numOfChars));
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        Intent intent = getIntent();

        isChatWithSingle = intent.hasExtra("Chat with single");

        if(intent.hasExtra("Conversation name")){
            conversationName = intent.getStringExtra("Conversation name");
            this.setTitle(conversationName);

            List<Message> messages = dataBL.getConversationMessages(conversationName);
            for(int i=0; i < messages.size(); i++){
                Utils.printMessage(INSTANCE, messages.get(i), messages.get(i).getIsGroupCreateMsg(), conversationName,
                                                                                       lastMsgOwner, lastMsgDate, lastMsgTime);
                lastMsgOwner = messages.get(i).getMessage_owner();
                lastMsgDate = messages.get(i).getDate();
                lastMsgTime = messages.get(i).getTime();
            }
            if(intent.hasExtra("Group created successfully"))
                Toast.makeText(this, "The group \"" + conversationName + "\" was created successfully!", Toast.LENGTH_LONG).show();
            if(intent.hasExtra("Open conversation")) {
                Conversations.isConvsOpen.put(conversationName, true);
                chatWith = new ArrayList<ParseUser>();
                Utils.setGroupUsersForChatting(this,conversationName);
            }
        }else { // Not supposed to got here
            conversationName = null;
            Toast.makeText(this, "Some error occurred in conversation name assigning!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
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
                Utils.getCurrentTime(), Utils.getCurrentDate(), false, 0, -1);

        //Print the message to the sender screen.
        Utils.printMessage(INSTANCE, newMsg, newMsg.getIsGroupCreateMsg(), conversationName, lastMsgOwner, lastMsgDate, lastMsgTime);
        lastMsgOwner = newMsg.getMessage_owner();
        lastMsgDate = newMsg.getDate();
        lastMsgTime = newMsg.getTime();

        // Insert the message to current user local db.
        long ownerMsgId = dataBL.addMessageToDbTable(newMsg, conversationName);

        if(ownerMsgId < 0){
            Log.e("Add message to db", "Error occurred while try to push message to db");
            Toast.makeText(this, "Error occurred while try to push message to db", Toast.LENGTH_SHORT).show();
            return;
        }
        newMsg.setOwnerMessageId(ownerMsgId);

        ParseQuery<ParseInstallation> destQuery = ParseQuery.getQuery(ParseInstallation.class);

        for(ParseUser user :chatWith) { // Every iteration send the message to one of the users group except the current user.
            if(!ParseUser.getCurrentUser().getObjectId().equals(user.getObjectId())) {
                ParseQuery<ParseInstallation> destination = destQuery.whereEqualTo("user", user);
                try {
                    String conversationName;
                    if (isChatWithSingle)
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

        }
        txtMessage.setText("");
        sendButton.setEnabled(false);
    }

    public static String getLastMsgOwner() {
        return lastMsgOwner;
    }

    public static String getLastMsgDate() {
        return lastMsgDate;
    }

    public static String getLastMsgTime() {
        return lastMsgTime;
    }

    public static void setLastMsgDate(String lastMsgDate) {
        Chat.lastMsgDate = lastMsgDate;
    }

    public static void setLastMsgOwner(String lastMsgOwner) {
        Chat.lastMsgOwner = lastMsgOwner;
    }

    public static void setLastMsgTime(String lastMsgTime) {
        Chat.lastMsgTime = lastMsgTime;
    }
}











