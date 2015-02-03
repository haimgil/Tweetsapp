package com.example.haim.tweetsapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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


public class Users_list extends Activity implements AdapterView.OnItemClickListener{

    ListView users_list;
    ArrayAdapter<String> arrayAdapter;
    List<ParseUser> usersObjects;
    ArrayList<String> usersNames;

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
        if(position < usersObjects.size()) {
            Chat.chatWith = usersObjects.get(position);
        }
        Intent iChat = new Intent(this, Chat.class);
        startActivity(iChat);
    }
}
