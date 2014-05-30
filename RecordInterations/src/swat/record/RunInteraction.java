package swat.record;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;
import java.util.regex.Pattern;

import mswat.core.CoreController;
import mswat.core.feedback.FeedBack;
import mswat.core.macro.Touch;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;

import android.text.Editable;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RunInteraction extends Activity {
	private final static String LT = "interactionLog";
	private SparseArray<Queue<Touch>> interaction;
	private ImageView img;
	String imgBasePath;
	final int UPDATE_IMAGE = 0;

	RelativeLayout layout;
	float x = 0;
	float y = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.run_interaction);
	
		String filepath = Environment.getExternalStorageDirectory().toString()
				+ "/intlog/log";
		imgBasePath = Environment.getExternalStorageDirectory().toString()
				+ "/intlog/";
		String imgPath = Environment.getExternalStorageDirectory().toString()
				+ "/intlog/0.png";

		img = (ImageView) findViewById(R.id.screenshot);
		Bitmap bm = BitmapFactory.decodeFile(imgPath);
		img.setImageBitmap(bm);

		File f = new File(filepath);
		interaction = new SparseArray<Queue<Touch>>();
		Scanner scanner;
		int index = -1;
		
		
				 
		try {
			scanner = new Scanner(f);
			Queue<Touch> q = new LinkedList<Touch>();
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String split[] = line.split(",");

				if (split.length > 1) {
					if (Integer.parseInt(split[0]) != index) {
						q = new LinkedList<Touch>();
						index++;
						interaction.put(index, q);
						q.add(new Touch(Integer.parseInt(split[1]), Integer
								.parseInt(split[2]),
								Integer.parseInt(split[3]), Double
										.parseDouble(split[4])));
					} else
						q.add(new Touch(Integer.parseInt(split[1]), Integer
								.parseInt(split[2]),
								Integer.parseInt(split[3]), Double
										.parseDouble(split[4])));
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.run_macro, menu);

		return true;
	}

	public void run(View v) {
		runImage(0);
	}

	public void runImage(final int index) {
		Settings.System.putInt(getContentResolver(),
                "show_touches", 1);
		final Queue<Touch> q = interaction.get(index);

		Message msg = handler.obtainMessage();
		msg.what = UPDATE_IMAGE;
		msg.obj = index;
		handler.sendMessage(msg);
		new Thread() {
			public void run() {

				try {
					sleep(1000);

					if (q != null) {
						Touch t; 
						long delay = -1;

						while ((t = q.poll()) != null) {
							if (delay != -1) {

								sleep((long) (t.getTimestamp() - delay));

							}
							delay = (long) t.getTimestamp();

							CoreController.injectToTouch(t.getType(),
									t.getCode(), t.getValue());

						}
						if (index < (interaction.size() - 1)) {
							runImage(index + 1);
						}else
						{
							Settings.System.putInt(getContentResolver(),
					                "show_touches", 0);
							finish();
						}

					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();

	}

	final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == UPDATE_IMAGE) {
				Bitmap bm = BitmapFactory.decodeFile(imgBasePath
						+ (int) msg.obj + ".png");
				img.setImageBitmap(bm);
			}
			super.handleMessage(msg);
		}
	};

}
