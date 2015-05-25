package il.tweetsapp.proj.tweetsapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import il.tweetsapp.proj.tweetsapp.Database.DataBL;
import il.tweetsapp.proj.tweetsapp.R;
import il.tweetsapp.proj.tweetsapp.helpers.ListItemAdapter;


public class Users_list extends ActionBarActivity implements AdapterView.OnItemClickListener{

    private ListView users_list;
    private ListItemAdapter cAdapter;
    private List<ParseUser> usersObjects;
    private List<String> usersNames;
    private DataBL dataBL;

    //Todo - Handle in search option

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_users_list);
        users_list = (ListView) findViewById(R.id.usersList);
        usersNames = new ArrayList<String>();
        dataBL = new DataBL(this);
        getUsersObjects();
        getUsersNames();
        cAdapter = new ListItemAdapter(this, usersNames);
        users_list.setAdapter(cAdapter);
        users_list.setOnItemClickListener(this);

    }

    /**
     * Getting the users names from the user objects.
     * The user names using for showing in the users List.
     */
    private void getUsersNames() {
        int i;
        for(i=0; i < usersObjects.size(); i++)
            usersNames.add(usersObjects.get(i).getUsername());
    }

    /**
     * Getting list of users objects.
     * User object contains the all details that may be necessary.
     */
    private void getUsersObjects() {
        ParseQuery<ParseUser> allUsers = ParseQuery.getQuery(ParseUser.class);
        allUsers = allUsers.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        try {
            usersObjects = allUsers.find();
        }catch (ParseException pe){
            Log.d("com.parse.ParseException", "Saving users objects failed");
            return;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_users_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        // Update the user to chat with.
        ParseUser user = usersObjects.get(position);
        Chat.chatWith = new ArrayList<ParseUser>();
        Chat.chatWith.add(user);

        String conversationName = user.getUsername();
        //Check if the conversation is exist and in case not, create it.
        if(dataBL.getConversation(conversationName) == null) {
            dataBL.addConversation(conversationName);
            dataBL.addUserToDbTable(conversationName, user.getUsername());
        }

        Conversations.isConvsOpen.put(conversationName, true);
        Intent iChat = new Intent(this, Chat.class);
        iChat.putExtra("Conversation name", user.getUsername());
        iChat.putExtra("Chat with single", true);
        startActivity(iChat);
    }
}
