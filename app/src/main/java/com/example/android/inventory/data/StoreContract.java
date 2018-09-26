package com.example.android.inventory.data;

import android.provider.BaseColumns;

public final class StoreContract {

    private StoreContract() {
    }

    public static final class StoreEntry implements BaseColumns {

        public final static String TABLE_NAME = "stock";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_NAME = "product_name";
        public final static String COLUMN_TYPE = "product_type";
        public final static String COLUMN_PRICE = "price";
        public final static String COLUMN_QUANTITY = "quantity";
        public final static String COLUMN_SUPPLIER = "supplier_name";
        public final static String COLUMN_SUPPLIER_NUM = "supplier_phone_number";

        //Added options for the product_type column
        public final static int TYPE_OTHER = 0;
        public final static int TYPE_HARDCOVER = 1;
        public final static int TYPE_PAPERBACK = 2;
        public final static int TYPE_EBOOK = 3;
        public final static int TYPE_AUDIO = 4;
    }
}
