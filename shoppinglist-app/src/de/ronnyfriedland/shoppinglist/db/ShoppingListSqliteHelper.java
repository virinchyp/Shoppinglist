package de.ronnyfriedland.shoppinglist.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import de.ronnyfriedland.shoppinglist.entity.Entry;
import de.ronnyfriedland.shoppinglist.entity.Shoppinglist;

public class ShoppingListSqliteHelper extends SQLiteOpenHelper {

	public ShoppingListSqliteHelper(Context context, String dbName, int version) {
		super(context, dbName, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS " + Entry.TABLE + "("
				+ Entry.COL_ID + " text primary key, " + Entry.COL_DESCRIPTION
				+ " text not null," + Entry.COL_STATUS + " text not null, "
				+ Entry.COL_QUANTITYVALUE + " integer not null, "
				+ Entry.COL_QUANTITY + " text not null,"
				+ Entry.COL_LIST + " list not null)");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS " + Shoppinglist.TABLE + "("
				+ Shoppinglist.COL_ID + " text not null)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

}
