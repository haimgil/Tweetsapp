package il.tweetsapp.proj.tweetsapp.Objcets;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Haim on 3/26/2015.
 */
public class Conversation {

    private String groupName;
    private List<ParseUser> users;
    private List<Message> messages_list;

    public Conversation(String groupName) {
        this.groupName = groupName;
        this.users = new ArrayList<ParseUser>();
        messages_list= new ArrayList<Message>();
    }

    public void setMessages_list(List<Message> messages){
        messages_list = messages;
    }

    public void setUsers(List<String> usersNames){
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        for(int i=0; i < usersNames.size(); i++) {
            query = query.whereEqualTo("username", usersNames.get(i));
            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> parseUsers, ParseException e) {
                    if (e == null) {  // some results found
                        for (int i = 0; i < parseUsers.size(); i++)
                            users.add(parseUsers.get(i));
                    }
                }
            });
        }
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Boolean addUserToGroup(ParseUser parseUser) throws NullPointerException{
        Boolean succeeded = this.users.add(parseUser);
        if(!succeeded){
            Log.e("UserAdding","Adding user to group is failed!");
            throw  new NullPointerException();
        }else
            Log.d("UserAdding","User added to group successfully!");

        return succeeded;
    }


    public Boolean removeUserFromGroup(ParseUser parseUser) throws NullPointerException{
        Boolean succeeded = users.remove(parseUser);
        if(!succeeded){
            Log.e("UserRemoving","Removing user from group is failed!");
            throw new NullPointerException();
        }else
            Log.d("UserRemoving","User removed from group successfully!");

        return succeeded;
    }

    public Boolean addMessageToList(Message message){
        Boolean succeeded = this.messages_list.add(message);
        if(!succeeded){
            Log.e("MessageAdding", "Adding message to list failed!");
            throw new NullPointerException();
        }else
            Log.d("MessageAdding", "Message was added to list successfully");

        return succeeded;
    }

    public Boolean removeMessageFromList(Message message) throws NullPointerException{
        Boolean succeeded = this.messages_list.remove(message);
        if(!succeeded){
            Log.e("MessageAdding", "Removing message from list failed!");
            throw new NullPointerException();
        }else
            Log.d("MessageAdding", "Message was removed from list successfully");

        return succeeded;
    }
}
