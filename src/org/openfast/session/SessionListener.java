package org.openfast.session;

public interface SessionListener {
	SessionListener NULL = new SessionListener() {
		public void onClose() {
		}};

	void onClose();
}
