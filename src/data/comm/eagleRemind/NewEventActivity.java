package data.comm.eagleRemind;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;

import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.EditText;
import android.widget.TimePicker;

import data.comm.eagleRemind.MySQLiteHelper;
import data.comm.eagleRemind.MapEvent;

import com.google.android.gms.maps.model.LatLng;

public class NewEventActivity extends Activity {

	public static final String PREFS_NAME = "objectsPrefFile";
	public int newEventMonth = 0;
	public int newEventDay = 0;
	public int newEventYear = 0;
	public Time t = new Time();
	protected double newLat;
	protected double newLong;
	MySQLiteHelper db = new MySQLiteHelper(this);
	MapEvent me = new MapEvent();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_event);

		// Get the intent. If another Activity wants to Edit an event, run
		// accordingly.
		final Bundle extras = getIntent().getExtras();
		TimePicker timeView = (TimePicker) findViewById(R.id.newEventTime);
		CalendarView view = (CalendarView) findViewById(R.id.addEventCalendar);

		// If editing an entry...
		if (extras != null) {
			// Set Relevant fields and variables.
			EditText nameEdit = (EditText) findViewById(R.id.eventName);
			nameEdit.setText(extras.getString("eventName"));
			me.setName(extras.getString("eventName"));

			// Set the Date and Time
			Calendar calendar2 = Calendar.getInstance();
			calendar2.setTimeInMillis(extras.getLong("eventTimeDate"));
			timeView.setCurrentHour(calendar2.get(Calendar.HOUR));
			timeView.setCurrentMinute(calendar2.get(Calendar.MINUTE));
			newEventMonth = calendar2.get(Calendar.MONTH);
			newEventDay = calendar2.get(Calendar.DAY_OF_MONTH);
			newEventYear = calendar2.get(Calendar.YEAR);
			t.set(0, calendar2.get(Calendar.MINUTE),
					calendar2.get(Calendar.MONTH), newEventDay, newEventMonth,
					newEventYear);
			view.setDate(extras.getLong("eventTimeDate"), true, false);

			// Set the Latitude and Longitude
			EditText editLong = (EditText) findViewById(R.id.newEventLongitude);
			EditText editLat = (EditText) findViewById(R.id.newEventLatitude);
			editLat.setText(Double.toString(extras.getDouble("eventLatitude")));
			editLong.setText(Double.toString(extras.getDouble("eventLongitude")));
			newLat = extras.getDouble("eventLatitude");
			newLong = extras.getDouble("eventLongitude");
		}

		// When the Date changes, make sure the activity knows.
		view.setOnDateChangeListener(new OnDateChangeListener() {

			@Override
			public void onSelectedDayChange(CalendarView arg0, int year,
					int month, int date) {
				newEventMonth = month;
				newEventDay = date;
				newEventYear = year;
			}
		});

		// When the Time changes, make sure the activity knows.
		timeView.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {

			@Override
			public void onTimeChanged(TimePicker timeView, int hourOfDay,
					int minute) {
				t.set(0, minute, hourOfDay, newEventDay, newEventMonth,
						newEventYear);

			}
		});

		// Set the location by opening the 'SetEventLocation' activity and
		// choosing a point on the map. Return the Latitude and Longitude.
		Button setLocationButton = (Button) findViewById(R.id.setLocationButton);
		setLocationButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getApplicationContext(),
						SetEventLocation.class);
				startActivityForResult(i, 1);
			}
		});

		// Save the Event into the Database.
		Button saveEventButton = (Button) findViewById(R.id.saveEventButton);
		saveEventButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				EditText nameEdit;
				nameEdit = (EditText) findViewById(R.id.eventName);
				if (extras != null) {
					me = new MapEvent(nameEdit.getText().toString(), t,
							new LatLng(newLat, newLong));
					db.addEvent(me);
				} else {
					me = new MapEvent(nameEdit.getText().toString(), t,
							new LatLng(newLat, newLong));
					db.addEvent(me);
				}
				db.close();
				finish();
			}
		});
	}

	// If coming back from the SetEventLocation activity, set Latitude and
	// Longitude.
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				CharSequence longitude = data.getStringExtra("longitude");
				CharSequence latitude = data.getStringExtra("latitude");
				newLat = Double.parseDouble((String) latitude);
				newLong = Double.parseDouble((String) longitude);
				EditText editLong = (EditText) findViewById(R.id.newEventLongitude);
				EditText editLat = (EditText) findViewById(R.id.newEventLatitude);
				editLong.setText(longitude);
				editLat.setText(latitude);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_event, menu);
		return true;
	}

}
