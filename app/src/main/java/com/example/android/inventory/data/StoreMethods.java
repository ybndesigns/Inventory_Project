package com.example.android.inventory.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.android.inventory.data.StoreContract.StoreEntry;

public final class StoreMethods { //A class which holds the two methods to read and write to the database

    private StoreMethods() {
    }

    /**
     * Method that adds new entries to the stock table. Parameters added so they can be easily used
     * in several activities (for instance, if dummy data needs to be entered in one activity and user input data in another).
     *
     * @param helper      is the iteration of @StoreDBHelper that is being used on the page.
     * @param name        corresponds to the name column in the table
     * @param type        corresponds to the product type
     * @param price       corresponds to price
     * @param quantity    corresponds to quantity
     * @param supplier    corresponds to supplier
     * @param supplierNum corresponds to supplier phone number
     */
    public static void addStoreItem(StoreDBHelper helper, String name, int type, int price, int quantity, String supplier, int supplierNum) {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(StoreEntry.COLUMN_NAME, name);
        values.put(StoreEntry.COLUMN_TYPE, type);
        values.put(StoreEntry.COLUMN_PRICE, price);
        values.put(StoreEntry.COLUMN_QUANTITY, quantity);
        values.put(StoreEntry.COLUMN_SUPPLIER, supplier);
        values.put(StoreEntry.COLUMN_SUPPLIER_NUM, supplierNum);

        long newRowId = db.insert(StoreEntry.TABLE_NAME, null, values);

        if (newRowId != -1) {
            Log.i("New entry added: ", "ID #" + newRowId);
        } else {
            Log.i("Error adding entry.", "You might wanna look into it.");
        }
    }

    /**
     * Method to read the entirety of the table.
     *
     * @param helper is the iteration of @StoreDBHelper that is being used on the page.
     * @return is a String so it can be easily placed in any TextView
     */
    public static String viewStock(StoreDBHelper helper) {
        SQLiteDatabase db = helper.getReadableDatabase();

        String div = " || ";
        String line = "\n";

        Cursor cursor = db.query( //currently choosing to leave columns as null since everything is to be viewed
                StoreEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        //The intro of the text that will be viewed
        String stockInfo = "We offer " + cursor.getCount() + "items";
        stockInfo += line +
                StoreEntry._ID + div +
                StoreEntry.COLUMN_NAME + div +
                StoreEntry.COLUMN_TYPE + div +
                StoreEntry.COLUMN_PRICE + div +
                StoreEntry.COLUMN_QUANTITY + div +
                StoreEntry.COLUMN_SUPPLIER + div +
                StoreEntry.COLUMN_SUPPLIER_NUM;


        try {
            int columnIndexID = cursor.getColumnIndex(StoreEntry._ID);
            int columnIndexName = cursor.getColumnIndex(StoreEntry.COLUMN_NAME);
            int columnIndexType = cursor.getColumnIndex(StoreEntry.COLUMN_TYPE);
            int columnIndexPrice = cursor.getColumnIndex(StoreEntry.COLUMN_PRICE);
            int columnIndexQuantity = cursor.getColumnIndex(StoreEntry.COLUMN_QUANTITY);
            int columnIndexSupplier = cursor.getColumnIndex(StoreEntry.COLUMN_SUPPLIER);
            int columnIndexSupplierNum = cursor.getColumnIndex(StoreEntry.COLUMN_SUPPLIER_NUM);

            //Honestly, switched to a StringBuilder because that's what AndroidStudio suggests when adding to a string in a loop
            StringBuilder stockInfoBuilder = new StringBuilder(stockInfo);

            while (cursor.moveToNext()) {
                int currentID = cursor.getInt(columnIndexID);
                String currentName = cursor.getString(columnIndexName);
                int currentType = cursor.getInt(columnIndexType);
                int currentPrice = cursor.getInt(columnIndexPrice);
                int currentQuantity = cursor.getInt(columnIndexQuantity);
                String currentSupplier = cursor.getString(columnIndexSupplier);
                int currentSupplierNum = cursor.getInt(columnIndexSupplierNum);

                stockInfoBuilder.append(line).append(currentID).append(div).append(currentName).append(div).append(currentType).append(div).append(currentPrice).append(div).append(currentQuantity).append(div).append(currentSupplier).append(div).append(currentSupplierNum);
                Log.i("Row Read ", String.valueOf(currentID));
            }
            stockInfo = stockInfoBuilder.toString();

            Log.i("Table complete: ", cursor.getCount() + " rows displayed.");

        } finally {
            cursor.close();
        }
        return stockInfo;

    }
}
