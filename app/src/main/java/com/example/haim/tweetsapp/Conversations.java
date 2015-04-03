package com.example.haim.tweetsapp;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.haim.tweetsapp.Objcets.Conversation;

import java.util.ArrayList;
import java.util.List;


public class Conversations extends ActionBarActivity implements AdapterView.OnItemClickListener {

    private ListView conversationsListView;
    private List<Conversation> userConversations;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> conversationsNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations);

        conversationsListView = (ListView)findViewById(R.id.conversationsList);
        userConversations = getUserConversations();
        conversationsNames = getConversationsNames();
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_2, conversationsNames);
        conversationsListView.setAdapter(arrayAdapter);
        conversationsListView.setOnItemClickListener(this);
    }

    /**
     * Method that collects the names of all groups that user involved in.
     * @return ArrayList
     */
    private ArrayList<String> getConversationsNames() {
        ArrayList<String> names = new ArrayList<String>();
        // Get name of every group and adding it to the list.
        for (int i=0; i < userConversations.size(); i++){
            names.add(userConversations.get(i).getGroupName());
        }
        return names;
    }

    /**
     * Method that gets all conversations from db.
     * @return List
     */
    private List<Conversation> getUserConversations() {
        //TODO - Get all conversations from db
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
