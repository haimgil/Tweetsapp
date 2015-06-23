package il.tweetsapp.proj.tweetsapp.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
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
import il.tweetsapp.proj.tweetsapp.Activities.Comments;
import il.tweetsapp.proj.tweetsapp.Activities.Conversations;
import il.tweetsapp.proj.tweetsapp.Database.DataBL;
import il.tweetsapp.proj.tweetsapp.Objcets.Comment;
import il.tweetsapp.proj.tweetsapp.Objcets.Message;
import il.tweetsapp.proj.tweetsapp.R;

/**
 * Created by Haim on 12/27/2014.
 */
public class Utils {

    private static String POSITIVE_WORDS[] = {"best show ever", "very good", "very exciting", "excellent", "delightful", "entertaining", "fine",
                                            "heartbreaking", "astonishing", "wonderful", "amazing", "shocking", "magical", "awesome",
                                        "ingenious", "brilliant", "enjoying", "great", "epic", "surprising", "fun", "good", "not bad",
                                    "hypnotist"};

    private static String NEGATIVE_WORDS[] = {"worst show ever", "very bad", "disappointing", "downfall", "ridiculous", "dreadful", "awful",
                                    "suck", "too long", "pointless", "predictable", "boring", "shit", "bad", "not so good", "disaster",
                                "terrible", "poorly", "worse"};

    public static int MAX_CHARACTERS_IN_MESSAGE = 60;
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

    public static void printMessage(final Activity activity, final Message msg, boolean isGroupCreateMsg, final String conversationName,
                                                        String lastMsgOwner, String lastMsgDate, String lastMsgTime){
        final Context ctx = activity.getApplicationContext();
        final String convName = conversationName;

        TextView msgTxtV;
        TextView timeTxtV;
        TextView dateTxtV;

        dataBL = new DataBL(ctx);
        LinearLayout messages = (LinearLayout)activity.findViewById(R.id.messages);
        ImageButton menuButton;
        LinearLayout inflatedView;

        if(msg.getMessage_owner().equals(ParseUser.getCurrentUser().getUsername())) {
            //inflate view for current user messages
            inflatedView = (LinearLayout) View.inflate(activity.getApplicationContext(), R.layout.current_user_message_layout, null);

            msgTxtV = (TextView)inflatedView.findViewById(R.id.msgTextView);
            timeTxtV = (TextView)inflatedView.findViewById(R.id.msgTimeText);
            dateTxtV = (TextView)inflatedView.findViewById(R.id.msgDateTextView);
            msgTxtV.setText(msg.getMessage_text());

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
                            if(item.getTitle().equals("Comments")){
                                Intent commentsIntent = new Intent().setClass(activity.getApplication(), Comments.class);
                                commentsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                                commentsIntent.putExtra("messageId", menuClickedMessage.getMessageId());
                                commentsIntent.putExtra("conversationName", conversationName);
                                activity.getApplication().startActivity(commentsIntent);

                                List<Comment> comments = dataBL.getMessageComments(conversationName, menuClickedMessage.getMessageId());

                            }

                            else if(item.getTitle().equals("Should I?")){
                                (new AsyncTask<Void, Void, Void>(){
                                    ProgressDialog pDialog;
                                    String recommendation;
                                    @Override
                                    protected void onPreExecute() {
                                        super.onPreExecute();
                                        pDialog = new ProgressDialog(Chat.getInstance());
                                        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                        pDialog.setMessage("Please wait while we generate your recommendation...");
                                        pDialog.show();
                                    }

                                    @Override
                                    protected void onPostExecute(Void aVoid) {
                                        super.onPostExecute(aVoid);
                                        pDialog.dismiss();
                                        openRecommendationDialog(Chat.getInstance(), recommendation);
                                    }

                                    @Override
                                    protected Void doInBackground(Void... params) {
                                        recommendation = generateRecommendation(ctx, conversationName, menuClickedMessage.getMessageId());
                                        return null;
                                    }
                                }).execute();
                                generateRecommendation(ctx, conversationName, menuClickedMessage.getMessageId());
                            }
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
            msgTxtV = (TextView)inflatedView.findViewById(R.id.msgTextView);
            timeTxtV = (TextView)inflatedView.findViewById(R.id.msgTimeText);
            dateTxtV = (TextView)inflatedView.findViewById(R.id.msgDateTextView);

            msgTxtV.setText(msg.getMessage_text());

        }else {
            //inflate view for other users messages
            inflatedView = (LinearLayout) View.inflate(activity.getApplicationContext(), R.layout.users_message_layout, null);

            msgTxtV = (TextView)inflatedView.findViewById(R.id.msgTextView);
            TextView usernameTextV = (TextView)inflatedView.findViewById(R.id.usernameTView);
            if(msg.getMessage_owner().equals(lastMsgOwner)){
                usernameTextV.setVisibility(View.GONE);
            }else {
                usernameTextV.setText(msg.getMessage_owner());
                Chat.setLastMsgOwner(msg.getMessage_owner());
            }
            msgTxtV.setText(msg.getMessage_text());

            timeTxtV = (TextView)inflatedView.findViewById(R.id.msgTimeText);
            dateTxtV = (TextView)inflatedView.findViewById(R.id.msgDateTextView);



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
        if(msg.getDate().equals(lastMsgDate)){
            dateTxtV.setVisibility(View.GONE);
        }else {
            dateTxtV.setText(msg.getDate());
            Chat.setLastMsgDate(msg.getDate());
        }
        if(msg.getTime().equals(lastMsgTime)){
            timeTxtV.setVisibility(View.GONE);
        }else {
            timeTxtV.setText(msg.getTime());
            Chat.setLastMsgTime(msg.getTime());
        }
        inflatedView.requestFocus();

        messages.addView(inflatedView);
    }

    private static String generateRecommendation(Context ctx, String conversationName, long messageId) {
        List<Comment> comments = dataBL.getMessageComments(conversationName, messageId);
        int positiveCounter = 0, negativeCounter = 0;
        int positiveLength = 0, negativeLength = 0;

        for(Comment comment : comments){
            if(comment.getCommentClassification().equals("Positive")){
                positiveCounter++;
                for(String word : NEGATIVE_WORDS){ // Move on all the negative words
                    // Checks if the word contained in the comment
                    if(comment.getCommentText().toLowerCase().contains(word)){
                        positiveLength -= word.length();
                        if(!word.equals("bad")) { // Remove the word if its contained except 'bad' for using in positive words.
                            String tmpComment = comment.getCommentText().toLowerCase().replace(word, "");
                            comment.setCommentText(tmpComment);
                        }
                    }
                }
                for(String word : POSITIVE_WORDS){ // Move on all the positive words
                    // Checks if the word contained in the comment
                    if(comment.getCommentText().toLowerCase().contains(word)){
                        if(word.equals("not bad"))
                            positiveLength += 2; // 'not bad' is longer then, lets say 'good', but the mining is lower quality.
                        else
                            positiveLength += word.length();
                        // Remove the word if its contained in comment.
                        String tmpComment = comment.getCommentText().toLowerCase().replace(word, "");
                        comment.setCommentText(tmpComment);

                    }
                }
            }else if(comment.getCommentClassification().equals("Negative")){
                negativeCounter++;
                for(String word : POSITIVE_WORDS){ // Move on all the negative words
                    // Checks if the word contained in the comment
                    if(comment.getCommentText().toLowerCase().contains(word)){
                        negativeLength -= word.length();
                        if(!word.equals("good")) { // Remove the word if its contained except 'good' for using in positive words.
                            String tmpComment = comment.getCommentText().toLowerCase().replace(word, "");
                            comment.setCommentText(tmpComment);
                        }
                    }
                }
                for(String word : NEGATIVE_WORDS){ // Move on all the positive words
                    // Checks if the word contained in the comment
                    if(comment.getCommentText().toLowerCase().contains(word)){
                        if(word.equals("not so good"))
                            positiveLength += 2; // 'not so good' is longer then, lets say 'bad', but the mining is higher quality.
                        else
                            positiveLength += word.length();
                        // Remove the word if its contained in comment.
                        String tmpComment = comment.getCommentText().toLowerCase().replace(word, "");
                        comment.setCommentText(tmpComment);

                    }
                }
            }
        }
        String recommendation = "";
        if (positiveCounter > negativeCounter){
            if(positiveLength > negativeLength +20){
                recommendation = "Most of your friends that commented about your message thought that you have to watch the movie/series!\r\n" +
                        "You should know that your friends strongly recommend about that movie/series!";
            }else if(positiveLength > negativeLength){
                recommendation = "Most of your friends that commented about your message thought that the movie/series is very good!\r\n" +
                        "You should know that they recommended you to watch in that movie/series!";
            }else if(positiveLength +20 < negativeLength){
                recommendation = "Most of your friends gives good reviews about that movie/series but in their opinion it could be " +
                        "better!";
            }else if(positiveLength < negativeLength){
                recommendation = "Most of your friends that commented about your message thought that the movie/series is good!\r\n" +
                        "Some of your friends restricted and thought that parts from the movie/series was little less good!";
            }
        }else if (positiveCounter == negativeCounter){
            if(positiveLength > negativeLength){
                recommendation = "Your friends was divided over their mind!\r\n" +
                        "However, judging by each comment separately, you should watch this movie/series!";
            }else if(positiveLength == negativeLength){
                recommendation = "Your friends was totally divided over their mind!\r\n" +
                        "In this case you should take a chance, watch in the movie/series and come back to give us your opinion!";
            }else if(positiveLength < negativeLength){
                recommendation = "Your friends was divided over their mind!\r\n" +
                        "However, judging by each comment separately, not sure that you want to waste your time about that movie/series!";
            }
        }else {
            if(positiveLength > negativeLength +20){
                recommendation = "Most of your friends that commented about your message thought that the movie/series is not so good!\r\n" +
                        "Yet, maybe you should try that movie/series because the positive comments contained \"warm\" words.";
            }else if(positiveLength > negativeLength){
                recommendation = "Most of your friends that commented about your message thought that the movie/series is not so good!\r\n" +
                        "And yet, some of them thought you should watch in it so...";
            }else if(positiveLength +20 < negativeLength){
                recommendation = "Most of your friends that commented about your message thought that the movie/series is waste of time" +
                        "and money!!!\r\n So ask about another movie/series because that movie/series you shouldn't watch!";
            }else if(positiveLength < negativeLength){
                recommendation = "Most of your friends that commented about your message thought that the movie/series is not good!\r\n" +
                        "You should looking for another movie/series to watch!";
            }
        }
        return recommendation;
    }

    private static void openRecommendationDialog(final Context ctx, final String recommendation){
        LayoutInflater li = LayoutInflater.from(ctx);
        final View promptsView = li.inflate(R.layout.recommendation_dialog_layout, null);

        TextView recommendationTView = (TextView)promptsView.findViewById(R.id.recommendationBody);
        recommendationTView.setText(recommendation);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Chat.getInstance());
        alertDialogBuilder.setView(promptsView);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Thanks",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    private static void openCommentDialog(final Context ctx, final String conversationName) {
        // get prompts.xml view
        final Context context = ctx;
        LayoutInflater li = LayoutInflater.from(ctx);
        final View promptsView = li.inflate(R.layout.comment_dialog_layout, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Chat.getInstance());

        // set prompts.xml to alertDialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userComment = (EditText) promptsView.findViewById(R.id.commentText);
        final RadioGroup classifyRadioGroup = (RadioGroup)promptsView.findViewById(R.id.rGroupClassification);
        final TextView numOfCharacters = (TextView)promptsView.findViewById(R.id.numOfChars);

        userComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = userComment.getText().toString();
                int numOfChars = MAX_CHARACTERS_IN_MESSAGE - text.length();
                numOfCharacters.setText(String.valueOf(numOfChars));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                int rButtonId = classifyRadioGroup.getCheckedRadioButtonId();
                                if (rButtonId < 0) { // No RadioButton is checked
                                    Toast.makeText(context, "You have to classify your comment!\r\n" +
                                                            "      (Positive/Negative)",
                                            Toast.LENGTH_LONG).show();
                                    return;
                                }
                                RadioButton chosenButton = (RadioButton)promptsView.findViewById(rButtonId);
                                Comment comment = new Comment(userComment.getText().toString(),
                                        ParseUser.getCurrentUser().getUsername(),
                                        getCurrentDate(), getCurrentTime(), chosenButton.getText().toString());
                                sendCommentToConversationUsers(ctx, comment, conversationName, menuClickedMessage);
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

    private static void sendCommentToConversationUsers(Context ctx, Comment comment, String convName, Message message) {

        ParseQuery<ParseInstallation> destQuery = ParseQuery.getQuery(ParseInstallation.class);
        for(ParseUser user : Chat.chatWith){
            if(!ParseUser.getCurrentUser().getObjectId().equals(user.getObjectId())) {
                ParseQuery<ParseInstallation> destination = destQuery.whereEqualTo("user", user);
                try {
                    final JSONObject commentDetails = Utils.generateCommentJSONObject(comment, message);
                    if(Chat.isChatWithSingle)
                        commentDetails.put("Conversation name", ParseUser.getCurrentUser().getUsername());
                    else
                        commentDetails.put("Conversation name", convName);

                    ParsePush.sendDataInBackground(commentDetails, destination);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static JSONObject generateCommentJSONObject(Comment comment, Message message) throws JSONException{
        JSONObject object = new JSONObject();
        object.put("comment_alert", comment.getCommentText());
        object.put("comment_owner", comment.getCommentOwner());
        object.put("comment_date", comment.getCommentDate());
        object.put("comment_time", comment.getCommentTime());
        object.put("comment_classification", comment.getCommentClassification());
        object.put("msg_owner", message.getMessage_owner());
        object.put("msgId_comment", message.getOwnerMessageId());
        return object;
    }


    public static JSONObject generateMessageJSONObject(Message msg) throws JSONException {
        JSONObject object = new JSONObject();
        object.put("alert", msg.getMessage_text());
        object.put("msg_owner", msg.getMessage_owner());
        object.put("msg_time", msg.getTime());
        object.put("msg_date", msg.getDate());
        object.put("msg_gCreate", msg.getIsGroupCreateMsg());
        object.put("msg_ownerMsgId", msg.getOwnerMessageId());

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
