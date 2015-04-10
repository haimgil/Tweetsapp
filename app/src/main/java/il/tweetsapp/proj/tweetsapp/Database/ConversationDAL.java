package il.tweetsapp.proj.tweetsapp.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;

import il.tweetsapp.proj.tweetsapp.helpers.Constants;

/**
 * Created by Haim on 4/10/2015.
 */
public class ConversationDAL {

    private SQLiteDatabase db;
    private TweetsAppDbHelper dbHelper;

    public ConversationDAL(Context context){
        dbHelper = new TweetsAppDbHelper(context);
    }

    public Boolean insertNewRowToTable(String conversationName){
        db = dbHelper.getWritableDatabase();

        // Save the values of a new row in ContentValues object
        ContentValues values = new ContentValues();
        values.put(Constants.COLUMN_CONVERSATION_NAME, conversationName);

        // Insert the row to the table and.
        try {
            db.insertOrThrow(Constants.CONVERSATIONS_TABLE_NAME, null, values);
        }catch (SQLException){ // in case that some exception occurs, handling it and close the db.
            Log.e("InsertNewRowToTable", "Insert new row failed!");
            db.close();
            return false;
        }
        db.close();
        return true;
    }

    public Cursor getConversations(){
        db = dbHelper.getReadableDatabase();
        String[] columns = {Constants.COLUMN_CONVERSATION_NAME};

        Cursor cursor = db.query(Constants.CONVERSATIONS_TABLE_NAME,
                                    columns, // Columns to return from query.
                                        null, null, null, null, null);
        db.close();
        return cursor;
    }

    public Cursor getSpecificConversation(String conversationName){
        db = dbHelper.getReadableDatabase();
        String[] columns = {Constants.COLUMN_CONVERSATION_NAME};

        Cursor cursor = db.query(Constants.CONVERSATIONS_TABLE_NAME,
                            columns, // Columns to return from query.
                                Constants.COLUMN_CONVERSATION_NAME + "=?", new String[]{conversationName}, null, null, null);
        db.close();
        return cursor;
    }
}
