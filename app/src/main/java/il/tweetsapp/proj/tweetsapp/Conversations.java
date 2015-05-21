package il.tweetsapp.proj.tweetsapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

import il.tweetsapp.proj.tweetsapp.Database.DataBL;
import il.tweetsapp.proj.tweetsapp.Objcets.Conversation;
import il.tweetsapp.proj.tweetsapp.helpers.Utils;
import il.tweetsapp.proj.tweetsapp.helpers.listItemAdapter;


public class Conversations extends ActionBarActivity implements AdapterView.OnItemClickListener {

    private ListView conversationsListView;
    private listItemAdapter cAdapter;
    private List<String> conversationsNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations);

        conversationsListView = (ListView)findViewById(R.id.conversationsList);
        conversationsNames = getConversationsNames();
        cAdapter = new listItemAdapter(this, conversationsNames);
        conversationsListView.setAdapter(cAdapter);
        conversationsListView.setOnItemClickListener(this);
    }

    /**
     * Method that collects the names of all groups that user involved in.
     * @return ArrayList - contains the names of the conversation/s (user name/group name)
     */
    private List<String> getConversationsNames() {
        DataBL dataBL = new DataBL(this);
        List<String> names = dataBL.getConversationsNames();

        return names;
    }

    /**
     * Method that gets all conversations from db.
     * @return List - contains all conversations of specific user.
     */
    private List<Conversation> getUserConversations() {
        //TODO - get conversations from db
        return null;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_conversations, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent actionIntent = null;
        switch (id) {
            case R.id.action_settings:

                break;
            case R.id.action_group_create:
                actionIntent = new Intent(this, GroupCreate.class);
                actionIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                break;
            case R.id.action_new_conversation:
                actionIntent = new Intent(this, Users_list.class);
                break;
            case R.id.action_search:

                break;
        }
        if(actionIntent != null)
            startActivity(actionIntent);

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Update the user to chat with.
        String conversationName = conversationsNames.get(position);
        ParseQuery<ParseObject> groupQuery = ParseQuery.getQuery("Group");
        groupQuery = groupQuery.whereEqualTo("name", conversationName);
        ParseObject group = null;
        try {
            group = groupQuery.getFirst();
        } catch (ParseException e) {
            e.printStackTrace();
            Utils.alert(this, "Conversation error!", "Some error occurred when trying to open \"" + conversationName + "\" conversation.\r\n" +
                    "The conversation may be deleted");
            return;
        }
        ParseRelation<ParseUser> groupUsers = group.getRelation("users");
        ParseQuery<ParseUser> usersQuery = groupUsers.getQuery();

        try {
            Chat.chatWith = usersQuery.find();
        } catch (ParseException e) {
            e.printStackTrace();
            Utils.alert(this, "Conversation users error!", "Some error occurred when trying to get users details of \"" +
                    conversationName +  "\" conversation.\r\n");
        }
        //Remove the current user from the list that hold the users that will get the messages.
        Chat.chatWith.remove(ParseUser.getCurrentUser());

        Intent iChat = new Intent(this, Chat.class);
        iChat.putExtra("Conversation name", conversationName);
        startActivity(iChat);
    }
}
