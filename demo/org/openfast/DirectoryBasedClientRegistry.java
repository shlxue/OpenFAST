package org.openfast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;

import org.openfast.session.Client;
import org.openfast.template.Group;
import org.openfast.template.loader.XMLMessageTemplateLoader;

public class DirectoryBasedClientRegistry implements ClientRegistry {

	private final File directory;
	private final Map clientEntries = new HashMap();

	public DirectoryBasedClientRegistry(String directory) {
		this.directory = new File(directory);
	}

	public Map getTemplates(Client client) {
		if (!clientEntries.containsKey(client)) {
			File templateFile = getClientTemplateFile(client);
			if (templateFile == null)
				return new HashMap();
			clientEntries.put(client, new ClientEntry(templateFile));
		}
		return ((ClientEntry) clientEntries.get(client)).getTemplateMap();
	}

	private File getClientTemplateFile(Client client) {
		File[] files = directory.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".xml");
			}});
		for (int i=0; i<files.length; i++) {
			if (files[i].getName().startsWith(client.getName()))
				return files[i];
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
					Group[] templates = loader.load(new FileInputStream(templateFile));
					templateMap = new HashMap();
					for (int i=0; i<templates.length; i++) {
						templateMap.put(new Integer(i+1), templates[i]);
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
