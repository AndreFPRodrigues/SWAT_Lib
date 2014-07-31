package hcim.auric.database;

import hcim.auric.recognition.Picture;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

public class ConfigurationDatabase {
	public static ConfigurationDatabase INSTANCE;
	public static ConfigurationDatabase SERVICE_INSTANCE;

	public static final String MY_PICTURE_ID = "myface";

	public static final String ORIGINAL_MODE = "Original Mode";
	public static final String WIFI_MODE = "Laboratory Test Mode";
	public static final String NONE = "None";

	private SQLitePicture pictureDB;
	private SQLiteState stateDB;

	private Picture myPicture;
	private List<Picture> allPictures;

	private String mode;

	private ConfigurationDatabase(Context c) {
		pictureDB = new SQLitePicture(c);
		stateDB = new SQLiteState(c);
		
		myPicture = getMyPicture();
		mode = getMode();

		if(mode == null){
			stateDB.insertMode(NONE);
			mode = NONE;
		}
	}

	public Picture getMyPicture() {
		if (myPicture == null)
			if (pictureDB != null)
				myPicture = pictureDB.getPicture(MY_PICTURE_ID);

		return myPicture;
	}

	public void setMyPicture(Bitmap bitmap) {
		Picture pic = new Picture(MY_PICTURE_ID, bitmap);

		if (myPicture == null)
			pictureDB.addPicture(pic);
		else
			pictureDB.updatePicture(pic);

		myPicture = pic;
	}

	public List<Picture> getAllPictures() {
		if (allPictures == null) {
			if (pictureDB != null) {
				allPictures = pictureDB.getAllPictures();
			}
		}
		return allPictures;
	}

	public String getMode() {
		if (mode == null) {
			if (stateDB != null) {
				mode = stateDB.getMode();
			}
		}
		Log.d("SCREEN", "mode=" + mode);
		return mode;
	}

	public void setMode(String newMode) {
		stateDB.updateMode(newMode);
		mode = newMode;
		Log.d("SCREEN", mode);
	}

	public static ConfigurationDatabase getInstance(Context c){
		if(INSTANCE == null){
			INSTANCE = new ConfigurationDatabase(c);
		}
		
		return INSTANCE;
	}
	
/*	public static ConfigurationDatabase getInstanceForService(Context c){
		if(SERVICE_INSTANCE == null){
			SERVICE_INSTANCE = new ConfigurationDatabase(c);
		}
		
		return SERVICE_INSTANCE;
	}*/
}
