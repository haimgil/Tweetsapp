package il.tweetsapp.proj.tweetsapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import il.tweetsapp.proj.tweetsapp.Database.DataBL;
import il.tweetsapp.proj.tweetsapp.Objcets.Comment;


public class Comments extends ActionBarActivity {

    DataBL dataBL;
    LinearLayout comments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        comments = (LinearLayout)findViewById(R.id.comments);
        dataBL = new DataBL(this);

        Intent intent = getIntent();
        if (!intent.hasExtra("messageId")){
            Toast.makeText(this, "Error occurred while trying to fetch comments from db", Toast.LENGTH_LONG).show();
            return;
        }
        List<Comment> comments = dataBL.getMessageComments(intent.getStringExtra("conversationName"), intent.getIntExtra("messageId", -1));

        printCommentsToScreen(comments);
    }

    private void printCommentsToScreen(List<Comment> comments) {
        ImageView prosOrConsView;
        TextView commentText;
        TextView commentDateText;
        TextView commentTimeText;

        for(Comment comment : comments){
            LinearLayout inflatedView = (LinearLayout) View.inflate(this.getApplicationContext(), R.layout.comment_row, null);
            prosOrConsView = (ImageView)inflatedView.findViewById(R.id.pros_cons_view);
            commentText = (TextView)inflatedView.findViewById(R.id.commentTView);
            commentDateText = (TextView)inflatedView.findViewById(R.id.commentDateTView);
            commentTimeText = (TextView)inflatedView.findViewById(R.id.commentTimeTView);
            if(comment.getCommentClassification().equals("Positive"))
                prosOrConsView.setImageResource(R.drawable.pros_view);
            else
                prosOrConsView.setImageResource(R.drawable.cons_view);
            commentText.setText(comment.getCommentText());
            commentDateText.setText(comment.getCommentDate());
            commentTimeText.setText(comment.getCommentTime());

            this.comments.addView(inflatedView);
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_comments, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}