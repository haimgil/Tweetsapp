package il.tweetsapp.proj.tweetsapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import il.tweetsapp.proj.tweetsapp.Database.DataBL;
import il.tweetsapp.proj.tweetsapp.Objcets.Conversation;


public class Conversations extends ActionBarActivity implements AdapterView.OnItemClickListener {

    private ListView conversationsListView;
    private List<Conversation> userConversations;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> conversationsNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations);

        conversationsListView = (ListView)findViewById(R.id.conversationsList);
        userConversations = getUserConversations();
        conversationsNames = getConversationsNames();
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, conversationsNames);
        conversationsListView.setAdapter(arrayAdapter);
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

    }
}
