package org.openfast.codec;

import org.openfast.Context;
import org.openfast.Message;
import org.openfast.template.Group;
import org.openfast.template.MessageTemplate;

/* In 1.5 version use : static import java.lang.Math.*; */

public class FastEncoder implements Coder {

	private Context context;
	
	public FastEncoder(Context context)
	{
		this.context = context;
	}

	/**
	 * WARNING: Not thread-safe.
	 * @param message
	 * @return
	 */
	public byte[] encode(Message message) {
		MessageTemplate template = context.getTemplate(message.getTemplateId());
		if (template == null) return null;
		return template.encode(message, context);
	}

	public void reset() {
		context.reset();
	}

	public void registerTemplate(int templateId, Group template) {
		context.registerTemplate(templateId, template);
	}

}
