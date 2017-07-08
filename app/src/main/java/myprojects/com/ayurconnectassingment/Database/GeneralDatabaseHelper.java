package myprojects.com.ayurconnectassingment.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by mayur on 6/7/17.
 */

public class GeneralDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "GeneralDatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static String DATABASE_NAME = "default";

    /****************************************************************************************************************************************************/

    // Table Names
    private static final String DB_SHIRT_COLLECTION_TABLE = "shirtcollection";

    // column names
    private static final String KEY_SHIRT_ID = "id";
    private static final String KEY_SHIRT = "shirt";

    // Table create statement
    private static final String CREATE_TABLE_SHIRT_COLLECTION = "CREATE TABLE " + DB_SHIRT_COLLECTION_TABLE + "("+
            KEY_SHIRT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            KEY_SHIRT + " TEXT);";

    /****************************************************************************************************************************************************/

    // Table Names
    private static final String DB_PANT_COLLECTION_TABLE = "pantcollection";

    // column names
    private static final String KEY_PANT_ID = "id";
    private static final String KEY_PANT = "pant";

    // Table create statement
    private static final String CREATE_TABLE_PANT_COLLECTION = "CREATE TABLE " + DB_PANT_COLLECTION_TABLE + "("+
            KEY_PANT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            KEY_PANT + " TEXT);";

    /****************************************************************************************************************************************************/
    // Table Names
    private static final String DB_BOOKMARK_TABLE = "bookmark";

    // column names
    private static final String KEY_BOOKMARK_SHIRT_URI = "shirturi";
    private static final String KEY_BOOKMARK_PANT_URI = "panturi";

    // Table create statement
    private static final String CREATE_TABLE_BOOKMARK = "CREATE TABLE " + DB_BOOKMARK_TABLE + "("+
            KEY_BOOKMARK_SHIRT_URI + " TEXT,"+
            KEY_BOOKMARK_PANT_URI + " TEXT);";

    /****************************************************************************************************************************************************/

    public GeneralDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SHIRT_COLLECTION);
        db.execSQL(CREATE_TABLE_PANT_COLLECTION);
        db.execSQL(CREATE_TABLE_BOOKMARK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DB_SHIRT_COLLECTION_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DB_PANT_COLLECTION_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DB_BOOKMARK_TABLE);
        onCreate(db);
    }

    /*
    * Set user database id as database name
    * */

    public static Boolean setDatabaseName(String name){

        DATABASE_NAME = name;

        Log.d(TAG,"Database name changed to "+name);

        return  true;
    }

    /*
    * Add shirt to the database
    * */

    public boolean addToShirtCollection(String shirtUri) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SHIRT, shirtUri);

        // Inserting Row
        db.insert(DB_SHIRT_COLLECTION_TABLE, null, values);

        db.close();

        Log.d(TAG,shirtUri+" Added successfully to "+DATABASE_NAME+" Database");

        return true;   //returns true if added successfully
    }

    /*
    * Add pant to the database
    * */

    public boolean addToPantCollection(String pantUri) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PANT, pantUri);

        // Inserting Row
        db.insert(DB_PANT_COLLECTION_TABLE, null, values);

        db.close();

        Log.d(TAG,pantUri+" Added successfully to "+DATABASE_NAME+" Database");

        return true;   //returns true if added successfully
    }

    /*
    * Returns total number of shirts available
    * */
    public long getShirtCollectionCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        long cnt  = DatabaseUtils.queryNumEntries(db, DB_SHIRT_COLLECTION_TABLE);
        db.close();
        return cnt;
    }

    /*
    * Returns total number of pant available
    * */
    public long getPantCollectionCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        long cnt  = DatabaseUtils.queryNumEntries(db, DB_PANT_COLLECTION_TABLE);
        db.close();
        return cnt;
    }

    /*
    * Return shirt image path for corresponding shirt id
    * */
    public String getShirtFromCollection(long shirtId){

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(DB_SHIRT_COLLECTION_TABLE, new String[] {
                        KEY_SHIRT }, KEY_SHIRT_ID + "=?",
                new String[] { String.valueOf(shirtId) }, null, null, null, null);
        if (cursor.moveToFirst()) {

            Log.d(TAG,cursor.getString(0));
            return cursor.getString(0);
        }
        else {
            Log.e(TAG, "Content Not Found.");
            return null;
        }
    }

    /*
    * Return pant image path for corresponding pant id
    * */

    public String getPantFromCollection(long pantId){

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(DB_PANT_COLLECTION_TABLE, new String[] {
                        KEY_PANT }, KEY_PANT_ID + "=?",
                new String[] { String.valueOf(pantId) }, null, null, null, null);
        if (cursor.moveToFirst()) {

            Log.d(TAG,cursor.getString(0));
            return cursor.getString(0);
        }
        else {
            Log.e(TAG, "Content Not Found.");
            return null;
        }
    }

    /*********************************** Operations For Bookmark Table *****************************************************************************/


    /*
    * Add pair to bookmark table
    * */

    public boolean addToBookmark(String shirtUri, String pantUri) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_BOOKMARK_SHIRT_URI, shirtUri);
        values.put(KEY_BOOKMARK_PANT_URI, pantUri);

        // Inserting Row
        db.insert(DB_BOOKMARK_TABLE, null, values);

        db.close();

        Log.d(TAG,shirtUri+ " & "+pantUri+" Added successfully to Bookamrk table in "+DATABASE_NAME+" Database");

        return true;   //returns true if added successfully
    }

    /*
    * Return all pairs from bookmark table
    * */
    public Cursor getAllBookmarkedPair() {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(DB_BOOKMARK_TABLE, new String[] {
                        KEY_BOOKMARK_SHIRT_URI, KEY_BOOKMARK_PANT_URI }, null,
                null, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();
        else
            Log.e(TAG,"Table is Empty.");

        return cursor;
    }

    /*
    * Check whether the pair is already bookmarked
    * */
    public boolean isBookmarked(String shirtUri, String pantUri){

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(DB_BOOKMARK_TABLE, new String[] {
                        KEY_BOOKMARK_PANT_URI }, KEY_BOOKMARK_SHIRT_URI + "=?",
                new String[] { shirtUri }, null, null, null, null);

        if(cursor.moveToFirst() && pantUri.equals(cursor.getString(0))){

            Log.d(TAG,"uri matched");
            return  true;

        }else{

            Log.d(TAG,"Pair is not bookmarked");
            return false;
        }
    }
}
