package data.comm.eagleRemind;

import android.app.IntentService;
import android.app.Service;   // Used to run this as a process independent of application
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;



public class ERPullService extends Service {
	
	private static final String TAG = ERPullService.class.getSimpleName();;    // The TAG is good convention when using logs and should be in called in our log() calls
	
	 
	  @Override
	  public void onCreate() {				// Things that go on when the service starts
	    super.onCreate();
	    Toast.makeText(this, "Service created...", Toast.LENGTH_LONG).show();
	    Log.i(TAG, "Service creating");
	    System.out.println(" onCreate in pull service");
	   
	  }
	 
	  // Here is where we clean up things when the service ends.   i.e - Close file objects and things of that nature
	  @Override
	  public void onDestroy() {		
	    super.onDestroy();
	    Toast.makeText(this, "Service destroyed...", Toast.LENGTH_LONG).show();
	    Log.i(TAG, "Service destroying");
	     
	
	  }

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	

}
