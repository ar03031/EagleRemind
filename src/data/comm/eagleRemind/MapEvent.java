package data.comm.eagleRemind;

import com.google.android.gms.maps.model.LatLng;

import android.text.format.Time;

public class MapEvent {
	String name;
	Time time;
	int year, month, date;
	LatLng location;
	public MapEvent() {
		name="No Name Specified";
		year=2000;
		month=1;
		date=1;
	}
	
	public void setDate(int month, int date, int year){
		this.month=month;
		this.date=date;
		this.year=year;
	}
	
	public void setName(String name){
		this.name=name;
	}
	
	public void setLocation(LatLng location){
		this.location=location;
	}
	
	public void setTime(Time t){
		this.time=t;
	}
}
