package app.saikat.SocketSDK.Exceptions;

import app.saikat.DIManagement.Interfaces.DIBean;

public class WrongHandlerMethodException extends Exception {

	private static final long serialVersionUID = 1L;

	public WrongHandlerMethodException(DIBean<?> bean) {
		super(String.format("Method %s has wrong parameters as messageHandler", bean.getInvokable().getName()));
	}
	
}