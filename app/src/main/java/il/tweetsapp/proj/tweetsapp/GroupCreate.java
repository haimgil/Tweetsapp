package il.tweetsapp.proj.tweetsapp;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
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
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;


public class GroupCreate extends ActionBarActivity {

    private TextView groupCreateTextView;
    private MyCustomAdapter dataAdapter = null;
    private Dialog addUserDialog;
    private List<ParseUser> newGroupUsers;

    /******************************************************/
    final HashMap<ParseUser, Boolean> hashMap = new LinkedHashMap<ParseUser, Boolean>();
    /*****************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_create);

        newGroupUsers = new ArrayList<ParseUser>();
        addUserDialog = null;
        groupCreateTextView = (TextView) findViewById(R.id.groupCreateButton);
        Typeface myFont = Typeface.createFromAsset(getAssets(), "fonts/FFF_Tusj.ttf");
        groupCreateTextView.setTypeface(myFont);
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

    public void onAddUsersClick(View view) {

        addUserDialog = openUserSelectDialog();
        checkButtonClick();

    }

    private Dialog openUserSelectDialog() {
        Dialog addUserDialog = new Dialog(this);
        addUserDialog.setTitle("Select user");
        addUserDialog.setContentView(R.layout.listview_dialog_layout);
        ArrayList<ParseUser> usersList = getUsersObjects();
        insertToHashMap(usersList);
        dataAdapter = new MyCustomAdapter(this, R.layout.row_listview_dialog_layout, usersList);
        ListView listView = (ListView)addUserDialog.findViewById(R.id.dialogListView);
        listView.setAdapter(dataAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ParseUser user = (ParseUser) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),
                        "Clicked on parseUser: " + user.getUsername(),
                        Toast.LENGTH_LONG).show();
            }
        });

        addUserDialog.show();
        return addUserDialog;
    }

    private void insertToHashMap(ArrayList<ParseUser> usersList) {
        for(int i=0; i < usersList.size(); i++){
            hashMap.put(usersList.get(i), false);
        }
    }

    private void checkButtonClick() {

        Button myButton = (Button) addUserDialog.findViewById(R.id.dialogConfirm);
        myButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                StringBuffer responseText = new StringBuffer();
                responseText.append("The following were selected...\n");

                ArrayList<ParseUser> usersList = dataAdapter.usersList;
                for(int i=0; i < usersList.size(); i++){
                    ParseUser parseUser = usersList.get(i);
                    if(hashMap.get(parseUser)){
                        responseText.append("\n" + parseUser.getUsername());
                    }
                }
                hashMap.clear();
                Toast.makeText(getApplicationContext(),
                        responseText, Toast.LENGTH_LONG).show();
                addUserDialog.dismiss();
            }
        });
    }

    public void onCreateGroupClick(View view) {

    }

    /**
     * Getting list of users objects.
     * User object contains the all details that may be necessary.
     */
    private ArrayList<ParseUser> getUsersObjects() {
        ParseQuery<ParseUser> allUsers = ParseQuery.getQuery(ParseUser.class);
        allUsers = allUsers.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        try {
            return new ArrayList<ParseUser>(allUsers.find());

        }catch (ParseException pe){
            Log.d("com.parse.ParseException", "Saving users objects failed");
            return null;
        }
    }


    private class MyCustomAdapter extends ArrayAdapter<ParseUser> {
        private ArrayList<ParseUser> usersList;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<ParseUser> usersList) {
            super(context, textViewResourceId, usersList);
            this.usersList = new ArrayList<ParseUser>();
            this.usersList.addAll(usersList);
        }

        private class ViewHolder {
            CheckBox username;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.row_listview_dialog_layout, null);

                holder = new ViewHolder();
                holder.username = (CheckBox) convertView.findViewById(R.id.checkBox);
                convertView.setTag(holder);

                holder.username.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View view) {
                        CheckBox cb = (CheckBox) view ;
                        ParseUser parseUser= (ParseUser) cb.getTag();
                        Toast.makeText(getApplicationContext(),
                                "Clicked on Checkbox: " + cb.getText() +
                                        " is " + cb.isChecked(),
                                Toast.LENGTH_LONG).show();
                        hashMap.put(parseUser, cb.isChecked());
                    }
                });
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            ParseUser parseUser = usersList.get(position);
            holder.username.setText(parseUser.getUsername());
            holder.username.setTag(parseUser);

            return convertView;
        }
    }

}