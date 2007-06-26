package org.openfast.session;

import junit.framework.TestCase;

public class FastServerTest extends TestCase {

	private FastServer server = new FastServer("test", SessionFactory.NULL);

	public void testConstructWithNullFactory() {
		try {
			new FastServer("name", null);
			fail();
		} catch (NullPointerException e) {
		}
	}
	
	public void testConstructWithNullOrEmptyName() {
		try {
			new FastServer(null, SessionFactory.NULL);
			fail();
		} catch (NullPointerException e) {
		}

		try {
			new FastServer(" ", SessionFactory.NULL);
			fail();
		} catch (NullPointerException e) {
		}
	}
	
	public void testSetConnectionListenerNull() {
		try {
			server.setConnectionListener(null);
			fail();
		} catch (NullPointerException e) {
		}
	}

	public void testSetErrorHandlerNull() {
		try {
			server.setErrorHandler(null);
			fail();
		} catch (NullPointerException e) {
		}
	}

}
