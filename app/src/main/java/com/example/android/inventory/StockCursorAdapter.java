package com.example.android.inventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventory.data.StoreContract.StoreEntry;

import java.text.NumberFormat;

public class StockCursorAdapter extends CursorAdapter { //Adapter for the ListView

    ImageView buyButton;
    int mQuantity;

    public StockCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    //Makes new blank list item view
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    //Binds information to blank list item view
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        TextView nameTextview = view.findViewById(R.id.name_textview);
        TextView typeTextview = view.findViewById(R.id.type_textview);
        TextView priceTextview = view.findViewById(R.id.price_textview);
        TextView qtyTextview = view.findViewById(R.id.quantity_textview);

        buyButton = view.findViewById(R.id.buy_button);

        final int id = cursor.getInt(cursor.getColumnIndexOrThrow(StoreEntry._ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(StoreEntry.COLUMN_NAME));
        Integer type = cursor.getInt(cursor.getColumnIndexOrThrow(StoreEntry.COLUMN_TYPE));
        Integer price = cursor.getInt(cursor.getColumnIndexOrThrow(StoreEntry.COLUMN_PRICE));
        final Integer quantity = cursor.getInt(cursor.getColumnIndexOrThrow(StoreEntry.COLUMN_QUANTITY));

        nameTextview.setText(name);

        switch (type) {
            case StoreEntry.TYPE_OTHER:
                typeTextview.setText(R.string.type_other);
                break;
            case StoreEntry.TYPE_HARDCOVER:
                typeTextview.setText(R.string.type_hardcover);
                break;
            case StoreEntry.TYPE_PAPERBACK:
                typeTextview.setText(R.string.type_paperback);
                break;
            case StoreEntry.TYPE_EBOOK:
                typeTextview.setText(R.string.type_ebook);
                break;
            case StoreEntry.TYPE_AUDIO:
                typeTextview.setText(R.string.type_audio);
                break;
        }

        //Displays the price in currency formatting
        double parsed = Double.parseDouble(price.toString());
        String formatted = NumberFormat.getCurrencyInstance().format((parsed / 100));
        priceTextview.setText(formatted);

        qtyTextview.setText(R.string.quantity_abv);
        qtyTextview.append(" " + quantity);

        //Sets buyButton to sell one of item
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mQuantity = quantity;
                sellProduct(context, id);
            }
        });
    }

    //The Method that updates and saves the new quantity amount
    private void sellProduct(Context c, int id) {

        if (mQuantity <= 0) {
            Toast.makeText(c, R.string.neg_quantity, Toast.LENGTH_SHORT).show();
            return;
        }

        int newQuantity = mQuantity - 1;

        Uri productUri = ContentUris.withAppendedId(StoreEntry.CONTENT_URI, id);

        ContentValues values = new ContentValues();
        values.put(StoreEntry.COLUMN_QUANTITY, newQuantity);
        c.getContentResolver().update(productUri, values, null, null);
    }
}
