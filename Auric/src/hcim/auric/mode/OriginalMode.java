package hcim.auric.mode;

import hcim.auric.audit.AuditTask;
import hcim.auric.receiver.OriginalReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class OriginalMode extends AbstractMode {
		
	public OriginalMode(Context c){
		super(c);
		
		task = new AuditTask(context);
		
		receiver = new OriginalReceiver((AuditTask)task);
		
		filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
	}
}
