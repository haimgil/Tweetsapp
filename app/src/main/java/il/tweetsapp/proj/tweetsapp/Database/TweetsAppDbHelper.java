package il.tweetsapp.proj.tweetsapp.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import il.tweetsapp.proj.tweetsapp.helpers.Constants;

/**
 * Created by Haim on 4/10/2015.
 */
public class TweetsAppDbHelper extends SQLiteOpenHelper {

    public  TweetsAppDbHelper(Context context){
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Constants.CONVERSATIONS_TABLE_NAME + "(" +
                Constants._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Constants.COLUMN_CONVERSATION_NAME + " TEXT NOT NULL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Constants.CONVERSATIONS_TABLE_NAME);
    }
}
