package org.openfast.template;

import java.io.InputStream;

import org.openfast.Context;
import org.openfast.FieldValue;

public abstract class Field {

	protected final String name;
	protected final String key;
	protected final boolean optional;
	
	public Field(String name, boolean optional) {
		this.name = name;
		this.key = name;
		this.optional = optional;
	}
	
	public Field(String name, String key, boolean optional) {
		this.name = name;
		this.key = key;
		this.optional = optional;
	}

	public String getName() {
		return name;
	}

	public boolean isOptional() {
		return optional;
	}
	
	public String getKey() {
		return key;
	}

	public abstract byte[] encode(FieldValue value, Group template, Context context);

	public abstract FieldValue decode(InputStream in, Group template, Context context, boolean present);

	public abstract boolean usesPresenceMapBit();

	public abstract boolean isPresenceMapBitSet(byte[] encoding, FieldValue fieldValue);

	public abstract Class getValueType();

	public abstract FieldValue createValue(String value);

	public abstract String getTypeName();

}
