package il.tweetsapp.proj.tweetsapp.helpers;

import android.provider.BaseColumns;

/**
 * Created by Haim on 4/10/2015.
 */
public class Constants implements BaseColumns{
    public static int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "tweetsappDbFile.db";
    public static final String CONVERSATIONS_TABLE_NAME =  "conversations";
    public static final String USERS_IN_CONVERSATION_TABLE_NAME = "conversation users";
    public static final String MESSAGES_TABLE_NAME = "messages";
    public static final String COLUMN_CONVERSATION_NAME = "conversation name"; // In case that its group conversation - the group name, otherwise the friend  username.
    public static final String COLUMN_USER_NAME = "user name";
    public static final String COLUMN_MSG_TXT_NAME = "text";
    public static final String COLUMN_MSG_OWNER_NAME = "owner";
    public static final String COLUMN_MSG_TIME_NAME = "time";
    public static final String COLUMN_MSG_RATING_NAME = "rating";
    public static final String COLUMN_MSG_QUANTITY_RATINGS_NAME = "quantity ratings";
}