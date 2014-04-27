package data.comm.eagleRemind;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service; // Used to run this as a process independent of application
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

public class ERPullService extends Service implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener, LocationListener,
		android.location.LocationListener {
	// The TAG is good convention when using logs and should be in called in our
	// log() calls
	private static final String TAG = ERPullService.class.getSimpleName();
	protected LocationManager locationManager;
	protected LocationListener locationListener;
	List<MapEvent> events;
	public PeriodicCheck pCheck;
	public Location lastKnownLocation, eventLocation;
	List<Integer> dismissedID = new ArrayList<Integer>();
	public int notificationId;

	@Override
	public void onCreate() {
		// Things that go on when the service starts
		super.onCreate();
		Intent intent = new Intent(this, NavigationActivity.class);
		PendingIntent pIntent = PendingIntent.getActivity(this,0,intent,0);

		Toast.makeText(this, "Service created...", Toast.LENGTH_LONG).show();
		Log.i(TAG, "Service creating");

		// Finding Location
		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 0, 0,
				(android.location.LocationListener) this);

		final MySQLiteHelper db = new MySQLiteHelper(getApplicationContext());
		db.getReadableDatabase();
		events = db.getAllEvents();
		db.close();
		pCheck = new PeriodicCheck(new Runnable() {
			@Override
			public void run() {
				Time now = new Time();
				now.setToNow();
				if (lastKnownLocation != null) {
					double longitude = lastKnownLocation.getLongitude();
					double latitude = lastKnownLocation.getLatitude();
					float[] result = new float[5];
					for (int i = 0; i < events.size(); i++) {
						Location.distanceBetween(latitude, longitude,
								events.get(i).location.latitude,
								events.get(i).location.longitude, result);
						Double d = (result[0] * .000621371);
						String formattedDistance = String.format("%.2f", d);
						double distance = result[0] * .000621371;
						long timeUntil = events.get(i).time.toMillis(true)
								- now.toMillis(true);
						long minutesUntil = timeUntil / 60000;
						notificationId = events.get(i).getId();

						if (((minutesUntil < 15 && minutesUntil > 0 && distance > 2)
								|| (minutesUntil < 30 && minutesUntil > 0 && distance > 5)) && !dismissedID.contains(notificationId)) {
							// Build a notification and send it out.
							NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
									getApplicationContext())
									.setSmallIcon(R.drawable.eagle_icon)
									.setContentTitle(
											"Event Notification: "
													+ events.get(i).name) 
									.setContentText(
											"Starting in " + minutesUntil
													+ " Minutes. Distance: "
													+ formattedDistance
													+ " mi.")
									.setVibrate(new long[] { 0, 500 })
									.setOnlyAlertOnce(true);
							try {
							    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
							    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
							    r.play();
							} catch (Exception e) {
							    e.printStackTrace();
							}

							// Sets an ID for the notification

							dismissedID.add(notificationId);
							
							// Gets an instance of the NotificationManager
							// service
							NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
							// Builds the notification and issues it.
							mNotifyMgr.notify(notificationId, mBuilder.build());
						}

						Log.d("DISTANCE", d + " mi");
						Log.d("TIME UNTIL", "" + timeUntil / 60000 + " minutes");
					}

					String locLat = String.valueOf(latitude) + ","
							+ String.valueOf(longitude);
					Log.d("LocLat", locLat);
				}
				Log.d("Periodic Check", "Checked!");

			}
		});
		pCheck.startUpdates();
	}

	// Here is where we clean up things when the service ends. i.e - Close file
	// objects and things of that nature
	@Override
	public void onDestroy() {
		super.onDestroy();
		Toast.makeText(this, "Service destroyed...", Toast.LENGTH_LONG).show();
		Log.i(TAG, "Service destroying");
		pCheck.stopUpdates();

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

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

	@Override
	public void onLocationChanged(Location location) {
		// Log.d("New coords (Service)", "Lat: " + location.getLatitude());
		// Log.d("New coords (Service)", "Lng: " + location.getLongitude());
		lastKnownLocation = location;
		final MySQLiteHelper db = new MySQLiteHelper(getApplicationContext());
		db.getReadableDatabase();
		events = db.getAllEvents();
		db.close();
	}
}
