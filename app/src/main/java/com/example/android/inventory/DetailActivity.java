package com.example.android.inventory;

import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventory.data.StoreContract.StoreEntry;

import java.text.NumberFormat;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PRODUCT_LOADER = 0;
    private Uri mCurrentProductUri;
    private TextView nameTextView;
    private TextView priceTextView;
    private TextView quantityTextView;
    private TextView typeTextView;
    private TextView supplierTextView;
    private TextView supplierNumTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        if (mCurrentProductUri != null) {
            getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
        } else {
            Toast.makeText(this, R.string.no_product_found, Toast.LENGTH_SHORT).show();
            finish();
        }

        nameTextView = findViewById(R.id.detail_name);
        priceTextView = findViewById(R.id.detail_price);
        quantityTextView = findViewById(R.id.detail_quantity);
        typeTextView = findViewById(R.id.detail_type);
        supplierTextView = findViewById(R.id.detail_supplier_name);
        supplierNumTextView = findViewById(R.id.detail_supplier_num);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detailed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_edit:
                Intent intent = new Intent(DetailActivity.this, EditorActivity.class);
                intent.setData(mCurrentProductUri);
                startActivity(intent);
                return true;
            case R.id.action_delete_detailed:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(DetailActivity.this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteProduct();
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

        builder.create().show();
    }

    private void deleteProduct() {
        int rowsDeleted;

        if (mCurrentProductUri != null) {
            rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);
            if (rowsDeleted > 0) {
                Toast.makeText(this, R.string.delete_success, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, R.string.delete_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                StoreEntry._ID,
                StoreEntry.COLUMN_NAME,
                StoreEntry.COLUMN_PRICE,
                StoreEntry.COLUMN_TYPE,
                StoreEntry.COLUMN_QUANTITY,
                StoreEntry.COLUMN_SUPPLIER,
                StoreEntry.COLUMN_SUPPLIER_NUM
        };

        return new CursorLoader(this,
                mCurrentProductUri,
                projection,
                null,
                null,
                null);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            int name = data.getColumnIndex(StoreEntry.COLUMN_NAME);
            nameTextView.setText(data.getString(name));

            int price = data.getColumnIndex(StoreEntry.COLUMN_PRICE);
            double parsed = Double.parseDouble(data.getString(price));
            String formatted = NumberFormat.getCurrencyInstance().format((parsed / 100));
            priceTextView.setText(formatted);

            int type = data.getColumnIndex(StoreEntry.COLUMN_TYPE);
            switch (data.getInt(type)) {
                case StoreEntry.TYPE_OTHER:
                    typeTextView.setText(R.string.type_other);
                    break;
                case StoreEntry.TYPE_HARDCOVER:
                    typeTextView.setText(R.string.type_hardcover);
                    break;
                case StoreEntry.TYPE_PAPERBACK:
                    typeTextView.setText(R.string.type_paperback);
                    break;
                case StoreEntry.TYPE_EBOOK:
                    typeTextView.setText(R.string.type_ebook);
                    break;
                case StoreEntry.TYPE_AUDIO:
                    typeTextView.setText(R.string.type_audio);
                    break;
            }

            int quantity = data.getColumnIndex(StoreEntry.COLUMN_QUANTITY);
            quantityTextView.setText(Integer.toString(data.getInt(quantity)));

            int supplier = data.getColumnIndex(StoreEntry.COLUMN_SUPPLIER);
            if (data.getString(supplier).isEmpty()) {
                supplierTextView.setText(R.string.unknown_supplier);
            } else {
                supplierTextView.setText(data.getString(supplier));
            }

            int supplierNum = data.getColumnIndex(StoreEntry.COLUMN_SUPPLIER_NUM);
            if (data.getInt(supplierNum) == 0) {
                supplierNumTextView.setText(R.string.number_not_found);
            } else {
                String phoneNum = PhoneNumberUtils.formatNumber(data.getString(supplierNum), "US");
                supplierNumTextView.setText(phoneNum);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
