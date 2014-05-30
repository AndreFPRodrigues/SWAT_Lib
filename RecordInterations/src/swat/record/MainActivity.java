package swat.record;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

	}

	public void start(View v) {
		Intent intent = new Intent();
		intent.setAction("swat_interaction");
		intent.putExtra("logging", true);
		sendBroadcast(intent);

	}

	public void stop(View v) {
		Intent intent = new Intent();
		intent.setAction("swat_interaction");
		intent.putExtra("logging", false);
		sendBroadcast(intent);
	}

}
