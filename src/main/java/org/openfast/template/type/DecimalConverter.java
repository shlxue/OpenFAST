package org.openfast.template.type;

import org.openfast.DecimalValue;
import org.openfast.FieldValue;
import org.openfast.IntegerValue;
import org.openfast.ScalarValue;
import org.openfast.template.ComposedValueConverter;
import org.openfast.template.LongValue;

public class DecimalConverter implements ComposedValueConverter {
    private static final long serialVersionUID = 1L;

    private static final FieldValue[] NULL_SET = new FieldValue[] { null, null };

    private static final FieldValue[] UNDEFINED_SET = new FieldValue[] { ScalarValue.UNDEFINED, ScalarValue.UNDEFINED };

    public FieldValue[] split(FieldValue value) {
        if (value == null)
            return NULL_SET;
        else if (value == ScalarValue.UNDEFINED)
            return UNDEFINED_SET;
        DecimalValue decimal = (DecimalValue) value;
        return new FieldValue[] { new IntegerValue(decimal.exponent), new LongValue(decimal.mantissa) };
    }

    public FieldValue compose(FieldValue[] values) {
        if (values[0] == null)
            return null;
        if (values[0] == ScalarValue.UNDEFINED)
            return ScalarValue.UNDEFINED;
        return new DecimalValue(((ScalarValue)values[1]).toLong(), ((IntegerValue) values[0]).value);
    }
}
