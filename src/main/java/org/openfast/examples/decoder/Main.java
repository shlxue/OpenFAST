package org.openfast.examples.decoder;

import java.io.File;
import java.io.IOException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.openfast.examples.Assert;
import org.openfast.examples.OpenFastExample;

public class Main extends OpenFastExample {
    private static Options options = new Options();
    
    static {
        options.addOption("?", "help", false, "Displays this message");
        options.addOption("d", FAST_DATA_FILE, true, "FAST encoded data file");
        options.addOption("e", ERROR, false, "Show stacktrace information");
        options.addOption("n", NAMESPACE_AWARENESS, false, "Enables namespace awareness");
        options.addOption("t", MESSAGE_TEMPLATE_FILE, true, "Message template definition file");
        options.addOption("v", TRACE, false, "Trace");
        options.addOption("j", READ_OFFSET, true, READ_OFFSET_DESCRIPTION);
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        CommandLine cl = parseCommandLine("consumer", args, options);
        if (cl.hasOption("help")) {
            displayHelp("consumer", options);
        }
        boolean showStacktrace = cl.hasOption(ERROR);
        File templatesFile = null;
        File dataFile = null;
        try {
            dataFile = getFile(cl, FAST_DATA_FILE);
            Assert.assertTrue(dataFile.exists(), "The fast data file \"" + dataFile.getAbsolutePath() + "\" does not exist.");
            Assert.assertTrue(!dataFile.isDirectory(), "The fast data file \"" + dataFile.getAbsolutePath() + "\" is a directory.");
            Assert.assertTrue(dataFile.canRead(), "The fast data file \"" + dataFile.getAbsolutePath() + "\" is not readable.");
            templatesFile = getFile(cl, MESSAGE_TEMPLATE_FILE);
            Assert.assertTrue(templatesFile.exists(), "The template definition file \"" + templatesFile.getAbsolutePath() + "\" does not exist.");
            Assert.assertTrue(!templatesFile.isDirectory(), "The template definition file \"" + templatesFile.getAbsolutePath() + "\" is a directory.");
            Assert.assertTrue(templatesFile.canRead(), "The template definition file \"" + templatesFile.getAbsolutePath() + "\" is not readable.");
        } catch (AssertionError e) {
            System.out.println(e.getMessage());
            displayHelp("consumer", options);
        }

        final int readOffset = cl.hasOption(READ_OFFSET) ? getInteger(cl, READ_OFFSET) : 0;
        FastMessageDecoder consumer = new FastMessageDecoder(dataFile, templatesFile, cl.hasOption(NAMESPACE_AWARENESS), readOffset);
        if (cl.hasOption(TRACE))
            consumer.setTraceEnabled();
        try {
            consumer.start();
        } catch (IOException e) {
            if (showStacktrace)
                e.printStackTrace();
            System.out.println("An IO error occurred while consuming messages: " + e.getMessage());
            System.exit(1);
        }
    }
}
