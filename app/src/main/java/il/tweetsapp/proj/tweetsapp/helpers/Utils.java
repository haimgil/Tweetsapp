package il.tweetsapp.proj.tweetsapp.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import il.tweetsapp.proj.tweetsapp.Activities.Chat;
import il.tweetsapp.proj.tweetsapp.Activities.Conversations;
import il.tweetsapp.proj.tweetsapp.Comments;
import il.tweetsapp.proj.tweetsapp.Database.DataBL;
import il.tweetsapp.proj.tweetsapp.Objcets.Comment;
import il.tweetsapp.proj.tweetsapp.Objcets.Message;
import il.tweetsapp.proj.tweetsapp.R;

/**
 * Created by Haim on 12/27/2014.
 */
public class Utils {
    public static Message menuClickedMessage = null;
    public static DataBL dataBL;
    public static int notificationId = 0;

    public static void alert(Context ctx, String title, String msg){
        AlertDialog alertDialog = new AlertDialog.Builder(ctx).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public static void printMessage(final Activity activity, Message msg, boolean isGroupCreateMsg, final String conversationName){
        final Context ctx = activity.getApplicationContext();
        final String convName = conversationName;
        dataBL = new DataBL(ctx);
        LinearLayout messages = (LinearLayout)activity.findViewById(R.id.messages);
        ImageButton menuButton;
        LinearLayout inflatedView;

        if(msg.getMessage_owner().equals(ParseUser.getCurrentUser().getUsername())) {
            //inflate view for current user messages
            inflatedView = (LinearLayout) View.inflate(activity.getApplicationContext(), R.layout.current_user_message_layout, null);

            //Handle in button that used for message menu
            menuButton = (ImageButton)inflatedView.findViewById(R.id.curr_user_menu);
            menuButton.setTag(msg);
            menuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menuClickedMessage = (Message)v.getTag(); // Get the message that it's menu just clicked

                    PopupMenu popup = new PopupMenu(ctx, v);
                    /** Adding menu items to the popUpMenu */
                    popup.getMenuInflater().inflate(R.menu.menu_current_user_msg, popup.getMenu());
                    /** Defining menu item click listener for the popup menu */
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            Toast.makeText(ctx, "You selected the action : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                            Toast.makeText(ctx, menuClickedMessage.getMessage_text(), Toast.LENGTH_LONG).show();
                            return true;
                        }
                    });
                    /** Showing the popup menu */
                    popup.show();
                }
            });

        }else if(isGroupCreateMsg) {
            //inflate view for group create message
            inflatedView = (LinearLayout) View.inflate(activity.getApplicationContext(), R.layout.group_create_message_layout, null);
        }else {
            //inflate view for other users messages
            inflatedView = (LinearLayout) View.inflate(activity.getApplicationContext(), R.layout.users_message_layout, null);

            //Handle in button that used for message menu
            menuButton = (ImageButton)inflatedView.findViewById(R.id.msg_user_menu);
            menuButton.setTag(msg);
            menuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menuClickedMessage = (Message)v.getTag(); // Get the message that it's menu just clicked

                    PopupMenu popup = new PopupMenu(ctx, v);
                    /** Adding menu items to the popUpMenu */
                    popup.getMenuInflater().inflate(R.menu.users_msg_menu, popup.getMenu());
                    /** Defining menu item click listener for the popup menu */
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            //Todo - handle this scenarios - Show all comments.
                            if(item.getTitle().equals("Add comment...")){
                                openCommentDialog(ctx, conversationName);
                            }
                            else if(item.getTitle().equals("Comments")){
                                Intent commentsIntent = new Intent().setClass(activity.getApplication(), Comments.class);
                                commentsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                                commentsIntent.putExtra("messageId", menuClickedMessage.getMessageId());
                                commentsIntent.putExtra("conversationName", conversationName);
                                activity.getApplication().startActivity(commentsIntent);

                                List<Comment> comments = dataBL.getMessageComments(conversationName, menuClickedMessage.getMessageId());
                                //Todo - remove Toast from here
                                Toast.makeText(ctx, comments.get(0).getCommentText() + " Classify is: " + comments.get(0).getCommentClassification() + "\r\n" + comments.get(1).getCommentText()+ " Classify is: " + comments.get(1).getCommentClassification() + "\r\n" +
                                comments.get(2).getCommentText()+ " Classify is: " + comments.get(2).getCommentClassification(), Toast.LENGTH_LONG).show();
                            }
                            return true;
                        }
                    });
                    /** Showing the popup menu */
                    popup.show();
                }
            });

        }
        //View newMsgLayout = inflater.inflate(R.layout.current_user_message_layout, messages, true);
        TextView msgTxtV = (TextView)inflatedView.findViewById(R.id.msgTextView);
        TextView timeTxtV = (TextView)inflatedView.findViewById(R.id.msgTimeText);
        TextView dateTxtV = (TextView)inflatedView.findViewById(R.id.msgDateTextView);
        msgTxtV.setText(msg.getMessage_owner() + ": " + msg.getMessage_text());
        timeTxtV.setText(msg.getTime());
        dateTxtV.setText(msg.getDate());


        messages.addView(inflatedView);
    }

    private static void openCommentDialog(Context ctx, final String conversationName) {
        // get prompts.xml view
        final Context context = ctx;
        LayoutInflater li = LayoutInflater.from(ctx);
        final View promptsView = li.inflate(R.layout.comment_dialod, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Chat.getInstance());

        // set prompts.xml to alertDialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userComment = (EditText) promptsView.findViewById(R.id.commentText);
        final RadioGroup classifyRadioGroup = (RadioGroup)promptsView.findViewById(R.id.rGroupClassification);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                int rButtonId = classifyRadioGroup.getCheckedRadioButtonId();
                                if (rButtonId < 0) { // No RadioButton is checked
                                    Toast.makeText(context, "You have to classify your comment!\r\n\r\t\r\t\r\t\r\t\r\tPositive/Negative",
                                            Toast.LENGTH_LONG).show();
                                    return;
                                }
                                RadioButton chosenButton = (RadioButton)promptsView.findViewById(rButtonId);
                                Comment comment = new Comment(userComment.getText().toString(),
                                        ParseUser.getCurrentUser().getUsername(),
                                        getCurrentDate(), getCurrentTime(), chosenButton.getText().toString());
                                // Notify that the comment has benn added.
                                Toast.makeText(context, "Your comment successfully added!\r\n " +
                                                         "Go to 'Comments' to watch all comments", Toast.LENGTH_LONG).show();

                                dataBL.addCommentToDbTable(conversationName, menuClickedMessage.getMessageId(), comment);
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }


    public static JSONObject generateMessageJSONObject(Message msg) throws JSONException {
        JSONObject object = new JSONObject();
        object.put("alert", msg.getMessage_text());
        object.put("msg_owner", msg.getMessage_owner());
        object.put("msg_time", msg.getTime());
        object.put("msg_date", msg.getDate());
        object.put("msg_gCreate", msg.getIsGroupCreateMsg());

        return object;
    }


    public static String getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String date = sdf.format(cal.getTime());
        return date;
    }


    public static String getCurrentTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String time = sdf.format(cal.getTime());
        return time;
    }

    public static void setGroupUsersForChatting(Context context, String conversationName){
        ParseQuery<ParseObject> groupQuery = ParseQuery.getQuery("Group");
        groupQuery = groupQuery.whereEqualTo("name", conversationName);
        ParseObject group = null;
        boolean groupNameExist = true;
        try {
            group = groupQuery.getFirst();
        } catch (ParseException e) {
            // The conversation is only between 2 users so in some cases it is saved only in local db.
            groupNameExist = false;
        }
        if(groupNameExist) { // Some user create the group
            ParseRelation<ParseUser> groupUsers = group.getRelation("users");
            ParseQuery<ParseUser> usersQuery = groupUsers.getQuery();

            try {
                Chat.chatWith = usersQuery.find();
            } catch (ParseException e) {
                e.printStackTrace();
                Utils.alert(context, "Conversation users error!", "Some error occurred when trying to get users details of \"" +
                        conversationName + "\" conversation.\r\n");
                return;
            }
            //Remove the current user from the list that hold the users that will get the messages.
            Chat.chatWith.remove(ParseUser.getCurrentUser());
        }
        else { // The current user opened conversation with specific user or vice versa.
            Chat.isChatWithSingle = true;
            if(Conversations.iChat != null)
                Conversations.iChat.putExtra("Chat with single", 0); // Update that the conversation is with single user (not a group)
            ParseQuery<ParseUser> userQuery = ParseQuery.getQuery(ParseUser.class);
            userQuery = userQuery.whereEqualTo("username", conversationName);
            Chat.chatWith = new ArrayList<ParseUser>();
            try{
                Chat.chatWith.add(userQuery.getFirst());
            }catch (ParseException pe){
                Utils.alert(context, "Conversation error!", "Some error occurred when trying to open \"" + conversationName +
                        "\" conversation.\r\n" + "The conversation may be deleted");
                return;
            }
        }
    }

    public static boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
