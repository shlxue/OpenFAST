package org.openfast.session;

import org.openfast.Message;
import org.openfast.template.Field;
import org.openfast.template.MessageTemplate;

abstract class AbstractSessionControlProtocol implements SessionProtocol {
    static final int FAST_RESET_TEMPLATE_ID = 120;
    
    static final MessageTemplate FAST_RESET_TEMPLATE = new MessageTemplate("Reset",
            new Field[] {  
    		});
    
    static final Message RESET = new Message(FAST_RESET_TEMPLATE) {
    	private static final long serialVersionUID = 1L;
    };

    public Message getResetMessage() {
    	return RESET;
    }
}
