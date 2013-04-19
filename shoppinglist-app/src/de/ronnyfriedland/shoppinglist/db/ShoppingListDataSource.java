package de.ronnyfriedland.shoppinglist.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.ronnyfriedland.shoppinglist.entity.Entry;
import de.ronnyfriedland.shoppinglist.entity.Quantity;
import de.ronnyfriedland.shoppinglist.entity.Shoppinglist;

/**
 * @author Ronny Friedland
 */
public class ShoppingListDataSource {

	private static final String SHOPPINGLIST_DB_NAME = "shoppinglist.db";
	private static final Integer SHOPPINGLIST_DB_VERSION = 1;
	private static ShoppingListDataSource datasource = null;

	private final SQLiteDatabase database;

	public static ShoppingListDataSource getInstance(final Context context) {
		synchronized (ShoppingListDataSource.class) {
			if (null == datasource) {
				datasource = new ShoppingListDataSource(context);
			}
		}
		return datasource;
	}

	private ShoppingListDataSource(final Context context) {
		SQLiteDatabase.openDatabase(context.getFilesDir().getPath() + "/"
				+ SHOPPINGLIST_DB_NAME, null,
				SQLiteDatabase.CREATE_IF_NECESSARY);

		database = new ShoppingListSqliteHelper(context, SHOPPINGLIST_DB_NAME,
				SHOPPINGLIST_DB_VERSION).getReadableDatabase();
	}

	public void createEntry(final Entry entry) {
		if (null != entry) {
			ContentValues values = new ContentValues();
			values.put(Entry.COL_ID, entry.getUuid());
			values.put(Entry.COL_DESCRIPTION, entry.getDescription());
			values.put(Entry.COL_STATUS, entry.getStatus().name());
			values.put(Entry.COL_QUANTITYVALUE, entry.getQuantity().getValue());
			values.put(Entry.COL_QUANTITY, entry.getQuantity().getUnit());
			values.put(Entry.COL_LIST, entry.getList().getUuid());

			database.beginTransaction();
			try {
				database.insert(Entry.TABLE, null, values);
				database.setTransactionSuccessful();
			} finally {
				database.endTransaction();
			}
		}
	}

	public void updateEntry(final Entry entry) {
		if (null != entry) {
			ContentValues values = new ContentValues();
			values.put(Entry.COL_DESCRIPTION, entry.getDescription());
			values.put(Entry.COL_STATUS, entry.getStatus().name());
			values.put(Entry.COL_QUANTITYVALUE, entry.getQuantity().getValue());
			values.put(Entry.COL_QUANTITY, entry.getQuantity().getUnit());

			database.beginTransaction();
			try {
				database.update(Entry.TABLE, values, Entry.COL_ID + "=?",
						new String[] { entry.getUuid() });
				database.setTransactionSuccessful();
			} finally {
				database.endTransaction();
			}
		}
	}

	public List<Entry> getEntries() {
		List<Entry> entries = new ArrayList<Entry>();
		Cursor cursor = database.query(Entry.TABLE, new String[] {
				Entry.COL_ID, Entry.COL_DESCRIPTION, Entry.COL_STATUS,
				Entry.COL_QUANTITYVALUE, Entry.COL_QUANTITY }, null, null,
				null, null, null);
		try {
			if (cursor.moveToFirst()) {
				do {
					Entry entry = new Entry(cursor.getString(0));
					entry.setStatus(cursor.getString(2));
					entry.setDescription(cursor.getString(1));
					entry.setQuantity(new Quantity(cursor.getInt(3), cursor
							.getString(4)));
					entries.add(entry);
				} while (cursor.moveToNext());
			}
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return entries;
	}

	public void deleteEntry(final Entry entry) {
		if (null != entry) {
			database.beginTransaction();
			try {
				database.delete(Entry.TABLE, Entry.COL_ID + "=?",
						new String[] { entry.getUuid() });
				database.setTransactionSuccessful();
			} finally {
				database.endTransaction();
			}
		}
	}

	public void createList(final Shoppinglist list) {
		if (null != list) {
			database.beginTransaction();
			try {
				database.delete(Shoppinglist.TABLE, null, null);
				database.setTransactionSuccessful();
			} finally {
				database.endTransaction();
			}
		}
	}

	public Shoppinglist getList() {
		Shoppinglist list = null;
		Cursor cursor = database.query(Shoppinglist.TABLE,
				new String[] { Shoppinglist.COL_ID }, null, null, null, null,
				null);
		try {
			if (cursor.moveToFirst()) {
				list = new Shoppinglist(cursor.getString(0));
			}
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return list;
	}

	public void updateList(final Shoppinglist list) {
		if (null != list) {
			ContentValues values = new ContentValues();
			values.put(Shoppinglist.COL_ID, list.getUuid());

			database.beginTransaction();
			try {
				database.update(Shoppinglist.TABLE, values, null, null);
				database.setTransactionSuccessful();
			} finally {
				database.endTransaction();
			}
		}
	}

	public void close() {
		if (null != database) {
			database.close();
		}
	}
}
