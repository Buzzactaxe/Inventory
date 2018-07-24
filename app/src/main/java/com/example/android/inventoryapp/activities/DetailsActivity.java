package com.example.android.inventoryapp.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.R;
import com.example.android.inventoryapp.data.ItemContract;
import com.example.android.inventoryapp.data.ItemContract.ItemEntry;
import com.example.android.inventoryapp.data.ItemDbHelper;
import static com.example.android.inventoryapp.data.ItemContract.ItemEntry.doubleToStringNoDecimal;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // Identifier for the item data loader
    private static final int EXISTING_ITEM_LOADER = 0;
    private int quantity;
    // Content URI for the existing item.
    private Uri mCurrentItemUri;

    // String for the supplier's Phone Number.
    private String supNumber;

    TextView mNameText, mPriceText, mQuantityText,
            mSuppNameText, mSuppPhoneText;

    private String rowId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_activity);

        // get the data that was sent from the intent that launched this activity.
        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();


        // Initialize a loader to read the item data from the database
        // and display the current values.
        getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);

        // Find The Views in the hierarchy of the activity_main.xml
        mNameText = findViewById(R.id.item_title_details);
        mPriceText = findViewById(R.id.price);
        mQuantityText = findViewById(R.id.quantity);
        mSuppNameText = findViewById(R.id.supplier_name);
        mSuppPhoneText = findViewById(R.id.supplier_number);
        Button plusButton = findViewById(R.id.plus_button);
       final Button minusButton = findViewById(R.id.minus_button);
        Button orderButton = findViewById(R.id.order);
        Button editButton = findViewById(R.id.edit);
        Button doneButton = findViewById(R.id.done);
        Button deleteButton = findViewById(R.id.delete);

        // Set up the click listener for the Plus Button.
        plusButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {

                ItemDbHelper dbHelper = new ItemDbHelper(getApplicationContext());
                final SQLiteDatabase database = dbHelper.getWritableDatabase();

                quantity += 1;

                ContentValues values = new ContentValues();
                values.put(ItemEntry.COLUMN_ITEM_QUANTITY, quantity);
                String selection = ItemEntry._ID + "=?";
                String[] selectionArgs = new String[]{String.valueOf(rowId)};
                database.update(ItemContract.ItemEntry.TABLE_NAME, values, selection, selectionArgs);
                mQuantityText.setText(doubleToStringNoDecimal(quantity));
            }
        });

        // Set up the click listener for the Minus Button.
        minusButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {

                ItemDbHelper dbHelper = new ItemDbHelper(getApplicationContext());
                final SQLiteDatabase database = dbHelper.getWritableDatabase();

                quantity -= 1;

                ContentValues values = new ContentValues();
                values.put(ItemEntry.COLUMN_ITEM_QUANTITY, quantity);

                String selection = ItemEntry._ID + "=?";
                String[] selectionArgs = new String[]{String.valueOf(rowId)};
                if (quantity == -1) {
                    Toast.makeText(DetailsActivity.this, "No Stock Left ", Toast.LENGTH_SHORT).show();
                } else {
                    database.update(ItemEntry.TABLE_NAME, values, selection, selectionArgs);
                }
                mQuantityText.setText(doubleToStringNoDecimal(quantity));
            }
        });

        // Set up the click listener for the Order Button.
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Make an intent to open the Phone App and ring the supplier.
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", supNumber, null));
                startActivity(phoneIntent);
            }
        });

        // Set up the click listener for the Edit Button.
        // Opens the Edit Activity to edit the selected item.
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentEdit();
            }
        });

        // Set up the click listener for the Done Button.
        // Update the quantity and go back to Main Activity.
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Set up the click listener for the Delete Button.
        // Delete the selected item from the item table.
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });
    }

    // This method is called when the Delete Button is clicked
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteItem();
                onBackPressed();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // This method is called to delete the selected item.
    private void deleteItem() {

        if (mCurrentItemUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(DetailsActivity.this, getString(R.string.editor_delete_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(DetailsActivity.this, getString(R.string.editor_delete_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(DetailsActivity.this, "Klein", Toast.LENGTH_SHORT).show();
        }
    }

    // This method creates an intent to send the user the Edit Activity.
    private void intentEdit() {
        // Create new intentEdit to go to {@link EditActivity}
        Intent intent = new Intent(DetailsActivity.this, EditorActivity.class);

        // Send the URI on the data field of the intentEdit.
        intent.setData(mCurrentItemUri);

        // Launch the{@link EditActivity} to display the data for the current item.
        startActivity(intent);
    }

    // Method called when the back button is pressed.
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DetailsActivity.this, CatalogActivity.class);
        startActivity(intent);
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ItemEntry._ID,
                ItemEntry.COLUMN_ITEM_NAME,
                ItemEntry.COLUMN_ITEM_PRICE,
                ItemEntry.COLUMN_ITEM_QUANTITY,
                ItemEntry.COLUMN_ITEM_SUPP_NAME,
                ItemEntry.COLUMN_ITEM_SUPP_PHONE_NUMBER
        };

        return new CursorLoader(this,
                mCurrentItemUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if(data.moveToFirst()) {
            int titleColumnIndex = data.getColumnIndex(ItemEntry.COLUMN_ITEM_NAME);
            int priceColumnIndex = data.getColumnIndex(ItemEntry.COLUMN_ITEM_PRICE);
            final int quantityColumnIndex = data.getColumnIndex(ItemEntry.COLUMN_ITEM_QUANTITY);
            int supNameColumnIndex = data.getColumnIndex(ItemEntry.COLUMN_ITEM_SUPP_NAME);
            int supNumberColumnIndex = data.getColumnIndex(ItemEntry.COLUMN_ITEM_SUPP_PHONE_NUMBER);
            int rowIndex = data.getColumnIndex(ItemEntry._ID);

            // Read the item attributes from the Cursor for the current item
            String title = data.getString(titleColumnIndex);
            double price = data.getDouble(priceColumnIndex);
            quantity = data.getInt(quantityColumnIndex);
            rowId = data.getString(rowIndex);
            String supName = data.getString(supNameColumnIndex);
            supNumber = data.getString(supNumberColumnIndex);

            mNameText.setText(title);
            mPriceText.setText(Double.toString(price));
            mQuantityText.setText(Integer.toString(quantity));
            mSuppNameText.setText(supName);
            mSuppPhoneText.setText(supNumber);
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
