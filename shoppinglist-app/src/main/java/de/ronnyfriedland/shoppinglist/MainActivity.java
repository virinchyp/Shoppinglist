package de.ronnyfriedland.shoppinglist;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;
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

    // notification
    private NotificationManager notificationManager;

    // common (tab)
    private transient TabHost tabHost;
    // tab1
    private transient ListView listView;
    private transient GestureOverlayView gestureOverlayTab1;
    // tab2
    private transient SeekBar seekBar;
    private transient EditText textQuantityValue;
    private transient EditText textUuid;
    private transient EditText textImage;
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
        ((ShoppingListAdapter<Entry>) listView.getAdapter()).notifyDataSetChanged();
    }

    private void initCreateTabData(final String uuid, final int quantity, final int quantityUnitRes,
            final String description, final String imagePath, final boolean important) {
        textUuid.setText(uuid);
        textImage.setText(imagePath);
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
        textDescription.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list
                .toArray(new String[list.size()])));

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        String uuid = textUuid.getText().toString();
                        String imagePath = textImage.getText().toString();
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
                            entry = ShoppingListDataSource.getInstance(getBaseContext()).getEntry(uuid);
                        }
                        entry.setQuantity(new Quantity(quantityValue, quantityUnit));
                        entry.setDescription(description);
                        entry.setList(list);
                        entry.setImportant(important);

                        if (null != imagePath && !"".equals(imagePath)) {
                            Bitmap image = BitmapFactory.decodeFile(imagePath);

                            DisplayMetrics metrics = new DisplayMetrics();
                            getWindowManager().getDefaultDisplay().getMetrics(metrics);

                            int width = (int) (metrics.widthPixels * 0.75);
                            int height = (int) (metrics.heightPixels * 0.75);

                            Bitmap resizedBitmap = Bitmap.createScaledBitmap(image, width, height, false);
                            ByteArrayOutputStream buffer = new ByteArrayOutputStream(resizedBitmap.getWidth()
                                    * resizedBitmap.getHeight());
                            resizedBitmap.compress(CompressFormat.PNG, 90, buffer);
                            entry.setImage(buffer.toByteArray());
                        }

                        if (null == uuid || "".equals(uuid)) {
                            ShoppingListDataSource.getInstance(getBaseContext()).createEntry(entry);
                            ((ShoppingListAdapter<Entry>) listView.getAdapter()).add(entry);
                        } else {
                            ShoppingListDataSource.getInstance(getBaseContext()).updateEntry(entry);
                            ((ShoppingListAdapter<Entry>) listView.getAdapter()).update(entry);
                        }
                    }
                });

                ((ShoppingListAdapter<Entry>) listView.getAdapter()).notifyDataSetChanged();

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                Toast.makeText(getBaseContext(), getResources().getString(R.string.createSuccess), Toast.LENGTH_LONG)
                        .show();

                initCreateTabData("", 1, 0, "", "", false);
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                initCreateTabData("", 1, 0, "", "", false);
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
            public void onTabChanged(final String arg0) {
                if (getResources().getString(R.string.list).equals(arg0)) {
                    initCreateTabData("", 1, 0, "", "", false);
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
                    public void onClick(final DialogInterface dialog, final int id) {
                        if (Log.isLoggable(getClass().getSimpleName(), Log.DEBUG)) {
                            Log.d(getClass().getSimpleName(), "Clear list");
                        }
                        Shoppinglist list = ShoppingListDataSource.getInstance(getBaseContext()).getList();
                        ShoppingListDataSource.getInstance(getBaseContext()).deleteEntry(list);
                        ShoppingListDataSource.getInstance(getBaseContext()).deleteList();
                        ((ShoppingListAdapter<Entry>) listView.getAdapter()).clear();
                        ((ShoppingListAdapter<Entry>) listView.getAdapter()).notifyDataSetChanged();

                        Toast.makeText(getBaseContext(), getResources().getString(R.string.deleteSuccess),
                                Toast.LENGTH_LONG).show();
                    }
                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int id) {
                        if (Log.isLoggable(getClass().getSimpleName(), Log.DEBUG)) {
                            Log.d(getClass().getSimpleName(), "Clear list canceled");
                        }
                    }
                }).create();
        dialog.show();
    }

    private void clearFinishedItems() {
        List<Entry> entries = new ArrayList<Entry>();
        for (int i = 0; i < listView.getAdapter().getCount(); i++) {
            Entry entry = (Entry) listView.getAdapter().getItem(i);
            if (Status.FINISHED.equals(entry.getStatus())) {
                if (Log.isLoggable(getClass().getSimpleName(), Log.DEBUG)) {
                    Log.d(getClass().getSimpleName(), String.format("Delete entry with id %s.", entry.getUuid()));
                }
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

        Toast.makeText(getBaseContext(), getResources().getString(R.string.clearEntriesSuccess), Toast.LENGTH_LONG)
                .show();
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
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
        textImage = (EditText) findViewById(R.id.textViewImage);
        gestureOverlayTab1 = (GestureOverlayView) findViewById(R.id.gestureOverlayTab1);
        gestureOverlayTab2 = (GestureOverlayView) findViewById(R.id.gestureOverlayTab2);

        gestureLib = GestureLibraries.fromRawResource(getBaseContext(), R.raw.gestures);
        gestureLib.load();

        notificationManager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);

        configureTabs();
        configureListView();
        configureNewEntryView();

        initListTabData();
        initCreateTabData("", 1, 0, "", "", false);
        initTimer();
        initImage();
    }

    private void initImage() {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
                if (imageUri != null) {
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };
                    Cursor cursor = getContentResolver().query(imageUri, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    initCreateTabData("", 1, 0, "", cursor.getString(columnIndex), false);
                    tabHost.setCurrentTabByTag(getResources().getString(R.string.create));
                }
            }
        }
    }

    private void initTimer() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Timer notificationTimer = new Timer();
        notificationTimer.schedule(new TimerTask() {
            /**
             * {@inheritDoc}
             * 
             * @see java.util.TimerTask#run()
             */
            @Override
            public void run() {
                int importantCount = 0;
                List<Entry> entries = ShoppingListDataSource.getInstance(getBaseContext()).getEntries();
                for (Entry entry : entries) {
                    if (entry.getImportant() && Status.OPEN.equals(entry.getStatus())) {
                        importantCount++;
                    }
                }
                if (0 < importantCount) { // only if important entries found
                    PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(), 0, getIntent(), 0);
                    Notification notification = new NotificationCompat.Builder(getBaseContext())
                            .setContentTitle(getResources().getString(R.string.app_name))
                            .setContentText(getResources().getString(R.string.notificationImportantEntryCount))
                            .setSmallIcon(R.drawable.ic_launcher).setContentIntent(contentIntent)
                            .setOnlyAlertOnce(true).build();
                    notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
                    notificationManager.notify(0, notification);
                }
            }
        }, cal.getTime(), 1000 * 60 * 60 * 24); // show every day

    }

    /**
     * {@inheritDoc}
     * 
     * @see android.app.Activity#onTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onTouchEvent(final MotionEvent me) {
        boolean ret = false;
        try {
            ret = gestureScanner.onTouchEvent(me);
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Got exception on touch event.", e);
        }
        return ret;
    }

    /**
     * {@inheritDoc}
     * 
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
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
    public void onCreateContextMenu(final ContextMenu menu, final View v, final ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        // menu.setHeaderTitle("Context Menu");
        menu.add(0, v.getId(), 0, getResources().getString(R.string.show));
        menu.add(0, v.getId(), 1, getResources().getString(R.string.edit));
        menu.add(0, v.getId(), 2, getResources().getString(R.string.delete));
    }

    /**
     * {@inheritDoc}
     * 
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
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
    public boolean onContextItemSelected(final MenuItem item) {
        boolean result = false;
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Entry entry = (Entry) listView.getAdapter().getItem(info.position);
        if (item.getTitle() == getResources().getString(R.string.delete)) {
            if (Log.isLoggable(getClass().getSimpleName(), Log.DEBUG)) {
                Log.d(getClass().getSimpleName(), String.format("Delete entry with id %s.", entry.getUuid()));
            }

            ShoppingListDataSource.getInstance(getBaseContext()).deleteEntry(entry);
            ((ShoppingListAdapter<Entry>) listView.getAdapter()).remove(entry);
            ((ShoppingListAdapter<Entry>) listView.getAdapter()).notifyDataSetChanged();

            result = true;
        } else if (item.getTitle() == getResources().getString(R.string.edit)) {
            if (Log.isLoggable(getClass().getSimpleName(), Log.DEBUG)) {
                Log.d(getClass().getSimpleName(), String.format("Edit entry with id %s.", entry.getUuid()));
            }
            int quantityUnitRes = 0;
            String[] quantity = getResources().getStringArray(R.array.quantity);
            for (int i = 0; i < quantity.length; i++) {
                if (entry.getQuantity().getUnit().equals(quantity[i])) {
                    quantityUnitRes = i;
                }
            }

            initCreateTabData(entry.getUuid(), entry.getQuantity().getValue(), quantityUnitRes, entry.getDescription(),
                    "", entry.getImportant());
            tabHost.setCurrentTabByTag(getResources().getString(R.string.create));

            result = true;
        } else if (item.getTitle() == getResources().getString(R.string.show)) {
            if (Log.isLoggable(getClass().getSimpleName(), Log.DEBUG)) {
                Log.d(getClass().getSimpleName(), String.format("Show image for entry with id %s.", entry.getUuid()));
            }
            byte[] image = entry.getImage();

            if (null != image) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                ImageView pictureView = new ImageView(getBaseContext());
                pictureView.setScaleType(ScaleType.FIT_XY);
                pictureView.setImageBitmap(bitmap);
                pictureView.setMinimumWidth(bitmap.getHeight());
                pictureView.setMinimumHeight(bitmap.getWidth());
                // build dialog
                Builder builder = new AlertDialog.Builder(this);
                builder.setView(pictureView);
                // show dialog
                Dialog dialog = builder.create();
                dialog.show();
                result = true;
            } else {
                Toast.makeText(getBaseContext(), R.string.noImage, Toast.LENGTH_SHORT).show();
                result = false;
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(final Bundle savedInstanceState) {
        savedInstanceState.putInt(CURRENT_TAB, tabHost.getCurrentTab());
    }

    /**
     * {@inheritDoc}
     * 
     * @see android.app.Activity#onRestoreInstanceState(android.os.Bundle)
     */
    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
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
        public void onProgressChanged(final SeekBar arg0, final int arg1, final boolean arg2) {
            EditText textAction = (EditText) findViewById(R.id.textViewQuantityValue);
            textAction.setText(String.valueOf(arg1));
        }

        /**
         * {@inheritDoc}
         * 
         * @see android.widget.SeekBar.OnSeekBarChangeListener#onStartTrackingTouch(android.widget.SeekBar)
         */
        @Override
        public void onStartTrackingTouch(final SeekBar arg0) {
            // nothing to do
        }

        /**
         * {@inheritDoc}
         * 
         * @see android.widget.SeekBar.OnSeekBarChangeListener#onStopTrackingTouch(android.widget.SeekBar)
         */
        @Override
        public void onStopTrackingTouch(final SeekBar arg0) {
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
        public void afterTextChanged(final Editable arg0) {
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
        public void beforeTextChanged(final CharSequence arg0, final int arg1, final int arg2, final int arg3) {
            // nothing to do
        }

        /**
         * {@inheritDoc}
         * 
         * @see android.text.TextWatcher#onTextChanged(java.lang.CharSequence,
         *      int, int, int)
         */
        @Override
        public void onTextChanged(final CharSequence arg0, final int arg1, final int arg2, final int arg3) {
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
        public void onItemClick(final AdapterView<?> adapterView, final View view, final int pos, final long id) {
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
        public void onGesturePerformed(final GestureOverlayView overlay, final Gesture gesture) {
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
