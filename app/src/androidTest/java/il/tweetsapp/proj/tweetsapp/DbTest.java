package il.tweetsapp.proj.tweetsapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.Map;
import java.util.Set;

import il.tweetsapp.proj.tweetsapp.Database.TweetsAppDbHelper;
import il.tweetsapp.proj.tweetsapp.helpers.Constants;

/**
 * Created by Haim on 6/18/2015.
 */
public class DbTest extends AndroidTestCase {
    public static final String LOG_TAG = DbTest.class.getSimpleName();
    static final String TEST_MESSAGE_CONV_NAME = "Test Conversation name";
    static final String TEST_MESSAGE_TXT = "Hello world";
    static final String TEST_MESSAGE_OWNER = "Haimon";
    static final String TEST_MESSAGE_DATE = "19/6/15";
    static final String TEST_MESSAGE_TIME = "13:30";
    static final int TEST_MESSAGE_ROW_ID = 11223344;
    static final String TEST_COMMENT_TXT = "Very good movie";
    static final String TEST_COMMENT_CLASSIFICATION = "Positive";

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(Constants.DATABASE_NAME);
        SQLiteDatabase db = new TweetsAppDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    public void testInsertReadDb() {

        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        TweetsAppDbHelper dbHelper = new TweetsAppDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = createMessageValues();

        long rowId;
        rowId = db.insert(Constants.MESSAGES_TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue(rowId != -1);
        Log.d(LOG_TAG, "New row id: " + rowId);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                Constants.MESSAGES_TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        validateCursor(cursor, testValues);

        // Fantastic.  Now that we have a location, add some weather!
        ContentValues commentValues = createCommentValues(rowId);

        long weatherRowId = db.insert(Constants.COMMENTS_TABLE_NAME, null, commentValues);
        assertTrue(weatherRowId != -1);

        // A cursor is your primary interface to the query results.
        Cursor weatherCursor = db.query(
                Constants.COMMENTS_TABLE_NAME,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );

        validateCursor(weatherCursor, commentValues);

        dbHelper.close();
    }

    static ContentValues createCommentValues(long locationRowId) {
        ContentValues weatherValues = new ContentValues();
        weatherValues.put(Constants.COLUMN_COMMENT_TEXT_NAME, TEST_COMMENT_TXT);
        weatherValues.put(Constants.COLUMN_COMMENT_OWNER_NAME, TEST_MESSAGE_OWNER);
        weatherValues.put(Constants.COLUMN_MSG_DATE_NAME, TEST_MESSAGE_DATE);
        weatherValues.put(Constants.COLUMN_MSG_TIME_NAME, TEST_MESSAGE_TIME);
        weatherValues.put(Constants.COLUMN_CONVERSATION_NAME, TEST_MESSAGE_CONV_NAME);
        weatherValues.put(Constants.COLUMN_CLASSIFICATION_NAME, TEST_COMMENT_CLASSIFICATION);
        weatherValues.put(Constants.COLUMN_MESSAGES_ID_NAME, TEST_MESSAGE_ROW_ID);

        return weatherValues;
    }

    static ContentValues createMessageValues() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(Constants.COLUMN_CONVERSATION_NAME, TEST_MESSAGE_CONV_NAME);
        testValues.put(Constants.COLUMN_MSG_TXT_NAME, TEST_MESSAGE_TXT);
        testValues.put(Constants.COLUMN_MSG_OWNER_NAME, TEST_MESSAGE_OWNER);
        testValues.put(Constants.COLUMN_MSG_DATE_NAME, TEST_MESSAGE_DATE);
        testValues.put(Constants.COLUMN_MSG_TIME_NAME, TEST_MESSAGE_TIME);
        testValues.put(Constants.COLUMN_OWNER_MSG_ID_NAME, TEST_MESSAGE_ROW_ID);
        testValues.put(Constants.COLUMN_MSG_BOOLEAN_NAME, 0);

        return testValues;
    }

    static void validateCursor(Cursor valueCursor, ContentValues expectedValues) {

        assertTrue(valueCursor.moveToFirst());

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(idx));
        }
        valueCursor.close();
    }

}
