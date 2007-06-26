package org.openfast.util;

import java.io.IOException;
import java.io.InputStream;

import org.openfast.ByteUtil;

public class RecordingInputStream extends InputStream {
	private byte[] buffer = new byte[1024]; // TODO - listify buffer.
	private int index = 0;
	private InputStream in;
	public RecordingInputStream(InputStream inputStream) {
		this.in = inputStream;
	}

	public int read() throws IOException {
		int read = in.read();
		buffer[index++] = (byte) read;
		return read;
	}
	
	public String toString() {
		return ByteUtil.convertByteArrayToBitString(buffer, index);
	}
	
	public byte[] getBuffer() {
		byte[] b = new byte[index];
		System.arraycopy(buffer, 0, b, 0, index);
		return b;
	}

	public void clear() {
		index = 0;
	}
}
