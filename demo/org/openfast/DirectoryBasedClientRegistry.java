/*
The contents of this file are subject to the Mozilla Public License
Version 1.1 (the "License"); you may not use this file except in
compliance with the License. You may obtain a copy of the License at
http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS"
basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
License for the specific language governing rights and limitations
under the License.

The Original Code is OpenFAST.

The Initial Developer of the Original Code is The LaSalle Technology
Group, LLC.  Portions created by The LaSalle Technology Group, LLC
are Copyright (C) The LaSalle Technology Group, LLC. All Rights Reserved.

Contributor(s): Jacob Northey <jacob@lasalletech.com>
                Craig Otis <cotis@lasalletech.com>
*/


package org.openfast;

import org.openfast.session.Client;

import org.openfast.template.Group;
import org.openfast.template.loader.XMLMessageTemplateLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;

import java.util.HashMap;
import java.util.Map;


public class DirectoryBasedClientRegistry implements ClientRegistry {
    private final File directory;
    private final Map clientEntries = new HashMap();

    public DirectoryBasedClientRegistry(String directory) {
        this.directory = new File(directory);
    }

    public Map getTemplates(Client client) {
        if (!clientEntries.containsKey(client)) {
            File templateFile = getClientTemplateFile(client);

            if (templateFile == null) {
                return new HashMap();
            }

            clientEntries.put(client, new ClientEntry(templateFile));
        }

        return ((ClientEntry) clientEntries.get(client)).getTemplateMap();
    }

    private File getClientTemplateFile(Client client) {
        File[] files = directory.listFiles(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        return name.endsWith(".xml");
                    }
                });

        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().startsWith(client.getName())) {
                return files[i];
            }
        }

        return null;
    }

    private class ClientEntry {
        private final File templateFile;
        private long lastModified = 0L;
        private Map templateMap;

        public ClientEntry(File templateFile) {
            this.templateFile = templateFile;
        }

        public Map getTemplateMap() {
            if (templateFile.lastModified() > lastModified) {
                try {
                    XMLMessageTemplateLoader loader = new XMLMessageTemplateLoader();
                    Group[] templates = loader.load(new FileInputStream(
                                templateFile));
                    templateMap = new HashMap();

                    for (int i = 0; i < templates.length; i++) {
                        templateMap.put(new Integer(i + 1), templates[i]);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }

            return templateMap;
        }
    }
}
