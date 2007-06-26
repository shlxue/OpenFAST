package org.openfast.template.operator;

public abstract class AlwaysPresentOperator extends Operator {

	protected AlwaysPresentOperator(String name, Integer[] types) {
		super(name, types);
	}

	public boolean usesPresenceMapBit(boolean optional) {
		return false;
	}

}
