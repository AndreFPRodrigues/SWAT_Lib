package hcim.auric.intrusiondetection;

import hcim.auric.authentication.BackgroundService;
import hcim.auric.database.ConfigurationDatabase;
import hcim.auric.recognition.FaceRecognition;
import hcim.auric.recognition.Picture;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.hcim.intrusiondetection.R;

public class ConfigurationsView extends Activity {
	private static final int REQUEST_IMAGE_CAPTURE = 1;
	private Spinner spinner;
	private ImageView img;
	private String currentMode;
	private Bitmap myPicture;
	private Context context;
	private ConfigurationDatabase configDB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.configurations_view);

		context = getApplicationContext();
		configDB = ConfigurationDatabase.getInstance(context);

		// get data from database
		Picture p = configDB.getMyPicture();
		currentMode = configDB.getMode();
		myPicture = p == null ? null : p.getImage();

		// init image view
		img = (ImageView) findViewById(R.id.mypicture);
		if (myPicture != null) {
			img.setImageBitmap(myPicture);
		}

		// init button
		Button myPicture = (Button) findViewById(R.id.change_picture_button);
		myPicture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dispatchTakePictureIntent();
			}
		});

		// init spinner
		spinner = (Spinner) findViewById(R.id.mode_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.mode_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		selectCurrentMode();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
			Bundle extras = data.getExtras();
			Bitmap imageBitmap = (Bitmap) extras.get("data");
			String name = ConfigurationDatabase.MY_PICTURE_ID;

			FaceRecognition recognition = FaceRecognition.getInstance(context);
			boolean result = recognition.trainPicture(imageBitmap, name);

			if (result) {
				if (myPicture == null) { // first config
					Log.d("SCREEN", "add all pictures");
					addPictures();
				}
				myPicture = imageBitmap;
				img.setImageBitmap(imageBitmap);

				// store
				configDB.setMyPicture(myPicture);
				recognition.stopTrain();

			} else {
				Toast.makeText(getApplicationContext(),
						"Face Detection Failed! Take another picture!",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void finish() {
		changeMode();
		super.finish();
	}

	private void selectCurrentMode() {
		Log.d("SCREEN", currentMode);

		if (currentMode.equals(ConfigurationDatabase.NONE)) {
			spinner.setSelection(0);
		}
		if (currentMode.equals(ConfigurationDatabase.WIFI_MODE)) {
			spinner.setSelection(1);
		}
		if (currentMode.equals(ConfigurationDatabase.ORIGINAL_MODE)) {
			spinner.setSelection(2);
		}
	}

	private void addPictures() {
		FaceRecognition recognition = FaceRecognition.getInstance(context);
		Context context = getApplicationContext();
		String name;
		Bitmap grayBitmap;
		boolean result;

		name = "pic_a";
		grayBitmap = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.g);
		result = recognition.trainPicture(grayBitmap, name);
		Log.d("SCREEN", "train: " + name + result);

		name = "pic_b";
		grayBitmap = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.p);
		result = recognition.trainPicture(grayBitmap, name);
		Log.d("SCREEN", "train: " + name + result);

		name = "pic_c";
		grayBitmap = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.m);
		result = recognition.trainPicture(grayBitmap, name);
		Log.d("SCREEN", "train: " + name + result);
	}

	private void notifyMode() {
		String msg = null;

		if (currentMode.equals(ConfigurationDatabase.NONE)) {
			msg = "Mode: none";
		}
		if (currentMode.equals(ConfigurationDatabase.WIFI_MODE)) {
			msg = "Mode: Laboratory Test Mode";
		}
		if (currentMode.equals(ConfigurationDatabase.ORIGINAL_MODE)) {
			msg = "Mode: Original Mode";
		}

		if (msg != null) {
			Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG)
					.show();
		}
	}

	private void changeMode() {
		String selectedMode = (String) spinner.getSelectedItem();

		if (currentMode.equals(selectedMode))
			return;

		configDB.setMode(selectedMode);

		String none = ConfigurationDatabase.NONE;

		if (selectedMode.equals(none)) { // current must be original or wifi
			stopService(new Intent(ConfigurationsView.this,
					BackgroundService.class));
		} else if (currentMode.equals(none)) { // selectedMode must be original
												// or wifi
			startService(new Intent(ConfigurationsView.this,
					BackgroundService.class));
		} else { // switch original/wifi
			stopService(new Intent(ConfigurationsView.this,
					BackgroundService.class));
			startService(new Intent(ConfigurationsView.this,
					BackgroundService.class));
		}

		currentMode = selectedMode;
		notifyMode();
	}

	private void dispatchTakePictureIntent() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
		}
	}
}
