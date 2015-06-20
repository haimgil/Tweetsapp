package il.tweetsapp.proj.tweetsapp.Activities;

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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import il.tweetsapp.proj.tweetsapp.Database.DataBL;
import il.tweetsapp.proj.tweetsapp.R;
import il.tweetsapp.proj.tweetsapp.helpers.ListItemAdapter;
import il.tweetsapp.proj.tweetsapp.helpers.Utils;


public class Conversations extends ActionBarActivity implements AdapterView.OnItemClickListener {

    private ListView conversationsListView;
    private SearchView searchView;
    private ListItemAdapter cAdapter;
    private List<String> conversationsNames;
    private DataBL dataBL;
    public static Intent iChat;

    public static HashMap<String, Boolean> isConvsOpen = null; // Save for every conversation if it now open.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations);
        dataBL = new DataBL(this);
        if(isConvsOpen == null) // In some cases that HashMap initialized in TweetsBroadcastReceiver class.
            isConvsOpen = new LinkedHashMap<String, Boolean>();

        conversationsListView = (ListView)findViewById(R.id.conversationsList);
        conversationsNames = getConversationsNames();

        //initial all the conversations to be false (not open)
        for(String convName : conversationsNames){
            isConvsOpen.put(convName, false);
        }

        cAdapter = new ListItemAdapter(this, conversationsNames);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_conversations, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        if(searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(false);
        }

        final Context ctx = this;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(s.equals("")) {
                    conversationsListView.setAdapter(cAdapter);
                    return true;
                }
                List<String> searchList = new ArrayList<String>();
                // Check for every conversation name if contains the string 's'
                for(String convName : conversationsNames){
                    if(convName.toLowerCase().contains(s.toLowerCase())) {
                        searchList.add(convName);
                    }
                }
                ListItemAdapter searchAdapter = new ListItemAdapter(ctx, searchList);
                conversationsListView.setAdapter(searchAdapter);

                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
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
                if(Utils.isNetworkAvailable(this)) {
                    actionIntent = new Intent(this, GroupCreate.class);
                    actionIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                }else
                    Toast.makeText(this, getResources().getString(R.string.network_connection_error),Toast.LENGTH_LONG).show();
                break;
            case R.id.action_new_conversation:
                if(Utils.isNetworkAvailable(this))
                    actionIntent = new Intent(this, Users_list.class);
                else
                    Toast.makeText(this, getResources().getString(R.string.network_connection_error),Toast.LENGTH_LONG).show();
                break;
            case R.id.action_search:
                actionIntent = new Intent(Intent.ACTION_SEARCH);

                break;
        }
        if(actionIntent != null)
            startActivity(actionIntent);

        return true;// super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(!Utils.isNetworkAvailable(this)){ // Check that network connection is available.
            Toast.makeText(this, getResources().getString(R.string.network_connection_error), Toast.LENGTH_LONG).show();
            return;
        }
        iChat = new Intent(this, Chat.class);
        // Update the user to chat with.
        String conversationName = conversationsNames.get(position);
        Utils.setGroupUsersForChatting(this, conversationName);
        isConvsOpen.put(conversationName, true);
        iChat.putExtra("Conversation name", conversationName);
        startActivity(iChat);
    }
}
