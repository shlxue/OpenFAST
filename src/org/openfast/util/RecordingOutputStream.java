package org.openfast.util;

import java.io.IOException;
import java.io.OutputStream;

import org.openfast.ByteUtil;

public class RecordingOutputStream extends OutputStream {
	private byte[] buffer = new byte[1024]; // TODO - listify buffer.
	private int index = 0;
	private OutputStream out;
	
	public RecordingOutputStream(OutputStream outputStream) {
		this.out = outputStream;
	}

	public void write(int b) throws IOException {
		buffer[index++] = (byte) b;
		out.write(b);
	}

	public String toString() {
		return ByteUtil.convertByteArrayToBitString(buffer, index);
	}
	
}
