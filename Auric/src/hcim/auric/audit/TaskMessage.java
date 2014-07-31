package hcim.auric.audit;

import android.graphics.Bitmap;

public class TaskMessage {
	String ID;
	Bitmap pic;
	long timestamp;

	public TaskMessage(String id) {
		this.ID = id;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public Bitmap getPic() {
		return pic;
	}

	public void setPic(Bitmap pic) {
		this.pic = pic;
	}

}
