package org.openfast.template.serializer;

import java.io.OutputStream;
import org.openfast.template.MessageTemplate;
import org.openfast.util.XmlWriter;

public class XMLMessageTemplateSerializer implements MessageTemplateSerializer {
    private SerializingContext initialContext;
    public XMLMessageTemplateSerializer() {
        this.initialContext = createInitialContext();
    }
    public static SerializingContext createInitialContext() {
        SerializerRegistry registry = new SerializerRegistry();
        registry.addFieldSerializer(new ScalarSerializer());
        registry.addFieldSerializer(new DynamicTemplateReferenceSerializer());
        registry.addFieldSerializer(new StaticTemplateReferenceSerializer());
        registry.addFieldSerializer(new ComposedDecimalSerializer());
        registry.addFieldSerializer(new GroupSerializer());
        registry.addFieldSerializer(new SequenceSerializer());
        registry.addFieldSerializer(new TemplateSerializer());
        SerializingContext context = SerializingContext.createInitialContext(registry);
        return context;
    }
    public void serialize(MessageTemplate[] templates, OutputStream destination) {
        XmlWriter writer = new XmlWriter(destination);
        writer.start("templates");
        SerializingContext context = new SerializingContext(initialContext);
        for (int i=0; i<templates.length; i++) {
            context.serialize(writer, templates[i]);
        }
        writer.end();
    }
}
