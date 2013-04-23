package de.ronnyfriedland.shoppinglist.helper;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.widget.TextView;

/**
 * @author ronnyfriedland
 * 
 */
public final class UIHelper {

    public static void toggleStrikeThrough(final TextView view, final boolean restore) {
        if (restore) {
            view.setPaintFlags(view.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            view.setPaintFlags(view.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    public static void setFont(final Context context, final TextView view) {
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/karine.ttf");
        view.setTypeface(tf);
    }
}
