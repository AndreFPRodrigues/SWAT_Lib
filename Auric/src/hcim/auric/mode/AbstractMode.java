package hcim.auric.mode;

import hcim.auric.audit.AbstractAuditTask;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

public abstract class AbstractMode {
	protected BroadcastReceiver receiver;
	protected IntentFilter filter;
	protected AbstractAuditTask task;
	protected Context context;

	public AbstractMode(Context c) {
		context = c;
	}

	public AbstractAuditTask getTask() {
		return task;
	}

	public void setTask(AbstractAuditTask task) {
		this.task = task;
	}

	public BroadcastReceiver getReceiver() {
		return receiver;
	}

	public IntentFilter getFilter() {
		return filter;
	}

	public void destroy() {
		task.interrupt();
	}
}
