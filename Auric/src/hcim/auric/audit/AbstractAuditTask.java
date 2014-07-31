package hcim.auric.audit;

import hcim.auric.authentication.IntrusionNotifier;
import hcim.auric.camera.CameraManager;
import hcim.auric.camera.FrontPictureCallback;
import hcim.auric.database.IntrusionsDatabase;
import hcim.auric.intrusion.Intrusion;
import hcim.auric.periodic.AuricTimerTask;

import java.util.Timer;
import java.util.concurrent.LinkedBlockingQueue;

import android.content.Context;

public abstract class AbstractAuditTask extends Thread {
	public static final String ACTION_NEW_PICTURE = "NEW PICTURE";
	public static int PERIOD = 5000;

	protected AuricTimerTask timerTask;
	protected Timer timer;
	protected CameraManager camera;
	protected Intrusion currentIntrusion;
	protected IntrusionNotifier notifier;
	protected LinkedBlockingQueue<TaskMessage> queue;
	protected IntrusionsDatabase intrusionsDB;
	protected Context context;
 
	protected boolean screenOff;

	public AbstractAuditTask(Context context) {
		this.context = context;
		this.queue = new LinkedBlockingQueue<TaskMessage>();
		this.notifier = new IntrusionNotifier(context);
		this.camera = new CameraManager(new FrontPictureCallback(this));
		this.intrusionsDB = IntrusionsDatabase.getInstance(context);
		this.screenOff = false;
		this.timer = new Timer();
	}

	public void addTaskMessage(TaskMessage msg) {
		// ignore task ACTION INTRUSION DETECTEION after screen is off
		if (screenOff && msg.getID() == ACTION_NEW_PICTURE) {
			return;
		}
		queue.add(msg);
	}

	public Context getContext() {
		return context;
	}

	public AuricTimerTask getTimerTask() {
		return timerTask;
	}

	@Override
	public void interrupt() {
		if(timer != null){
			timer.cancel();
		}
		super.interrupt();
	}
	
	
}
