package app.saikat.SocketSDK.IO;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.Lists;

import app.saikat.PojoCollections.CommonObjects.Tuple;
import app.saikat.SocketSDK.CommonFiles.MessageHeader;
import app.saikat.SocketSDK.Exceptions.WrongHandlerMethodException;
import app.saikat.SocketSDK.GenricServerClient.Client;
import app.saikat.SocketSDK.GenricServerClient.Server;
import app.saikat.SocketSDK.GenricServerClient.interfaces.Sender;

public class MessageHandlers {

	private Map<Class<?>, List<Tuple<Object, Method>>> handlers;
	private Map<Class<?>, List<Tuple<Object, Method>>> unmodifiableHandlers;

	public MessageHandlers() {
		handlers = new ConcurrentHashMap<>();
		unmodifiableHandlers = Collections.unmodifiableMap(handlers);
	}

	/**
	 * Adds a message handler
	 * @param method method to invoke when a message is received.Method can have optional parameters
	 *			  'UUID', 'MessageHeader' and any one of 'Server', 'Client' or 'Sender'. Other than these,
	 *			   only one more parameter must be added. When a message of the mandatory type parameter is
	 *			   received, this method is invoked
	 * @param parentObj object on which method will be invoked. Can be null if the method is static
	 * @throws WrongHandlerMethodException if the method has incorrect parameters
	 */
	public void addHandler(Method method, Object parentObj) throws WrongHandlerMethodException {
		handlers.compute(getHandlerClass(method), (k, v) -> {
			List<Tuple<Object, Method>> list = v;
			if (list == null) {
				list = new ArrayList<>();
			}

			list.add(Tuple.of(parentObj, method));
			return list;
		});
	}

	Map<Class<?>, List<Tuple<Object, Method>>> getHandlers() {
		return unmodifiableHandlers;
	}

	private Class<?> getHandlerClass(Method method) throws WrongHandlerMethodException {
		List<Class<?>> parameters = Lists.newArrayList(method.getParameterTypes());
		parameters.removeAll(Lists.newArrayList(UUID.class, MessageHeader.class, Server.class, Client.class, Sender.class));

		if (parameters.size() != 1) {
			throw new WrongHandlerMethodException(method);
		}
		return parameters.get(0);
	}
}