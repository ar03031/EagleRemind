package data.comm.eagleRemind;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import data.comm.eagleRemind.MySQLiteHelper;

public class ManageEvents extends Activity {

	Button dataLocationButton;
	List<MapEvent> events;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_events);
		// Show the Up button in the action bar.
		setupActionBar();
		// Populate the Table
		populateTable();
	}

	// Read the database to populate the table.
	@SuppressLint("SimpleDateFormat")
	public void populateTable() {
		// Set up a helper to allow access to the Database.
		final MySQLiteHelper helper = new MySQLiteHelper(
				getApplicationContext());
		helper.getReadableDatabase();
		// Make a table for your data.
		TableLayout tbl = (TableLayout) findViewById(R.id.dataTable);
		tbl.removeAllViews();
		try {
			// Create TextViews for your headers.
			events = helper.getAllEvents();
			TableRow headers = new TableRow(getApplicationContext());
			TextView idHeader = new TextView(this);
			idHeader.setText("ID");
			idHeader.setWidth(100);

			TextView nameHeader = new TextView(this);
			nameHeader.setText("Event Name");
			nameHeader.setWidth(250);

			TextView dateHeader = new TextView(this);
			dateHeader.setText("Event Date");
			dateHeader.setWidth(300);

			TextView locationHeader = new TextView(this);
			locationHeader.setText("Location");
			locationHeader.setWidth(150);

			// Add them to the top of your table.
			headers.addView(idHeader);
			headers.addView(nameHeader);
			headers.addView(dateHeader);
			headers.addView(locationHeader);

			tbl.addView(headers);

			// For every event, make a record on your table.
			for (int i = 0; i < events.size(); i++) {
				TableRow dataRow = new TableRow(getApplicationContext());

				TextView dataId = new TextView(getApplicationContext());
				dataId.setText(Integer.toString(events.get(i).getId()));

				TextView dataName = new TextView(getApplicationContext());
				dataName.setText(events.get(i).getName());

				SimpleDateFormat formatter = new SimpleDateFormat(
						"MM/dd/yyyy hh:mm a");
				String dateString = formatter.format(new Date(events.get(i)
						.getTime()));
				TextView dataDate = new TextView(getApplicationContext());
				dataDate.setText(dateString);

				// Generate buttons to open the map to the Event's location
				Button dataLocationButton = new Button(getApplicationContext());
				dataLocationButton.setId(i + 1);
				dataLocationButton.setText("Show Location");
				dataLocationButton
						.setOnClickListener(new View.OnClickListener() {

							// Make sure to close the Database. Zoom to event on
							// map.
							@Override
							public void onClick(View v) {
								helper.close();
								zoomToMapEvent(v);
							}
						});

				// Add the record to the table.
				dataRow.addView(dataId);
				dataRow.addView(dataName);
				dataRow.addView(dataDate);
				dataRow.addView(dataLocationButton);
				dataRow.setId(i + 1);
				tbl.addView(dataRow);
				registerForContextMenu(dataRow);
				Log.d("printing table", "" + i);
			}
			// If there aren't any records to show, eat the exception and show
			// an empty table. Close the database.
		} catch (Exception e) {
			Log.d("No Events Error", "No Events to show!");
			helper.close();
		}
		// Make sure the database is closed.
		helper.close();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Choose an Action");
		menu.add(0, v.getId(), 0, "Open Location");
		menu.add(0, v.getId(), 0, "Edit Event");
		menu.add(0, v.getId(), 0, "Delete Event");
		menu.add(0, v.getId(), 0, "Share Event");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getTitle() == "Open Location") {
			zoomToMapEvent((View) findViewById(item.getItemId()));
		} else if (item.getTitle() == "Edit Event") {
			editEvent((View) findViewById(item.getItemId()));
		} else if (item.getTitle() == "Delete Event") {
			deleteEvent((View) findViewById(item.getItemId()));
		} else if (item.getTitle() == "Share Event") {
			// function2(item.getItemId());
		} else {
			return false;
		}
		return true;
	}
	@Override
	public void onResume(){
		super.onResume();
		populateTable();
	}
	
	public void editEvent(View v){
		int id = v.getId();
		Intent i = new Intent(getApplicationContext(),NewEventActivity.class);
		i.putExtra("eventName", events.get(id-1).name);
		i.putExtra("eventTimeDate", events.get(id-1).getTime());
		i.putExtra("eventLatitude", events.get(id-1).getLatitude());
		i.putExtra("eventLongitude", events.get(id-1).getLongitude());
		deleteEvent(v);
		startActivity(i);
	}

	// Deletes an Event from the database.
	public void deleteEvent(View v) {
		int id=v.getId();
		final MySQLiteHelper helper = new MySQLiteHelper(
				getApplicationContext());
		MapEvent eventToDelete = events.get(id - 1);
		helper.deleteEvent(eventToDelete);
		populateTable();
	}

	// Read the Button's ID and relate it to the list of events and their saved
	// locations. Open MainActivity to zoom to selected location.
	public void zoomToMapEvent(View v) {
		int id = v.getId();
		Log.d("Lat/lng test",
				"lat at location " + (id - 1) + ": "
						+ events.get(id - 1).location.latitude);
		Log.d("Lat/lng test",
				"long at location " + (id - 1) + ": "
						+ events.get(id - 1).location.longitude);
		Intent i = new Intent(getApplicationContext(), MainActivity.class);
		i.putExtra("latitude", events.get(id - 1).location.latitude);
		i.putExtra("longitude", events.get(id - 1).location.longitude);
		startActivity(i);
	}

	// If the 'up' option is pressed, go back to NavigationActivity.
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.manage_events, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
