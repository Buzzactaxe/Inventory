package com.example.android.inventoryapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ItemContract;
import com.example.android.inventoryapp.data.ItemContract.ItemEntry;
import com.example.android.inventoryapp.data.ItemDbHelper;

import java.text.NumberFormat;

/**
 * {@link ItemCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of pet data as its data source. This adapter knows
 * how to create list items for each row of pet data in the {@link Cursor}.
 */
public class ItemCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link ItemCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public ItemCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView itemName = (TextView) view.findViewById(R.id.item_title);
        TextView itemPrice = (TextView) view.findViewById(R.id.item_price);
        final TextView itemQuantity = (TextView) view.findViewById(R.id.item_quantity);
        ImageView imageView = view.findViewById(R.id.sale_button);


        // Find the columns of item attributes that we're interested in
        int titleColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_PRICE);
        final int quantityColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_QUANTITY);
        int rowIndex = cursor.getColumnIndex(ItemEntry._ID);

        // Read the item attributes from the Cursor for the current item
        String itemTitle = cursor.getString(titleColumnIndex);
        double price = cursor.getDouble(priceColumnIndex);
        final String quantity = cursor.getString(quantityColumnIndex);
        final int rowId = cursor.getInt(rowIndex);

        imageView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {

                ItemDbHelper dbHelper = new ItemDbHelper(context);
                SQLiteDatabase database = dbHelper.getWritableDatabase();

                int quantity = Integer.parseInt(itemQuantity.getText().toString());
                quantity = quantity - 1;

                ContentValues values = new ContentValues();
                values.put(ItemEntry.COLUMN_ITEM_QUANTITY, quantity);
                String selection = ItemEntry._ID + "=?";
                String[] selectionArgs = new String[] {String.valueOf(rowId)};
                if (quantity == -1) {
                    Toast.makeText(context, "No Stock Left ", Toast.LENGTH_SHORT).show();
                } else {
                    int rowsAffected = database.update(ItemEntry.TABLE_NAME, values, selection, selectionArgs);
                    itemQuantity.setText(Integer.toString(quantity));
                }

            }
        });

        // Format the price number to have two digits to the right of the decimal point
        // so for example it will show '8.10' instead of '8'.
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMinimumFractionDigits(2);
        String priceFormatted = numberFormat.format(price);
        String currency = context.getString(R.string.price_currency) + priceFormatted;

        //Populate fields with extracted properties
        itemName.setText(itemTitle);
        itemPrice.setText(currency);
        itemQuantity.setText(quantity);
    }
}