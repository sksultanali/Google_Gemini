package com.developerali.google_gemini;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "chat.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "chat_history";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ROLE = "role";
    public static final String COLUMN_TEXT = "text";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_ROLE + " TEXT, " +
                    COLUMN_TEXT + " TEXT);";

//    private static final String TABLE_CREATE =
//            "CREATE TABLE " + TABLE_NAME + " (" +
//                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                    COLUMN_ROLE + " TEXT, " +
//                    COLUMN_TEXT + " TEXT, " +
//                    "image BLOB);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
