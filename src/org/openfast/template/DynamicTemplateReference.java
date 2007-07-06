package org.openfast.template;

import java.io.InputStream;

import org.openfast.BitVectorBuilder;
import org.openfast.Context;
import org.openfast.FieldValue;
import org.openfast.Message;
import org.openfast.codec.FastDecoder;

public class DynamicTemplateReference extends Field {

	public DynamicTemplateReference() {
		super(null, false);
	}

	public FieldValue createValue(String value) {
		return null;
	}

	public FieldValue decode(InputStream in, Group template, Context context, boolean present) {
		return new FastDecoder(context, in).readMessage();
	}

	public byte[] encode(FieldValue value, Group template, Context context, BitVectorBuilder presenceMapBuilder) {
		Message message = (Message) value;
		return message.getTemplate().encode(message, context);
	}

	public String getTypeName() {
		return null;
	}

	public Class getValueType() {
		return null;
	}

	public boolean isPresenceMapBitSet(byte[] encoding, FieldValue fieldValue) {
		return false;
	}

	public boolean usesPresenceMapBit() {
		return false;
	}

}
