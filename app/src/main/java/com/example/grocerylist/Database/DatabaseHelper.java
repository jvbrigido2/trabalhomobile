package com.example.grocerylist.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "app_database";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "items";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_ITEM = "item";
    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_ITEM + " TEXT);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public static String getTableName() {
        return TABLE_NAME;
    }
    public static String getItemColumnName() {
        return COLUMN_ITEM;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Caso tenha atualizacao no futuro
    }

}
