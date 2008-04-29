/**
 * 
 */
package org.openfast.template.type;

import org.openfast.Global;
import org.openfast.IntegerValue;
import org.openfast.ScalarValue;
import org.openfast.error.FastConstants;
import org.openfast.template.LongValue;
import org.openfast.template.type.codec.TypeCodec;
import org.openfast.util.Util;

public abstract class IntegerType extends SimpleType {

    protected final long minValue;
    protected final long maxValue;

    public IntegerType(String typeName, long minValue, long maxValue, TypeCodec codec, TypeCodec nullableCodec) {
        super(typeName, codec, nullableCodec);
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
    /**
     * @param value
     * @return either longvalue or integervalue depending on size of parsed
     *         number
     */
    protected ScalarValue getVal(String value) {
        long longValue;
        try {
            longValue = Long.parseLong(value);
        } catch (NumberFormatException e) {
            Global.handleError(FastConstants.S3_INITIAL_VALUE_INCOMP, "The value \"" + value + "\" is not compatable with type "
                    + this);
            return null;
        }
        if (Util.isBiggerThanInt(longValue)) {
            return new LongValue(longValue);
        }
        return new IntegerValue((int) longValue);
    }
    /**
     * @return Returns a default value
     */
    public ScalarValue getDefaultValue() {
        return new IntegerValue(0);
    }
    /**
     * @param previousValue
     *            The previous value of the Field, used in determining the
     *            corresponding field value for the current message being
     *            decoded.
     * @return Returns true if the passed value is an instance of an integer or
     *         long
     */
    public boolean isValueOf(ScalarValue previousValue) {
        return previousValue instanceof IntegerValue || previousValue instanceof LongValue;
    }
    /**
     * Validates the passed ScalarValue, if fails, throws error.
     * 
     * @param value
     *            The ScalarValue object to be validated
     * 
     */
    public void validateValue(ScalarValue value) {
        if (value == null || value.isUndefined())
            return;
        if (value.toLong() > maxValue || value.toLong() < minValue) {
            Global.handleError(FastConstants.D2_INT_OUT_OF_RANGE, "The value " + value + " is out of range for type " + this);
        }
    }
}