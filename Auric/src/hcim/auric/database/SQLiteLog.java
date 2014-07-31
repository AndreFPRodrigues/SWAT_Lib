package hcim.auric.database;

import hcim.auric.intrusion.Log;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteLog extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "LogDB";

	private static final String TABLE_LOGS = "logs";
	private static final String KEY_ID = "id";
	private static final String KEY_LOG = "log";
	private static final String[] COLUMNS = { KEY_ID, KEY_LOG };

	public SQLiteLog(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_INTRUSION_TABLE = "CREATE TABLE logs ( "
				+ "id TEXT PRIMARY KEY, " + "log TEXT )";

		db.execSQL(CREATE_INTRUSION_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS logs");

		this.onCreate(db);
	}

	public void addLog(String id, Log log) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, id);
		values.put(KEY_LOG, log.getId());

		db.insert(TABLE_LOGS, null, values);

		db.close();
	}

	public String getLog(String id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(true, TABLE_LOGS, COLUMNS, KEY_ID + "='" + id
				+ "'", null, null, null, null, null);

		if (cursor != null)
			cursor.moveToFirst();

		if (cursor.getCount() <= 0)
			return null;

		String l = cursor.getString(1);

		return l;
	}

	public void deleteLog(String id) {
		SQLiteDatabase db = this.getWritableDatabase();

		db.delete(TABLE_LOGS, KEY_ID + "= '" + id, null);

		db.close();
	}

}
