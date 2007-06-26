package org.openfast;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.openfast.codec.FastDecoder;
import org.openfast.template.MessageTemplate;

public class MessageInputStream implements MessageStream {
	private InputStream in;
	private FastDecoder decoder;
	private Context context;
	private Map handlers = Collections.EMPTY_MAP;

	public MessageInputStream(InputStream inputStream) {
		this(inputStream, new Context());
	}
	
	public MessageInputStream(InputStream inputStream, Context context) {
		this.in = inputStream;
		this.context = context;
		this.decoder = new FastDecoder(context, in);
	}

	/**
	 * @throws java.io.EOFException
	 * @return
	 */
	public Message readMessage() {
		Message message = decoder.readMessage();
		if (message == null) return null;
		Integer id = new Integer(message.getTemplateId());
		if (handlers.containsKey(id)) {
			((MessageHandler)handlers.get(id)).handleMessage(message, context, decoder);
			return readMessage();
		}
		return message;
	}
	
	public void registerTemplate(int templateId, MessageTemplate template) {
		context.registerTemplate(templateId, template);
	}

	public void close() {
		try {
			in.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public InputStream getUnderlyingStream() {
		return in;
	}

	public void addMessageHandler(int templateId, MessageHandler handler) {
		if (handlers == Collections.EMPTY_MAP) handlers = new HashMap();
		handlers.put(new Integer(templateId), handler);
	}
}
