package de.ronnyfriedland.shoppinglist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
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
import de.ronnyfriedland.shoppinglist.xml.ReadXMLFile;

/**
 * @author Ronny Friedland
 */
@SuppressWarnings("unchecked")
public class MainActivity extends Activity {

    // constants
    private static final String CURRENT_TAB = "currenttab";

    // gestures
    private GestureDetector gestureScanner;
    private GestureLibrary gestureLib;

    // common (tab)
    private transient TabHost tabHost;
    // tab1
    private transient ListView listView;
    private transient GestureOverlayView gestureOverlayTab1;
    // tab2
    private transient SeekBar seekBar;
    private transient EditText textQuantityValue;
    private transient EditText textUuid;
    private transient Spinner spinnerQuantity;
    private transient CheckBox checkboxImportant;
    private transient AutoCompleteTextView textDescription;
    private transient Button saveButton;
    private transient Button resetButton;
    private transient GestureOverlayView gestureOverlayTab2;

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
            final String description, final boolean important) {
        textUuid.setText(uuid);
        spinnerQuantity.setSelection(quantityUnitRes);
        textDescription.setText(description);
        seekBar.setProgress(quantity);
        textQuantityValue.setText(String.valueOf(quantity));
        checkboxImportant.setChecked(important);
    }

    private void configureNewEntryView() {
        seekBar.setOnSeekBarChangeListener(new ShoppingListSeekBarListener());

        textQuantityValue.addTextChangedListener(new ShoppingListTextWatcher());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getBaseContext(), R.array.quantity,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerQuantity.setAdapter(adapter);

        Collection<String> list = ReadXMLFile.parseFile(getResources().getString(R.raw.description));
        textDescription.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, list
                .toArray(new String[list.size()])));

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uuid = textUuid.getText().toString();
                String quantity = textQuantityValue.getText().toString();

                Integer quantityValue = "".equals(quantity) ? 0 : Integer.valueOf(textQuantityValue.getText()
                        .toString());
                String quantityUnit = (String) spinnerQuantity.getSelectedItem();
                String description = textDescription.getText().toString();
                Boolean important = checkboxImportant.isChecked();

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
                entry.setImportant(important);

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
            @Override
            public void onClick(View v) {
                initCreateTabData("", 1, 0, "", false);
            }
        });

        gestureOverlayTab2.addOnGesturePerformedListener(new ShoppingListGestureListener());
    }

    private void configureListView() {
        ShoppingListAdapter<Entry> myAdapter = new ShoppingListAdapter<Entry>(getBaseContext(),
                android.R.layout.simple_expandable_list_item_1);
        listView.setAdapter(myAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(new ShoppingListClickListener());
        registerForContextMenu(listView);

        gestureOverlayTab1.addOnGesturePerformedListener(new ShoppingListGestureListener());
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
                    initCreateTabData("", 1, 0, "", false);
                }
            }
        });

        TabSpec spec1 = tabHost.newTabSpec(getResources().getString(R.string.list));
        spec1.setContent(R.id.tab1);
        spec1.setIndicator(getResources().getString(R.string.list));

        TabSpec spec2 = tabHost.newTabSpec(getResources().getString(R.string.create));
        spec2.setIndicator(getResources().getString(R.string.create));
        spec2.setContent(R.id.tab2);

        tabHost.addTab(spec1);
        tabHost.addTab(spec2);
    }

    private void clearItems() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder.setMessage(R.string.confirmDelete)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i(getClass().getCanonicalName(), "Clear list");
                        Shoppinglist list = ShoppingListDataSource.getInstance(getBaseContext()).getList();
                        ShoppingListDataSource.getInstance(getBaseContext()).deleteEntry(list);
                        ShoppingListDataSource.getInstance(getBaseContext()).deleteList();
                        ((ShoppingListAdapter<Entry>) listView.getAdapter()).clear();
                        ((ShoppingListAdapter<Entry>) listView.getAdapter()).notifyDataSetChanged();
                    }
                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i(getClass().getCanonicalName(), "Clear list canceled");
                    }
                }).create();
        dialog.show();
    }

    private void clearFinishedItems() {
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
            ShoppingListDataSource.getInstance(getBaseContext()).deleteList();
        }
        ((ShoppingListAdapter<Entry>) listView.getAdapter()).clear();
        ((ShoppingListAdapter<Entry>) listView.getAdapter()).addAll(entries);
        ((ShoppingListAdapter<Entry>) listView.getAdapter()).notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        saveButton = (Button) findViewById(R.id.buttonSave);
        textQuantityValue = (EditText) findViewById(R.id.textViewQuantityValue);
        spinnerQuantity = (Spinner) findViewById(R.id.spinnerQuantity);
        checkboxImportant = (CheckBox) findViewById(R.id.checkBoxImportant);
        textDescription = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewEntryDescription);
        listView = (ListView) findViewById(R.id.listViewList);
        tabHost = (TabHost) findViewById(R.id.tabHost);
        resetButton = (Button) findViewById(R.id.buttonReset);
        seekBar = (SeekBar) findViewById(R.id.seekBarQuantityValue);
        textUuid = (EditText) findViewById(R.id.textViewUuid);
        gestureOverlayTab1 = (GestureOverlayView) findViewById(R.id.gestureOverlayTab1);
        gestureOverlayTab2 = (GestureOverlayView) findViewById(R.id.gestureOverlayTab2);

        gestureLib = GestureLibraries.fromRawResource(getBaseContext(), R.raw.gestures);
        if (!gestureLib.load()) {
            finish();
        }
        configureTabs();
        configureListView();
        configureNewEntryView();

        initListTabData();
        initCreateTabData("", 1, 0, "", false);
    }

    /**
     * {@inheritDoc}
     * 
     * @see android.app.Activity#onTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onTouchEvent(MotionEvent me) {
        boolean ret = false;
        try {
            ret = gestureScanner.onTouchEvent(me);
        } catch (Exception e) {
            Log.e(getClass().getCanonicalName(), "Got exception on touch event.", e);
        }
        return ret;
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
            finish();
            ret = true;
            break;
        case R.id.clearlistItems: {
            clearItems();
            ret = true;
        }
            break;
        case R.id.clearFinishedItems: {
            clearFinishedItems();
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
        boolean result = false;
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Entry entry = (Entry) listView.getAdapter().getItem(info.position);
        if (item.getTitle() == getResources().getString(R.string.delete)) {
            Log.d(getClass().getCanonicalName(), String.format("Delete entry with id %s.", entry.getUuid()));

            ShoppingListDataSource.getInstance(getBaseContext()).deleteEntry(entry);
            ((ShoppingListAdapter<Entry>) listView.getAdapter()).remove(entry);
            ((ShoppingListAdapter<Entry>) listView.getAdapter()).notifyDataSetChanged();

            result = true;
        } else if (item.getTitle() == getResources().getString(R.string.edit)) {
            Log.d(getClass().getCanonicalName(), String.format("Edit entry with id %s.", entry.getUuid()));

            int quantityUnitRes = 0;
            String[] quantity = getResources().getStringArray(R.array.quantity);
            for (int i = 0; i < quantity.length; i++) {
                if (entry.getQuantity().getUnit().equals(quantity[i])) {
                    quantityUnitRes = i;
                }
            }

            initCreateTabData(entry.getUuid(), entry.getQuantity().getValue(), quantityUnitRes, entry.getDescription(),
                    entry.getImportant());
            tabHost.setCurrentTabByTag(getResources().getString(R.string.create));

            result = true;
        }
        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(CURRENT_TAB, tabHost.getCurrentTab());
    }

    /**
     * {@inheritDoc}
     * 
     * @see android.app.Activity#onRestoreInstanceState(android.os.Bundle)
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        tabHost.setCurrentTab(savedInstanceState.getInt(CURRENT_TAB));
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
                UIHelper.toggleStrikeThrough(getBaseContext(), textView, false);
                entry.setStatus(Status.FINISHED);
            } else {
                UIHelper.toggleStrikeThrough(getBaseContext(), textView, true);
                entry.setStatus(Status.OPEN);
            }
            ShoppingListDataSource.getInstance(getBaseContext()).updateEntry(entry);
        }
    }

    /**
     * @author Ronny Friedland
     */
    class ShoppingListGestureListener implements OnGesturePerformedListener {
        @Override
        public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
            ArrayList<Prediction> predictions = gestureLib.recognize(gesture);
            if (predictions.size() > 0 && predictions.get(0).score > 1.0) {
                String result = predictions.get(0).name;
                if ("clearall".equalsIgnoreCase(result)) {
                    int currentTab = tabHost.getCurrentTab();
                    if (0 == currentTab) {
                        clearItems();
                    }
                } else if ("clearfinished".equalsIgnoreCase(result)) {
                    int currentTab = tabHost.getCurrentTab();
                    if (0 == currentTab) {
                        clearFinishedItems();
                    }
                } else {
                    // move in left
                    TranslateAnimation animLeftIn = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 1.0f,
                            Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
                            Animation.RELATIVE_TO_PARENT, 0.0f);
                    animLeftIn.setDuration(180);
                    animLeftIn.setInterpolator(new AccelerateInterpolator());
                    // move out left
                    TranslateAnimation animLeftOut = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
                            Animation.RELATIVE_TO_PARENT, -1.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
                            Animation.RELATIVE_TO_PARENT, 0.0f);
                    animLeftOut.setDuration(180);
                    animLeftOut.setInterpolator(new AccelerateInterpolator());
                    // move in right
                    TranslateAnimation animRightIn = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -1.0f,
                            Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
                            Animation.RELATIVE_TO_PARENT, 0.0f);
                    animRightIn.setDuration(180);
                    animRightIn.setInterpolator(new AccelerateInterpolator());
                    // move out right
                    TranslateAnimation animRightOut = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
                            Animation.RELATIVE_TO_PARENT, 1.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
                            Animation.RELATIVE_TO_PARENT, 0.0f);
                    animRightOut.setDuration(180);
                    animRightOut.setInterpolator(new AccelerateInterpolator());

                    if ("moveleft".equalsIgnoreCase(result)) {
                        int currentTab = tabHost.getCurrentTab();
                        int childCount = tabHost.getTabWidget().getChildCount();
                        View currentView = tabHost.getCurrentView();
                        if (currentTab > 0) {
                            tabHost.setCurrentTab(currentTab - 1);
                        } else {
                            tabHost.setCurrentTab(childCount - 1);
                        }
                        View nextView = tabHost.getCurrentView();
                        currentView.setAnimation(animLeftOut);
                        nextView.setAnimation(animLeftIn);
                    } else if ("moveright".equalsIgnoreCase(result)) {
                        int currentTab = tabHost.getCurrentTab();
                        int childCount = tabHost.getTabWidget().getChildCount();
                        View currentView = tabHost.getCurrentView();
                        if (childCount - 1 > currentTab) {
                            tabHost.setCurrentTab(currentTab + 1);
                        } else {
                            tabHost.setCurrentTab(0);
                        }
                        View nextView = tabHost.getCurrentView();
                        currentView.setAnimation(animRightOut);
                        nextView.setAnimation(animRightIn);
                    }
                }
            }
        }
    }
}
