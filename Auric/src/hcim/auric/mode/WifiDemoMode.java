package hcim.auric.mode;

import hcim.auric.audit.WifiDemoAuditTask;
import hcim.auric.receiver.WifiDemoReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class WifiDemoMode extends AbstractMode {

	public WifiDemoMode(Context c){
		super(c);
		
		task = new WifiDemoAuditTask(context);
		
		receiver = new WifiDemoReceiver((WifiDemoAuditTask)task);
		
		filter = new IntentFilter();
		filter.addAction("swat_interaction");
		filter.addAction(Intent.ACTION_SCREEN_OFF);
	}
}
