package data.comm.eagleRemind;

import java.util.LinkedList;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.Time;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {
	// Database Version
	private static final int DATABASE_VERSION = 1;
	// Database Name
	private static final String DATABASE_NAME = "EventDB";
	private static final String TABLE_EVENTS = "events";

	// Events table columns names
	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "eventName";
	private static final String KEY_DATETIME = "datetime";
	private static final String KEY_LATITUDE = "latitude";
	private static final String KEY_LONGITUDE = "longitude";

	private static final String[] COLUMNS = { KEY_ID, KEY_NAME, KEY_DATETIME,
			KEY_LATITUDE, KEY_LONGITUDE };

	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// SQL statement to create event table
		String CREATE_EVENT_TABLE = "CREATE TABLE events ( "
				+ "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "eventName TEXT, "
				+ "datetime INTEGER, " + "latitude TEXT, " + "longitude TEXT)";

		// create events table
		db.execSQL(CREATE_EVENT_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older events table if existed
		db.execSQL("DROP TABLE IF EXISTS events");

		// create fresh events table
		this.onCreate(db);
	}

	public void addEvent(MapEvent event) {
		// for logging
		Log.d("AddEvent", event.toString());

		// 1. Get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();

		// 2. Create ContentValues to add key "column"/value
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, event.getName());
		values.put(KEY_DATETIME, event.getTime());
		values.put(KEY_LATITUDE, event.getLatitude());
		values.put(KEY_LONGITUDE, event.getLongitude());

		// 3. Insert
		db.insert(TABLE_EVENTS, null, values);

		// 4. Close
		db.close();
	}

	public MapEvent getEvent(int id) {
		// 1. Get reference to writable DB
		SQLiteDatabase db = this.getReadableDatabase();

		// 2. Build Query
		Cursor cursor = db.query(TABLE_EVENTS, COLUMNS, " id = ?",
				new String[] { String.valueOf(id) }, null, null, null, null);

		// 3, If we get a result, return the first.
		if (cursor != null)
			cursor.moveToFirst();

		// 4. Build MapEvent Object.
		Time t = new Time();
		MapEvent event = new MapEvent();
		event.setId(Integer.parseInt(cursor.getString(0)));
		event.setName(cursor.getString(1));
		t.set(Long.parseLong(cursor.getString(2)));
		event.setTime(t);
		event.setLocation(new LatLng(Double.parseDouble(cursor.getString(3)),
				Double.parseDouble(cursor.getString(4))));

		// log
		Log.d("getEvent(" + id + ")", event.toString());

		// 5. Return Event
		return event;
	}

	// Get All events
	public List<MapEvent> getAllEvents() {
		
		List<MapEvent> events = new LinkedList<MapEvent>();

		// 1. build the query
		String query = "SELECT  * FROM " + TABLE_EVENTS;

		// 2. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null);

		// 3. go over each row, build event and add it to list
		MapEvent event = null;
		if (cursor.moveToFirst()) {
			do {
				event = new MapEvent();
				event.setId(Integer.parseInt(cursor.getString(0)));
				event.setName(cursor.getString(1));
				Time t = new Time();
				t.set(Long.parseLong(cursor.getString(2)));
				event.setTime(t);
				event.setLocation(new LatLng(Double.parseDouble(cursor
						.getString(3)), Double.parseDouble(cursor.getString(4))));

				// Add event to events
				events.add(event);
			} while (cursor.moveToNext());
		}

		Log.d("getAllEvents()", event.toString());

		// return events
		return events;
	}

	// Updating single event
	public int updateEvents(MapEvent event) {

		// 1. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();

		// 2. create ContentValues to add key "column"/value
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, event.getName());
		values.put(KEY_DATETIME, event.getTime());
		values.put(KEY_LATITUDE, event.getLatitude());
		values.put(KEY_LONGITUDE, event.getLongitude());

		// 3. updating row
		int i = db.update(TABLE_EVENTS, // table
				values, // column/value
				KEY_ID + " = ?", // selections
				new String[] { String.valueOf(event.getId()) }); // selection
																	// args

		// 4. close
		db.close();

		return i;

	}

	// Deleting single event
	public void deleteEvent(MapEvent event) {

		// 1. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();

		// 2. delete
		db.delete(TABLE_EVENTS, KEY_ID + " = ?",
				new String[] { String.valueOf(event.getId()) });

		// 3. close
		db.close();

		Log.d("deleteEvent", event.toString());

	}

	//Deletes all events. Useful for Debugging purposes.
	public void deleteAllEvents() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("delete from " + TABLE_EVENTS);
		db.execSQL("delete from sqlite_sequence where name = '" + TABLE_EVENTS
				+ "'");
	}
}
