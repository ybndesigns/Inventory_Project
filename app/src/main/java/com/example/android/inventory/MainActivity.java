package com.example.android.inventory;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.inventory.data.StoreDBHelper;

import static com.example.android.inventory.data.StoreContract.StoreEntry.TYPE_OTHER;
import static com.example.android.inventory.data.StoreMethods.addStoreItem;
import static com.example.android.inventory.data.StoreMethods.viewStock;

public class MainActivity extends AppCompatActivity { //Exists currently to test if the database is running correctly

    private StoreDBHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDbHelper = new StoreDBHelper(this);

        final TextView testDisplay = findViewById(R.id.test_display);
        //Setting the text to the table data so the app isn't blank when opened
        testDisplay.setText(viewStock(mDbHelper));

        //A button to insert arbitrary dummy data into the table
        Button dummyData = findViewById(R.id.dummy_data);
        dummyData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = "The Book of Herbal Healing";
                String supplier = "Unknown";

                addStoreItem(mDbHelper, name, TYPE_OTHER, 000, 1, supplier, 12345);
                testDisplay.setText(viewStock(mDbHelper));
            }
        });
    }
}
