package org.openfast.template.loader;

import java.io.InputStream;

import org.openfast.template.MessageTemplate;

public interface MessageTemplateLoader {
	MessageTemplate[] load(InputStream source);
}
