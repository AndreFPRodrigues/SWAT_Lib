package hcim.auric.receiver;

import hcim.auric.audit.TaskMessage;
import hcim.auric.audit.WifiDemoAuditTask;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

@SuppressLint("SimpleDateFormat")
public class WifiDemoReceiver extends AbstractReceiver {
	
	public WifiDemoReceiver(WifiDemoAuditTask task){
		this.task = task;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("swat_interaction")) {
			boolean run = intent.getBooleanExtra("logging", false);

			if (run) {
				long timestamp = intent.getLongExtra("timestamp", 0);

				TaskMessage t = new TaskMessage(WifiDemoAuditTask.ACTION_START);
				t.setTimestamp(timestamp);
				this.task.addTaskMessage(t);

			} else {
				TaskMessage t = new TaskMessage(WifiDemoAuditTask.ACTION_STOP);
				this.task.addTaskMessage(t);
			}

		} else { // screen off
			TaskMessage t = new TaskMessage(WifiDemoAuditTask.ACTION_STOP);
			this.task.addTaskMessage(t);
		}

	}
}
