package hcim.auric.receiver;

import hcim.auric.audit.AuditTask;
import hcim.auric.audit.TaskMessage;
import hcim.auric.database.ConfigurationDatabase;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class OriginalReceiver extends AbstractReceiver{ 
	private ConfigurationDatabase configDB;

	public OriginalReceiver(AuditTask task) {
		this.task = task;
		configDB = ConfigurationDatabase.getInstance(task.getContext());
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (configDB.getMyPicture() == null) {
			Log.d("SCREEN", "DB null get my picture");
			return;
		}

		String action = intent.getAction();

		if (action.equals(Intent.ACTION_SCREEN_OFF)) {
			task.addTaskMessage(new TaskMessage(AuditTask.ACTION_OFF));
		}

		if (action.equals(Intent.ACTION_SCREEN_ON)) {
			task.addTaskMessage(new TaskMessage(AuditTask.ACTION_ON));
		}
	}
}
