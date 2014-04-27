package data.comm.eagleRemind;

import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationListener;

import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.view.*;
import data.comm.eagleRemind.MySQLiteHelper;
import android.app.IntentService;
import android.app.Service;
import android.widget.TextView;
import android.widget.Toast;


public class NavigationActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener, LocationListener, android.location.LocationListener  {
	private NfcAdapter mNfcAdapter;
	public LocationManager locationManager;
	public LocationListener locationListener;
	public static double lat ;
	public static double lng ;
	//Instantiate a database for the App to read.
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

		
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
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
			Log.d("Error", "No records to show!");}
		db.close();
		// Here's where I start the ERPUllService which starts our service in the background when this Activity is called
		startService(new Intent(ERPullService.class.getName()));
		
	}

	protected void onResume(Bundle savedInstanceState){
		mNfcAdapter.disableForegroundDispatch(this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.navigation, menu);
		return true;
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLocationChanged(Location location) {
		lat= location.getLatitude();
		lng= location.getLongitude();
		//Log.d("New coords", "Lat: "+lat);
		//Log.d("New coords", "Lng: "+lng);		
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}

}
