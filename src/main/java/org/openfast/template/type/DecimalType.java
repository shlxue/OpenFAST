/**
 * 
 */
package org.openfast.template.type;

import org.openfast.DecimalValue;
import org.openfast.Global;
import org.openfast.ScalarValue;
import org.openfast.error.FastConstants;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.codec.TypeCodec;

final class DecimalType extends SimpleType {
    private static final long serialVersionUID = 1L;

    DecimalType() {
        super("decimal", TypeCodec.SF_SCALED_NUMBER, TypeCodec.NULLABLE_SF_SCALED_NUMBER);
    }

    /**
     * Get the approprivate codec for the passed operator
     * 
     * @param operator
     *            The operator object in which the codec is trying to get
     * @param optional
     *            Determines if the Field is required or not for the data
     * @return Returns the codec if the field is required
     */
    public TypeCodec getCodec(Operator operator, boolean optional) {
        return super.getCodec(operator, optional);
    }

    /**
     * @param value
     * @return
     */
    protected ScalarValue getVal(String value) {
        try {
            return new DecimalValue(Double.parseDouble(value));
        } catch (NumberFormatException e) {
            Global.handleError(FastConstants.S3_INITIAL_VALUE_INCOMP, "The value \"" + value + "\" is not compatible with type "
                    + this);
            return null;
        }
    }

    /**
     * @return Returns a new DecimalValue with a defualt value
     */
    public ScalarValue getDefaultValue() {
        return new DecimalValue(0.0);
    }

    /**
     * Determines if previousValue is of type DecimalValue
     * 
     * @param previousValue
     *            The previous value of the Field, used in determining the
     *            corresponding field value for the current message being
     *            decoded.
     * @return Returns true if the previousValue is an instance of DecimalValue,
     *         false otherwise
     */
    public boolean isValueOf(ScalarValue previousValue) {
        return previousValue instanceof DecimalValue;
    }
}