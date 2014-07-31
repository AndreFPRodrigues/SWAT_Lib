package hcim.auric.database;

import hcim.auric.calendar.CalendarManager;
import hcim.auric.intrusion.Intrusion;
import hcim.auric.intrusion.Log;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;

public class IntrusionsDatabase {
	private static IntrusionsDatabase INSTANCE;

	SQLiteIntrusion intrusionsDB;
	SQLiteLog logsDB;
	SQLiteIntrusionPictures intrusionPicturesDB;

	private IntrusionsDatabase(Context c) {
		intrusionsDB = new SQLiteIntrusion(c);
		logsDB = new SQLiteLog(c);
		intrusionPicturesDB = new SQLiteIntrusionPictures(c);
	}

	public int getNumberOfLogs() {
		return 1;
	}

	public List<Intrusion> getIntrusionsFromADay(String date) {
		List<Intrusion> list = intrusionsDB.getAllIntrusionsFromDay(date);

		if (list == null)
			return null;

		String id;
		List<Bitmap> pics;
		String log;

		for (Intrusion i : list) {
			id = i.getID();
			pics = intrusionPicturesDB.getAllIntrusionPicture(id);
			log = logsDB.getLog(id);

			i.setImages(pics);
			i.setLog(new Log(log));
		}

		return list;
	}

	public void addIntrusion(Intrusion i) {
		String id = i.getID();
		List<Bitmap> list = i.getImages();

		intrusionsDB.addIntrusion(id, i.getDate(), i.getTime());

		if (list != null) {
			for (Bitmap bitmap : list)
				intrusionPicturesDB.addIntrusionPicture(i.getID(), bitmap);
		}
		logsDB.addLog(id, i.getLog());
	}

	public void removeIntrusion(Intrusion i) {
		String id = i.getID();

		intrusionsDB.deleteIntrusion(id);
		intrusionPicturesDB.deleteAllIntrusionPicture(id);
		logsDB.deleteLog(id);
	}

	public Intrusion getIntrusion(String id) {
		Intrusion i = intrusionsDB.getIntrusion(id);
		List<Bitmap> pics = intrusionPicturesDB.getAllIntrusionPicture(id);
		String log = logsDB.getLog(id);

		i.setLog(new Log(log));
		i.setImages(pics);

		return i;
	}

	public boolean dayOfIntrusion(String theday, String themonth, String theyear) {
		return dayOfIntrusion(CalendarManager.getDateFormat(theday, themonth,
				theyear));
	}

	public boolean dayOfIntrusion(String date_month_year) {
		List<Intrusion> list = intrusionsDB
				.getAllIntrusionsFromDay(date_month_year);

		return !(list == null || list.size() == 0);
	}

	public static IntrusionsDatabase getInstance(Context c) {
		if (INSTANCE == null)
			INSTANCE = new IntrusionsDatabase(c);
		return INSTANCE;
	}

//	public static IntrusionsDatabase getInstanceForService(Context c) {
//		if (SERVICE_INSTANCE == null) {
//			SERVICE_INSTANCE = new IntrusionsDatabase(c);
//		}
//
//		return SERVICE_INSTANCE;
//	}
}
