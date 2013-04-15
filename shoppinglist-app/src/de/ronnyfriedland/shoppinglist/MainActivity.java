package de.ronnyfriedland.shoppinglist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		SeekBar bar = (SeekBar) findViewById(R.id.seekBar1); // make seekbar
																// object
		bar.setOnSeekBarChangeListener(new ShoppingListSeekBarListener()); // set
																			// seekbar
																			// listener.

		TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
		tabHost.setup();

		TabSpec spec1 = tabHost.newTabSpec("Liste");
		spec1.setContent(R.id.tab1);
		spec1.setIndicator("Liste");

		TabSpec spec2 = tabHost.newTabSpec("Eintrag");
		spec2.setIndicator("Eintrag");
		spec2.setContent(R.id.tab2);

		TabSpec spec3 = tabHost.newTabSpec("Registrieren");
		spec3.setIndicator("Registrieren");
		spec3.setContent(R.id.tab3);

		tabHost.addTab(spec1);
		tabHost.addTab(spec2);
		tabHost.addTab(spec3);

		List<Map<String, String>> planetsList = new ArrayList<Map<String, String>>();
		for (int i = 1; i < 10; i++) {
			HashMap<String, String> planet = new HashMap<String, String>();
			planet.put("planet", "Pfannkuchenmehl" + i);
			planetsList.add(planet);
		}

		ListView list = (ListView) findViewById(R.id.listView1);
		list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		SimpleAdapter simpleAdpt = new SimpleAdapter(this, planetsList,
				android.R.layout.simple_list_item_1, new String[] { "planet" },
				new int[] { android.R.id.text1 }) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				TextView view = (TextView) super.getView(position, convertView,
						parent);
				Typeface tf = Typeface.createFromAsset(getAssets(),
						"fonts/arcade.ttf");
				view.setTypeface(tf);
				return view;
			}

		};
		list.setAdapter(simpleAdpt);
		registerForContextMenu(list);

		Spinner spinner = (Spinner) findViewById(R.id.spinner1);
		// Create an ArrayAdapter using the string array and a default spinner
		// layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.quantity, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
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
		menu.add(0, v.getId(), 0, "delete");
		menu.add(0, v.getId(), 1, "edit");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean ret;
		if (item.getItemId() == R.id.exit) {
			System.exit(0);
			ret = true;
		} else {
			ret = super.onOptionsItemSelected(item);
		}
		return ret;
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getTitle() == "delete") {
			ListView list = (ListView) findViewById(R.id.listView1);
			list.getCheckedItemIds();
		} else if (item.getTitle() == "edit") {
			ListView list = (ListView) findViewById(R.id.listView1);
			list.getCheckedItemIds();
		} else {
			return false;
		}
		return true;
	}

	class ShoppingListSeekBarListener implements OnSeekBarChangeListener {

		@Override
		public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
			TextView textAction = (TextView) findViewById(R.id.textViewAction);
			textAction.setText("menge: " + arg1);
		}

		@Override
		public void onStartTrackingTouch(SeekBar arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStopTrackingTouch(SeekBar arg0) {
			// TODO Auto-generated method stub

		}

	}

	class ShoppingListMenuListener implements OnMenuItemClickListener {

		@Override
		public boolean onMenuItemClick(MenuItem arg0) {
			// TODO Auto-generated method stub
			return false;
		}

	}

}
