package com.example.android.inventory;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventory.data.StoreContract.StoreEntry;

import java.text.NumberFormat;

public class StockCursorAdapter extends CursorAdapter { //Adapter for the ListView

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
    public void bindView(View view, Context context, Cursor cursor) {

        TextView nameTextview = view.findViewById(R.id.name_textview);
        TextView typeTextview = view.findViewById(R.id.type_textview);
        TextView priceTextview = view.findViewById(R.id.price_textview);

        String name = cursor.getString(cursor.getColumnIndexOrThrow(StoreEntry.COLUMN_NAME));
        Integer type = cursor.getInt(cursor.getColumnIndexOrThrow(StoreEntry.COLUMN_TYPE));
        Integer price = cursor.getInt(cursor.getColumnIndexOrThrow(StoreEntry.COLUMN_PRICE));

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

        double parsed = Double.parseDouble(price.toString());
        String formatted = NumberFormat.getCurrencyInstance().format((parsed / 100));
        priceTextview.setText(formatted);
    }
}
