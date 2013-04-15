package de.ronnyfriedland.shoppinglist.db;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Comment;

import de.ronnyfriedland.shoppinglist.entity.Entry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ShoppingListDataSource {

//	SQLiteDatabase database;
//	
//	public ShoppingListDataSource(Context context) {
//		database = new SQLiteOpenHelper(context, "shoppinglist.db", null, 1).getWritableDatabase();
//	}
//
//	public void open() throws SQLException {
//	}
//
//	public void close() {
//		dbHelper.close();
//	}
//
//	public Comment createComment(String comment) {
//		ContentValues values = new ContentValues();
//		values.put(MySQLiteHelper.COLUMN_COMMENT, comment);
//		long insertId = database.insert(MySQLiteHelper.TABLE_COMMENTS, null,
//				values);
//		Cursor cursor = database.query(MySQLiteHelper.TABLE_COMMENTS,
//				allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
//				null, null, null);
//		cursor.moveToFirst();
//		Comment newComment = cursorToComment(cursor);
//		cursor.close();
//		return newComment;
//	}
//
//	public void deleteComment(Comment comment) {
//		long id = comment.getId();
//		System.out.println("Comment deleted with id: " + id);
//		database.delete(MySQLiteHelper.TABLE_COMMENTS, MySQLiteHelper.COLUMN_ID
//				+ " = " + id, null);
//	}
//
//	public List<Comment> getAllComments() {
//		List<Comment> comments = new ArrayList<Comment>();
//
//		Cursor cursor = database.query(MySQLiteHelper.TABLE_COMMENTS,
//				allColumns, null, null, null, null, null);
//
//		cursor.moveToFirst();
//		while (!cursor.isAfterLast()) {
//			Comment comment = cursorToComment(cursor);
//			comments.add(comment);
//			cursor.moveToNext();
//		}
//		// Make sure to close the cursor
//		cursor.close();
//		return comments;
//	}
//
//	private Comment cursorToComment(Cursor cursor) {
//		Entry entry = new Entry();
//		entry.setComment(cursor.getString(1));
//		return entry;
//	}
}
