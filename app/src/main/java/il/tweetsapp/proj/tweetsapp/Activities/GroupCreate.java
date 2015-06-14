package il.tweetsapp.proj.tweetsapp.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import il.tweetsapp.proj.tweetsapp.Database.DataBL;
import il.tweetsapp.proj.tweetsapp.Objcets.Message;
import il.tweetsapp.proj.tweetsapp.R;
import il.tweetsapp.proj.tweetsapp.helpers.Utils;


public class GroupCreate extends ActionBarActivity {

    private GroupCreateAdapter dataAdapter = null;
    private List<ParseUser> newGroupUsers;
    private ListView usersListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_create);
        final Context ctx = this;

        usersListView = (ListView)findViewById(R.id.users_for_group);
        newGroupUsers = new ArrayList<ParseUser>();


        Button groupCreateButton = (Button) findViewById(R.id.groupCreateButton);
        Typeface myFont = Typeface.createFromAsset(getAssets(), "fonts/Top_Secret.ttf");
        groupCreateButton.setTypeface(myFont);
        dataAdapter = new GroupCreateAdapter(this, R.layout.row_listview_dialog_layout, R.id.usernameCheckedTView, getUsersObjects());

        SearchView userSearch = (SearchView)findViewById(R.id.userSearchView);
        userSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(s.equals("")) {
                    dataAdapter = new GroupCreateAdapter(ctx, R.layout.row_listview_dialog_layout,
                                                                 R.id.usernameCheckedTView, getUsersObjects());
                    usersListView.setAdapter(dataAdapter);
                    return true;
                }
                List<ParseUser> searchList = new ArrayList<ParseUser>();
                // Check for every conversation name if contains the string 's'
                List<ParseUser> users = getUsersObjects();
                for(ParseUser user : users){
                    if(user.getUsername().contains(s)) {
                        searchList.add(user);
                    }
                }
                dataAdapter = new GroupCreateAdapter(ctx, R.layout.row_listview_dialog_layout,R.id.usernameCheckedTView, searchList);
                usersListView.setAdapter(dataAdapter);

                return true;
            }
        });
        usersListView.setAdapter(dataAdapter);

        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dataAdapter.toggleChecked(position);

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group_create, menu);
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



    public void onCreateGroupClick(View view) {
        EditText groupNameEditT = (EditText)findViewById(R.id.groupNameFiled);
        final String groupName = groupNameEditT.getText().toString().trim();
        // Checks that group name was entered.
        if(groupName.length() < 1){
            Toast.makeText(this, "Group name must contains at least 1 character!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validates that some users has been selected.
        if(newGroupUsers.size() < 1){
            Toast.makeText(this, "At least 1 user is required for create a new group!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Creating new group instance in Parse and update the values.
        final ParseObject group = new ParseObject("Group");
        group.put("name",groupName);
        group.put("owner",ParseUser.getCurrentUser());
        ParseRelation<ParseUser> groupUsers = group.getRelation("users");
        groupUsers.add(ParseUser.getCurrentUser());
        for(ParseUser newUser : newGroupUsers){
            groupUsers.add(newUser);
        }

        final Context ctx = this;

        group.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e!=null){
                    // Some error occurred.
                    Toast.makeText(ctx, "Error occurred in group create progress", Toast.LENGTH_LONG).show();
                    return;
                }else{
                    (new AsyncTask<ParseObject,Void,Void>(){
                        ProgressDialog pDialog;

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            pDialog = new ProgressDialog(ctx);
                            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            pDialog.setMessage("Please wait...");
                            pDialog.show();

                        }

                        @Override
                        protected Void doInBackground(ParseObject... groups) {



                            ParseObject group = groups[0];

                            // save group in local DB
                            DataBL dbObject = new DataBL(ctx);
                            dbObject.addConversation(groupName);
                            for(ParseUser newUser :newGroupUsers){
                                dbObject.addUserToDbTable(groupName, newUser.getUsername());
                            }
                            //Push automatic message to local db of the group creator about creating new group.
                            Message message = new Message("You just created the group \"" + groupName + "\"!", groupName,
                                                            Utils.getCurrentTime(), Utils.getCurrentDate(), true, 0, 0);
                            dbObject.addMessageToDbTable(message, groupName);

                            // send notification to group members
                            String msg = "You have been added to group \"" + groupName + "\" by " + ParseUser.getCurrentUser().getUsername();
                            Message gCreateNotify = new Message(msg, groupName,
                                    Utils.getCurrentTime(),Utils.getCurrentDate(), true, 0, 0);
                            ParseQuery<ParseInstallation> destination;
                            try {
                                JSONObject jsonObject = Utils.generateMessageJSONObject(gCreateNotify);
                                jsonObject.put("groupID", group.getObjectId());
                                for(ParseUser user : newGroupUsers) {
                                    destination = ParseQuery.getQuery(ParseInstallation.class);
                                    destination = destination.whereEqualTo("user", user);
                                    ParsePush.sendDataInBackground(jsonObject, destination);
                                }
                            }catch (JSONException je){
                                onCancelled();
                                je.printStackTrace();
                            }
                            try {
                                Thread.sleep(2000); //2 second.
                            } catch(InterruptedException ex) {
                                onCancelled();
                                Thread.currentThread().interrupt();
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            if(pDialog.isShowing())
                                pDialog.dismiss();

                            Intent iChat = new Intent(ctx, Chat.class);
                            iChat.putExtra("Conversation name",groupName);
                            iChat.putExtra("Group created successfully",0);
                            startActivity(iChat);
                        }

                        @Override
                        protected void onCancelled() {
                            Utils.alert(ctx.getApplicationContext(), "New group error", "Group creating failed. Try again!");
                        }
                    }).execute(group);
                }
            }
        });
    }

    /**
     * Getting list of users objects.
     * User object contains the all details that may be necessary.
     */
    private List<ParseUser> getUsersObjects() {
        ParseQuery<ParseUser> allUsers = ParseQuery.getQuery(ParseUser.class);
        allUsers = allUsers.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        try {
            List<ParseUser> users = allUsers.find();
            return users;

        }catch (ParseException pe){
            Log.d("com.parse.ParseException", "Saving users objects failed");
            Utils.alert(this, "Import users error", "Some error occurred while trying to import users from server!");
            // Return empty list instead null that will cause to crashing.
            return new ArrayList<ParseUser>();
        }
    }

    private class GroupCreateAdapter extends ArrayAdapter<ParseUser> {

        private HashMap<ParseUser, Integer> checkedUsers = new HashMap<ParseUser, Integer>();
        private HashMap<Integer, Boolean> checkedPositions = new HashMap<Integer, Boolean>();
        private HashMap<ParseUser, Boolean> checkedUser = new LinkedHashMap<ParseUser, Boolean>();

        private List<ParseUser> users;
        private ParseUser user;

        public GroupCreateAdapter(Context context, int resource, int textViewResourceId, List<ParseUser> users) {
            super(context, resource, textViewResourceId, users);

            boolean userOnGroupList = false;
            for(ParseUser user : users){
                for(ParseUser user1 : newGroupUsers) {
                    if (user.getObjectId().equals(user1.getObjectId())) {
                        userOnGroupList = true;
                        break;
                    }
                }
                if(userOnGroupList){
                    checkedUser.put(user, true);
                    userOnGroupList = false;
                }
                else
                    checkedUser.put(user, false);
            }
            this.users = users;
        }

        public void toggleChecked(int position){
            ParseUser user = users.get(position);
            //int userPosition = checkedUsers.get(user);
            if(checkedUser.get(user)){
                checkedUser.put(user, false);
                for(int i=0; i < newGroupUsers.size(); i++) {
                    if(user.getObjectId().equals(newGroupUsers.get(i).getObjectId())) {
                        newGroupUsers.remove(i);
                        break;
                    }
                }
            }else{
                checkedUser.put(user, true);
                newGroupUsers.add(user);
            }

            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;

            if(row==null){
                LayoutInflater inflater=getLayoutInflater();
                row=inflater.inflate(R.layout.row_listview_dialog_layout, parent, false);
            }

            CheckedTextView checkedTextView = (CheckedTextView)row.findViewById(R.id.usernameCheckedTView);
            checkedTextView.setText(users.get(position).getUsername());

            ParseUser user = users.get(position);
            Boolean checked = checkedUser.get(user);
            if (checked != null) {
                checkedTextView.setChecked(checked);
            }

            return row;
        }

    }

}
