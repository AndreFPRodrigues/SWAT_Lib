package mswat.interfaces;

import android.view.accessibility.AccessibilityEvent;

public interface EventReceiver {
	/**
	 * Registers receiver, returns the receiver identifier (index)
	 * 
	 * @return
	 */
	public int registerEventReceiver();

	/**
	 * Receives arrayList with the updated content
	 * 
	 * @param content
	 */
	public abstract void onUpdateEvent(AccessibilityEvent event);
	
	public abstract  int [] getType();
}
