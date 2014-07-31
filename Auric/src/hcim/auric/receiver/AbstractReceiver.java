package hcim.auric.receiver;

import hcim.auric.audit.AbstractAuditTask;
import android.content.BroadcastReceiver;

public abstract class AbstractReceiver extends BroadcastReceiver{
	protected AbstractAuditTask task;

	public AbstractAuditTask getTask() {
		return task;
	}

	public void setTask(AbstractAuditTask task) {
		this.task = task;
	}
}
