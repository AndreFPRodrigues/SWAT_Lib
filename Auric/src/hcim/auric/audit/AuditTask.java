package hcim.auric.audit;

import hcim.auric.intrusion.Intrusion;
import hcim.auric.periodic.AuricTimerTask;
import hcim.auric.recognition.FaceRecognition;

import java.util.Timer;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

public class AuditTask extends AbstractAuditTask {
	public static final String ACTION_OFF = "OFF";
	public static final String ACTION_ON = "ON";
		
	protected FaceRecognition recognizer;
	protected boolean startLog;
	  
	public AuditTask(Context c) {
		super(c);

		recognizer = FaceRecognition.getInstance(context);
		startLog = false;
	}

	@Override
	public void run() {
		Log.d("SCREEN", "start AuditTask");

		TaskMessage taskMessage;
		String id;
		while (true) {

			if (!queue.isEmpty()) {
				try {
					taskMessage = queue.take();
					id = taskMessage.getID();

					Log.d("SCREEN", "task = " + id);

					if (id.equals(ACTION_OFF)) {
						screenOff = true;
						actionOff();
					}
					if (id.equals(ACTION_ON)) {
						screenOff = false;
						actionOn();
					}
					if (id.equals(ACTION_NEW_PICTURE)) {
						actionNewPicture(taskMessage.getPic());
					}

				} catch (InterruptedException e) {
					Log.e("SCREEN", e.getMessage());
				}

			}
		}
	}

	public void actionOn() {
		if (!startLog) {
			Log.d("SCREEN", "take picture");
			camera.takePicture();
		}
	}

	public void actionOff() {
		if (startLog) {
			currentIntrusion.stopLogging();
			Log.d("SCREEN", "stop logging");
	
			if (timer != null) {
				timer.cancel();
				timer = null;
				
				Log.d("SCREEN", "STOP periodic thread");
			}
			timerTask = null;
	
			intrusionsDB.addIntrusion(currentIntrusion);
			currentIntrusion = null;
	
			notifier.cancelNotification();
		}
	
		startLog = false;
	}

	public void actionNewPicture(Bitmap capturedFace) {
		boolean intrusion = !recognizer.recognizePicture(capturedFace);

		Log.d("SCREEN", "intrusion=" + intrusion);

		if (intrusion) {
			if (!startLog) { // iniciar auditoria
				startLog = true;

				Log.d("SCREEN", "iniciar auditoria - new Intrusion");
				currentIntrusion = new Intrusion(context);
				currentIntrusion.addImage(capturedFace);
				currentIntrusion.startLogging();
				Log.d("SCREEN", "start logging");

				notifier.notifyUser();

				timerTask = new AuricTimerTask(this.camera);
				timer = new Timer();
				timer.schedule(timerTask, PERIOD);
				
				Log.d("SCREEN", "start periodic thread");
			} else { // continuar auditoria
				Log.d("SCREEN", "continuar auditoria");
				if (currentIntrusion == null) {
					return;
				}
				currentIntrusion.addImage(capturedFace);
			}
		} else {
			if (startLog) { // parar auditoria
				Log.d("SCREEN", "parar auditoria");

				if (timer != null) {
					timer.cancel();
					timer = null;
					Log.d("SCREEN", "STOP periodic thread");
				}
				timerTask = null;

				if (currentIntrusion != null) {
					currentIntrusion.stopLogging();
					intrusionsDB.removeIntrusion(currentIntrusion);
					currentIntrusion = null;
				}
				startLog = false;

				Log.d("SCREEN", "stop logging");

				notifier.cancelNotification();
			}
		}
	}
}
