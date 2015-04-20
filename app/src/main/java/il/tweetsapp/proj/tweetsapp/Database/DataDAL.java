package il.tweetsapp.proj.tweetsapp.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import il.tweetsapp.proj.tweetsapp.helpers.Constants;

/**
 * Created by Haim on 4/10/2015.
 */
public class DataDAL {

    private SQLiteDatabase db;
    private TweetsAppDbHelper dbHelper;

    public DataDAL(Context context){
        dbHelper = new TweetsAppDbHelper(context);
    }


    /**
     * Adding new row to conversations table
     * @param conversationName - The conversation that contains those messages.
     * @return True if row added successfully, otherwise return false.
     */
    public boolean pushRowToConversationsTable(String conversationName){
        try{
            db = dbHelper.getWritableDatabase();
        }catch (SQLiteException e){
            Log.e("getWritableDatabase", "Failed!");
            return false;
        }

        // Save the values of a new row in ContentValues object
        ContentValues values = new ContentValues();
        values.put(Constants.COLUMN_CONVERSATION_NAME, conversationName);
        // Insert the row to the table and close the connection
        db.insertOrThrow(Constants.CONVERSATIONS_TABLE_NAME, null, values);
        db.close();

        return true;
    }


    /**
     * Adding new row to conversation users table
     * @param conversationName - The conversation that contains those messages.
     * @param userName - the user name to add.
     * @return True if row added successfully, otherwise return false.
     */
    public boolean pushRowToConversationUsersTable(String conversationName, String userName){
        try{
            db = dbHelper.getWritableDatabase();
        }catch (SQLiteException e){
            Log.e("getWritableDatabase", "Failed!");
            return false;
        }

        // Save the values of a new row in ContentValues object
        ContentValues values = new ContentValues();
        values.put(Constants.COLUMN_CONVERSATION_NAME, conversationName);
        values.put(Constants.COLUMN_USER_NAME, userName);
        // Insert the row to the table and close the connection
        db.insertOrThrow(Constants.USERS_IN_CONVERSATION_TABLE_NAME, null, values);
        db.close();

        return true;
    }

    /**
     * Adding new row to messages table
     * @param conversationName - The conversation that contains those messages.
     * @param msgTxt - Message text.
     * @param msgOwner - The user who send this message.
     * @param msgTime - Time the message has been sent.
     * @param msgRating - The message total rating.
     * @param msgRatingsQuantity - The number of ratings for this message.
     * @return True if row added successfully, otherwise return false.
     */
    public boolean pushRowToMessagesTable(String conversationName, String msgTxt, String msgOwner,
                                          String msgTime, int msgRating, int msgRatingsQuantity){
        try{
            db = dbHelper.getWritableDatabase();
        }catch (SQLiteException e){
            Log.e("getWritableDatabase", "Failed!");
            return false;
        }

        // Save the values of a new row in ContentValues object
        ContentValues values = new ContentValues();
        values.put(Constants.COLUMN_CONVERSATION_NAME, conversationName);
        values.put(Constants.COLUMN_MSG_TXT_NAME, msgTxt);
        values.put(Constants.COLUMN_MSG_OWNER_NAME, msgOwner);
        values.put(Constants.COLUMN_MSG_TIME_NAME, msgTime);
        values.put(Constants.COLUMN_MSG_RATING_NAME, msgRating);
        values.put(Constants.COLUMN_MSG_QUANTITY_RATINGS_NAME, msgRatingsQuantity);
        // Insert the row to the table and close the connection
        db.insertOrThrow(Constants.MESSAGES_TABLE_NAME, null, values);
        db.close();

        return true;
    }

    /**
     * Getting all conversations name from the database.
     * @return cursor to all existing conversations names
     */
    public Cursor pullConversationsNames(){
        try{
            db = dbHelper.getReadableDatabase();
        }catch (SQLiteException e){
            Log.e("getReadableDatabase", "Failed!");
            return null;
        }
        String[] columns = {Constants.COLUMN_CONVERSATION_NAME};

        Cursor cursor = db.query(Constants.CONVERSATIONS_TABLE_NAME,
                                    columns, // Columns to return from query.
                                        null, null, null, null, null);
        db.close();
        return cursor;
    }

    public Cursor pullSpecificConversationName(String conversationName){
        try{ // Open the database for reading data
            db = dbHelper.getReadableDatabase();
        }catch (SQLiteException e){ // Handling exception
            Log.e("getReadableDatabase", "Failed!");
            return null;
        }
        //Columns that get from preferably table
        String[] columns = {Constants.COLUMN_CONVERSATION_NAME};

        Cursor cursor = db.query(Constants.CONVERSATIONS_TABLE_NAME,
                            columns, // Columns to return from query.
                                Constants.COLUMN_CONVERSATION_NAME + "=?", new String[]{conversationName}, null, null, null);
        db.close();
        return cursor;
    }


    /**
     * Getting users names that participating in specific conversation from database
     * @param ConversationName - the conversation name to get user from.
     * @return cursor to all existing users in this conversation.
     */
    public Cursor pullConversationUsers(String ConversationName){
        try{ // Open the database for reading data
            db = dbHelper.getReadableDatabase();
        }catch (SQLiteException e){ // Handling exception
            Log.e("getReadableDatabase", "Failed!");
            return null;
        }

        Cursor cursor;
        //Columns that get from preferably table
        String [] columns = {Constants.COLUMN_USER_NAME};

        cursor = db.query(Constants.USERS_IN_CONVERSATION_TABLE_NAME,
                            columns,
                                Constants.COLUMN_CONVERSATION_NAME + "=?", new String[] {ConversationName}, null, null, null);
        db.close();
        return cursor;
    }


    /**
     * Getting all messages that has been sent in the specific conversation.
     * @param ConversationName - the conversation name to get all messages from.
     * @return cursor to all existing messages in this conversation.
     */
    public Cursor pullConversationMessages(String ConversationName){

        try{ // Open the database for reading data
            db = dbHelper.getReadableDatabase();
        }catch (SQLiteException e){ // Handling exception
            Log.e("getReadableDatabase", "Failed!");
            return null;
        }

        Cursor cursor;
        //Columns that get from preferably table
        String [] columns = {Constants.COLUMN_MSG_TXT_NAME, Constants.COLUMN_MSG_OWNER_NAME,
                                Constants.COLUMN_MSG_TIME_NAME, Constants.COLUMN_MSG_RATING_NAME,
                                    Constants.COLUMN_MSG_QUANTITY_RATINGS_NAME};

        cursor = db.query(Constants.MESSAGES_TABLE_NAME,
                            columns,
                             Constants.COLUMN_CONVERSATION_NAME + "=?", new String[] {ConversationName}, null, null, null);
        db.close();
        return cursor;
    }

}
