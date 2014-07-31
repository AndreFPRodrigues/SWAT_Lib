package hcim.auric.intrusion;

import hcim.auric.calendar.CalendarManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;

public class Intrusion implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private List<Bitmap> images;
	private Log log;
	private String date;
	private String time;

	public Intrusion(Context c) {
		id = CalendarManager.getCurrentDateAndTime();
		int idx = id.indexOf(" ");

		date = id.substring(0, idx);
		time = id.substring(idx + 1);
		
		log = new Log(c); //MainActivity.context
		images = new ArrayList<Bitmap>();
	}

	public Intrusion(String id, String date, String time) {
		this.id = id;
		this.date = date;
		this.time = time;
	}

	public Intrusion(String id, String date, String time, String timestamp) {
		this.id = id;
		this.date = date;
		this.time = time;
		this.log = new Log(timestamp);
		images = new ArrayList<Bitmap>();
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getID() {
		return id;
	}

	public void setID(String id) {
		this.id = id;
	}

	public List<Bitmap> getImages() {
		return images;
	}

	public void setImages(List<Bitmap> images) {
		this.images = images;
	}

	public void addImage(Bitmap capturedFace) {
		this.images.add(capturedFace);
	}

	public Log getLog() {
		return log;
	}

	public void setLog(Log log) {
		this.log = log;
	}

	public void startLogging() {
		log.startLogging();
	}

	public void stopLogging() {
		log.stopLogging(); 
	}

	@Override
	public String toString() {
		return "Intrusion " + time;
	}
}
