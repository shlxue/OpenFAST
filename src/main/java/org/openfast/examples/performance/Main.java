package org.openfast.examples.performance;

import java.io.File;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.openfast.examples.Assert;
import org.openfast.examples.OpenFastExample;

public class Main extends OpenFastExample {
    private static Options options = new Options();
    
    static {
        options.addOption("?", "help", false, "Displays this message.");
        options.addOption("n", "ns", false, "Enables namespace awareness");
        options.addOption("t", "template", true, "Message Template definition file");
        options.addOption("d", "data", true, "FAST Encoded data");
        options.addOption("p", "preload", false, "Preload data into memory instead of decoding directly from file");
        options.addOption("e", "error", false, "Show stacktrace information");
        options.addOption("r", "repeat", true, "Re process data file X number of times");
        options.addOption("f", "format", true, "Data format [hex|binary] default is binary ");
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        CommandLine cl = parseCommandLine(args, options);
        if (cl.hasOption("help")) {
            displayHelp(options);
        }
        File templatesFile = new File(getString(cl, "template"));
        File dataFile = new File(getString(cl, "data"));
        try {
            PerformanceRunner performanceRunner = new PerformanceRunner(templatesFile, dataFile);
            if (cl.hasOption("ns"))
                performanceRunner.setNamespaceAwareness(true);
            if (cl.hasOption("error"))
                performanceRunner.setShowStacktrace(true);
            if (cl.hasOption("preload"))
                performanceRunner.setPreloadData(true);
            if (cl.hasOption("format"))
                performanceRunner.setFormat(cl.getOptionValue("format"));
            PerformanceResult result = performanceRunner.run();
    
            System.out.println("Decoded " + result.getMessageCount() + " messages in " + result.getTime() + " milliseconds.");
            System.out.println("Average decode time per message: " + ((result.getTime() * 1000) / (result.getMessageCount())) + " microseconds");
        } catch (AssertionError ae) {
            System.out.println(ae.getMessage());
        } catch (Exception e) {
            if (cl.hasOption("error"))
                e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    private static String getString(CommandLine cl, String option) {
        Assert.assertTrue(cl.hasOption(option), "The required option \"" + option + "\" was not specified.");
        return cl.getOptionValue(option);
    }
}
