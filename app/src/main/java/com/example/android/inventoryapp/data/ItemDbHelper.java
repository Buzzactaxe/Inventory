package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.android.inventoryapp.data.ItemContract.ItemEntry;

public class ItemDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = ItemDbHelper.class.getSimpleName();
    public static final String DATABASE_NAME = "shop.db";
    public static final int DATABASE_VERSION = 1;

    String SQL_CREATE_ITEMS_TABLES = " CREATE TABLE " + ItemEntry.TABLE_NAME + " ( "
            + ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ItemEntry.COLUMN_ITEM_NAME + " TEXT NOT NULL, "
            + ItemEntry.COLUMN_ITEM_PRICE + " DOUBLE, "
            + ItemEntry.COLUMN_ITEM_QUANTITY + " INTEGER, "
            + ItemEntry.COLUMN_ITEM_SUPP_NAME + " TEXT NOT NULL, "
            + ItemEntry.COLUMN_ITEM_SUPP_PHONE_NUMBER + " LONG);";

    //Constructor
    public ItemDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.v(LOG_TAG,SQL_CREATE_ITEMS_TABLES);
        db.execSQL(SQL_CREATE_ITEMS_TABLES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // The database is still at version 1, so there's nothing to do be done here.
    }

}
