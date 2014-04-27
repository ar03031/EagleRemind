package data.comm.eagleRemind;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class ShareActivity extends Activity implements
		CreateNdefMessageCallback {
	private NfcAdapter mNfcAdapter;
	PendingIntent nfcPendingIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share);
		// Show the Up button in the action bar.
		setupActionBar();

		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		mNfcAdapter.setNdefPushMessageCallback(this, this);

	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public void onNewIntent(Intent intent) {
		// onResume gets called after this to handle the intent
		setIntent(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.share, menu);
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

	protected void onResume(Bundle savedInstanceState) {
		super.onResume();
	}

	protected void onPause(Bundle savedInstanceState) {
		super.onPause();
	}

	@Override
	public NdefMessage createNdefMessage(NfcEvent event) {
		// Get the data from the event that is to be shared.
		final Bundle extras = getIntent().getExtras();
		String eventName = extras.getString("eventName");
		String eventTime = String.valueOf(extras.getLong("eventTimeDate"));
		String eventLongitude = String.valueOf(extras.getDouble("eventLongitude"));
		String eventLatitude = String.valueOf(extras.getDouble("eventLatitude"));
		NdefMessage msg = new NdefMessage(new NdefRecord[] {
				NdefRecord.createMime(
						"application/vnd.com.example.android.beam",
						eventName.getBytes()),
				NdefRecord.createMime(
						"application/vnd.com.example.android.beam",
						eventTime.getBytes()),
				NdefRecord.createMime(
						"application/vnd.com.example.android.beam",
						eventLatitude.getBytes()),
				NdefRecord.createMime(
						"application/vnd.com.example.android.beam",
						eventLongitude.getBytes()) });
		return msg;
	}


}
