package hcim.auric.database;

import hcim.auric.recognition.Picture;
import hcim.auric.serial.Converter;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

public class SQLitePicture extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "PictureDB";
	
	private static final String TABLE_PICTURES = "pictures";
	private static final String KEY_ID = "id";
	private static final String KEY_BITMAP = "picture";
	private static final String[] COLUMNS = { KEY_ID, KEY_BITMAP };

	public SQLitePicture(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_PICTURES_TABLE = "CREATE TABLE pictures ( "
				+ "id TEXT PRIMARY KEY, " + "picture BLOB )";

		db.execSQL(CREATE_PICTURES_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS pictures");

		this.onCreate(db);
	}

	public void addPicture(Picture pic) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, pic.getName());
		values.put(KEY_BITMAP, Converter.bitmapToByteArray(pic.getImage()));

		db.insert(TABLE_PICTURES, null,	values); 

		db.close();
	}

	public Picture getPicture(String myPictureId) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_PICTURES, COLUMNS, KEY_ID + "='"
				+ myPictureId + "'", null, null, null, null, null);

		if (cursor != null)
			cursor.moveToFirst();

		if (cursor.getCount() <= 0)
			return null;

		String id = cursor.getString(0);
		byte[] blob = cursor.getBlob(1);

		Picture picture = new Picture(id, (Bitmap) Converter.byteArrayToBitmap(blob));

		return picture;
	}

	public List<Picture> getAllPictures() {
		List<Picture> result = new LinkedList<Picture>();

		String query = "SELECT  * FROM " + TABLE_PICTURES;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null);

		Picture pic = null;
		
		if (cursor.moveToFirst()) {
			if (cursor.getCount() <= 0)
				return null;
			do {
				pic = new Picture(cursor.getString(0),
						(Bitmap) Converter.byteArrayToBitmap(cursor.getBlob(1)));

				result.add(pic);
			} while (cursor.moveToNext());
		}

		return result;
	}

	public int updatePicture(Picture pic) {

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_BITMAP, Converter.bitmapToByteArray(pic.getImage())); 

		int i = db.update(TABLE_PICTURES,
				values,
				KEY_ID + " = '" + pic.getName() + "'",
				null);

		db.close();

		return i;
	}

	public void deletePicture(Picture pic) {
		SQLiteDatabase db = this.getWritableDatabase();

		db.delete(TABLE_PICTURES, KEY_ID + "= '" + pic.getName(), null);

		db.close();
	}
}