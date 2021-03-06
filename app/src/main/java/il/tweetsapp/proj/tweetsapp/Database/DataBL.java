package il.tweetsapp.proj.tweetsapp.Database;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import il.tweetsapp.proj.tweetsapp.Objcets.Comment;
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
        dataDAL.closeDb();
        if(conversation != null) {
            List<String> conversationUsers = getConversationUsers(conversation.getGroupName());
            List<Message> conversationMessages = getConversationMessages(conversation.getGroupName());

            conversation.setUsers(conversationUsers);
            conversation.setMessages_list(conversationMessages);
        }

        return conversation;
    }

    public List<Message> getConversationMessages(String conversationName) {
        List<Message> messages = new ArrayList<Message>();
        Cursor cursor = dataDAL.pullConversationMessages(conversationName);
        Message tmpMsg = null;
        while(cursor.moveToNext()){
            tmpMsg = new Message(cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    (cursor.getInt(4)==1)? true : false,
                    cursor.getLong(5),
                    cursor.getLong(6));
            messages.add(tmpMsg);
        }
        dataDAL.closeDb();
        return messages;
    }


    public List<String> getConversationUsers(String conversationName) {
        Cursor cursor = dataDAL.pullConversationUsers(conversationName);
        List<String> users = new ArrayList<String>();

        while(cursor.moveToNext()){
            users.add(cursor.getString(0));
        }
        dataDAL.closeDb();
        return users;
    }

    public Message getMessageById(String conversationName, long messageId){
        Cursor cursor = dataDAL.pullMessageById(conversationName, messageId);
        if(cursor.moveToNext()) {
            Message message = new Message(cursor.getString(0),
                                          cursor.getString(1),
                                          cursor.getString(2),
                                          cursor.getString(3),
                                          (cursor.getInt(4)==1)? true : false,
                                          cursor.getLong(5),
                                          cursor.getLong(6));
            return message;
        }
        Log.e("GetMessageById", "Conversation name or messageId is incorrect!");
        return null;
    }

    /**
     * Get the message ID of the current user by the message owner and the message owner id.
     * @param conversationName
     * @param msgOwner
     * @param ownerMsgId
     * @return the local message id or -1 if some error occurred.
     */

    public long getMessageLocalId(String conversationName, String msgOwner, long ownerMsgId){
        long msgId;
        Cursor cursor = dataDAL.pullLocalMsgId(conversationName, msgOwner, ownerMsgId);

        if(cursor.moveToNext()){
            msgId = cursor.getLong(0);
            return msgId;
        }

        return -1;
    }

    public List<Comment> getMessageComments(String conversationName, long messageId){
        Cursor cursor = dataDAL.pullMessageComments(conversationName, messageId);
        List<Comment> comments = new ArrayList<Comment>();

        Comment commentToAdd;
        while(cursor.moveToNext()){
            commentToAdd = new Comment(cursor.getString(0),
                                       cursor.getString(1),
                                       cursor.getString(2),
                                       cursor.getString(3),
                                       cursor.getString(4));
            comments.add(commentToAdd);
        }
        return comments;
    }


    public boolean addConversation(String conversationName){
        return dataDAL.pushRowToConversationsTable(conversationName);
    }

    public boolean addUserToDbTable(String conversationName, String userName){
        return dataDAL.pushRowToConversationUsersTable(conversationName, userName);
    }

    public long addMessageToDbTable(Message message, String conversationName){
        return dataDAL.pushRowToMessagesTable(conversationName, message.getMessage_text(), message.getMessage_owner(),
                                                message.getTime(), message.getDate(),
                                                    (message.getIsGroupCreateMsg())? 1:0, message.getOwnerMessageId());
    }

    public boolean addCommentToDbTable(String conversationName, long messageId, Comment comment){
        return dataDAL.pushRowToCommentsTable(conversationName, messageId, comment);
    }

    public int updateMsgId(long msgId){
        return dataDAL.updateRowInMessageTable(msgId);
    }
}


















