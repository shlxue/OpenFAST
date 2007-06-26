package org.openfast;

import org.openfast.codec.Coder;

public interface MessageHandler {
	void handleMessage(GroupValue readMessage, Context context, Coder coder);
}
