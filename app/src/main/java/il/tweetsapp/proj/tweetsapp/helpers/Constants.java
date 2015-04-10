package il.tweetsapp.proj.tweetsapp.helpers;

import android.provider.BaseColumns;

/**
 * Created by Haim on 4/10/2015.
 */
public class Constants implements BaseColumns{
    public static int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "tweetsappDbFile.db";
    public static final String CONVERSATIONS_TABLE_NAME =  "conversations";
    public static final String COLUMN_CONVERSATION_NAME = "conversation name"; // In case that its group conversation - the group name, otherwise the friend  username.
}
