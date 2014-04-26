package data.comm.eagleRemind;

import java.util.List;
import data.comm.eagleRemind.MySQLiteHelper;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends android.support.v4.app.FragmentActivity
		implements OnMapClickListener,
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener, LocationListener, android.location.LocationListener {

	private GoogleMap mMap;
	private static final String PROVIDER = "flp";
	private static double lat ;
	private static double lng ;
	public LocationClient mLocationClient;
	protected LocationManager locationManager;
	protected LocationListener locationListener;

	MySQLiteHelper helper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		helper = new MySQLiteHelper(getApplicationContext());
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mLocationClient = new LocationClient(this, this, this);
		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
		

		mMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();

	}

	@Override
	protected void onPause() {
		super.onPause();
		helper.close();
	}

	@Override
	public void onLocationChanged(Location location) {
		lat= location.getLatitude();
		lng= location.getLongitude();
		Log.d("New coords", "Lat: "+lat);
		Log.d("New coords", "Lng: "+lng);
	}

	@Override
	protected void onStart() {
		LatLng latLng=null;
		super.onStart();
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			double zoomToLatitude = extras.getDouble("latitude");
			double zoomToLongitude = extras.getDouble("longitude");
			latLng = new LatLng(zoomToLatitude, zoomToLongitude);
			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
					latLng, 15);
			mMap.animateCamera(cameraUpdate);
			Log.d("Lat/Lng debug", "" + zoomToLatitude + ", " + zoomToLongitude);
		} else {
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
			latLng = new LatLng(lat,
					lng);
			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
					latLng, 15);
			mMap.animateCamera(cameraUpdate);
			mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng))
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.little_red_dot)));
		}
		// Connect the client.
		mLocationClient.connect();
		MySQLiteHelper helper = new MySQLiteHelper(getApplicationContext());
		helper.getWritableDatabase();
		try {
			List<MapEvent> events;
			events = helper.getAllEvents();
			for (int i = 0; i < events.size(); i++) {
				mMap.addMarker(new MarkerOptions().position(
						new LatLng(events.get(i).getLatitude(), events.get(i)
								.getLongitude())).icon(
						BitmapDescriptorFactory
								.fromResource(R.drawable.map_marker)));
			}
		} catch (Exception e) {
			Log.d("No Events Error", "No Events to show!");
		}
		helper.close();
	}

	/*
	 * Called when the Activity is no longer visible.
	 */
	@Override
	protected void onStop() {
		// Disconnecting the client invalidates it.
		mLocationClient.disconnect();
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.AddEvent:
			Intent i = new Intent(getApplicationContext(),
					NewEventActivity.class);
			startActivity(i);
			return true;
		default:
			return true;
		}
	}

	public Location createLocation(double lat, double lng, float accuracy) {
		// Create a new Location
		Location newLocation = new Location(PROVIDER);
		newLocation.setLatitude(lat);
		newLocation.setLongitude(lng);
		newLocation.setAccuracy(accuracy);
		return newLocation;
	}

	@Override
	public void onMapClick(LatLng point) {
		mMap.addMarker(new MarkerOptions().position(point).icon(
				BitmapDescriptorFactory.fromResource(R.drawable.map_marker)));

	}

	public void onConnected(Bundle dataBundle) {
		// Display the connection status
		Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();


	}

	/*
	 * Called by Location Services if the connection to the location client
	 * drops because of an error.
	 */
	@Override
	public void onDisconnected() {
		// Display the connection status
		Toast.makeText(this, "Disconnected. Please re-connect.",
				Toast.LENGTH_SHORT).show();
	}

	/*
	 * Called by Location Services if the attempt to Location Services fails.
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
		if (connectionResult.hasResolution()) {
			Toast.makeText(this, "Disconnected. Please re-connect.",
					Toast.LENGTH_SHORT).show();
		} else {
			/*
			 * If no resolution is available, display a dialog to the user with
			 * the error.
			 */
			showDialog(connectionResult.getErrorCode());
		}
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}

}
