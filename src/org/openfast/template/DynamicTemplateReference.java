package org.openfast.template;

import java.io.InputStream;

import org.openfast.BitVectorBuilder;
import org.openfast.BitVectorReader;
import org.openfast.Context;
import org.openfast.FieldValue;
import org.openfast.Message;
import org.openfast.QName;
import org.openfast.codec.FastDecoder;

public class DynamicTemplateReference extends Field {
	private static final long serialVersionUID = 1L;

	public DynamicTemplateReference() {
		super(QName.NULL, false);
	}

	/**
	 * @return Returns null
	 */
	public FieldValue createValue(String value) {
		return null;
	}

	/**
	 * @param in
	 * @param template
	 * @param context
	 * @param present
	 * @return the next message in the decoder
	 */
	public FieldValue decode(InputStream in, Group template, Context context, BitVectorReader pmapReader) {
		return new FastDecoder(context, in).readMessage();
	}

	/**
	 * @param value
	 * @param template
	 * @param context
	 * @param presenceMapBuilder
	 * @return the encoding of the message given its template
	 */
	public byte[] encode(FieldValue value, Group template, Context context, BitVectorBuilder presenceMapBuilder) {
		Message message = (Message) value;
		return message.getTemplate().encode(message, context);
	}

	/**
	 * @return Returns null
	 */
	public String getTypeName() {
		return null;
	}

	/**
	 * @return Returns null
	 */
	public Class getValueType() {
		return null;
	}

	/**
	 * @return Returns false
	 */
	public boolean isPresenceMapBitSet(byte[] encoding, FieldValue fieldValue) {
		return false;
	}

	/**
	 * @return Returns false
	 */
	public boolean usesPresenceMapBit() {
		return false;
	}

	public boolean equals(Object obj) {
		return obj != null && obj.getClass().equals(this.getClass());
	}
}
