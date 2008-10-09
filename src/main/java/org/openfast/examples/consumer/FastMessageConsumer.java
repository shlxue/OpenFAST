package org.openfast.examples.consumer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.openfast.Context;
import org.openfast.Message;
import org.openfast.codec.FastDecoder;
import org.openfast.session.Connection;
import org.openfast.session.Endpoint;
import org.openfast.session.FastConnectionException;
import org.openfast.template.TemplateRegistry;
import org.openfast.template.loader.XMLMessageTemplateLoader;

public class FastMessageConsumer {
    private final Endpoint endpoint;
    private final TemplateRegistry templateRegistry;

    public FastMessageConsumer(Endpoint endpoint, File templatesFile) {
        this.endpoint = endpoint;
        XMLMessageTemplateLoader loader = new XMLMessageTemplateLoader();
        loader.setLoadTemplateIdFromAuxId(true);
        try {
            loader.load(new FileInputStream(templatesFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        this.templateRegistry = loader.getTemplateRegistry();
    }

    public void start() throws FastConnectionException, IOException {
        final Connection connection = endpoint.connect();
        Context context = new Context();
        context.setTemplateRegistry(templateRegistry);
        FastDecoder decoder = new FastDecoder(context, connection.getInputStream());
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                connection.close();
            }
        });
        while (true) {
            Message message = decoder.readMessage();
            System.out.println(message.toString());
        }
    }
}
