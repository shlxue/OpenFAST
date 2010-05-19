package org.openfast;

import org.openfast.error.ErrorHandler;
import org.openfast.logging.FastMessageLogger;
import org.openfast.template.MessageTemplate;
import org.openfast.template.TemplateRegistry;

public class NullOpenFastContext implements OpenFastContext {

    @Override
    public MessageTemplate getTemplate(int templateId) {
        return null;
    }

    @Override
    public int getTemplateId(MessageTemplate template) {
        return 0;
    }

    @Override
    public TemplateRegistry getTemplateRegistry() {
        return null;
    }

    @Override
    public void registerTemplate(int templateId, MessageTemplate template) {
    }

    @Override
    public void setErrorHandler(ErrorHandler errorHandler) {
    }

    @Override
    public void setTemplateRegistry(TemplateRegistry registry) {
    }

    @Override
    public FastMessageLogger getLogger() {
        return FastMessageLogger.NULL;
    }

    @Override
    public void setLogger(FastMessageLogger logger) {
    }

}
