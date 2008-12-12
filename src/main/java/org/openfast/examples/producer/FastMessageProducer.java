package org.openfast.examples.producer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.openfast.Context;
import org.openfast.Global;
import org.openfast.Message;
import org.openfast.MessageOutputStream;
import org.openfast.error.ErrorHandler;
import org.openfast.session.Connection;
import org.openfast.session.ConnectionListener;
import org.openfast.session.Endpoint;
import org.openfast.session.FastConnectionException;
import org.openfast.template.TemplateRegistry;
import org.openfast.template.loader.XMLMessageTemplateLoader;

public class FastMessageProducer implements ConnectionListener {
    private final Endpoint endpoint;
    private final TemplateRegistry templateRegistry;
    private Thread acceptThread;
    private List connections = new ArrayList();

    public FastMessageProducer(Endpoint endpoint, File templatesFile) {
        Global.setErrorHandler(ErrorHandler.NULL);
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

    public void encode(File xmlDataFile) throws FastConnectionException, IOException {
        XmlCompressedMessageConverter converter = new XmlCompressedMessageConverter();
        converter.setTemplateRegistry(templateRegistry);
        List messages = converter.parse(new FileInputStream(xmlDataFile));
        while (true) {
            for (int i=0; i<messages.size(); i++) {
                Message message = (Message) messages.get(i);
                for (int j=0; j<connections.size(); j++) {
                    MessageOutputStream out = (MessageOutputStream) connections.get(j);
                    out.writeMessage(message);
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }
    }

    public void start() {
        System.out.println("Listening on " + endpoint);
        if (acceptThread != null) return;
        endpoint.setConnectionListener(this);
        acceptThread = new Thread("Producer Accept Thread") {
            public void run() {
                try {
                    endpoint.accept();
                } catch (FastConnectionException e) {
                    System.out.println("Error occurred while listening for connections: " + e.getMessage());
                }
            }
        };
        acceptThread.start();
    }

    public void stop() {
        endpoint.close();
    }

    public void onConnect(Connection connection) {
        synchronized(connections) {
            Context context = new Context();
            context.setErrorHandler(ErrorHandler.NULL);
            context.setTemplateRegistry(templateRegistry);
            try {
                MessageOutputStream out = new MessageOutputStream(connection.getOutputStream(), context);
                connections.add(out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

