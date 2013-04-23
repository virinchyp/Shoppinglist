package de.ronnyfriedland.shoppinglist;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import de.ronnyfriedland.shoppinglist.adapter.ShoppingListAdapter;
import de.ronnyfriedland.shoppinglist.db.ShoppingListDataSource;
import de.ronnyfriedland.shoppinglist.entity.Entry;
import de.ronnyfriedland.shoppinglist.entity.Shoppinglist;
import de.ronnyfriedland.shoppinglist.entity.enums.Quantity;
import de.ronnyfriedland.shoppinglist.entity.enums.Status;
import de.ronnyfriedland.shoppinglist.helper.UIHelper;

/**
 * @author Ronny Friedland
 */
@SuppressWarnings("unchecked")
public class MainActivity extends Activity {

    // common (tab)
    TabHost tabHost;
    // tab1
    ListView listView;
    // tab2
    SeekBar seekBar;
    EditText textQuantityValue;
    EditText textUuid;
    Spinner spinnerQuantity;
    AutoCompleteTextView textDescription;
    Button saveButton;
    Button resetButton;

    // tab3

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        saveButton = (Button) findViewById(R.id.buttonSave);
        textQuantityValue = (EditText) findViewById(R.id.textViewQuantityValue);
        spinnerQuantity = (Spinner) findViewById(R.id.spinnerQuantity);
        textDescription = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewEntryDescription);
        listView = (ListView) findViewById(R.id.listViewList);
        tabHost = (TabHost) findViewById(R.id.tabHost);
        resetButton = (Button) findViewById(R.id.buttonReset);
        seekBar = (SeekBar) findViewById(R.id.seekBarQuantityValue);
        textUuid = (EditText) findViewById(R.id.textViewUuid);

        configureTabs();
        configureListView();
        configureNewEntryView();
        configureNewRegistrationView();

        initListTabData();
        initCreateTabData("", 1, 0, "");
    }

    private void initListTabData() {
        Shoppinglist list = ShoppingListDataSource.getInstance(getBaseContext()).getList();
        if (null == list) {
            list = new Shoppinglist();
            ShoppingListDataSource.getInstance(getBaseContext()).createList(list);
        }
        List<Entry> entries = new ArrayList<Entry>();
        entries.addAll(ShoppingListDataSource.getInstance(getBaseContext()).getEntries());
        ((ShoppingListAdapter<Entry>) listView.getAdapter()).addAll(entries);
    }

    private void initCreateTabData(final String uuid, final int quantity, final int quantityUnitRes,
            final String description) {
        textUuid.setText(uuid);
        spinnerQuantity.setSelection(quantityUnitRes);
        textDescription.setText(description);
        seekBar.setProgress(quantity);
        textQuantityValue.setText(String.valueOf(quantity));
    }

    private void configureNewRegistrationView() {

    }

    private void configureNewEntryView() {
        seekBar.setOnSeekBarChangeListener(new ShoppingListSeekBarListener());

        // Create an ArrayAdapter using the string array and a default spinner
        // layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getBaseContext(), R.array.quantity,
                android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerQuantity.setAdapter(adapter);

        textQuantityValue.addTextChangedListener(new ShoppingListTextWatcher());

        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String uuid = textUuid.getText().toString();
                Integer quantityValue = Integer.valueOf(textQuantityValue.getText().toString());
                String quantityUnit = (String) spinnerQuantity.getSelectedItem();
                String description = textDescription.getText().toString();

                Shoppinglist list = ShoppingListDataSource.getInstance(getBaseContext()).getList();

                Entry entry;
                if (null == uuid || "".equals(uuid)) {
                    entry = new Entry();
                } else {
                    entry = new Entry(uuid);
                }
                entry.setQuantity(new Quantity(quantityValue, quantityUnit));
                entry.setDescription(description);
                entry.setList(list);

                if (null == uuid || "".equals(uuid)) {
                    ShoppingListDataSource.getInstance(getBaseContext()).createEntry(entry);
                    ((ShoppingListAdapter<Entry>) listView.getAdapter()).add(entry);
                } else {
                    ShoppingListDataSource.getInstance(getBaseContext()).updateEntry(entry);
                    ((ShoppingListAdapter<Entry>) listView.getAdapter()).update(entry);
                }

                ((ShoppingListAdapter<Entry>) listView.getAdapter()).notifyDataSetChanged();

                tabHost.setCurrentTabByTag(getResources().getString(R.string.list));
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                initCreateTabData("", 1, 0, "");
            }
        });

    }

    private void configureListView() {
        ShoppingListAdapter<Entry> myAdapter = new ShoppingListAdapter<Entry>(getBaseContext(),
                android.R.layout.simple_expandable_list_item_1);
        listView.setAdapter(myAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(new ShoppingListClickListener());
        registerForContextMenu(listView);
    }

    private void configureTabs() {
        tabHost.setup();
        tabHost.setOnTabChangedListener(new OnTabChangeListener() {

            /**
             * {@inheritDoc}
             * 
             * @see android.widget.TabHost.OnTabChangeListener#onTabChanged(java.lang.String)
             */
            @Override
            public void onTabChanged(String arg0) {
                if (getResources().getString(R.string.list).equals(arg0)) {
                    initCreateTabData("", 1, 0, "");
                }
            }

        });

        TabSpec spec1 = tabHost.newTabSpec(getResources().getString(R.string.list));
        spec1.setContent(R.id.tab1);
        spec1.setIndicator(getResources().getString(R.string.list));

        TabSpec spec2 = tabHost.newTabSpec(getResources().getString(R.string.create));
        spec2.setIndicator(getResources().getString(R.string.create));
        spec2.setContent(R.id.tab2);

        TabSpec spec3 = tabHost.newTabSpec(getResources().getString(R.string.register));
        spec3.setIndicator(getResources().getString(R.string.register));
        spec3.setContent(R.id.tab3);

        tabHost.addTab(spec1);
        tabHost.addTab(spec2);
        // tabHost.addTab(spec3);
    }

    /**
     * {@inheritDoc}
     * 
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see android.app.Activity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * {@inheritDoc}
     * 
     * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu,
     *      android.view.View, android.view.ContextMenu.ContextMenuInfo)
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        // menu.setHeaderTitle("Context Menu");
        menu.add(0, v.getId(), 0, getResources().getString(R.string.delete));
        menu.add(0, v.getId(), 1, getResources().getString(R.string.edit));
    }

    /**
     * {@inheritDoc}
     * 
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean ret;
        switch (item.getItemId()) {
        case R.id.exit:
            System.exit(0);
            ret = true;
            break;
        case R.id.clearlistItems: {
            Shoppinglist list = ShoppingListDataSource.getInstance(getBaseContext()).getList();
            ShoppingListDataSource.getInstance(getBaseContext()).deleteEntry(list);
            ShoppingListDataSource.getInstance(getBaseContext()).deleteList(list);
            ((ShoppingListAdapter<Entry>) listView.getAdapter()).clear();
            ((ShoppingListAdapter<Entry>) listView.getAdapter()).notifyDataSetChanged();
            ret = true;
        }
            break;
        case R.id.clearFinishedItems: {
            List<Entry> entries = new ArrayList<Entry>();
            for (int i = 0; i < listView.getAdapter().getCount(); i++) {
                Entry entry = (Entry) listView.getAdapter().getItem(i);
                if (Status.FINISHED.equals(entry.getStatus())) {
                    Log.d(getClass().getCanonicalName(), String.format("Delete entry with id %s.", entry.getUuid()));
                    ShoppingListDataSource.getInstance(getBaseContext()).deleteEntry(entry);
                } else {
                    entries.add(entry);
                }
            }
            if (0 == entries.size()) {
                Shoppinglist list = ShoppingListDataSource.getInstance(getBaseContext()).getList();
                ShoppingListDataSource.getInstance(getBaseContext()).deleteList(list);
            }
            ((ShoppingListAdapter<Entry>) listView.getAdapter()).clear();
            ((ShoppingListAdapter<Entry>) listView.getAdapter()).addAll(entries);
            ((ShoppingListAdapter<Entry>) listView.getAdapter()).notifyDataSetChanged();
            ret = true;
        }
            break;
        default:
            ret = super.onOptionsItemSelected(item);
            break;
        }
        return ret;
    }

    /**
     * {@inheritDoc}
     * 
     * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Entry entry = (Entry) listView.getAdapter().getItem(info.position);
        if (item.getTitle() == getResources().getString(R.string.delete)) {
            Log.d(getClass().getCanonicalName(), String.format("Delete entry with id %s.", entry.getUuid()));

            ShoppingListDataSource.getInstance(getBaseContext()).deleteEntry(entry);
            ((ShoppingListAdapter<Entry>) listView.getAdapter()).remove(entry);
            ((ShoppingListAdapter<Entry>) listView.getAdapter()).notifyDataSetChanged();
        } else if (item.getTitle() == getResources().getString(R.string.edit)) {
            Log.d(getClass().getCanonicalName(), String.format("Edit entry with id %s.", entry.getUuid()));

            int quantityUnitRes = 0;
            String[] quantity = getResources().getStringArray(R.array.quantity);
            for (int i = 0; i < quantity.length; i++) {
                if (entry.getQuantity().getUnit().equals(quantity[i])) {
                    quantityUnitRes = i;
                }
            }

            initCreateTabData(entry.getUuid(), entry.getQuantity().getValue(), quantityUnitRes, entry.getDescription());

            tabHost.setCurrentTabByTag(getResources().getString(R.string.create));
        } else {
            return false;
        }
        return true;
    }

    /**
     * Listener to track changes of the quantity value seekbar
     * 
     * @author Ronny Friedland
     */
    class ShoppingListSeekBarListener implements OnSeekBarChangeListener {

        /**
         * {@inheritDoc}
         * 
         * @see android.widget.SeekBar.OnSeekBarChangeListener#onProgressChanged(android.widget.SeekBar,
         *      int, boolean)
         */
        @Override
        public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
            EditText textAction = (EditText) findViewById(R.id.textViewQuantityValue);
            textAction.setText(String.valueOf(arg1));
        }

        /**
         * {@inheritDoc}
         * 
         * @see android.widget.SeekBar.OnSeekBarChangeListener#onStartTrackingTouch(android.widget.SeekBar)
         */
        @Override
        public void onStartTrackingTouch(SeekBar arg0) {
            // nothing to do
        }

        /**
         * {@inheritDoc}
         * 
         * @see android.widget.SeekBar.OnSeekBarChangeListener#onStopTrackingTouch(android.widget.SeekBar)
         */
        @Override
        public void onStopTrackingTouch(SeekBar arg0) {
            // nothing to do
        }
    }

    /**
     * Listener to track changes of the quantity value textfield
     * 
     * @author Ronny Friedland
     */
    class ShoppingListTextWatcher implements TextWatcher {
        /**
         * {@inheritDoc}
         * 
         * @see android.text.TextWatcher#afterTextChanged(android.text.Editable)
         */
        @Override
        public void afterTextChanged(Editable arg0) {
            int position = arg0.length();
            Selection.setSelection(arg0, position);
        }

        /**
         * {@inheritDoc}
         * 
         * @see android.text.TextWatcher#beforeTextChanged(java.lang.CharSequence,
         *      int, int, int)
         */
        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            // nothing to do
        }

        /**
         * {@inheritDoc}
         * 
         * @see android.text.TextWatcher#onTextChanged(java.lang.CharSequence,
         *      int, int, int)
         */
        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            try {
                seekBar.setProgress(Integer.parseInt(arg0.toString()));
            } catch (NumberFormatException e) {
                seekBar.setProgress(1);
                textQuantityValue.setText("1");
            }
        }

    }

    /**
     * Listener to track clicks on the list view.
     * 
     * @author Ronny Friedland
     */
    class ShoppingListClickListener implements OnItemClickListener {
        /**
         * {@inheritDoc}
         * 
         * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView,
         *      android.view.View, int, long)
         */
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
            ListView list = (ListView) adapterView;
            TextView textView = (TextView) view;

            Entry entry = (Entry) list.getAdapter().getItem(pos);
            if (Status.OPEN == entry.getStatus()) {
                UIHelper.toggleStrikeThrough(textView, false);
                entry.setStatus(Status.FINISHED);
            } else {
                UIHelper.toggleStrikeThrough(textView, true);
                entry.setStatus(Status.OPEN);
            }
            ShoppingListDataSource.getInstance(getBaseContext()).updateEntry(entry);
        }
    }
}
