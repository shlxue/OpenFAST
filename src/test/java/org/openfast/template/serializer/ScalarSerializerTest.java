package org.openfast.template.serializer;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import org.openfast.Dictionary;
import org.openfast.IntegerValue;
import org.openfast.QName;
import org.openfast.ScalarValue;
import org.openfast.StringValue;
import org.openfast.template.Scalar;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;
import org.openfast.test.OpenFastTestCase;
import org.openfast.util.XmlWriter;

public class ScalarSerializerTest extends OpenFastTestCase {
    ScalarSerializer serializer = new ScalarSerializer();
    OutputStream byteOut = new ByteArrayOutputStream();
    XmlWriter writer = new XmlWriter(byteOut);
    
    public void testSerializeFull() {
        Scalar scalar = new Scalar(new QName("value", "http://openfast.org"), Type.U32, Operator.DELTA, new IntegerValue(100), true);
        scalar.setKey(new QName("data", "http://www.openfast.org"));
        scalar.setDictionary(Dictionary.TEMPLATE);
        scalar.setId("101");
        String expected = 
            "<uInt32 name=\"value\" ns=\"http://openfast.org\" presence=\"optional\" id=\"101\">" + NL +
            "    <delta dictionary=\"template\" key=\"data\" ns=\"http://www.openfast.org\" value=\"100\"/>" + NL +
            "</uInt32>" + NL;
        serializer.serialize(writer, scalar, XMLMessageTemplateSerializer.createInitialContext());
        assertEquals(expected, byteOut.toString());
    }
    
    public void testSerializeBasic() {
        Scalar scalar = new Scalar("simple", Type.DECIMAL, Operator.NONE, ScalarValue.UNDEFINED, false);
        String expected = "<decimal name=\"simple\"/>" + NL;
        serializer.serialize(writer, scalar, XMLMessageTemplateSerializer.createInitialContext());
        assertEquals(expected, byteOut.toString());
    }
    
    public void testSerializeString() {
        Scalar scalar = new Scalar(new QName("name", "http://openfast.org"), Type.UNICODE, Operator.COPY, new StringValue("abcd"), false);
        scalar.setKey(new QName("items", "http://www.openfast.org"));
        scalar.setDictionary(Dictionary.TEMPLATE);
        scalar.setId("5");
        String expected = 
            "<string name=\"name\" ns=\"http://openfast.org\" id=\"5\" charset=\"unicode\">" + NL +
            "    <copy dictionary=\"template\" key=\"items\" ns=\"http://www.openfast.org\" value=\"abcd\"/>" + NL +
            "</string>" + NL;
        serializer.serialize(writer, scalar, XMLMessageTemplateSerializer.createInitialContext());
        assertEquals(expected, byteOut.toString());
    }
    
    public void IGNOREtestSerializeByteVector() {
        // TODO - turn this test on when support for foreign elements is added
        Scalar scalar = new Scalar(new QName("bytes", "http://openfast.org"), Type.BYTE_VECTOR, Operator.TAIL, ScalarValue.UNDEFINED, true);
        scalar.setKey(new QName("items", "http://www.openfast.org"));
        scalar.setDictionary(Dictionary.TEMPLATE);
        scalar.setId("345");
//        BasicNode node = new BasicNode("length");
//        node.addAttribute("name", "bytesLength");
//        node.addAttribute("namespace", "http://www.openfast.org");
//        node.addAttribute("id", "344");
//        scalar.addNode(node);
        String expected = 
            "<byteVector name=\"bytes\" ns=\"http://openfast.org\" id=\"345\" presence=\"optional\">" + NL +
            "    <length name=\"bytesLength\" namespace=\"http://www.openfast.org\" id=\"344\"/>" + NL +
            "    <tail dictionary=\"template\" key=\"items\" ns=\"http://www.openfast.org\"/>" + NL +
            "</byteVector>" + NL;
        serializer.serialize(writer, scalar, XMLMessageTemplateSerializer.createInitialContext());
        assertEquals(expected, byteOut.toString());
    }
}
