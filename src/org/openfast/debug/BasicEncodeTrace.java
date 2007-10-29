package org.openfast.debug;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.openfast.ByteUtil;
import org.openfast.FieldValue;
import org.openfast.template.Field;
import org.openfast.template.Group;

public class BasicEncodeTrace implements Trace {

	private Stack stack = new Stack();

	public void groupStart(Group group) {
		TraceGroup traceGroup = new TraceGroup(group);
		if (!stack.isEmpty())
			((TraceGroup)stack.peek()).addGroup(traceGroup);
		stack.push(traceGroup);
	}

	public void field(Field field, FieldValue value, FieldValue encoded, byte[] encoding, int pmapIndex) {
		((TraceGroup)stack.peek()).addField(field, value, encoded, pmapIndex, encoding);
	}

	public void groupEnd() {
		TraceGroup group = (TraceGroup) stack.pop();
		if (stack.isEmpty()) {
			System.out.println(group);
		}
	}

	public void pmap(byte[] pmap) {
		((TraceGroup)stack.peek()).setPmap(pmap);
	}

	private class TraceGroup implements TraceNode {

		private List nodes;
		private byte[] pmap;
		private Group group;

		public TraceGroup(Group group) {
			this.group = group;
			this.nodes = new ArrayList(group.getFieldCount());
		}

		public void setPmap(byte[] pmap) {
			this.pmap = pmap;
		}

		public void addField(Field field, FieldValue value, FieldValue encoded, int fieldIndex, byte[] encoding) {
			nodes.add(new TraceField(field, value, encoded, fieldIndex, encoding));
		}

		public void addGroup(TraceGroup traceGroup) {
			nodes.add(traceGroup);
		}

		public StringBuilder serialize(StringBuilder builder, int indent) {
			builder.append(indent(indent)).append(group.getName()).append("\n");
			indent +=2;
			if (pmap != null)
				builder.append(indent(indent)).append("PMAP: ").append(ByteUtil.convertByteArrayToBitString(pmap)).append("\n");
			for (int i=0; i<nodes.size(); i++) {
				((TraceNode) nodes.get(i)).serialize(builder, indent);
			}
			indent -=2;
			return builder;
		}
		
		public String toString() {
			return serialize(new StringBuilder(), 0).toString();
		}
	}
	
	private class TraceField implements TraceNode {
		private Field field;
		private int pmapIndex;
		private byte[] encoding;
		private FieldValue value;
		private FieldValue encoded;

		public TraceField(Field field, FieldValue value, FieldValue encoded, int pmapIndex, byte[] encoding) {
			this.field = field;
			this.value = value;
			this.encoded = encoded;
			this.pmapIndex = pmapIndex;
			this.encoding = encoding;
		}

		public StringBuilder serialize(StringBuilder builder, int indent) {
			builder.append(indent(indent));
			builder.append(field.getName()).append("[");
			if (field.usesPresenceMapBit())
				builder.append("pmapIndex:").append(pmapIndex);
			builder.append("]: ").append(value).append(" = ").append(encoded).append(" -> ");
			builder.append(ByteUtil.convertByteArrayToBitString(encoding));
			builder.append("\n");
			return builder;
		}
	}
	
	private interface TraceNode {
		StringBuilder serialize(StringBuilder builder, int indent);
	}

	public String indent(int indent) {
		String tab = "";
		for (int i=0; i<indent; i++)
			tab += " ";
		return tab;
	}
}
