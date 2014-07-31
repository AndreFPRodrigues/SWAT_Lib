package hcim.auric.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteState extends SQLiteOpenHelper {

	public static final String AURIC_MODE_ID = "auric_mode";

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "AuricStateDB";

	private static final String TABLE_STATE = "auricstate";
	private static final String KEY_ID = "id";
	private static final String KEY_MODE = "mode";
	private static final String[] COLUMNS = { KEY_ID, KEY_MODE };

	public SQLiteState(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String table = "CREATE TABLE " + TABLE_STATE + " ( "
				+ KEY_ID + " TEXT PRIMARY KEY, " + KEY_MODE + " TEXT )";

		db.execSQL(table);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATE);

		this.onCreate(db);
	}

	public String getMode() {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_STATE, COLUMNS, KEY_ID + "='"
				+ AURIC_MODE_ID + "'", null, null, null, null, null);

		if (cursor != null)
			cursor.moveToFirst();

		if (cursor.getCount() <= 0)
			return null;

		String mode = cursor.getString(1);

		return mode;
	}

	public void insertMode(String mode){
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, AURIC_MODE_ID);
		values.put(KEY_MODE, mode);

		db.insert(TABLE_STATE, null, values);

		db.close();
	}
	
	public int updateMode(String mode) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_MODE, mode);

		int i = db.update(TABLE_STATE, values, KEY_ID + " = '" + AURIC_MODE_ID
				+ "'", null);

		db.close();

		return i;
	}
}