package data.comm.eagleRemind;

import com.google.android.gms.maps.model.LatLng;

import android.text.format.Time;

//The MapEvent is the core functionality of this application. It is a class that represents all of
//the objects on the map that a user specifies.
public class MapEvent {
	private int id;
	String name;
	Time time;
	int year, month, date;
	LatLng location;

	public MapEvent() {

	}

	public MapEvent(String name, Time time, LatLng location) {
		super();
		this.name = name;
		this.time = time;
		this.location = location;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setLocation(LatLng location) {
		this.location = location;
	}

	public double getLatitude() {
		return location.latitude;
	}

	public double getLongitude() {
		return location.longitude;
	}

	public void setTime(Time t) {
		this.time = t;
	}

	public long getTime() {
		return time.toMillis(false);
	}

	public String toString() {
		return "Event [id=" + id + ", Name=" + name + ", Time="
				+ this.getTime() + ", date= " + time.year + "-" + time.month
				+ "-" + time.monthDay + ", latitude=" + location.latitude
				+ ", longitude=" + location.longitude + "]";
	}
}
