package com.example.android.inventory;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.inventory.data.StoreContract.StoreEntry;

import java.text.NumberFormat;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PRODUCT_LOADER = 0;
    int mType = 0;
    private Uri mCurrentProductUri;
    private EditText mNameInput;
    private EditText mPriceInput;
    private Spinner mSpinnerType;
    private EditText mQuantityInput;
    private EditText mSupplierInput;
    private EditText mSupplierNumInput;
    private boolean mFieldChanged = false;

    private Button addQuantity;
    private Button minusQuantity;
    private int mQuantity = 0;

    private String current = "";

    private TextWatcher twPrice;
    private TextWatcher twQuantity;
    //Listening for edited views in order to trigger showUnsavedChangesDialogue
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mFieldChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        if (mCurrentProductUri == null) {
            setTitle(R.string.title_add);
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            invalidateOptionsMenu();
        } else {
            getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
            setTitle(R.string.title_edit);
        }

        //Finding EditText inputs for later use
        mNameInput = findViewById(R.id.name_input);
        mPriceInput = findViewById(R.id.price_input);
        mSpinnerType = findViewById(R.id.spinner_type);
        mQuantityInput = findViewById(R.id.quantity_input);
        mSupplierInput = findViewById(R.id.supplier_input);

        //Finding and setting up spinner for Product Type
        mSupplierNumInput = findViewById(R.id.supplier_num_input);
        setupSpinner();

        //Setting OnTouchListener for each interactive section
        mNameInput.setOnTouchListener(mTouchListener);
        mPriceInput.setOnTouchListener(mTouchListener);
        mSpinnerType.setOnTouchListener(mTouchListener);
        mQuantityInput.setOnTouchListener(mTouchListener);
        mSupplierInput.setOnTouchListener(mTouchListener);
        mSupplierNumInput.setOnTouchListener(mTouchListener);

        //Adding TextWatcher to mPriceInput EditText to have it display as local currency
        twPrice = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!s.toString().equals(current)) {
                    mPriceInput.removeTextChangedListener(this);

                    String replaceable = String.format("[%s,.\\s]", NumberFormat.getCurrencyInstance().getCurrency().getSymbol());
                    String cleanString = s.toString().replaceAll(replaceable, "");

                    double parsed = Double.parseDouble(cleanString);
                    String formatted = NumberFormat.getCurrencyInstance().format((parsed / 100));

                    current = formatted;
                    mPriceInput.setText(formatted);
                    mPriceInput.setSelection(formatted.length());

                    mPriceInput.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        mPriceInput.addTextChangedListener(twPrice);

        //Setting up the buttons to increment the Quantity amount
        minusQuantity = findViewById(R.id.minusQuantity);
        addQuantity = findViewById(R.id.addQuantity);

        minusQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mQuantity == 0) {
                    Toast.makeText(EditorActivity.this, R.string.neg_quantity, Toast.LENGTH_SHORT).show();
                    return;
                }

                mQuantity -= 1;
                mQuantityInput.setText(String.valueOf(mQuantity));
            }
        });

        addQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mQuantity += 1;
                mQuantityInput.setText(String.valueOf(mQuantity));
            }
        });

        twQuantity = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                mQuantityInput.removeTextChangedListener(this);

                String input = s.toString();
                if (!input.isEmpty() && Integer.parseInt(input) >= 0) {
                    mQuantity = Integer.parseInt(input);
                } else if (!input.isEmpty() && Integer.parseInt(input) < 0) {
                    Toast.makeText(EditorActivity.this, R.string.neg_quantity, Toast.LENGTH_SHORT).show();
                    mQuantity = 0;
                } else {
                    mQuantity = 0;
                }

                String quantityString = String.valueOf(mQuantity);
                mQuantityInput.setText(quantityString);
                mQuantityInput.setSelection(quantityString.length());
                mQuantityInput.addTextChangedListener(this);
            }
        };
        mQuantityInput.addTextChangedListener(twQuantity);
    }

    //Removing TextWatcher so there is no blank line error
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPriceInput.removeTextChangedListener(twPrice);
        mQuantityInput.removeTextChangedListener(twQuantity);
    }

    //Separate method to do the heavy lifting of setting up Spinner
    private void setupSpinner() {

        ArrayAdapter typeSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.array_type_options, android.R.layout.simple_spinner_item);
        typeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        mSpinnerType.setAdapter(typeSpinnerAdapter);

        mSpinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {

                    if (selection.equals(getString(R.string.type_other))) {
                        mType = StoreEntry.TYPE_OTHER;
                    } else if (selection.equals(getString(R.string.type_hardcover))) {
                        mType = StoreEntry.TYPE_HARDCOVER;
                    } else if (selection.equals(getString(R.string.type_paperback))) {
                        mType = StoreEntry.TYPE_PAPERBACK;
                    } else if (selection.equals(getString(R.string.type_ebook))) {
                        mType = StoreEntry.TYPE_EBOOK;
                    } else if (selection.equals(getString(R.string.type_audio))) {
                        mType = StoreEntry.TYPE_AUDIO;
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mType = 0;
            }
        });
    }

    //Inflating Menu in order to save and delete
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    //Linking Menu to actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveProduct();
                return true;
            case R.id.action_delete_editor:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mFieldChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Making the menu option "delete" invisible if new entry
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete_editor);
            menuItem.setVisible(false);
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        if (!mFieldChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    //Dialogue popup to make sure abandoning unsaved changes is intentional
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.discard_changes);
        builder.setPositiveButton(R.string.button_discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.button_keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        builder.create().show();
    }

    //Dialogue popup to make sure deletion is intentional
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

    //Method doing the technical work of deleting an entry as well as reporting its success
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

    //Method doing the technical work of saving an entry as well as reporting its success
    private void saveProduct() {

        String nameString = getAndTrim(mNameInput);

        //Double-checking that any unneeded user-entered characters are removed before saving
        String priceFormatted = getAndTrim(mPriceInput);
        String replaceable = String.format("[%s,.\\s]", NumberFormat.getCurrencyInstance().getCurrency().getSymbol());
        String priceString = priceFormatted.replaceAll(replaceable, "");

        String quantityString = getAndTrim(mQuantityInput);
        String supplierString = getAndTrim(mSupplierInput);
        String supplierNumString = getAndTrim(mSupplierNumInput);

        //A check to make sure an empty entry is not saved
        if (mCurrentProductUri == null &&
                TextUtils.isEmpty(nameString) &&
                TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) &&
                TextUtils.isEmpty(supplierString) &&
                TextUtils.isEmpty(supplierNumString) &&
                mType == StoreEntry.TYPE_OTHER) {
            Toast.makeText(this, R.string.empty_entry, Toast.LENGTH_SHORT).show();
            return;
        }

        //Making sure that name and price have been entered
        if (TextUtils.isEmpty(nameString) ||
                ((TextUtils.isEmpty(priceString)) || Integer.parseInt(priceString) == 0) ||
                TextUtils.isEmpty(quantityString) ||
                TextUtils.isEmpty(supplierString) ||
                TextUtils.isEmpty(supplierNumString)) {
            String err = "All fields are required";
            Toast.makeText(this, err, Toast.LENGTH_SHORT).show();
            return;
        }

        //Saving information to new ContentValues
        ContentValues values = new ContentValues();
        values.put(StoreEntry.COLUMN_NAME, nameString);
        values.put(StoreEntry.COLUMN_PRICE, priceString);
        values.put(StoreEntry.COLUMN_TYPE, mType);

        //Preventing error is if no quantity amount was entered
        int quantityFinal = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantityFinal = Integer.parseInt(quantityString);
        }
        values.put(StoreEntry.COLUMN_QUANTITY, quantityFinal);

        values.put(StoreEntry.COLUMN_SUPPLIER, supplierString);
        values.put(StoreEntry.COLUMN_SUPPLIER_NUM, supplierNumString);


        //The actual portion that saved the data and reports its success
        Uri newUri = null;
        int savedRows = 0;

        if (mCurrentProductUri == null) {
            newUri = getContentResolver().insert(StoreEntry.CONTENT_URI, values);
        } else {
            savedRows = getContentResolver().update(mCurrentProductUri, values, null, null);
        }

        Toast errorMessage = Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT);
        Toast successMessage = Toast.makeText(this, R.string.success, Toast.LENGTH_SHORT);

        if ((newUri != null) || (savedRows != 0)) {
            successMessage.show();
        } else {
            errorMessage.show();
        }

        startActivity(new Intent(this, MainActivity.class));
    }

    //Shortcut in cleaning up user entry
    private String getAndTrim(EditText editText) {
        return editText.getText().toString().trim();
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

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) { //Filling in all the information
            int name = data.getColumnIndex(StoreEntry.COLUMN_NAME);
            mNameInput.setText(data.getString(name));

            int price = data.getColumnIndex(StoreEntry.COLUMN_PRICE);
            mPriceInput.setText(data.getString(price));

            int type = data.getColumnIndex(StoreEntry.COLUMN_TYPE);
            switch (data.getInt(type)) {
                case StoreEntry.TYPE_OTHER:
                    mSpinnerType.setSelection(0);
                    break;
                case StoreEntry.TYPE_HARDCOVER:
                    mSpinnerType.setSelection(1);
                    break;
                case StoreEntry.TYPE_PAPERBACK:
                    mSpinnerType.setSelection(2);
                    break;
                case StoreEntry.TYPE_EBOOK:
                    mSpinnerType.setSelection(3);
                    break;
                case StoreEntry.TYPE_AUDIO:
                    mSpinnerType.setSelection(4);
                    break;
            }

            int quantity = data.getColumnIndex(StoreEntry.COLUMN_QUANTITY);
            mQuantityInput.setText(Integer.toString(data.getInt(quantity)));

            int supplier = data.getColumnIndex(StoreEntry.COLUMN_SUPPLIER);
            mSupplierInput.setText(data.getString(supplier));

            int supplierNum = data.getColumnIndex(StoreEntry.COLUMN_SUPPLIER_NUM);
            mSupplierNumInput.setText(data.getString(supplierNum));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameInput.setText("");
        mPriceInput.setText("");
        mSpinnerType.setSelection(0);
        mQuantityInput.setText("");
        mSupplierInput.setText("");
        mSupplierNumInput.setText("");
    }
}
