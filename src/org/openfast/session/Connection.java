package org.openfast.session;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Connection {
	InputStream getInputStream() throws IOException;
	OutputStream getOutputStream() throws IOException;
	void close();
}
