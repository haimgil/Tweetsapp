package il.tweetsapp.proj.tweetsapp.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
        db.insertOrThrow(Constants.CONVERSATIONS_TABLE_NAME, null, values);
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
