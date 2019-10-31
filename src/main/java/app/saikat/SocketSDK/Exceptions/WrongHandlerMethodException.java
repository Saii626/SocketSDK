package app.saikat.SocketSDK.Exceptions;

import java.lang.reflect.Method;

public class WrongHandlerMethodException extends Exception {

	private static final long serialVersionUID = 1L;

	public WrongHandlerMethodException(Method method) {
		super(String.format("Method %s has wrong parameters as messageHandler", method.getName()));
	}
	
}