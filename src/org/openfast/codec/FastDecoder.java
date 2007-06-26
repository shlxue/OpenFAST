package org.openfast.codec;

import java.io.InputStream;

import org.openfast.BitVector;
import org.openfast.BitVectorValue;
import org.openfast.Context;
import org.openfast.IntegerValue;
import org.openfast.Message;
import org.openfast.template.MessageTemplate;
import org.openfast.template.type.Type;

public class FastDecoder implements Coder {

	private final InputStream in;
	private final Context context;
	
	public FastDecoder(Context session, InputStream in) {
		this.in = in;
		this.context = session;
	}

	public Message readMessage() {
		BitVectorValue bitVectorValue = (BitVectorValue)Type.BIT_VECTOR.decode(in);
		if (bitVectorValue == null) return null; // Must have reached end of stream;
		BitVector pmap = (bitVectorValue).value;
		// if template id is not present, use previous, else decode template id
		int templateId = (pmap.isSet(0)) ? ((IntegerValue) Type.UINT.decode(in)).value : context.getLastTemplateId();
		MessageTemplate template = context.getTemplate(templateId);
		if (template == null) return null;
		context.setLastTemplateId(templateId);
		return template.decode(in, templateId, pmap, context);
	}

	public void reset() {
		context.reset();
	}

}
