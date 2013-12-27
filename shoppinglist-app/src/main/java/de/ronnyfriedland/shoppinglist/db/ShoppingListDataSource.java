package de.ronnyfriedland.shoppinglist.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import de.ronnyfriedland.shoppinglist.entity.Entry;
import de.ronnyfriedland.shoppinglist.entity.Shoppinglist;
import de.ronnyfriedland.shoppinglist.entity.enums.Quantity;
import de.ronnyfriedland.shoppinglist.entity.enums.Status;

/**
 * @author Ronny Friedland
 */
public class ShoppingListDataSource extends SQLiteOpenHelper {

    private static final String SHOPPINGLIST_DB_NAME = "shoppinglist.db";
    private static final Integer SHOPPINGLIST_DB_VERSION = 4;
    private static ShoppingListDataSource datasource = null;

    /**
     * Return the (single) instance of {@link ShoppingListDataSource}.
     * 
     * @param context
     *            the base context of the app
     * @return instance of {@link ShoppingListDataSource}
     */
    public static ShoppingListDataSource getInstance(final Context context) {
        synchronized (ShoppingListDataSource.class) {
            if (null == datasource) {
                datasource = new ShoppingListDataSource(context);
            }
        }
        return datasource;
    }

    /**
     * {@inheritDoc}
     * 
     * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
     */
    @Override
    public void onCreate(final SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + Entry.TABLE + "(" + Entry.COL_ID + " text primary key, "
                + Entry.COL_DESCRIPTION + " text not null," + Entry.COL_STATUS + " text not null, "
                + Entry.COL_QUANTITYVALUE + " integer not null, " + Entry.COL_QUANTITY + " text not null,"
                + Entry.COL_LIST + " list not null)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + Shoppinglist.TABLE + "(" + Shoppinglist.COL_ID
                + " text primary key)");

        db.execSQL("ALTER TABLE " + Entry.TABLE + " ADD COLUMN " + Entry.COL_IMPORTANT + " integer not null default(0)");

        db.execSQL("ALTER TABLE " + Entry.TABLE + " ADD COLUMN " + Entry.COL_IMAGE + " blob");
    }

    /**
     * {@inheritDoc}
     * 
     * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase,
     *      int, int)
     */
    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + Entry.TABLE + " ADD COLUMN " + Entry.COL_IMPORTANT
                    + " integer not null default(0)");
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + Entry.TABLE + " ADD COLUMN " + Entry.COL_IMAGE + " blob");
        }
    }

    private ShoppingListDataSource(final Context context) {
        super(context, SHOPPINGLIST_DB_NAME, null, SHOPPINGLIST_DB_VERSION);
    }

    /**
     * Creates a new {@link Entry}.
     * 
     * @param entry
     *            the entry to persist
     */
    public void createEntry(final Entry entry) {
        if (null != entry) {
            ContentValues values = new ContentValues();
            values.put(Entry.COL_ID, entry.getUuid());
            values.put(Entry.COL_DESCRIPTION, entry.getDescription());
            values.put(Entry.COL_STATUS, entry.getStatus().name());
            values.put(Entry.COL_QUANTITYVALUE, entry.getQuantity().getValue());
            values.put(Entry.COL_QUANTITY, entry.getQuantity().getUnit());
            values.put(Entry.COL_IMPORTANT, entry.getImportant());
            values.put(Entry.COL_LIST, entry.getList().getUuid());
            values.put(Entry.COL_IMAGE, entry.getImage());

            SQLiteDatabase database = getWritableDatabase();
            database.beginTransaction();
            try {
                database.insert(Entry.TABLE, null, values);
                database.setTransactionSuccessful();
            } finally {
                database.endTransaction();
                database.close();
            }
        }
    }

    /**
     * Updates the given entry based on the uuid.
     * 
     * @param entry
     *            the {@link Entry} to update
     */
    public void updateEntry(final Entry entry) {
        if (null != entry) {
            ContentValues values = new ContentValues();
            values.put(Entry.COL_DESCRIPTION, entry.getDescription());
            values.put(Entry.COL_STATUS, entry.getStatus().name());
            values.put(Entry.COL_QUANTITYVALUE, entry.getQuantity().getValue());
            values.put(Entry.COL_QUANTITY, entry.getQuantity().getUnit());
            values.put(Entry.COL_IMPORTANT, entry.getImportant());
            values.put(Entry.COL_IMAGE, entry.getImage());

            SQLiteDatabase database = getWritableDatabase();
            database.beginTransaction();
            try {
                database.update(Entry.TABLE, values, Entry.COL_ID + "=?", new String[] { entry.getUuid() });
                database.setTransactionSuccessful();
            } finally {
                database.endTransaction();
                database.close();
            }
        }
    }

    /**
     * Retrieves a list of {@link Entry} stored in the database
     * 
     * @return list of all entries
     */
    public List<Entry> getEntries() {
        List<Entry> entries = new ArrayList<Entry>();
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query(Entry.TABLE, new String[] { Entry.COL_ID, Entry.COL_DESCRIPTION,
                Entry.COL_STATUS, Entry.COL_QUANTITYVALUE, Entry.COL_QUANTITY, Entry.COL_IMPORTANT, Entry.COL_LIST,
                Entry.COL_IMAGE }, null, null, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Entry entry = new Entry(cursor.getString(0));
                    entry.setStatus(cursor.getString(2));
                    entry.setDescription(cursor.getString(1));
                    entry.setQuantity(new Quantity(cursor.getInt(3), cursor.getString(4)));
                    entry.setImportant(cursor.getInt(5));
                    entry.setList(new Shoppinglist(cursor.getString(6)));
                    entry.setImage(cursor.getBlob(7));
                    entries.add(entry);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            database.close();
        }
        return entries;
    }

    /**
     * Retrieves the current {@link Entry}.
     * 
     * @param uuid
     *            the uuid of the entry
     * 
     * @return the current {@link Entry}
     */
    public Entry getEntry(final String uuid) {
        Entry entry = null;
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query(Entry.TABLE, new String[] { Entry.COL_ID, Entry.COL_DESCRIPTION,
                Entry.COL_STATUS, Entry.COL_QUANTITYVALUE, Entry.COL_QUANTITY, Entry.COL_IMPORTANT, Entry.COL_LIST,
                Entry.COL_IMAGE }, Entry.COL_ID + "=?", new String[] { uuid }, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                entry = new Entry(cursor.getString(0));
                entry.setDescription(cursor.getString(1));
                entry.setStatus(Status.valueOf(cursor.getString(2)));
                entry.setQuantity(new Quantity(cursor.getInt(3), cursor.getString(4)));
                entry.setImportant(cursor.getInt(5));
                entry.setList(new Shoppinglist(cursor.getString(6)));
                entry.setImage(cursor.getBlob(7));
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            database.close();
        }
        return entry;
    }

    /**
     * Deletes the given {@link Entry}.
     * 
     * @param entry
     *            the {@link Entry} to delete
     */
    public void deleteEntry(final Entry entry) {
        if (null != entry) {
            SQLiteDatabase database = getWritableDatabase();
            database.beginTransaction();
            try {
                database.delete(Entry.TABLE, Entry.COL_ID + "=?", new String[] { entry.getUuid() });
                database.setTransactionSuccessful();
            } finally {
                database.endTransaction();
                database.close();
            }
        }
    }

    /**
     * Deletes every {@link Entry} which is associated to the given
     * {@link Shoppinglist}.
     * 
     * @param list
     *            the associated {@link Shoppinglist}.
     */
    public void deleteEntry(final Shoppinglist list) {
        if (null != list) {
            SQLiteDatabase database = getWritableDatabase();
            database.beginTransaction();
            try {
                database.delete(Entry.TABLE, Entry.COL_LIST + "=?", new String[] { list.getUuid() });
                database.setTransactionSuccessful();
            } finally {
                database.endTransaction();
                database.close();
            }
        }
    }

    /**
     * Deletes the {@link Shoppinglist} given as parameter.
     */
    public void deleteList() {
        SQLiteDatabase database = getWritableDatabase();
        database.beginTransaction();
        try {
            database.delete(Shoppinglist.TABLE, null, null);
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
            database.close();
        }
    }

    /**
     * Retrieves the current {@link Shoppinglist}.
     * 
     * @return the current {@link Shoppinglist}
     */
    public Shoppinglist getList() {
        Shoppinglist list = null;
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query(Shoppinglist.TABLE, new String[] { Shoppinglist.COL_ID }, null, null, null,
                null, null);
        try {
            if (cursor.moveToFirst()) {
                list = new Shoppinglist(cursor.getString(0));
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            database.close();
        }
        return list;
    }

    /**
     * Creates a new {@link Shoppinglist}.
     * 
     * @param list
     *            the {@link Shoppinglist} to create
     */
    public void createList(final Shoppinglist list) {
        if (null != list) {
            ContentValues values = new ContentValues();
            values.put(Shoppinglist.COL_ID, list.getUuid());

            SQLiteDatabase database = getWritableDatabase();
            database.beginTransaction();
            try {
                database.insert(Shoppinglist.TABLE, null, values);
                database.setTransactionSuccessful();
            } finally {
                database.endTransaction();
                database.close();
            }
        }
    }
}
