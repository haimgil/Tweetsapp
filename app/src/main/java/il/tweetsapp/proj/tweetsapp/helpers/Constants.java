package il.tweetsapp.proj.tweetsapp.helpers;

import android.provider.BaseColumns;

/**
 * Created by Haim on 4/10/2015.
 */
public class Constants implements BaseColumns{
    public static int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "tweetsappDbFile.db";
    public static final String CONVERSATIONS_TABLE_NAME =  "conversations";
    public static final String USERS_IN_CONVERSATION_TABLE_NAME = "conversation_users";
    public static final String MESSAGES_TABLE_NAME = "messages";
    public static final String COMMENTS_TABLE_NAME = "comments";
    public static final String COLUMN_MESSAGES_ID_NAME = "messages_id";
    public static final String COLUMN_OWNER_MSG_ID_NAME = "owner_msg_id";
    public static final String COLUMN_CLASSIFICATION_NAME = "classification";
    public static final String COLUMN_CONVERSATION_NAME = "conversation_name"; // In case that its group conversation - the group name, otherwise the friend  username.
    public static final String COLUMN_USER_NAME = "user_name";
    public static final String COLUMN_MSG_TXT_NAME = "text";
    public static final String COLUMN_MSG_OWNER_NAME = "owner";
    public static final String COLUMN_MSG_TIME_NAME = "time";
    public static final String COLUMN_MSG_DATE_NAME = "date";
    public static final String COLUMN_MSG_BOOLEAN_NAME = "group_create_message";
    public static final String COLUMN_COMMENT_TEXT_NAME = "text";
    public static final String COLUMN_COMMENT_OWNER_NAME = "owner";
}
