package data.comm.eagleRemind;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.*;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NewEventActivity extends Activity {
	public static final String PREFS_NAME = "objectsPrefFile";
	protected int newEventMonth = 0;
	protected int newEventDay = 0;
	protected int newEventYear = 0;
	protected double newLat;
	protected double newLong;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		final MapEvent me = new MapEvent();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_event);
		CalendarView view = (CalendarView) findViewById(R.id.addEventCalendar);

		view.setOnDateChangeListener(new OnDateChangeListener() {

			@Override
			public void onSelectedDayChange(CalendarView arg0, int year,
					int month, int date) {
				Toast.makeText(getApplicationContext(),
						(month + 1) + "/" + date + "/" + year, 4000).show();
				newEventMonth = month;
				newEventDay = date;
				newEventYear = year;
			}
		});

		Button setLocationButton = (Button) findViewById(R.id.setLocationButton);
		setLocationButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getApplicationContext(),
						SetEventLocation.class);
				startActivityForResult(i, 1);
			}
		});
		
		Button saveEventButton = (Button) findViewById(R.id.saveEventButton);
		saveEventButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				me.setDate(newEventMonth, newEventDay, newEventYear);
				me.setName(findViewById(R.id.eventName).toString());
				me.setLocation(new LatLng(newLat,newLong));
				SharedPreferences appSharedPrefs = PreferenceManager
						  .getDefaultSharedPreferences(getApplicationContext());
						  Editor prefsEditor = appSharedPrefs.edit();
						  Gson gson = new Gson();
						  String json = gson.toJson(me);
						  prefsEditor.putString("MyObject", json);
						  prefsEditor.commit();
						  Toast.makeText(getApplicationContext(), "Object stored in SharedPreferences", Toast.LENGTH_LONG).show();
				
				finish();
			}
		});
	}
	
	public static Object deserializeObject(byte[] b) { 
	    try { 
	      ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(b)); 
	      Object object = in.readObject(); 
	      in.close(); 
	 
	      return object; 
	    } catch(ClassNotFoundException cnfe) { 
	      Log.e("deserializeObject", "class not found error", cnfe); 
	 
	      return null; 
	    } catch(IOException ioe) { 
	      Log.e("deserializeObject", "io error", ioe); 
	 
	      return null; 
	    } 
	  } 
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				CharSequence longitude = data.getStringExtra("longitude");
				CharSequence latitude = data.getStringExtra("latitude");
				newLat=Double.parseDouble((String) latitude);
				newLong=Double.parseDouble((String) longitude);
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
