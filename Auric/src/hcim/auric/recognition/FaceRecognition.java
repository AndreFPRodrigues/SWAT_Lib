package hcim.auric.recognition;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.objdetect.CascadeClassifier;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.Log;

import com.hcim.intrusiondetection.R;

public class FaceRecognition {

	private static FaceRecognition INSTANCE;

	private static final String TAG = "FACE RECOGNITION";
	static final int MY_PICTURE = 1;
	static final int OTHER_PICTURE = 2;
	static final int CHECK = 3;
	static final long MAX_IMG = 10;

	public static final String MY_PICTURE_ID = "myface";
	public static final String OTHER_PICTURE_ID = "other";

	private File mCascadeFile;
	private CascadeClassifier faceDetector;
	private int mAbsoluteFaceSize = 0;

	private BaseLoaderCallback mLoaderCallback;
	String mPath = "";
	PersonRecognizer recognizer;
	int[] labels = new int[(int) MAX_IMG];
	int countImages = 0;
	Labels labelsFile;
	private Context context;

	public static FaceRecognition getInstance(Context c) {
		if (INSTANCE == null) {
			String filesDir = c.getFilesDir().toString();
			INSTANCE = new FaceRecognition(c, filesDir);
		}
		return INSTANCE;
	}

	public boolean trainPicture(Bitmap rgbBitmap, String name) {
		Bitmap grayBitmap = convertToGray(rgbBitmap);

		Mat rgbMat = new Mat();
		Mat grayMat = new Mat();

		Utils.bitmapToMat(rgbBitmap, rgbMat);
		Utils.bitmapToMat(grayBitmap, grayMat);

		MatOfRect faces = detect(grayMat);
		Rect[] facesArray = faces.toArray();

		if (facesArray == null || facesArray.length == 0) {
			return false;
		} else {
			train(facesArray, name, rgbMat);
			return true;
		}
	}

	public boolean recognizePicture(Bitmap rgbBitmap) {
		Bitmap grayBitmap = convertToGray(rgbBitmap);

		Mat rgbMat = new Mat();
		Mat grayMat = new Mat();

		Utils.bitmapToMat(rgbBitmap, rgbMat);
		Utils.bitmapToMat(grayBitmap, grayMat);

		MatOfRect faces = detect(grayMat);
		Rect[] facesArray = faces.toArray();

		if (facesArray == null || facesArray.length == 0) {
			Log.d("SCREEN", "face detection failed");
			return false;
		} else {
			Log.d("SCREEN", "face detected");
			int result = recognize(facesArray, grayMat);
			Log.d("SCREEN", "result=" + result);

			return result > 0 && result < 80;
		}
	}

	public void stopTrain() {
		recognizer.train();
	}

	MatOfRect detect(Mat gray) {
		MatOfRect faces = new MatOfRect();

		if (faceDetector != null)
			faceDetector.detectMultiScale(gray, faces, 1.1, 2, 2, new Size(
					mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());

		return faces;
	}

	FaceRecognition(Context c, String filesDir) {
		this.context = c;
		mLoaderCallback = new MyBaseLoaderCallback(c);

		if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, context,
				mLoaderCallback)) {
			Log.e(TAG, "Cannot connect to OpenCV Manager");
		}

		mPath = filesDir + "/facerecogOCV/";

		labelsFile = new Labels(mPath);

		boolean success = (new File(mPath)).mkdirs();
		if (!success) {
			Log.e("Error", "Error creating directory");
		}
	}

	void train(Rect[] facesArray, String name, Mat rgbMat) {
		Mat m = new Mat();
		Rect r = facesArray[0];

		m = rgbMat.submat(r);

		if (countImages < MAX_IMG) {
			recognizer.add(m, name);
			countImages++;
		}

	}

	int recognize(Rect[] facesArray, Mat grayMat) {
		Mat m = new Mat();
		m = grayMat.submat(facesArray[0]);
		String resultString = recognizer.predict(m);

		int result = -1;
		if (resultString.equals(MY_PICTURE_ID)) {
			result = recognizer.getProb();
		}else{
			Log.d("SCREEN", "MATCHS " + resultString);
		}
		return result;
	}

	static Bitmap convertToGray(Bitmap img) {
		int width, height;
		height = img.getHeight();
		width = img.getWidth();

		Bitmap bmpGrayscale = Bitmap.createBitmap(width, height,
				Bitmap.Config.RGB_565);
		Canvas c = new Canvas(bmpGrayscale);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(img, 0, 0, paint);

		return bmpGrayscale;
	}

	private class MyBaseLoaderCallback extends BaseLoaderCallback {
		private Context c;

		MyBaseLoaderCallback(Context c) {
			super(c);
			this.c = c;
		}

		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				Log.i(TAG, "OpenCV loaded successfully");

				recognizer = new PersonRecognizer(mPath);
				recognizer.load();

				try {
					InputStream is = c.getResources().openRawResource(
							R.raw.lbpcascade_frontalface);
					File cascadeDir = c.getDir("cascade", Context.MODE_PRIVATE);
					mCascadeFile = new File(cascadeDir, "lbpcascade.xml");
					FileOutputStream os = new FileOutputStream(mCascadeFile);

					byte[] buffer = new byte[4096];
					int bytesRead;
					while ((bytesRead = is.read(buffer)) != -1) {
						os.write(buffer, 0, bytesRead);
					}
					is.close();
					os.close();

					faceDetector = new CascadeClassifier(
							mCascadeFile.getAbsolutePath());
					if (faceDetector.empty()) {
						Log.e(TAG, "Failed to load cascade classifier");
						faceDetector = null;
					} else
						Log.i(TAG, "Loaded cascade classifier from "
								+ mCascadeFile.getAbsolutePath());

					cascadeDir.delete();

				} catch (IOException e) {
					e.printStackTrace();
					Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
				}
			}
				break;
			default: {
				super.onManagerConnected(status);
			}
				break;
			}
		}
	}
}
