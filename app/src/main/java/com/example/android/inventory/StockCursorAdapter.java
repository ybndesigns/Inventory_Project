package com.example.android.inventory;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.NumberPicker;
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

        //NumberPicker to be used in AlertDialog
        final NumberPicker numberPicker = new NumberPicker(context);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(1000);
        numberPicker.setWrapSelectorWheel(false);


        //Preparing AlertDialog for to be called on buyButton click
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.amount_sold);
        builder.setView(numberPicker);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mQuantity = quantity;
                sellProduct(context, numberPicker, id);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.create().show();
            }
        });
    }

    //The Method that updates and saves the new quantity amount
    private void sellProduct(Context c, NumberPicker np, int id) {
        int soldAmount = np.getValue();

        if (soldAmount <= 0) {
            Toast.makeText(c, R.string.pos_amount, Toast.LENGTH_SHORT).show();
            return;
        } else if (soldAmount > mQuantity) {
            Toast.makeText(c, R.string.larger_than_qty, Toast.LENGTH_SHORT).show();
            return;
        }

        int newQuantity = (mQuantity - soldAmount);

        Uri productUri = ContentUris.withAppendedId(StoreEntry.CONTENT_URI, id);

        ContentValues values = new ContentValues();
        values.put(StoreEntry.COLUMN_QUANTITY, newQuantity);
        c.getContentResolver().update(productUri, values, null, null);
    }
}
