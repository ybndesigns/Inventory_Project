package com.example.android.inventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.example.android.inventory.data.StoreContract.*;

public class StoreDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "bookstore.db";

    public StoreDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String TEXT = " TEXT";
        String INTEGER = " INTEGER";
        String NOT_NULL = " NOT NULL";
        String DEFAULT_0 = " DEFAULT 0";

        String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + StoreEntry.TABLE_NAME + " (" +
                        StoreEntry._ID + INTEGER + " PRIMARY KEY AUTOINCREMENT, " +
                        StoreEntry.COLUMN_NAME + TEXT + NOT_NULL + ", " +
                        StoreEntry.COLUMN_TYPE + INTEGER + NOT_NULL + DEFAULT_0 + ", " +
                        StoreEntry.COLUMN_PRICE + INTEGER + DEFAULT_0 + ", " +
                        StoreEntry.COLUMN_QUANTITY + INTEGER + DEFAULT_0 + ", " +
                        StoreEntry.COLUMN_SUPPLIER + TEXT + ", " +
                        StoreEntry.COLUMN_SUPPLIER_NUM + INTEGER + ");";

        Log.i("SQL Statement: ", SQL_CREATE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES);
     }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
