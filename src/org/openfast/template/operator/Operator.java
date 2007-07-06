package org.openfast.template.operator;

import java.util.HashMap;
import java.util.Map;

import org.openfast.template.type.Type;

public class Operator {
	private static final Map OPERATOR_NAME_MAP = new HashMap();
	private final String name;

	public static final Operator NONE = new Operator("none");
	public static final Operator CONSTANT = new Operator("constant");
	public static final Operator DEFAULT = new Operator("default");
	public static final Operator COPY = new Operator("copy");
	public static final Operator INCREMENT = new Operator("increment");
	public static final Operator DELTA = new Operator("delta");
	public static final Operator TAIL = new Operator("tail");
	public static final Operator TWIN = new Operator("twin");

	public Operator(String name) {
		this.name = name;
		OPERATOR_NAME_MAP.put(name, this);
	}
	
	public static Operator getOperator(String name) {
		if (!OPERATOR_NAME_MAP.containsKey(name))
			throw new IllegalArgumentException("The operator \"" + name + "\" does not exist.");
		return (Operator) OPERATOR_NAME_MAP.get(name);
	}
	
	public OperatorCodec getCodec(Type type) {
		return OperatorCodec.getCodec(this, type);
	}
	
	public String toString() {
		return name;
	}

	public String getName() {
		return name;
	}
}
