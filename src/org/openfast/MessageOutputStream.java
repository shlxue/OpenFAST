package org.openfast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.openfast.codec.FastEncoder;
import org.openfast.session.Session;
import org.openfast.template.MessageTemplate;

public class MessageOutputStream implements MessageStream {

	private final OutputStream out;
	private final FastEncoder encoder;
	private final Context context;
	private Map handlers = Collections.EMPTY_MAP;

	public MessageOutputStream(OutputStream outputStream) {
		this(outputStream, new Context());
	}

	public MessageOutputStream(OutputStream outputStream, Context context) {
		this.out = outputStream;
		this.encoder = new FastEncoder(context);
		this.context = context;
	}

	public void writeMessage(Message message) {
		try {
			Integer templateId = new Integer(message.getTemplateId());
			if (handlers.containsKey(templateId))
				((MessageHandler) handlers.get(templateId)).handleMessage(message, context, encoder);
			byte[] data = encoder.encode(message);
			if (data == null || data.length == 0) return;
			out.write(data);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void reset() {
		writeMessage(Session.RESET);
		encoder.reset();
	}

	public void registerTemplate(int templateId, MessageTemplate template) {
		encoder.registerTemplate(templateId, template);
	}

	public void close() {
		try {
			out.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public OutputStream getUnderlyingStream() {
		return out;
	}

	public void addMessageHandler(int templateId, MessageHandler handler) {
		if (handlers == Collections.EMPTY_MAP)
			handlers = new HashMap();
		handlers.put(new Integer(templateId), handler);
	}
}
