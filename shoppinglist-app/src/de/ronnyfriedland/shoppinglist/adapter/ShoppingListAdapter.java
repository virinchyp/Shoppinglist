package de.ronnyfriedland.shoppinglist.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.ronnyfriedland.shoppinglist.entity.Entry;
import de.ronnyfriedland.shoppinglist.entity.enums.Status;
import de.ronnyfriedland.shoppinglist.helper.UIHelper;

/**
 * @author Ronny Friedland
 * 
 * @param <T>
 */
public class ShoppingListAdapter<T extends Entry> extends ArrayAdapter<T> {

    private final List<T> entries = new ArrayList<T>();

    public ShoppingListAdapter(Context context, int resource, Collection<T> entries) {
        super(context, resource);
        addAll(entries);
    }

    @Override
    public int getCount() {
        return entries.size();
    }

    @Override
    public T getItem(int position) {
        return entries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getView(position, convertView, parent);
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/karine.ttf");
        view.setTypeface(tf);

        Entry value = getItem(position);
        if (Status.FINISHED == value.getStatus()) {
            UIHelper.toggleStrikeThrough(view, false);
        } else {
            UIHelper.toggleStrikeThrough(view, true);
        }

        return view;
    }

    public void addAll(Collection<? extends T> collection) {
        entries.addAll(collection);
    }

    @Override
    public void add(T entry) {
        entries.add(entry);
    }

    @Override
    public void remove(T entry) {
        entries.remove(entry);
    };

    public void update(T entry) {
        if (null != entry) {
            for (Entry existingEntry : entries) {
                if (existingEntry.getUuid().equals(entry.getUuid())) {
                    existingEntry.setDescription(entry.getDescription());
                    existingEntry.setQuantity(entry.getQuantity());
                    break;
                }
            }
        }
    };

    @Override
    public void clear() {
        entries.clear();
    }
}
