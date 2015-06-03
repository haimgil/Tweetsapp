package il.tweetsapp.proj.tweetsapp.Objcets;

/**
 * Created by Haim on 6/3/2015.
 */
public class Comment {

    private String commentText;
    private String commentOwner;
    private String commentDate;
    private String commentTime;

    public Comment(String commentText, String commentOwner, String commentDate, String commentTime) {
        this.commentText = commentText;
        this.commentOwner = commentOwner;
        this.commentDate = commentDate;
        this.commentTime = commentTime;
    }

    public String getCommentText() {
        return commentText;
    }

    public String getCommentOwner() {
        return commentOwner;
    }

    public String getCommentDate() {
        return commentDate;
    }

    public String getCommentTime() {
        return commentTime;
    }

}
