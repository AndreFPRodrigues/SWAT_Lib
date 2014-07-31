package hcim.auric.intrusion;

import android.content.Context;
import android.content.Intent;

public class Log {
	private Context c;
	private String timestamp;

	public Log(Context c) {
		this.c = c;
	}

	public Log(String log) {
		this.timestamp = log;
	}

	public String getId() {
		return timestamp;
	}

	public void setId(String id) {
		this.timestamp = id;
	}

	public void stopLogging() {
		Intent intent = new Intent();
		intent.setAction("swat_interaction");
		intent.putExtra("logging", false);
		c.sendBroadcast(intent);
	}

	public void startLogging() {
		this.timestamp = "" + System.currentTimeMillis();
		Intent intent = new Intent();
		intent.setAction("swat_interaction");
		intent.putExtra("logging", true);
		intent.putExtra("timestamp", timestamp);
		c.sendBroadcast(intent);
	}
}
