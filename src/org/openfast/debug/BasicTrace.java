package org.openfast.debug;

public class BasicTrace {

	protected static final String INDENT_SIZE = "  ";
	protected String indent = "";
	protected byte[] pmap;


	protected void moveDown() {
		indent += INDENT_SIZE;
	}

	protected void moveUp() {
		indent = indent.substring(0, indent.length() - INDENT_SIZE.length());
	}

	protected void print(Object object) {
		System.out.print(indent);
		System.out.println(object);
	}
}
