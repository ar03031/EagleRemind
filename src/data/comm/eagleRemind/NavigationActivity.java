package data.comm.eagleRemind;

import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.view.*;
import data.comm.eagleRemind.MySQLiteHelper;

public class NavigationActivity extends Activity {

	//Instantiate a database for the App to read.
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		MySQLiteHelper db = new MySQLiteHelper(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_navigation);
		final Button openMapButton = (Button) findViewById(R.id.openMapButton);
		openMapButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(),
						MainActivity.class);
				startActivity(i);
			}
		});

		//Brings the user to the new Event page.
		final Button addEventButton = (Button) findViewById(R.id.addEventButton);
		addEventButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(),
						NewEventActivity.class);
				startActivity(i);
			}
		});
		
		//Brings the user to the Manage Events page.
		final Button manageEventsButton = (Button) findViewById(R.id.manageEventsButton);
		manageEventsButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(),
						ManageEvents.class);
				startActivity(i);
			}
		});

		List<MapEvent> events;
		try {
			events = db.getAllEvents();
			for (int i = 0; i < events.size(); i++) {

				Log.d("Print Debug", events.get(i).toString());
			}
		} catch (Exception e) {
			Log.d("Error", "No records to show!");
		}

	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.navigation, menu);
		return true;
	}

}
