package hcim.auric.record;

import mswat.core.CoreController;
import mswat.interfaces.IOReceiver;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class WifiStart extends BroadcastReceiver implements IOReceiver {

	Context c;
		
	@Override
	public int registerIOReceiver() {
		Log.d("RESCUE", "registerrrd");

		return 	CoreController.registerIOReceiver(this);

	}

	@Override
	public void onUpdateIO(int device, int type, int code, int value,
			int timestamp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTouchReceived(int type) {
		Log.d("RESCUE","type="+ type);
		if(type==0){
			
			Intent intent = new Intent();
			intent.setAction("swat_interaction");
			intent.putExtra("logging", true);
			long time = System.currentTimeMillis();
			Log.d("RESCUE", "folder name:" + time);
			intent.putExtra("timestamp",time );
			c.sendBroadcast(intent);
		}
		else{
			Intent intent = new Intent();
			intent.setAction("swat_interaction");
			intent.putExtra("logging", false);
			c.sendBroadcast(intent);
		}
		
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("RESCUE", "on receive");
		c = context;
		int id = registerIOReceiver();
		Log.d("RESCUE", "WIFI REGISTERED IO:" + id);
	}

}
