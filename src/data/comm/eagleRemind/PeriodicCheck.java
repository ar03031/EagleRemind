package data.comm.eagleRemind;

import android.os.Handler;
import android.os.Looper;

public class PeriodicCheck {
	// Create a Handler that uses the Main Looper to run in
	private Handler mHandler = new Handler(Looper.getMainLooper());

	private Runnable mStatusChecker;
	private int UPDATE_INTERVAL = 10000;

	/**
	 * Creates an UIUpdater object, that can be used to perform UIUpdates on a
	 * specified time interval.
	 * 
	 * @param uiUpdater
	 *            A runnable containing the update routine.
	 */
	public PeriodicCheck(final Runnable pCheck) {
		mStatusChecker = new Runnable() {
			@Override
			public void run() {
				// Run the passed runnable
				pCheck.run();
				// Re-run it after the update interval
				mHandler.postDelayed(this, UPDATE_INTERVAL);
			}
		};
	}

	/**
	 * Starts the periodical update routine (mStatusChecker adds the callback to
	 * the handler).
	 */
	public synchronized void startUpdates() {
		mStatusChecker.run();
	}

	/**
	 * Stops the periodical update routine from running, by removing the
	 * callback.
	 */
	public synchronized void stopUpdates() {
		mHandler.removeCallbacks(mStatusChecker);
	}
}