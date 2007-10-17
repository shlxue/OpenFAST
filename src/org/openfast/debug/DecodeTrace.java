package org.openfast.debug;

import java.util.HashMap;
import java.util.Map;

import org.openfast.GroupValue;
import org.openfast.ScalarValue;
import org.openfast.template.Group;
import org.openfast.template.Scalar;
import org.openfast.template.operator.Operator;

public class DecodeTrace extends BasicTrace {


	private static final Map OPERATOR_SYMBOLS = new HashMap();
	static {
		OPERATOR_SYMBOLS.put(Operator.DELTA, "+");
		OPERATOR_SYMBOLS.put(Operator.COPY, "=");
		OPERATOR_SYMBOLS.put(Operator.DEFAULT, "!");
		OPERATOR_SYMBOLS.put(Operator.INCREMENT, "++");
		OPERATOR_SYMBOLS.put(Operator.TAIL, "---");
		OPERATOR_SYMBOLS.put(Operator.CONSTANT, "@");
		OPERATOR_SYMBOLS.put(Operator.NONE, ",");
	}

	public void groupStarted(Group group) {
		print(group);
		moveDown();
	}
	
	public void groupEnded(GroupValue groupValue) {
		print(groupValue);
		moveUp();
	}

	public void field(Scalar scalar, ScalarValue value, ScalarValue previousValue, ScalarValue decodedValue, byte[] buffer) {
		StringBuilder scalarDecode = new StringBuilder();
		scalarDecode.append(scalar.getName()).append(": ");
		if (previousValue == ScalarValue.UNDEFINED)
			scalarDecode.append("_");
		else
			scalarDecode.append(previousValue);
		scalarDecode.append(' ');
		scalarDecode.append(OPERATOR_SYMBOLS.get(scalar.getOperator()));
		scalarDecode.append(' ');
		scalarDecode.append(decodedValue).append(" -> ").append(value);
		print(scalarDecode);
	}
}
