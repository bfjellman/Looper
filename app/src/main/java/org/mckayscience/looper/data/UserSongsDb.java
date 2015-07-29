package org.mckayscience.looper.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.facebook.AccessToken;

import org.mckayscience.looper.model.UserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by B on 7/25/2015.
 */
public class UserSongsDb {

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "UserSongs.db";
    public static final String USER_TABLE = getUser();
    private UserSongsDBHelper mUserSongsDBHelper;
    private SQLiteDatabase mSQLiteDatabase;

    public UserSongsDb(Context context) {
        mUserSongsDBHelper = new UserSongsDBHelper(
                context, DB_NAME, null, DB_VERSION);
        mSQLiteDatabase = mUserSongsDBHelper.getWritableDatabase();
    }

    private static String getUser() {

        if(AccessToken.getCurrentAccessToken() == null) {
            return "UserGuest";
        }

        return "User" + AccessToken.getCurrentAccessToken().getUserId();
    }

    public boolean insertUser(String song, String track0, String track1,
                              String track2, String track3, String track4) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("song", song);
        contentValues.put("track0", track0);
        contentValues.put("track1", track1);
        contentValues.put("track2", track2);
        contentValues.put("track3", track3);
        contentValues.put("track4", track4);

        long rowId = mSQLiteDatabase.replace(USER_TABLE, null, contentValues);
        return rowId != -1;
    }

    public List<UserInfo> selectUsers(String user) {
        // Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] columns = {"song", "track0", "track1","track2","track3","track4"};


        Cursor c = mSQLiteDatabase.query(
                user,  // The table to query
                columns,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        c.moveToFirst();
        List<UserInfo> list = new ArrayList<UserInfo>();
        for (int i=0; i<c.getCount(); i++) {
            String song = c.getString(0);
            String track0 = c.getString(1);
            String track1 = c.getString(2);
            String track2 = c.getString(3);
            String track3 = c.getString(4);
            String track4 = c.getString(5);
            UserInfo userInfo = new UserInfo(song, track0, track1, track2, track3, track4);
            list.add(userInfo);
            c.moveToNext();
        }

        return list;
    }

    public void deleteSong(String song) {
        mSQLiteDatabase.delete(USER_TABLE, "song=?", new String[]{song});
    }

    public void closeDB() {
        mSQLiteDatabase.close();
    }



    class UserSongsDBHelper extends SQLiteOpenHelper {

        private final String CREATE_USER_SQL =
                "CREATE TABLE IF NOT EXISTS " + USER_TABLE + " (" +
                        "song TEXT " +
                        "PRIMARY KEY, " +
                        "track0 TEXT, " +
                        "track1 TEXT, " +
                        "track2 TEXT, " +
                        "track3 TEXT, " +
                        "track4 TEXT)";

        private final String DROP_USER_SQL =
                "DROP TABLE IF EXISTS " + USER_TABLE;

        public UserSongsDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_USER_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL(DROP_USER_SQL);
            onCreate(sqLiteDatabase);
        }
    }


}
