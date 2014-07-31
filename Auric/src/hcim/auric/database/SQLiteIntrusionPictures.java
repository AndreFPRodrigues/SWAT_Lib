package hcim.auric.database;

import hcim.auric.serial.Converter;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

public class SQLiteIntrusionPictures extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "IntrusionPicturesDB";
	
	private static final String TABLE_INTRUSIONS = "intrusionpictures";
	private static final String KEY_ID = "number";
	private static final String KEY_INT = "intrusion";
	private static final String KEY_PICTURE = "picture";
	private static final String[] COLUMNS = { KEY_ID, KEY_INT, KEY_PICTURE };

	public SQLiteIntrusionPictures(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_INTRUSION_TABLE = "CREATE TABLE intrusionpictures ( "
				+ "number INTEGER PRIMARY KEY AUTOINCREMENT," + "intrusion TEXT, " + "picture BLOB )";

		db.execSQL(CREATE_INTRUSION_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS intrusions");

		this.onCreate(db);
	}


	public void addIntrusionPicture(String intrusionID, Bitmap bitmap) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_INT, intrusionID);
		values.put(KEY_PICTURE, Converter.bitmapToByteArray(bitmap));

		db.insert(TABLE_INTRUSIONS, null, values); 

		db.close();
	}

	/**
	 * Gets all intrusions from a given day
	 * 
	 * @param date
	 *            : the day
	 * @return list of intrusions
	 */
	public List<Bitmap> getAllIntrusionPicture(String intrusionID) {
		List<Bitmap> result = new ArrayList<Bitmap>();
		Bitmap b;
		
		SQLiteDatabase db = this.getWritableDatabase();
		
		Cursor cursor = db.query(true, TABLE_INTRUSIONS, COLUMNS, KEY_INT + " = '" + intrusionID +"'",
				null, null, null, null, null);
		
		if (cursor == null)
			return null;
		
		if (cursor.moveToFirst()) {
			if (cursor.getCount() <= 0)
				return null;
			do {
				b = (Bitmap) Converter.byteArrayToBitmap(cursor.getBlob(2));

				result.add(b);
			} while (cursor.moveToNext());
		}

		return result;
	}


	public void deleteAllIntrusionPicture(String intrusionID){
		SQLiteDatabase db = this.getWritableDatabase();

		db.delete(TABLE_INTRUSIONS, KEY_INT + "= '" + intrusionID, null);

		db.close();
	}
}