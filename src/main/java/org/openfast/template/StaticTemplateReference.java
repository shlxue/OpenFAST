package org.openfast.template;

import java.io.InputStream;

import org.openfast.BitVectorBuilder;
import org.openfast.BitVectorReader;
import org.openfast.Context;
import org.openfast.FieldValue;

public class StaticTemplateReference extends Field {
	private static final long serialVersionUID = 1L;
	private MessageTemplate template;

	public StaticTemplateReference(MessageTemplate template) {
		super(template.getQName(), false);
		this.template = template;
	}

	public FieldValue createValue(String value) {
		return null;
	}

	public FieldValue decode(InputStream in, Group template, Context context, BitVectorReader pmapReader) {
		return null;
	}

	public byte[] encode(FieldValue value, Group template, Context context, BitVectorBuilder presenceMapBuilder) {
		return null;
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

	public MessageTemplate getTemplate() {
		return template;
	}
	
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		StaticTemplateReference other = (StaticTemplateReference) obj;
		return template.equals(other.template);
	}
}
