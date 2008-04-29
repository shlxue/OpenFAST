package org.openfast.template.type;

import org.openfast.template.type.codec.TypeCodec;

public class SignedIntegerType extends IntegerType {
    private static final long serialVersionUID = 1L;

    public SignedIntegerType(int numberBits, long min, long max) {
        super("int" + numberBits, min, max, TypeCodec.INTEGER, TypeCodec.NULLABLE_INTEGER);
    }
}
