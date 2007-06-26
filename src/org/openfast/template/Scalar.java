package org.openfast.template;

import java.io.InputStream;

import org.openfast.Context;
import org.openfast.FieldValue;
import org.openfast.ScalarValue;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;


public class Scalar extends Field {

	private final Type type;
	private final Operator operator;
	private final Integer typeName;
	private final String operatorName;
	private String dictionary;
	private ScalarValue defaultValue = ScalarValue.UNDEFINED;
	private final ScalarValue initialValue;
	
	public Scalar(String name, Integer type, String operator, boolean optional) {
		super(name, optional);
		this.typeName = type;
		this.operator = Operator.getOperator(operator, type);
		this.type = Type.getType(type, optional, this.operator);
		this.operatorName = operator;
		this.dictionary = "global";
		this.initialValue = (defaultValue == null || defaultValue.isUndefined()) ? this.type.getDefaultValue() : defaultValue;
	}
	
	// OLD CONSTRUCTORS
	
	private Scalar(String name, String operator, ScalarValue defaultValue, Integer typeEnum, boolean optional)
	{
		super(name, optional);
		this.typeName = typeEnum;
		this.operator = Operator.getOperator(operator, typeEnum);
		this.type = Type.getType(typeEnum, optional, this.operator);
		this.operatorName = operator;
		this.defaultValue = defaultValue;
		this.dictionary = "global";
		this.initialValue = (defaultValue == null || defaultValue.isUndefined()) ? type.getDefaultValue() : defaultValue;
	}
	
	public Scalar(String name, Integer type, Operator operator, ScalarValue defaultValue, boolean optional) {
		super(name, optional);
		this.operator = operator;
		this.operatorName = operator.getName();
		this.dictionary = "global";
		this.defaultValue = defaultValue;
		this.typeName = type;
		this.type = Type.getType(type, optional, operator);
		this.initialValue = (defaultValue == null || defaultValue.isUndefined()) ? this.type.getDefaultValue() : defaultValue;
	}
	
	public Scalar(String name, String typeName, String operator, boolean optional) {
		this(name, typeName, operator, ScalarValue.UNDEFINED, optional);
	}
	
	public Scalar(String name, Integer type, String operator, ScalarValue defaultValue, boolean optional) {
		this(name, operator, defaultValue, type, optional);
	}

	public Scalar(String name, String typeName, String operator, ScalarValue defaultValue, boolean optional) {
		this(name, operator, defaultValue, Type.getTypeEnum(typeName), optional);
	}

	public Scalar(String name, String typeName, String operator, String defaultValue, boolean optional) {
		this(name, operator, ScalarValue.getValue(Type.getTypeEnum(typeName), defaultValue), Type.getTypeEnum(typeName), optional);
	}

	public Integer getType() {
		return typeName;
	}

	public Operator getOperator() {
		return operator;
	}
	
	public String getOperatorName() {
		return operatorName;
	}

	public byte[] encode(FieldValue value, Group template, Context context) {
		try {
			ScalarValue priorValue = (ScalarValue) context.lookup(getDictionary(), template, getKey());
			ScalarValue valueToEncode = operator.getValueToEncode((ScalarValue) value, priorValue, this);
			if (!(operatorName == Operator.DELTA && value == null))
				context.store(getDictionary(), template, getKey(), (ScalarValue) value);
			if (valueToEncode == null)
				return new byte[0];
			return type.encode(valueToEncode);
		} catch (Exception e) {
			throw new RuntimeException("Error occurred while encoding scalar \"" + getName() + "\": " + e.getMessage(), e);
		}
	}

	public String getDictionary() {
		return dictionary;
	}

	public ScalarValue decodeValue(ScalarValue newValue, ScalarValue previousValue) {
		return operator.decodeValue(newValue, previousValue, this);
	}

	public ScalarValue getDefaultValue() {
		return defaultValue;
	}

	public ScalarValue decode(InputStream in, ScalarValue previousValue) {
		// TODO - Refactor out this if condition
		if (operatorName == Operator.CONSTANT)
			return operator.decodeValue(null, null, this);
		return decodeValue(type.decode(in), previousValue);
	}

	public ScalarValue decode(ScalarValue previousValue) {
		return operator.decodeEmptyValue(previousValue, this);
	}

	public boolean usesPresenceMapBit() {
		return operator.usesPresenceMapBit(optional); 	
	}

	public boolean isPresenceMapBitSet(byte[] encoding, FieldValue fieldValue) {
		return operator.isPresenceMapBitSet(encoding, fieldValue);
	}

	public FieldValue decode(InputStream in, Group template, Context context, boolean present) {
		ScalarValue previousValue = context.lookup(getDictionary(), template, getKey());
		ScalarValue value;
		if (present)
			value =  decode(in, previousValue);
		else
			if (getOperatorName() == Operator.CONSTANT && isOptional())
				value = null;
			else
				value = decode(previousValue);
		if (!(getOperatorName() == Operator.DELTA && value == null))
			context.store(getDictionary(), template, getKey(), value);
//		if (value != null)
//			System.out.print(getName() + "=" + value);
		return value;
	}


	public void setDictionary(String dictionary) {
		this.dictionary = dictionary;
	}


	public void setDefaultValue(ScalarValue defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void setDefaultValue(String value) {
	}
	
	public String toString() {
		return "Scalar [name=" + name + ", operator=" + operator + ", dictionary=" + dictionary + "]";
	}


	public Class getValueType() {
		return ScalarValue.class;
	}


	public FieldValue createValue(String value) {
		return type.parse(value);
	}

	public String getTypeName() {
		return "scalar";
	}


	public ScalarValue getInitialValue() {
		return initialValue;
	}
}
