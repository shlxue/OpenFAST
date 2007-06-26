package org.openfast.session;

import org.openfast.Message;
import org.openfast.error.ErrorCode;
import org.openfast.error.ErrorHandler;

public abstract class FastConnectionFactory {
	private ErrorHandler errorHandler = ErrorHandler.DEFAULT;
	
	public Session connect(String clientName) throws FastConnectionException {
		Session session = connectInternal();
		Message message = Session.createHelloMessage(clientName);
		session.out.writeMessage(message);
		Message hello = session.in.readMessage();
		if (hello.getTemplateId() == Session.FAST_ALERT_TEMPLATE_ID)
			errorHandler.error(ErrorCode.getAlertCode(hello), "Unable to connect.");
		if (hello.getTemplateId() != Session.FAST_HELLO_TEMPLATE_ID)
			throw new FastConnectionException("Unable to connect: Message with template id " + hello.getTemplateId() + " received, expected hello message (id=" + Session.FAST_HELLO_TEMPLATE_ID + ")");
		Client client = getClient(session, hello.getString(1));
		session.setClient(client);
		return session;
	}
	
	protected abstract Client getClient(Session session, String clientName);
	protected abstract Session connectInternal() throws FastConnectionException;
	
	public void setErrorHandler(ErrorHandler errorHandler) {
		if (errorHandler == null) throw new NullPointerException();
		this.errorHandler = errorHandler;
	}
}
