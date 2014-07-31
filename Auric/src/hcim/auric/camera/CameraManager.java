package hcim.auric.camera;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.util.Log;

public class CameraManager {
	private Camera camera;
	private FrontPictureCallback callback;

	public CameraManager(FrontPictureCallback c) {
		camera = null;
		callback = c;
	}

	public void takePicture() {
		camera = null;

		try {
			camera = Camera.open(CameraInfo.CAMERA_FACING_FRONT);
		} catch (RuntimeException e) {
			camera = null;
			Log.e("SCREEN", e.getMessage());
		}
		try {
			if (camera == null) {
			} else {
				SurfaceTexture dummySurfaceTextureF = new SurfaceTexture(0);
				try {
					camera.setPreviewTexture(dummySurfaceTextureF);
					camera.startPreview();
				} catch (Exception e) {
					Log.e("SCREEN", e.getMessage());
				}

				camera.takePicture(null, null, callback);
			}
		} catch (Exception e) {
			if (camera != null)
				camera.release();
		}
	}

}
