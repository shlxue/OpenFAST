package org.openfast.examples.tmplexch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.openfast.Context;
import org.openfast.Message;
import org.openfast.codec.FastDecoder;
import org.openfast.template.TemplateRegistry;
import org.openfast.template.loader.XMLMessageTemplateLoader;

public class FastMessageDecoder {
    private final TemplateRegistry templateRegistry;
    private final File fastDataFile;

    public FastMessageDecoder(File fastDataFile, File templatesFile, boolean namespaceAware) {
        this.fastDataFile = fastDataFile;
        XMLMessageTemplateLoader loader = new XMLMessageTemplateLoader(namespaceAware);
        loader.setLoadTemplateIdFromAuxId(true);
        try {
            loader.load(new FileInputStream(templatesFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        this.templateRegistry = loader.getTemplateRegistry();
    }

    public void start() throws IOException {
        Context context = new Context();
        context.setTemplateRegistry(templateRegistry);
        FastDecoder decoder = new FastDecoder(context, new FileInputStream(fastDataFile));
        while (true) {
            Message message = decoder.readMessage();
            if (message == null)
                break;
            System.out.println(message.toString());
        }
    }
}
