package com.example.haim.tweetsapp.helpers;

import android.provider.BaseColumns;

/**
 * Created by Haim on 4/10/2015.
 */
public class Constants implements BaseColumns{
    public static final String TABLE_NAME =  "conversations";
    public static final String COLUMN_NAME_ENTRY_ID = "id"; // In case that its group conversation - the group name, otherwise the friend  username.
    public static final String COLUMN_CONVERSATION_NAME = "conversation name";
}
