package com.example.android.inventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class StoreContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.inventory";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_STOCK = "stock";

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


        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_STOCK);
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of the products.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STOCK;
        /**
         * The MIME type of the {@link #CONTENT_URI} for a single product.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STOCK;

        /**
         * To check that whatever product type selected is compliant
         * with the predetermined options
         */
        public static boolean isValidType(int productType) {

            int[] typesList = // To iterate through
                    {TYPE_OTHER,
                            TYPE_HARDCOVER,
                            TYPE_PAPERBACK,
                            TYPE_EBOOK,
                            TYPE_AUDIO};

            for (int i = 0; i < typesList.length; i++) {
                if (productType == typesList[i]) {
                    return true;
                }
            }
            return false;
        }
    }
}
