package il.tweetsapp.proj.tweetsapp.Database;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import il.tweetsapp.proj.tweetsapp.Objcets.Conversation;
import il.tweetsapp.proj.tweetsapp.Objcets.Message;

/**
 * Created by Haim on 4/20/2015.
 */
public class DataBL {

    private DataDAL dataDAL;

    public DataBL(Context context){
        dataDAL = new DataDAL(context);
    }

    public List<String> getConversationsNames(){
        List<Conversation> conversationsList = new ArrayList<Conversation>();

        Cursor conversationsNamesCursor = dataDAL.pullConversationsNames();
        List<String> conversationsNamesList = new ArrayList<String>();
        while(conversationsNamesCursor.moveToNext()){
            conversationsNamesList.add(conversationsNamesCursor.getString(0));
        }

        return conversationsNamesList;
    }


    public List<String> getConversationName(String conversationName){
        List<String> conversationsFound = new ArrayList<String>();
        Cursor cursor = dataDAL.pullSpecificConversationName(conversationName);
        while(cursor.moveToNext()){
            conversationsFound.add(cursor.getString(0));
        }

        return conversationsFound;
    }


    public Conversation getConversation(String conversationName){

        Conversation conversation = null;
        Cursor cursor;
        cursor = dataDAL.pullSpecificConversationName(conversationName);

        while(cursor.moveToNext()){
                conversation = new Conversation(cursor.getString(0));
        }

        List<String> conversationUsers = getConversationUsers(conversation.getGroupName());
        List<Message> conversationMessages = getConversationMessages(conversation.getGroupName());

        conversation.setUsers(conversationUsers);
        conversation.setMessages_list(conversationMessages);

        return conversation;
    }

    private List<Message> getConversationMessages(String conversationName) {
        List<Message> messages = new ArrayList<Message>();
        Cursor cursor = dataDAL.pullConversationMessages(conversationName);
        Message tmpMsg = null;
        while(cursor.moveToNext()){
            tmpMsg = new Message(cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getInt(3),
                    cursor.getInt(4));
            messages.add(tmpMsg);
        }
        return messages;
    }


    private List<String> getConversationUsers(String conversationName) {
        Cursor cursor = dataDAL.pullConversationUsers(conversationName);
        List<String> users = new ArrayList<String>();

        while(cursor.moveToNext()){
            users.add(cursor.getString(0));
        }
        return users;
    }


    public boolean addConversation(String conversationName){
        return dataDAL.pushRowToConversationsTable(conversationName);
    }

    public boolean addUserToDbTable(String conversationName, String userName){
        return dataDAL.pushRowToConversationUsersTable(conversationName, userName);
    }

    public boolean addMessageToDbTable(String conversationName, String msgTxt, String msgOwner,
                                            String msgTime, int msgTotalRating, int ratingsQuantity){
        return dataDAL.pushRowToMessagesTable(conversationName, msgTxt, msgOwner, msgTime, msgTotalRating, ratingsQuantity);
    }
}