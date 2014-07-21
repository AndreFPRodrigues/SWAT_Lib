package swat.record;

import mswat.core.CoreController;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {
	private final String LT = "interactionLog";
	private WifiStart wf;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.d(LT, CoreController.M_HEIGHT + " " + CoreController.S_HEIGHT);
		wf =new WifiStart(this);
	}

	public void start(View v) {
//		Intent intent = new Intent();
//		intent.setAction("swat_interaction");
//		intent.putExtra("logging", true);
//		sendBroadcast(intent);
//		
		Intent intent = new Intent(getBaseContext(), RunInteraction.class);
		intent.putExtra("interaction", "null");
		startActivity(intent);

	}

	public void stop(View v) {
		Intent intent = new Intent();
		intent.setAction("swat_interaction");
		intent.putExtra("logging", false);
		sendBroadcast(intent);
		finish();
	}

}
