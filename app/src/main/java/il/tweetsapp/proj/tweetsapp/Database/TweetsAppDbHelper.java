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
        db.execSQL("CREATE TABLE IF NOT EXISTS " + Constants.CONVERSATIONS_TABLE_NAME + "(" +
                Constants._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Constants.COLUMN_CONVERSATION_NAME + " TEXT NOT NULL);");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + Constants.USERS_IN_CONVERSATION_TABLE_NAME + "(" +
                Constants._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Constants.COLUMN_CONVERSATION_NAME + " TEXT NOT NULL, " +
                Constants.COLUMN_USER_NAME + " TEXT NOT NULL);");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + Constants.MESSAGES_TABLE_NAME + "(" +
                Constants._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Constants.COLUMN_CONVERSATION_NAME + " TEXT NOT NULL, " +
                Constants.COLUMN_MSG_TXT_NAME + " TEXT NOT NULL, " +
                Constants.COLUMN_MSG_OWNER_NAME + " TEXT NOT NULL, " +
                Constants.COLUMN_MSG_TIME_NAME + " TEXT NOT NULL, " +
                Constants.COLUMN_MSG_DATE_NAME + " TEXT NOT NULL, " +
                Constants.COLUMN_OWNER_MSG_ID_NAME + " INTEGER NOT NULL, " +
                Constants.COLUMN_MSG_BOOLEAN_NAME + " INTEGER);");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + Constants.COMMENTS_TABLE_NAME + "(" +
                Constants._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Constants.COLUMN_COMMENT_TEXT_NAME + " TEXT NOT NULL, " +
                Constants.COLUMN_COMMENT_OWNER_NAME + " TEXT NOT NULL, " +
                Constants.COLUMN_MSG_TIME_NAME + " TEXT NOT NULL, " +
                Constants.COLUMN_MSG_DATE_NAME + " TEXT NOT NULL, " +
                Constants.COLUMN_CLASSIFICATION_NAME + " TEXT NOT NULL, " +
                Constants.COLUMN_CONVERSATION_NAME + " TEXT NOT NULL, " +
                Constants.COLUMN_MESSAGES_ID_NAME + " INTEGER NOT NULL);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Constants.CONVERSATIONS_TABLE_NAME);
    }
}
