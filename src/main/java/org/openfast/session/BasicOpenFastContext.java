package org.openfast.session;

import org.openfast.OpenFastContext;
import org.openfast.error.ErrorHandler;
import org.openfast.logging.FastMessageLogger;
import org.openfast.template.MessageTemplate;
import org.openfast.template.TemplateRegistry;

public class BasicOpenFastContext implements OpenFastContext {

    private FastMessageLogger logger;

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
        return this.logger != null ? this.logger : FastMessageLogger.NULL;
    }

    @Override
    public void setLogger(FastMessageLogger logger) {
        this.logger = logger;
    }

}
