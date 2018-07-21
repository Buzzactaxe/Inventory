package com.example.android.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;


public class ItemContract {
    //Empty Constructor to prevent initializing accidentally contract class
    private ItemContract() {}

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITIY = "com.example.android.inventoryapp";

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.Inventoryapp/items/ is a valid path for
     * looking at item data. content://com.example.android.Inventoryapp/dfjks/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "dfjks".
     */
    public static final String PATH_INVENTORY = "items";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITIY);


    /**
     * Inner class that defines constant values for the items database table.
     * Each entry in the table represents a single items.
     */
    public static final class ItemEntry implements BaseColumns {

        /** The content URI to access the item data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of items.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITIY + "/" + PATH_INVENTORY;
        /**
         * The MIME type of the {@link #CONTENT_URI} for a single item.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITIY + "/" + PATH_INVENTORY;

        /**
         * Names of each columns in the database table connected from the DbHelper class
         */
        public static final String TABLE_NAME = "items";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_ITEM_NAME = "name";
        public static final String COLUMN_ITEM_PRICE = "price";
        public static final String COLUMN_ITEM_QUANTITY = "quantity";
        public static final String COLUMN_ITEM_SUPP_NAME = "supplier";
        public static final String COLUMN_ITEM_SUPP_PHONE_NUMBER = "phone";

        public static String doubleToStringNoDecimal(double d) {
            DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.UK);
            formatter.applyPattern("#,###");
            return formatter.format(d);
        }

    }
}
