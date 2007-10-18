package org.openfast.debug;

import org.openfast.ByteUtil;
import org.openfast.GroupValue;
import org.openfast.template.Field;
import org.openfast.template.Group;

public class EncodeTrace extends BasicTrace {

	private StringBuilder fieldEncodings;

	public void groupStart(GroupValue groupValue) {
		print(groupValue);
		fieldEncodings = new StringBuilder();
		moveDown();
	}

	public void field(Field field, int fieldIndex, int pmapIndex, byte[] encoding) {
		fieldEncodings.append(indent);
		fieldEncodings.append(field.getName()).append("[fieldIndex:").append(fieldIndex);
		if (field.usesPresenceMapBit())
			fieldEncodings.append(", pmapIndex:").append(pmapIndex);
		fieldEncodings.append("] = ").append(ByteUtil.convertByteArrayToBitString(encoding));
		fieldEncodings.append("\n");
	}

	public void groupEnd() {
		print("PMAP: " + ByteUtil.convertByteArrayToBitString(pmap));
		System.out.println(fieldEncodings);
		moveUp();
	}

	public void pmap(byte[] pmap) {
		this.pmap = pmap;
	}

	public void groupStarted(Group group) {
		System.out.print(indent);
		System.out.print(group);
	}

}
