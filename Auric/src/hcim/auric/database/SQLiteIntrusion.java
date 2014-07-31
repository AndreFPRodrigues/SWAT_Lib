package hcim.auric.database;

import hcim.auric.intrusion.Intrusion;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteIntrusion extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "IntrusionDB";

	private static final String TABLE_INTRUSIONS = "intrusions";
	private static final String KEY_ID = "id";
	private static final String KEY_DATE = "date";
	private static final String KEY_TIME = "time";
	private static final String[] COLUMNS = { KEY_ID, KEY_DATE, KEY_TIME };

	public SQLiteIntrusion(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_INTRUSION_TABLE = "CREATE TABLE intrusions ( "
				+ "id TEXT PRIMARY KEY, " + "date TEXT, " + "time TEXT )";

		db.execSQL(CREATE_INTRUSION_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS intrusions");

		this.onCreate(db);
	}

	public void addIntrusion(String id, String date, String time) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, id);
		values.put(KEY_DATE, date);
		values.put(KEY_TIME, time);

		db.insert(TABLE_INTRUSIONS, null, values);

		db.close();
	}

	public Intrusion getIntrusion(String id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(true, TABLE_INTRUSIONS, COLUMNS, KEY_ID + "='"
				+ id + "'", null, null, null, null, null);

		if (cursor != null)
			cursor.moveToFirst();

		if (cursor.getCount() <= 0)
			return null;

		Intrusion intrusion = new Intrusion(cursor.getString(0),
				cursor.getString(1), cursor.getString(2));

		return intrusion;
	}

	/**
	 * Gets all intrusions from a given day
	 * 
	 * @param date
	 *            : the day
	 * @return list of intrusions
	 */
	public List<Intrusion> getAllIntrusionsFromDay(String date) {
		List<Intrusion> result = new LinkedList<Intrusion>();

		SQLiteDatabase db = this.getWritableDatabase();

		Cursor cursor = db.query(true, TABLE_INTRUSIONS, COLUMNS, KEY_DATE
				+ "='" + date + "'", null, null, null, null, null);

		Intrusion i = null;

		if (cursor == null)
			return null;

		if (cursor.moveToFirst()) {
			if (cursor.getCount() <= 0)
				return null;
			do {
				i = new Intrusion(cursor.getString(0), cursor.getString(1),
						cursor.getString(2));

				result.add(i);
			} while (cursor.moveToNext());
		}

		return result;
	}

	public void deleteIntrusion(String id) {
		SQLiteDatabase db = this.getWritableDatabase();

		db.delete(TABLE_INTRUSIONS, KEY_ID + "= '" + id, null);

		db.close();
	}

}