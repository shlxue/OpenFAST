package org.openfast.examples.consumer;

import java.io.File;
import java.io.IOException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.openfast.examples.Assert;
import org.openfast.examples.OpenFastExample;
import org.openfast.session.Endpoint;
import org.openfast.session.FastConnectionException;
import org.openfast.session.multicast.MulticastEndpoint;
import org.openfast.session.tcp.TcpEndpoint;

public class Main extends OpenFastExample {
    private static final String MESSAGE_TEMPLATE_FILE = "template";
    private static final String PORT = "port";
    private static final String PROTOCOL = "protocol";
    private static final String HOST = "host";
    private static final String ERROR = "error";
    
    private static Options options = new Options();
    
    static {
        options.addOption("?", "help", false, "Displays this message");
        options.addOption("t", "protocol", true, "Protocol [tcp|udp] defaults to tcp");
        options.addOption("p", PORT, true, "Port to connect to");
        options.addOption("h", "host", true, "The host name of the server (or group name for multicast)");
        options.addOption("e", "error", false, "Show stacktrace information");
        options.addOption("m", MESSAGE_TEMPLATE_FILE, true, "Message template definition file");
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        CommandLine cl = parseCommandLine(args, options);
        if (cl.hasOption("help")) {
            displayHelp(options);
        }
        Assert.assertTrue(cl.hasOption(PORT), "The required parameter " + PORT + " is missing.");
        int port = getInteger(cl, PORT);
        String host = cl.hasOption(HOST) ? cl.getOptionValue(HOST) : "localhost";
        boolean showStacktrace = cl.hasOption(ERROR);
        Endpoint endpoint = null;
        
        if (cl.hasOption(PROTOCOL)) {
            if ("udp".equals(cl.getOptionValue(PROTOCOL))) {
                endpoint = new MulticastEndpoint(port, host);
            }
        }
        if (endpoint == null) {
            endpoint = new TcpEndpoint(host, port);
        }
        File templatesFile = getFile(cl, MESSAGE_TEMPLATE_FILE);
        Assert.assertTrue(templatesFile.exists(), "The template definition file \"" + templatesFile.getAbsolutePath() + "\" does not exist.");
        Assert.assertTrue(!templatesFile.isDirectory(), "The template definition file \"" + templatesFile.getAbsolutePath() + "\" is a directory.");
        Assert.assertTrue(templatesFile.canRead(), "The template definition file \"" + templatesFile.getAbsolutePath() + "\" is not readable.");
        FastMessageConsumer consumer = new FastMessageConsumer(endpoint, templatesFile);
        try {
            consumer.start();
        } catch (FastConnectionException e) {
            if (showStacktrace)
                e.printStackTrace();
            System.out.println("Unable to connect to endpoint: " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            if (showStacktrace)
                e.printStackTrace();
            System.out.println("An IO error occurred while consuming messages: " + e.getMessage());
            System.exit(1);
        }
    }
}
