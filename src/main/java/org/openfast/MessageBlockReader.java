package org.openfast;

import java.io.InputStream;

public interface MessageBlockReader {

	MessageBlockReader NULL = new MessageBlockReader(){
		public boolean readBlock(InputStream in) {
			return true;
		}

		public void messageRead(InputStream in, Message message) {
		}};

	boolean readBlock(InputStream in);
	void messageRead(InputStream in, Message message);

}
