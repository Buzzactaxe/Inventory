package com.example.android.inventoryapp.activities;


import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;

import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import android.widget.ListView;
import android.widget.Toast;

import com.example.android.inventoryapp.ItemCursorAdapter;
import com.example.android.inventoryapp.R;
import com.example.android.inventoryapp.data.ItemContract.ItemEntry;
import com.example.android.inventoryapp.data.ItemDbHelper;


public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>  {

    private static final int ITEM_LOADER = 0;

    private ItemCursorAdapter mCursorAdapter;

    // Content URI for the existing item (null if its a new item).
    private Uri currentItemUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the item data
        ListView itemsListView = findViewById(R.id.listView);
        
        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
       View emptyView = findViewById(R.id.empty_view);
       itemsListView.setEmptyView(emptyView);

       //Create a ItemAdapter
        mCursorAdapter = new ItemCursorAdapter(this, null);

        itemsListView.setAdapter(mCursorAdapter);

        // Make the ListView use the mCursorAdapter created above, so that the
        // ListView will display list items.
        itemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(CatalogActivity.this, DetailsActivity.class);

                // Form the content URI that represents the specific item that was clicked on,
                // by appending the"id" (passed as input to this method) onto the
                // {@link ItemEntry#CONTENT_URI}.
                // FOr example, the URI would be "content://"com.example.android.inventoryapp/items/2"
                // If the set with ID 2 was clicked on.
                currentItemUri = ContentUris.withAppendedId(ItemEntry.CONTENT_URI, id);

                intent.setData(currentItemUri);

                startActivity(intent);
            }
        });
        // Restart the loader
        getLoaderManager().restartLoader(ITEM_LOADER, null, this);     
    }
    

    //Helper method to delete all items in the database.
    private void deleteAllItems() {
        int rowsDeleted = getContentResolver().delete(ItemEntry.CONTENT_URI, null, null);
        if (rowsDeleted == 0) {
            // If no rows were deleted, then there was an error with the delete.
            Toast.makeText(this, R.string.delete_items_failed,
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the delete was successful and we can display a toast.
            Toast.makeText(this, R.string.delete_items_successful,
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
                showDeleteConfirmationDialog();
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        ItemDbHelper dbHelper = new ItemDbHelper(getApplicationContext());
        final SQLiteDatabase database = dbHelper.getReadableDatabase();
        long numRows = DatabaseUtils.queryNumEntries(database, ItemEntry.TABLE_NAME);
        if (numRows == 0) {
            Toast.makeText(this, R.string.no_items_to_delete,
                    Toast.LENGTH_SHORT).show();
        } else {
            // Create an AlertDialog.Builder and set the message, and click listeners
            // for the positive and negative buttons on the dialog.
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.delete_dialog_msg_all);
            builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    deleteAllItems();
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
                ItemEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link mCursorAdapter} with this new cursor containing updated data
        mCursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback call when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }
}
