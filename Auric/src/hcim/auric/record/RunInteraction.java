package hcim.auric.record;

import hcim.auric.database.IntrusionsDatabase;
import hcim.auric.intrusion.Intrusion;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

import mswat.core.macro.Touch;
import mswat.touch.TouchRecognizer;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hcim.intrusiondetection.R;

public class RunInteraction extends Activity {
	private final static String LT = "interactionLog";
	private SparseArray<Queue<Touch>> interaction;
	private ImageView img;
	private ImageView background;
	private ImageView playImg;
	private LinearLayout slideIndicator;
	private int swipeIndex;
	private ArrayList<ImageView> swipeClues;
	private List<Bitmap> imgIntruser;

	String imgBasePath;
	String imgBasePathIntruser;
	Button run;
	final int UPDATE_IMAGE = 0;
	final int RESET = 1;

	RelativeLayout layout;
	float x = 0;
	float y = 0;

	private int currentImage;
	private String imgPath;

	private final int PREV = 0;
	private final int NEXT = 1;
	private final int PLAY = 2;
	private final int STOP = 3;
	private final int PLAY_ALL = 4;

	private int nav_command;
	private boolean play;
	private boolean reseting;
	private DrawingView dv;

	private final int interval = 1000; // 1 Second
	private int time = -1;
	private int totalTime = -1;

	private Handler handlerTimer = new Handler();
	private Runnable runnable = new Runnable() {
		public void run() {
			TextView t = (TextView) findViewById(R.id.timer);
			time--;
			t.setText((totalTime - time) + "/" + totalTime + "s");
			if (time >= 0) {
				handler.postDelayed(runnable, interval);

			} else {
				t.setText("");

			}

		}
	};

	private int intruser_img_index = 0;
	private int delay;
	private int numberImg;
	private Handler handler_photos = new Handler();
	private Runnable runnable_photos = new Runnable() {
		public void run() {
			intruser_img_index++;

			ImageView iv = (ImageView) findViewById(R.id.img_intruser);
			Bitmap bm = imgIntruser.get(intruser_img_index);
			if (bm != null) {
				iv.setImageBitmap(bm);
			}

			if (intruser_img_index < numberImg && time >= 0) {
				handler_photos.postDelayed(runnable_photos, delay);

			} else {
				intruser_img_index = 0;
			}

		}
	};

	// timestamp of last interaction
	double lastTouchTime;

	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View decorView = getWindow().getDecorView();
		// Hide the status bar.
		int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
		decorView.setSystemUiVisibility(uiOptions);
		// Remember that you should never show the action bar if the
		// status bar is hidden, so hide that too if necessary.
		ActionBar actionBar = getActionBar();
		actionBar.hide();
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		Intent intent = getIntent();
		// String folder = intent.getStringExtra("interaction");

		context = getApplicationContext();

		String intrusionID = intent.getStringExtra("interaction");
		Intrusion i = IntrusionsDatabase.getInstance(context).getIntrusion(
				intrusionID);
		String folder = i.getLog().getId();
		imgIntruser = i.getImages();

		dv = new DrawingView(this);
		dv.setZOrderOnTop(true);

		setContentView(R.layout.run_interaction);
		getWindow().addContentView(
				dv,
				new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
						ViewGroup.LayoutParams.FILL_PARENT));

		imgBasePath = Environment.getExternalStorageDirectory().toString()
				+ "/intlog/intrusions/" + folder + "/";
		imgBasePathIntruser = imgBasePath + "intruser/";
		imgPath = Environment.getExternalStorageDirectory().toString()
				+ "/intlog/intrusions/" + folder + "/0.png";
		Log.d(LT, " FilePath:" + imgBasePath);

		img = (ImageView) findViewById(R.id.screenshot);

		ImageView iv = (ImageView) findViewById(R.id.img_intruser);
		Bitmap bm = imgIntruser == null ? null : imgIntruser.get(0);
		if (bm != null) {
			iv.setImageBitmap(bm);

		}
		slideIndicator = (LinearLayout) findViewById(R.id.slideIndicator);
		swipeClues = new ArrayList<ImageView>();
		swipeIndex = 0;
		setControls();

		loadInteraction();
	}

	private void setControls() {
		background = (ImageView) findViewById(R.id.background1);
		playImg = (ImageView) findViewById(R.id.play);
		Bitmap bm = BitmapFactory.decodeFile(imgPath);
		img.setImageBitmap(bm);
		img.setOnTouchListener(new OnSwipeTouchListener(this) {

			public void onSwipeRight() {
				if (currentImage > 0) {
					nav_command = PREV;

					swipeClues.get(swipeIndex).setImageResource(
							R.drawable.circle);
					swipeIndex--;
					if (swipeIndex < 0)
						swipeIndex = 0;
					swipeClues.get(swipeIndex).setImageResource(
							R.drawable.circle_selected);

					boolean result;
					do {
						currentImage--;
						result = runImage(currentImage, -1);
					} while (!result && currentImage > 0);
				}

			}

			public void onSwipeLeft() {
				nav_command = NEXT;

				swipeClues.get(swipeIndex).setImageResource(R.drawable.circle);
				swipeIndex++;
				if (swipeIndex >= swipeClues.size())
					swipeIndex--;
				swipeClues.get(swipeIndex).setImageResource(
						R.drawable.circle_selected);

				boolean result;
				int threshold = 10;
				int aux = 0;
				do {
					currentImage++;
					aux++;
					result = runImage(currentImage, -1);
				} while (!result && aux < threshold);

			}

			public void onSingleTap() {
				if (!play) {
					nav_command = PLAY_ALL;
					runImage(currentImage, -1);
					play = true;
					background.setVisibility(View.INVISIBLE);
					playImg.setVisibility(View.INVISIBLE);
					slideIndicator.setVisibility(View.INVISIBLE);

				} else {
					playImg.setVisibility(View.VISIBLE);
					slideIndicator.setVisibility(View.VISIBLE);
					background.setVisibility(View.VISIBLE);
					nav_command = STOP;
					runImage(currentImage, -1);
					play = false;
				}

			}

			public boolean onTouch(View v, MotionEvent event) {
				boolean result = gestureDetector.onTouchEvent(event);
				return result;
			}

		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.run_macro, menu);

		return true;
	}

	private void loadInteraction() {
		String filepath = imgBasePath + "log";
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
						if (!reseting)
							addSwipeClue();
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
				lastTouchTime = Double.parseDouble(split[4]);
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// public void play(View v) {
	//
	// nav_command = PLAY;
	// runImage(currentImage, -1);
	// }

	private void addSwipeClue() {
		ImageView dot = new ImageView(context);
		if (swipeClues.size() != 0)
			dot.setImageResource(R.drawable.circle);
		else
			dot.setImageResource(R.drawable.circle_selected);

		dot.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		slideIndicator.addView(dot);
		swipeClues.add(dot);

	}

	/**
	 * Runs the interaction (changes screenshot, draws touches) Executes comands
	 * next prev
	 * 
	 * @param index
	 * @param lastTime
	 * @return
	 */
	public boolean runImage(final int index, final long lastTime) {
		currentImage = index;

		switch (nav_command) {
		case NEXT:
		case PREV:

			if (!existsImage(index)) {
				return false;
			} else {
				Message msg = handler.obtainMessage();
				msg.what = UPDATE_IMAGE;
				msg.obj = index;
				handler.sendMessage(msg);
				return true;
			}
		case PLAY:
		case PLAY_ALL:
			if (interaction.get(index) != null
					&& interaction.get(index).peek() != null && time <= -1) {
				setTimerReproduction(index);
				loadIntruserRecord();
				Log.d("RESCUE",
						"index:"
								+ index
								+ " time:"
								+ ((interaction.get(index).peek()
										.getTimestamp() / 1000) - (lastTouchTime / 1000))
								+ " last:" + lastTime);
			}
			play(index, lastTime);

			break;
		case STOP:
			time = -1;
			reset(true);
			break;
		}

		return true;
	}

	private void setTimerReproduction(int index) {
		time = (int) ((lastTouchTime / 1000) - (interaction.get(index).peek()
				.getTimestamp() / 1000));
		totalTime = time;
		handlerTimer.postDelayed(runnable, interval);

	}

	public void reset(boolean image) {

		reseting = true;
		if (image) {
			Log.d(LT, "reseteeeee");

			currentImage = 0;
			Message msg = handler.obtainMessage();
			msg.what = RESET;
			msg.obj = 0;
			handler.sendMessage(msg);
		}
		loadInteraction();
	}

	public void play(final int index, final long lastTime) {
		final Queue<Touch> q = interaction.get(index);
		if ((nav_command == PLAY && existsImage(index) && lastTime != -1)
				|| nav_command == STOP) {
			Log.d("RESCUE", "STTOOPED");
			return;
		}
		if (q != null && q.size() > 0) {
			Message msg = handler.obtainMessage();
			msg.what = UPDATE_IMAGE;
			msg.obj = index;
			handler.sendMessage(msg);
		}
		new Thread() {
			public void run() {

				try {

					// sleep(500);

					if (q != null) {
						Touch t;
						long delay = lastTime;
						int x = -1;
						while ((t = q.poll()) != null && nav_command != STOP) {
							if (delay != -1) {

								sleep((long) (t.getTimestamp() - delay));

							}
							delay = (long) t.getTimestamp();
							if (t.getCode() == TouchRecognizer.ABS_MT_POSITION_X) {
								// x =
								// CoreController.xToScreenCoord(t.getValue());
								x = t.getValue();
							} else {
								if (t.getCode() == TouchRecognizer.ABS_MT_POSITION_Y) {
									dv.onTouch(x, t.getValue());
								}
							}

						}
						if (index < (interaction.size() - 1)
								&& nav_command != STOP) {
							currentImage++;
							runImage(index + 1, delay);
						} else {
							reset(true);

							// finish();
						}

					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}

	/**
	 * Changes the background screenshot
	 */
	final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == UPDATE_IMAGE) {

				Bitmap bm = BitmapFactory.decodeFile(imgBasePath
						+ (int) msg.obj + ".png");
				if (bm != null) {
					img.setImageBitmap(bm);
					img.invalidate();
				}

			} else {
				if (msg.what == RESET) {
					swipeClues.get(swipeIndex).setImageResource(
							R.drawable.circle);
					swipeIndex = 0;
					swipeClues.get(swipeIndex).setImageResource(
							R.drawable.circle_selected);
					playImg.setVisibility(View.VISIBLE);
					slideIndicator.setVisibility(View.VISIBLE);
					background.setVisibility(View.VISIBLE);
					play = false;
				}

				Bitmap bm = BitmapFactory.decodeFile(imgBasePath
						+ (int) msg.obj + ".png");
				if (bm != null) {
					img.setImageBitmap(bm);
					img.invalidate();
				}
			}
			super.handleMessage(msg);
		}
	};

	private boolean existsImage(int index) {
		Bitmap bm = BitmapFactory.decodeFile(imgBasePath + index + ".png");
		if (bm != null) {
			return true;
		} else
			return false;
	}

	/**
	 * Handles the circle drawing that represents the touch
	 * 
	 * @author andre
	 * 
	 */
	class DrawingView extends SurfaceView {

		private final SurfaceHolder surfaceHolder;
		private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		private final Paint paintRed = new Paint(Paint.ANTI_ALIAS_FLAG);
		private final Paint paintBlack = new Paint(Paint.ANTI_ALIAS_FLAG);

		public DrawingView(Context context) {
			super(context);
			surfaceHolder = getHolder();
			surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
			paint.setColor(Color.WHITE);
			paint.setStyle(Style.FILL);
			paintBlack.setColor(Color.BLACK);
			paintBlack.setStyle(Style.FILL);
			paintRed.setColor(Color.RED);
			paintRed.setStyle(Style.FILL);
		}

		public boolean onTouch(int x, int y) {
			if (surfaceHolder.getSurface().isValid()) {
				Canvas canvas = surfaceHolder.lockCanvas();
				canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);

				if (y < 0) {
					// y = CoreController.yToScreenCoord(-y);
					y = -y;
					canvas.drawCircle(x, y, 16, paintBlack);
					canvas.drawCircle(x, y, 13, paintRed);
				} else {
					// y = CoreController.yToScreenCoord(y);
					canvas.drawCircle(x, y, 16, paintBlack);
					canvas.drawCircle(x, y, 13, paint);
				}
				surfaceHolder.unlockCanvasAndPost(canvas);
			}

			return false;
		}

	}

	/**
	 * Starts the reproduction of the recordings of the intruser
	 * 
	 * @param totalTime2
	 */
	private void loadIntruserRecord() {
		numberImg = imgIntruser.size();
		intruser_img_index = 0;

		delay = totalTime / numberImg * 1000;
		handler_photos.postDelayed(runnable_photos, delay);
	}

}
