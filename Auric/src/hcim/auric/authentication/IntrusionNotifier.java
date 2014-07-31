package hcim.auric.authentication;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import com.hcim.intrusiondetection.R;

public class IntrusionNotifier {
	private static final int NOTIFICATION_ID = 981532;

	Context context;
	NotificationManager notificationManager;
	Notification intrusionDetected;

	public IntrusionNotifier(Context context) {
		this.context = context;
		this.notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		intrusionDetected = new Notification.Builder(context)
				.setContentTitle("AURIC - Intrusion Detected")
				.setContentText("Background recording")
				.setSmallIcon(R.drawable.auric_icon).build();
	}

	public void notifyUser() {
		notificationManager.notify(NOTIFICATION_ID, intrusionDetected);
	}

	public void cancelNotification() {
		notificationManager.cancel(NOTIFICATION_ID);
	}
}
