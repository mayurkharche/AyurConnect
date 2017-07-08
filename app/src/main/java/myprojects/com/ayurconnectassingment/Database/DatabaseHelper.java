package myprojects.com.ayurconnectassingment.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.accounts.AccountManager.KEY_PASSWORD;

/**
 * Created by mayur on 5/7/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    // Main Database Version
    private static final int DATABASE_VERSION = 1;

    // Main Database Name
    private static final String DATABASE_NAME = "main_database";

    // User Table Name
    private static final String DB_TABLE = "users";

    // Column names
    private static final String KEY_DB_ID = "db_id";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_PASSWORD = "user_password";

    // Table create statement
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + DB_TABLE + "("+
            KEY_DB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            KEY_USER_EMAIL + " TEXT,"+
            KEY_USER_PASSWORD + " TEXT);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        Log.d(TAG,"Constructor called");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
        onCreate(db);
    }

    /*
    * Add login credentials to table
    * */

    public boolean addContentToMainDatabase(String userEmail, String userpassword) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USER_EMAIL, userEmail);
        values.put(KEY_USER_PASSWORD, userpassword);

        // Inserting Row
        db.insert(DB_TABLE, null, values);

        db.close();

        Log.d(TAG,userEmail+" & "+userpassword+" Added successfully");

        return true;   //returns true if added successfully
    }

    /*
    * Returns corresponding database id
    * */

    public String getDbid(String userEmail){

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(DB_TABLE, new String[] {
                        KEY_DB_ID }, KEY_USER_EMAIL + "=?",
                new String[] { String.valueOf(userEmail) }, null, null, null, null);
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
    * Check login credentials
    * */
    public Boolean checkCredentials(String userEmail, String userPassword){

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(DB_TABLE, new String[] {
                        KEY_USER_PASSWORD }, KEY_USER_EMAIL + "=?",
                new String[] { String.valueOf(userEmail) }, null, null, null, null);

        if(cursor.moveToFirst() && userPassword.equals(cursor.getString(0))){

            Log.d(TAG,"Password matched");
            return  true;
        }else{

            Log.d(TAG,"Wrong credentials");
            return false;
        }
    }
}
