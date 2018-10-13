package com.example.android.inventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.example.android.inventory.data.StoreContract.CONTENT_AUTHORITY;
import static com.example.android.inventory.data.StoreContract.PATH_STOCK;
import static com.example.android.inventory.data.StoreContract.StoreEntry;

public class StockProvider extends ContentProvider {

    //Log messages tag
    public static final String LOG_TAG = StockProvider.class.getSimpleName();
    //URI matcher code for entire stock table
    private static final int PRODUCTS = 100;
    //URI matcher code for single product in stock table
    private static final int PRODUCT_ID = 101;
    //URI object for the former two ints to go into
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_STOCK, PRODUCTS);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_STOCK + "/#", PRODUCT_ID);
    }

    //Helper object
    private StoreDBHelper mDbHelper;

    //Initialize the provider and database helper object
    @Override
    public boolean onCreate() {

        mDbHelper = new StoreDBHelper(getContext());
        return true;
    }

    //Perform the query
    @Override
    public Cursor query
    (@NonNull Uri uri,
     @Nullable String[] projection,
     @Nullable String selection,
     @Nullable String[] selectionArgs,
     @Nullable String sortOrder) {

        //Getting readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        //Seeing if URI matcher can match the given uri to our predetermined options
        int match = sUriMatcher.match(uri);

        switch (match) {
            case PRODUCTS: //For the entirety of the table
                cursor = database.query(StoreEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case PRODUCT_ID: //For an individual entry of the table
                selection = StoreEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(StoreEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    //To make sure we have the table or a table item we previously defined
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return StoreEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return StoreEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case PRODUCTS:

                nullCheck(values);

                SQLiteDatabase database = mDbHelper.getWritableDatabase();

                long id = database.insert(StoreEntry.TABLE_NAME, null, values);

                //Logging error in case insertion fails
                if (id == -1) {
                    Log.e(LOG_TAG, "Failed to insert row for " + uri);
                    return null;
                }

                getContext().getContentResolver().notifyChange(uri, null);

                return ContentUris.withAppendedId(uri, id);

            default:
                throw new IllegalArgumentException("Insertion is not supported for URI " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        //Getting writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);

        int rowsDeleted;

        switch (match) {
            case PRODUCTS:
                //All will be deleted (that match the selection and selection args)
                rowsDeleted = database.delete(StoreEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                //One will be deleted (determined by the ID in URI)
                selection = StoreEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                rowsDeleted = database.delete(StoreEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion not supported for URI " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case PRODUCTS:
                return updateProduct(uri, values, selection, selectionArgs);
            case PRODUCT_ID:
                selection = StoreEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for URI " + uri);
        }
    }

    //Separate method to do the update heavy-lifting
    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.size() == 0) {
            return 0;
        }

        nullCheck(values);

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(StoreEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    private void nullCheck(ContentValues values) { //To check if key sections are null

        if (values.containsKey(StoreEntry.COLUMN_NAME)) {
            String name = values.getAsString(StoreEntry.COLUMN_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Product requires a name");
            }
        }

        if (values.containsKey(StoreEntry.COLUMN_TYPE)) {
            Integer type = values.getAsInteger(StoreEntry.COLUMN_TYPE);
            if (type == null || !StoreEntry.isValidType(type)) {
                throw new IllegalArgumentException("Product requires valid type");
            }
        }

        if (values.containsKey(StoreEntry.COLUMN_QUANTITY)) {
            Integer quantity = values.getAsInteger(StoreEntry.COLUMN_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Product requires non-negative quantity");
            }
        }
    }
}
