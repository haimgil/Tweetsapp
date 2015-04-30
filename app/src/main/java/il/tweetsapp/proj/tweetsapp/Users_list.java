package il.tweetsapp.proj.tweetsapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class Users_list extends ActionBarActivity implements AdapterView.OnItemClickListener{

    private ListView users_list;
    private ArrayAdapter<String> arrayAdapter;
    private List<ParseUser> usersObjects;
    private ArrayList<String> usersNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_users_list);
        users_list = (ListView) findViewById(R.id.usersList);
        usersNames = new ArrayList<String>();
        getUsersObjects();
        getUsersNames();
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, usersNames);
        users_list.setAdapter(arrayAdapter);
        users_list.setOnItemClickListener(this);

    }

    /**
     * Getting the users names from the user objects.
     * The user names using for showing in the users List.
     */
    private void getUsersNames() {
        int i;
        for(i=0; i < usersObjects.size(); i++){
            usersNames.add(usersObjects.get(i).getUsername());
        }
        if(i>1)
            usersNames.add("All Users"); // Option to chatting with all users.
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
        if(position < usersObjects.size()) {
            Chat.chatWith = usersObjects.get(position);
        }
        Intent iChat = new Intent(this, Chat.class);
        //TODO - Remove this code after debug
        iChat.putExtra("Conversation name", Chat.chatWith.getUsername());
        //end debug
        startActivity(iChat);
    }
}
