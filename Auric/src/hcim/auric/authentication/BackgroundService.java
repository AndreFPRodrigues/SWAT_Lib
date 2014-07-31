package hcim.auric.authentication;

import hcim.auric.database.ConfigurationDatabase;
import hcim.auric.intrusiondetection.MainActivity;
import hcim.auric.mode.AbstractMode;
import hcim.auric.mode.OriginalMode;
import hcim.auric.mode.WifiDemoMode;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.hcim.intrusiondetection.R;

public class BackgroundService extends Service {

	private static final int NOTIFICATION_STICKY = 1;

	private Context context;
	private AbstractMode currentMode;
	private NotificationManager notificationManager;
	private ConfigurationDatabase configDB;
	
	private String modeDescription; 

	@Override
	public void onCreate() {
		context = getApplicationContext();
		configDB = ConfigurationDatabase.getInstance(context);

		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("SCREEN", "on start command");
		
		currentMode = getCurrentMode();
		registerReceiver(currentMode.getReceiver(), currentMode.getFilter());
		currentMode.getTask().start();

		startForeground(NOTIFICATION_STICKY, getNotification());

		return Service.START_STICKY;
	}

	@Override
	public void onDestroy() {
		Log.d("SCREEN", "on destroy method");
		currentMode.destroy();
		unregisterReceiver(currentMode.getReceiver());

		super.onDestroy();
	}

	private AbstractMode getCurrentMode() {
		modeDescription = configDB.getMode();

		if(modeDescription != null){
			if (modeDescription.equals(ConfigurationDatabase.ORIGINAL_MODE)) {
				return new OriginalMode(context);
			} else if (modeDescription.equals(ConfigurationDatabase.WIFI_MODE)) {
				return new WifiDemoMode(context);
			} else if (modeDescription.equals(ConfigurationDatabase.NONE)) {
				return null;
			} else {
				return null;
			}
		}
		return null;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("deprecation")
	private Notification getNotification() {

		if (notificationManager == null)
			notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		CharSequence contentTitle = "AURIC Service";
		CharSequence contentText = "AURIC Service is now running";

		Notification note = new Notification(R.drawable.auric_icon,
				contentTitle, 0);
		note.flags |= Notification.FLAG_NO_CLEAR;
		note.flags |= Notification.FLAG_FOREGROUND_SERVICE;

		PendingIntent intent = PendingIntent.getActivity(this, 0, new Intent(
				this, MainActivity.class), 0);

		note.setLatestEventInfo(this, contentTitle, contentText, intent);
		return note;
	}
}
