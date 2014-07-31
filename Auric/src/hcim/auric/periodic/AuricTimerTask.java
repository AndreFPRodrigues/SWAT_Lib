package hcim.auric.periodic;

import hcim.auric.camera.CameraManager;

import java.util.TimerTask;

public class AuricTimerTask extends TimerTask {
	CameraManager camera;
	
	public AuricTimerTask(CameraManager c){
		this.camera = c;
	}

	@Override
	public void run() {
		camera.takePicture();
	}
}
