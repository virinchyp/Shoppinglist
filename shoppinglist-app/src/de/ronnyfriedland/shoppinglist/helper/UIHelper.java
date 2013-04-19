package de.ronnyfriedland.shoppinglist.helper;

import android.graphics.Paint;
import android.widget.TextView;

/**
 * @author ronnyfriedland
 *
 */
public final class UIHelper {

	public static void toggleStrikeThrough(final TextView view, final boolean restore) {
		if(restore){
			view.setPaintFlags(view.getPaintFlags() &~ Paint.STRIKE_THRU_TEXT_FLAG);
		} else {
			view.setPaintFlags(view.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
		}
	}
}
