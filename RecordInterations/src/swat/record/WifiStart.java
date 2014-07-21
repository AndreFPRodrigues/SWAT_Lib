package swat.record;

import android.content.Context;
import android.content.Intent;
import android.text.method.Touch;
import android.util.Log;
import mswat.core.CoreController;
import mswat.interfaces.IOReceiver;
import mswat.touch.TouchRecognizer;

public class WifiStart implements IOReceiver {

	Context c;
	public WifiStart(Context c){
		registerIOReceiver();
		this.c=c;
	}
	
	@Override
	public int registerIOReceiver() {
		Log.d("RESCUE", "registerd");

		return 	CoreController.registerIOReceiver(this);

	}

	@Override
	public void onUpdateIO(int device, int type, int code, int value,
			int timestamp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTouchReceived(int type) {
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

}
