package org.openfast;

import java.util.Map;

import org.openfast.session.Client;

public interface ClientRegistry {
	Map getTemplates(Client client);
}
