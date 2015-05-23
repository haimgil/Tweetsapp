package il.tweetsapp.proj.tweetsapp;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import il.tweetsapp.proj.tweetsapp.Database.DataBL;
import il.tweetsapp.proj.tweetsapp.Objcets.Conversation;
import il.tweetsapp.proj.tweetsapp.helpers.Utils;
import il.tweetsapp.proj.tweetsapp.helpers.listItemAdapter;


public class Conversations extends ActionBarActivity implements AdapterView.OnItemClickListener {

    private ListView conversationsListView;
    private listItemAdapter cAdapter;
    private List<String> conversationsNames;
    private DataBL dataBL;

    public static HashMap<String, Boolean> isConvsOpen; // Save for every conversation if it now open.
    //Todo - create booleans for any conversation (is chat enabled at this time?)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations);
        dataBL = new DataBL(this);
        isConvsOpen = new LinkedHashMap<String, Boolean>();

        conversationsListView = (ListView)findViewById(R.id.conversationsList);
        conversationsNames = getConversationsNames();

        //initial all the conversations to be false (not open)
        for(String convName : conversationsNames){
            isConvsOpen.put(convName, false);
        }

        cAdapter = new listItemAdapter(this, conversationsNames);
        conversationsListView.setAdapter(cAdapter);
        conversationsListView.setOnItemClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // "Close" the conversation that just finished.
        for(String convName : conversationsNames){
            if(isConvsOpen.get(convName)){
                isConvsOpen.put(convName, false);
            }
        }
    }

    /**
     * Method that collects the names of all groups that user involved in.
     * @return ArrayList - contains the names of the conversation/s (user name/group name)
     */
    private List<String> getConversationsNames() {
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

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

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
                //Todo - Check internet connection
                actionIntent = new Intent(this, Users_list.class);
                break;
            case R.id.action_search:
                actionIntent = new Intent(Intent.ACTION_EDIT);

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
        boolean groupNameExist = true;
        try {
            group = groupQuery.getFirst();
        } catch (ParseException e) {
            // The conversation is only between 2 users so in some cases it is saved only in local db.
            groupNameExist = false;
        }
        if(groupNameExist) { // Some user create the group
            ParseRelation<ParseUser> groupUsers = group.getRelation("users");
            ParseQuery<ParseUser> usersQuery = groupUsers.getQuery();

            try {
                Chat.chatWith = usersQuery.find();
            } catch (ParseException e) {
                e.printStackTrace();
                Utils.alert(this, "Conversation users error!", "Some error occurred when trying to get users details of \"" +
                        conversationName + "\" conversation.\r\n");
                return;
            }
            //Remove the current user from the list that hold the users that will get the messages.
            Chat.chatWith.remove(ParseUser.getCurrentUser());
        }
        else { // The current user opened conversation with specific user or vice versa.
            ParseQuery<ParseUser> userQuery = ParseQuery.getQuery("User");
            userQuery = userQuery.whereEqualTo("username", conversationName);
            Chat.chatWith = new ArrayList<ParseUser>();
            try{
                Chat.chatWith.add(userQuery.getFirst());
            }catch (ParseException pe){
                Utils.alert(this, "Conversation error!", "Some error occurred when trying to open \"" + conversationName +
                        "\" conversation.\r\n" + "The conversation may be deleted");
                return;
            }
        }
        //"Open the conversation.
        isConvsOpen.put(conversationName, true);

        Intent iChat = new Intent(this, Chat.class);
        iChat.putExtra("Conversation name", conversationName);
        startActivity(iChat);
    }
}
