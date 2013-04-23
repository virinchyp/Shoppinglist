package de.ronnyfriedland.shoppinglist.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
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

    /**
     * Creates a new {@link ShoppingListAdapter}
     * 
     * @param context
     *            the base context
     * @param resource
     *            the resource
     * @param entries
     *            inital list of entries
     */
    public ShoppingListAdapter(Context context, int resource, Collection<T> entries) {
        super(context, resource);
        addAll(entries);
    }

    /**
     * Creates a new {@link ShoppingListAdapter}
     * 
     * @param context
     *            the base context
     * @param resource
     *            the resource
     */
    public ShoppingListAdapter(Context context, int resource) {
        super(context, resource);
    }

    /**
     * {@inheritDoc}
     * 
     * @see android.widget.ArrayAdapter#getCount()
     */
    @Override
    public int getCount() {
        return entries.size();
    }

    /**
     * {@inheritDoc}
     * 
     * @see android.widget.ArrayAdapter#getItem(int)
     */
    @Override
    public T getItem(int position) {
        return entries.get(position);
    }

    /**
     * {@inheritDoc}
     * 
     * @see android.widget.ArrayAdapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * {@inheritDoc}
     * 
     * @see android.widget.ArrayAdapter#getView(int, android.view.View,
     *      android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getView(position, convertView, parent);
        UIHelper.setFont(getContext(), view);

        Entry value = getItem(position);
        if (Status.FINISHED == value.getStatus()) {
            UIHelper.toggleStrikeThrough(view, false);
        } else {
            UIHelper.toggleStrikeThrough(view, true);
        }

        return view;
    }

    /**
     * {@inheritDoc}
     * 
     * @see android.widget.ArrayAdapter#addAll(java.util.Collection)
     */
    public void addAll(Collection<? extends T> collection) {
        entries.addAll(collection);
    }

    /**
     * {@inheritDoc}
     * 
     * @see android.widget.ArrayAdapter#add(java.lang.Object)
     */
    @Override
    public void add(T entry) {
        entries.add(entry);
    }

    /**
     * {@inheritDoc}
     * 
     * @see android.widget.ArrayAdapter#remove(java.lang.Object)
     */
    @Override
    public void remove(T entry) {
        entries.remove(entry);
    };

    /**
     * Update the given entry in the list.
     * 
     * @param entry
     *            the list entry to update
     */
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

    /**
     * {@inheritDoc}
     * 
     * @see android.widget.ArrayAdapter#clear()
     */
    @Override
    public void clear() {
        entries.clear();
    }
}
