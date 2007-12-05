/*
The contents of this file are subject to the Mozilla Public License
Version 1.1 (the "License"); you may not use this file except in
compliance with the License. You may obtain a copy of the License at
http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS"
basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
License for the specific language governing rights and limitations
under the License.

The Original Code is OpenFAST.

The Initial Developer of the Original Code is The LaSalle Technology
Group, LLC.  Portions created by The LaSalle Technology Group, LLC
are Copyright (C) The LaSalle Technology Group, LLC. All Rights Reserved.

Contributor(s): Jacob Northey <jacob@lasalletech.com>
                Craig Otis <cotis@lasalletech.com>
*/


package org.openfast;

public class ByteVectorValue extends ScalarValue {
    private static final long serialVersionUID = 1L;
	public final byte[] value;

    public ByteVectorValue(byte[] value) {
        this.value = value;
    }
    
    public byte[] getBytes() {
    	return value;
    }
    
    public String toString() {
    	StringBuffer builder = new StringBuffer(value.length*2);
    	for (int i=0; i<value.length; i++) {
    		String hex = Integer.toHexString(value[i]);
    		if (hex.length() == 1)
    			builder.append('0');
			builder.append(hex);
    	}
    	return builder.toString();
    }

    public boolean equals(Object obj) {
        if ((obj == null) || !(obj instanceof ByteVectorValue)) {
            return false;
        }

        return equals((ByteVectorValue) obj);
    }

    public boolean equals(ByteVectorValue other) {
        if (this.value.length != other.value.length) {
            return false;
        }

        for (int i = 0; i < this.value.length; i++)
            if (this.value[i] != other.value[i]) {
                return false;
            }

        return true;
    }

    public int hashCode() {
    	return value.hashCode();
    }
}
