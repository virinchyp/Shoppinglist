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
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import de.ronnyfriedland.shoppinglist.adapter.ShoppingListAdapter;
import de.ronnyfriedland.shoppinglist.db.ShoppingListDataSource;
import de.ronnyfriedland.shoppinglist.entity.Entry;
import de.ronnyfriedland.shoppinglist.entity.Quantity;
import de.ronnyfriedland.shoppinglist.entity.Shoppinglist;
import de.ronnyfriedland.shoppinglist.entity.Status;
import de.ronnyfriedland.shoppinglist.helper.UIHelper;

/**
 * @author Ronny Friedland
 */
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
	EditText textDescription;
	Button saveButton;
	Button resetButton;
	// tab3

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		saveButton = (Button) findViewById(R.id.buttonSave);
		textQuantityValue = (EditText) findViewById(R.id.textViewQuantityValue);
		spinnerQuantity = (Spinner) findViewById(R.id.spinnerQuantity);
		textDescription = (EditText) findViewById(R.id.autoCompleteTextViewEntryDescription);
		listView = (ListView) findViewById(R.id.listViewList);
		tabHost = (TabHost) findViewById(R.id.tabHost);
		resetButton = (Button) findViewById(R.id.buttonReset);
		seekBar = (SeekBar) findViewById(R.id.seekBarQuantityValue);
		textUuid = (EditText) findViewById(R.id.textViewUuid);

		configureTabs();
		configureListView();
		configureNewEntryView();
		configureNewRegistrationView();

		Log.v("lalalala", "" + ShoppingListDataSource.getInstance(getBaseContext()).getList());
	}

	private void configureNewRegistrationView() {

	}

	private void configureNewEntryView() {
		seekBar.setOnSeekBarChangeListener(new ShoppingListSeekBarListener());

		// Create an ArrayAdapter using the string array and a default spinner
		// layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				getBaseContext(), R.array.quantity,
				android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinnerQuantity.setAdapter(adapter);

		textQuantityValue.addTextChangedListener(new ShoppingListTextWatcher());

		saveButton.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("unchecked")
			public void onClick(View v) {
				String uuid = textUuid.getText().toString();
				Integer quantityValue = Integer.valueOf(textQuantityValue
						.getText().toString());
				String quantityUnit = (String) spinnerQuantity
						.getSelectedItem();
				String description = textDescription.getText().toString();

				Shoppinglist list = ShoppingListDataSource.getInstance(
						getBaseContext()).getList();
				if (null == list) {
					list = new Shoppinglist();
					ShoppingListDataSource.getInstance(getBaseContext())
							.createList(list);
				}

				Entry entry;
				if(null != uuid) {
					entry = new Entry(uuid);
				} else {
					entry = new Entry();
				}
				entry.setQuantity(new Quantity(quantityValue, quantityUnit));
				entry.setDescription(description);
				entry.setList(list);

				if(null == uuid) {
					ShoppingListDataSource.getInstance(getBaseContext())
					.createEntry(entry);
					((ShoppingListAdapter<Entry>) listView.getAdapter()).add(entry);
				} else {
					ShoppingListDataSource.getInstance(getBaseContext())
					.updateEntry(entry);
					((ShoppingListAdapter<Entry>) listView.getAdapter()).update(entry);
				}
				
				((ShoppingListAdapter<Entry>) listView.getAdapter())
						.notifyDataSetChanged();

				textUuid.setText("");
				spinnerQuantity.setSelection(0);
				textDescription.setText("");
				seekBar.setProgress(0);
				textQuantityValue.setText("");

				tabHost.setCurrentTabByTag(getResources().getString(
						R.string.list));
			}
		});

		resetButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				textUuid.setText("");
				spinnerQuantity.setSelection(0);
				textDescription.setText("");
				seekBar.setProgress(0);
				textQuantityValue.setText("");
			}
		});

	}

	private void configureListView() {
		List<Entry> entries = new ArrayList<Entry>();
		entries.addAll(ShoppingListDataSource.getInstance(getBaseContext())
				.getEntries());

		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		ShoppingListAdapter<Entry> myAdapter = new ShoppingListAdapter<Entry>(
				getBaseContext(),
				android.R.layout.simple_expandable_list_item_1, entries);
		listView.setAdapter(myAdapter);
		listView.setOnItemClickListener(new ShoppingListClickListener());
		registerForContextMenu(listView);
	}

	private void configureTabs() {
		tabHost.setup();

		TabSpec spec1 = tabHost.newTabSpec(getResources().getString(
				R.string.list));
		spec1.setContent(R.id.tab1);
		spec1.setIndicator(getResources().getString(R.string.list));

		TabSpec spec2 = tabHost.newTabSpec(getResources().getString(
				R.string.create));
		spec2.setIndicator(getResources().getString(R.string.create));
		spec2.setContent(R.id.tab2);

		TabSpec spec3 = tabHost.newTabSpec(getResources().getString(
				R.string.register));
		spec3.setIndicator(getResources().getString(R.string.register));
		spec3.setContent(R.id.tab3);

		tabHost.addTab(spec1);
		tabHost.addTab(spec2);
		tabHost.addTab(spec3);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		// menu.setHeaderTitle("Context Menu");
		menu.add(0, v.getId(), 0, getResources().getString(R.string.delete));
		menu.add(0, v.getId(), 1, getResources().getString(R.string.edit));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean ret;
		if (item.getItemId() == R.id.exit) {
			Log.v("lalalala", "" + ShoppingListDataSource.getInstance(getBaseContext()).getList());
			
			ShoppingListDataSource.getInstance(getBaseContext()).close();
			System.exit(0);
			ret = true;
		} else {
			ret = super.onOptionsItemSelected(item);
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		Entry entry = (Entry) listView.getAdapter().getItem(info.position);
		if (item.getTitle() == getResources().getString(R.string.delete)) {
			Log.d(getClass().getCanonicalName(),
					String.format("Delete entry with id %s.", entry.getUuid()));

			ShoppingListDataSource.getInstance(getBaseContext()).deleteEntry(
					entry);
			((ShoppingListAdapter<Entry>) listView.getAdapter()).remove(entry);
			((ShoppingListAdapter<Entry>) listView.getAdapter())
					.notifyDataSetChanged();
		} else if (item.getTitle() == getResources().getString(R.string.edit)) {
			Log.d(getClass().getCanonicalName(),
					String.format("Edit entry with id %s.", entry.getUuid()));
			
			textUuid.setText(entry.getUuid());
			textDescription.setText(entry.getDescription());
			seekBar.setProgress(entry.getQuantity().getValue());

			String[] quantity = getResources().getStringArray(R.array.quantity);
			for(int i=0; i<quantity.length; i++) {
				if(entry.getQuantity().getUnit().equals(quantity[i])) {
					spinnerQuantity.setSelection(i);
				}
			}
			
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

		@Override
		public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
			EditText textAction = (EditText) findViewById(R.id.textViewQuantityValue);
			textAction.setText(String.valueOf(arg1));
		}

		@Override
		public void onStartTrackingTouch(SeekBar arg0) {
			// nothing to do
		}

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

		@Override
		public void afterTextChanged(Editable arg0) {
			int position = arg0.length();
			Selection.setSelection(arg0, position);
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// nothing to do
		}

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			try {
				seekBar.setProgress(Integer.parseInt(arg0.toString()));
			} catch (NumberFormatException e) {
				seekBar.setProgress(0);
			}
		}

	}

	/**
	 * Listener to track clicks on the list view.
	 * 
	 * @author Ronny Friedland
	 */
	class ShoppingListClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int pos,
				long id) {
			ListView list = (ListView) adapterView;
			TextView textView = (TextView) view;

			Entry value = (Entry) list.getAdapter().getItem(pos);
			if (Status.OPEN == value.getStatus()) {
				UIHelper.toggleStrikeThrough(textView, false);
				value.setStatus(Status.FINISHED);
			} else {
				UIHelper.toggleStrikeThrough(textView, true);
				value.setStatus(Status.OPEN);
			}
		}
	}

	class ShoppingListMenuListener implements OnMenuItemClickListener {

		@Override
		public boolean onMenuItemClick(MenuItem item) {
			if (item.getTitle() == getResources().getString(R.string.clearList)) {
				return true;
			} else if (item.getTitle() == getResources().getString(
					R.string.clearEntries)) {
				return true;
			} else if (item.getTitle() == getResources().getString(
					R.string.syncList)) {
				return true;
			} else {
				return false;
			}
		}

	}

}
