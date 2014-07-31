package hcim.auric.record;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeoutException;

import mswat.core.CoreController;
import mswat.core.activityManager.Node;
import mswat.core.macro.Touch;
import mswat.interfaces.ContentReceiver;
import mswat.interfaces.IOReceiver;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;

import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.exceptions.RootDeniedException;
import com.stericson.RootTools.execution.Command;

public class InteractionLog extends BroadcastReceiver implements IOReceiver,
		ContentReceiver {
	private final String LT = "interactionLog";

	private static SparseArray<Queue<Touch>> interaction;
	private static  int id_content;
	private static int id_io; 
	private int monitor;
	private static int idScreenShot = -1;
	private static int idScreenTouch = 0;
	private long time;

	private static String filepath;
	
	private boolean recording;

	@Override
	public int registerIOReceiver() {
		return CoreController.registerIOReceiver(this);
	}

	@Override
	public void onUpdateIO(int device, int type, int code, int value,
			int timestamp) {

		if (device == monitor) {

			if (code == 54 && value > CoreController.M_HEIGHT)
				interaction.get(idScreenTouch).add(
						new Touch(type, code,
								(int) (-CoreController.M_HEIGHT + 10),
								timestamp));
			else
				interaction.get(idScreenTouch).add(
						new Touch(type, code, value, timestamp));
		}

	}

	@Override
	public void onTouchReceived(int type) {

	}

	@Override
	public int registerContentReceiver() {
		return CoreController.registerContentReceiver(this);
	}

	@Override
	public void onUpdateContent(ArrayList<Node> content) {
		if (time == -1)
			time = System.currentTimeMillis();
		else {
			if ((System.currentTimeMillis() - time) < 300) {
				return;
			} else
				time = System.currentTimeMillis();
		}
		screenShot();
	}

	@Override
	public int getType() {
		return DESCRIBABLE;
	}



	private void screenShot() {

		idScreenShot++;
		interaction.put(idScreenShot, new LinkedList<Touch>());
		new Thread(new Runnable() {
			public void run() {

				if (RootTools.isAccessGiven()) { /* magic root code here */
				}
				try {
					if (idScreenShot != -1) {
					
						Command command = new Command(0,
								"/system/bin/screencap -p " + filepath + "/"
										+ idScreenShot + ".png") {
							@Override
							public void output(int id, String line) {

							}

							@Override
							public void commandCompleted(int arg0, int arg1) {
								idScreenTouch = idScreenShot;
								Log.d(LT, "completed id:" + idScreenTouch);

							}

							@Override
							public void commandOutput(int arg0, String arg1) {

							}

							@Override
							public void commandTerminated(int arg0, String arg1) {
								idScreenTouch = idScreenShot;
								Log.d(LT, " terminated id:" + idScreenTouch);

							}
						};
						RootTools.getShell(true).add(command);
					}
				} catch (IOException e) {
					// something went wrong, deal with it here
				}

				catch (TimeoutException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (RootDeniedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();

	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("swat_interaction")) {
			boolean run = intent.getBooleanExtra("logging", false);
			if (run) {
				recording=true;
				String folder = intent.getLongExtra("timestamp",0) + "";

				filepath = Environment.getExternalStorageDirectory().toString()
						+ "/intlog/intrusions/" + folder;

				File f = new File(filepath);
				f.mkdirs();

				time = -1;
				Log.d(LT, "Recording interaction");
				monitor = CoreController.monitorTouch();

				interaction = new SparseArray<Queue<Touch>>();
				screenShot();

				id_content = registerContentReceiver();
				id_io = registerIOReceiver();
				Log.d("RESCUE", "REGISTER ID IO:" + id_io );
				Log.d("RESCUE", "REGISTER ID Content:" + id_content );


			} else {
				Log.d(LT, "Stoped recording1");
				writeLog();
				CoreController.unregisterIOReceiver(id_io);
				CoreController.unregisterContent(id_content);
				Log.d("RESCUE", "UNREGISTER ID IO:" + id_io );
				Log.d("RESCUE", "UNREGISTER ID Content:" + id_content );
				idScreenShot = -1;
				idScreenTouch = 0;

			}

		}else{
			if(recording){
				Log.d(LT, "Stoped recording1");
				writeLog();
				CoreController.unregisterIOReceiver(id_io);
				CoreController.unregisterContent(id_content);
				Log.d("RESCUE", "UNREGISTER ID IO:" + id_io );
				Log.d("RESCUE", "UNREGISTER ID Content:" + id_content );
				idScreenShot = -1;
				idScreenTouch = 0;
			}
				
		}

	}

	private void writeLog() {

		Queue<Touch> touches;
		ArrayList<String> interactions = new ArrayList<String>();
		for (int i = 0; i <= idScreenShot; i++) {
			touches = interaction.get(i);
			if (touches == null)
				return;
			Touch t;
			while ((t = touches.poll()) != null) {

				interactions.add(i + "," + t.getType() + "," + t.getCode()
						+ "," + t.getValue() + "," + t.getTimestamp());
			}
		}
		CoreController.writeToLog((ArrayList<String>) interactions.clone(),
				filepath + "/log");

	}

}
