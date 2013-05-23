package de.ronnyfriedland.shoppinglist.helper;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.widget.TextView;
import de.ronnyfriedland.shoppinglist.R;

/**
 * @author ronnyfriedland
 * 
 */
public final class UIHelper {

    public static void toggleStrikeThrough(final Context context, final TextView view, final boolean restore) {
        if (restore) {
            view.setPaintFlags(view.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            view.setPaintFlags(view.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    public static void markImportant(final Context context, final TextView view, final boolean important) {
        if (important) {
            view.setPaintFlags(view.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
            view.setTextColor(context.getResources().getColor(R.color.important));
        } else {
            view.setPaintFlags(view.getPaintFlags() & ~Paint.FAKE_BOLD_TEXT_FLAG);
            view.setTextColor(context.getResources().getColor(android.R.color.black));
        }
    }

    public static void setFont(final Context context, final TextView view) {
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/brigitte.ttf");
        view.setTypeface(tf);
        view.setTextSize(15);
    }
}
