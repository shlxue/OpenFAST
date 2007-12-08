package org.openfast.submitted;

import java.io.IOException;
import java.io.InputStream;

import org.openfast.Message;
import org.openfast.MessageBlockReader;
import org.openfast.MessageInputStream;
import org.openfast.template.loader.XMLMessageTemplateLoader;
import org.openfast.test.OpenFastTestCase;

public class OpraFeedTest extends OpenFastTestCase {
	private final class OpraBlockReader implements MessageBlockReader {
		private int bytesLeft;
		public boolean readBlock(InputStream in) {
        	try {
		        if (bytesLeft == 0) {
		            bytesLeft = ((in.read() << 24) + (in.read() << 16) + (in.read() << 8) + (in.read() << 0));
		            in.read(); // read SOH byte
		            bytesLeft--;
		        }
	            int msgLen =(0x000000FF & in.read()); // read message length
	            bytesLeft--;
	            if (msgLen == 3) {
	            	return readBlock(in);
	            } else {
		            bytesLeft-=msgLen;
	            }
		        return true;
        	} catch (IOException ioe) {
        		return false;
        	}
		}
		public void messageRead(InputStream in, Message message) {
		}
	}

	public void testReadFeed() {
		XMLMessageTemplateLoader loader = new XMLMessageTemplateLoader();
		loader.load(resource("OPRA/OPRATemplate.xml"));
		loader.getTemplateRegistry().register(0, "OPRA");
		
		MessageInputStream in = new MessageInputStream(resource("OPRA/messages.fast"));
		OpraBlockReader opraBlockReader = new OpraBlockReader();
		in.setBlockReader(opraBlockReader);
		in.setTemplateRegistry(loader.getTemplateRegistry());
		Message msg = in.readMessage();
		while (msg != null) {
			msg = in.readMessage();
		}
	}
}
