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


package org.openfast.template.operator;

import org.openfast.BitVectorBuilder;
import org.openfast.DecimalValue;
import org.openfast.IntegerValue;
import org.openfast.ScalarValue;
import org.openfast.template.Scalar;
import org.openfast.template.TwinValue;
import org.openfast.template.type.Type;


public class TwinOperatorCodec extends OperatorCodec {
	private static final long serialVersionUID = 1L;
	private static final TwinValue DEFAULT = new TwinValue(new IntegerValue(0), new IntegerValue(0));
	private OperatorCodec exponentOperator;
    private OperatorCodec mantissaOperator;

    public TwinOperatorCodec(Operator firstOperator, Operator secondOperator) {
        super(Operator.TWIN, new Type[] { Type.DECIMAL });
        this.exponentOperator = firstOperator.getCodec(Type.I32);
        this.mantissaOperator = secondOperator.getCodec(Type.I32);
    }

    /**
     * @return Returns null
     */
    public ScalarValue decodeEmptyValue(ScalarValue previousValue, Scalar field) {
        return null;
    }

    /**
     * @param val
     * @param priorVal
     * @return the actual value given the previous value and current value 
     */
    public ScalarValue decodeValue(ScalarValue val, ScalarValue priorVal,
        Scalar field) {
        if (val == null) return null;

        TwinValue priorValue = (priorVal == ScalarValue.UNDEFINED) ? DEFAULT : (TwinValue) priorVal;

        TwinValue value = (TwinValue) val;
        IntegerValue exponent = (value.first == null)
            ? (IntegerValue) exponentOperator.decodeEmptyValue(priorValue.first,
                field)
            : (IntegerValue) exponentOperator.decodeValue(value.first,
                priorValue.first, field);
        IntegerValue mantissa = (value.second == null)
            ? (IntegerValue) mantissaOperator.decodeEmptyValue(priorValue.second,
                field)
            : (IntegerValue) mantissaOperator.decodeValue(value.second,
                priorValue.second, field);

        return new DecimalValue(mantissa.value, exponent.value);
    }

    /**
     * 
     * @param val The current ScalarValue object
     * @param priorVal The prior ScalarValue object
     * @param field The Scalar object
     * @param presenceMapBuilder The BitVector object
     * @return the value that should be encoded over the fast stream
     */
    public ScalarValue getValueToEncode(ScalarValue val, ScalarValue priorVal, Scalar field, BitVectorBuilder presenceMapBuilder) {
        ScalarValue priorValue = priorVal.isUndefined() ? field.getDefaultValue() : toTwin(priorVal);
        ScalarValue firstPrior = priorValue.isUndefined() ? ScalarValue.UNDEFINED : ((TwinValue) priorValue).first;
        ScalarValue secondPrior = priorValue.isUndefined() ? ScalarValue.UNDEFINED : ((TwinValue) priorValue).second;
        if (val == null) {
            if (field.isOptional()) {
                return exponentOperator.getValueToEncode(null, firstPrior, field, presenceMapBuilder);
            } else {
                throw new IllegalArgumentException(
                    "Mandatory fields can't be null.");
            }
        }
        DecimalValue value = (DecimalValue) val;

        if (priorVal.isUndefined() && field.getDefaultValue().isUndefined()) {
            return new TwinValue(new IntegerValue(value.exponent),
                new IntegerValue(value.mantissa));
        }


        if (priorValue.equals(val)) {
            return null;
        }

        ScalarValue exponentValue = exponentOperator.getValueToEncode(new IntegerValue(value.exponent), firstPrior, field, presenceMapBuilder);
        ScalarValue mantissaValue = mantissaOperator.getValueToEncode(new IntegerValue(value.mantissa), secondPrior, field, presenceMapBuilder);

        return new TwinValue(exponentValue, mantissaValue);
    }

    /**
     * Convert a ScalarValue object to a TwinValue object
     * @param priorVal The ScalarValue object to be converted
     * @return Returns a TwinValue object 
     */
    private TwinValue toTwin(ScalarValue priorVal) {
    	DecimalValue val = (DecimalValue) priorVal;
		return new TwinValue(new IntegerValue(val.exponent), new IntegerValue(val.mantissa));
	}

    /**
     * Checks to see if the supplied object is a TwinOperator object
     * @param obj The object to be checked to see if its TwinOperator object
     * @return Returns true if the passed object is a TwinOperator object, false otherwise
     */
	public boolean equals(Object obj) {
        if ((obj == null) || !(obj instanceof TwinOperatorCodec)) {
            return false;
        }

        return equals((TwinOperatorCodec) obj);
    }

	/**
	 * Checks to see if the exponentOperator and the mantissaOperator of the passed object are the same as the one being compared to
	 * @param other The TwinOperator object to be checked if the decimal values are the same
	 * @return Returns true if the exponentOperator and the mantissaOperator of the passed object are the same as the one being compared, false otherwise
	 */
    private boolean equals(TwinOperatorCodec other) {
        return exponentOperator.equals(other.exponentOperator) &&
        mantissaOperator.equals(other.mantissaOperator);
    }
    
    public int hashCode() {
    	return exponentOperator.hashCode() * 37 + mantissaOperator.hashCode();
    }

	public ScalarValue getValueToEncode(ScalarValue value, ScalarValue priorValue, Scalar field) {
		throw new UnsupportedOperationException();
	}
	
	public boolean canEncode(ScalarValue val, Scalar field) {
		if (val == null || val.isUndefined())
			return true;
		TwinValue value = toTwin(val);
		return exponentOperator.canEncode(value.first, field) &&
		       mantissaOperator.canEncode(value.second, field);
	}
}
