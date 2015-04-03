package com.example.haim.tweetsapp.Objcets;

import android.util.Log;

import com.parse.ParseUser;

import java.util.ArrayList;

/**
 * Created by Haim on 3/26/2015.
 */
public class Conversation {

    private String groupName;
    private ArrayList<ParseUser> users;

    public Conversation(String groupName) {
        this.groupName = groupName;
        this.users = new ArrayList<ParseUser>();
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Boolean addUserToGroup(ParseUser parseUser){
        Boolean succeeded = this.users.add(parseUser);
        if(!succeeded){
            Log.e("UserAdding","Adding user to group is failed!");
        }else
            Log.d("UserAdding","User added to group successfully!");

        return succeeded;
    }


    public Boolean removeUserFromGroup(ParseUser parseUser){
        Boolean succeeded = users.remove(parseUser);
        if(!succeeded){
            Log.e("UserRemoving","Removing user from group is failed!");
        }else
            Log.d("UserRemoving","User removed from group successfully!");

        return succeeded;
    }
}
