package il.tweetsapp.proj.tweetsapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.ParseUser;


public class Main extends Activity {

    private boolean isLoggedIn;
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settings = getSharedPreferences("PrefsFile", MODE_PRIVATE);
        isLoggedIn = settings.getBoolean("login", false);


        Intent activity = null;
        if(ParseUser.getCurrentUser()!=null && isLoggedIn)
            activity = new Intent(this, Conversations.class);
        else {
            activity = new Intent(this, Login.class);
            activity.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        }

        startActivity(activity);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
