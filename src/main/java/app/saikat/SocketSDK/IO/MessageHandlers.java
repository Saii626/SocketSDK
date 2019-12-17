package app.saikat.SocketSDK.IO;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Provider;
import javax.inject.Singleton;

import com.google.common.collect.Lists;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import app.saikat.Annotations.SocketSDK.MessageHandler;
import app.saikat.DIManagement.Interfaces.DIBean;
import app.saikat.DIManagement.Interfaces.DIManager;
import app.saikat.PojoCollections.CommonObjects.Tuple;
import app.saikat.SocketSDK.CommonFiles.MessageHeader;
import app.saikat.SocketSDK.Exceptions.WrongHandlerMethodException;
import app.saikat.SocketSDK.GenricServerClient.Client;
import app.saikat.SocketSDK.GenricServerClient.Server;
import app.saikat.SocketSDK.GenricServerClient.interfaces.Sender;

@Singleton
public class MessageHandlers {

	private Map<Class<?>, List<Tuple<Provider<?>, Method>>> handlers;
	private Logger logger = LogManager.getLogger(MessageHandlers.class);

	public MessageHandlers(DIManager manager) {
		handlers = new ConcurrentHashMap<>();

		Set<DIBean<?>> messageHandlerBeans = manager.getBeansAnnotatedWith(MessageHandler.class);

		try {
			for (DIBean<?> diBean : messageHandlerBeans) {
				Method m = diBean.get().getRight().get();

				if (Modifier.isStatic(m.getModifiers())) {
					addHandler(m, null);
				} else {
					Class<?> parentCls = m.getDeclaringClass();
					Provider<?> parentProvider = manager.getBeanOfType(parentCls).getProvider();
					addHandler(m, parentProvider);
				}
			}
		} catch (WrongHandlerMethodException e) {
			logger.error(e);
		}

		handlers = Collections.unmodifiableMap(handlers);
	}

	/**
	 * Adds a message handler
	 * @param method method to invoke when a message is received.Method can have optional parameters
	 *			  'UUID', 'MessageHeader' and any one of 'Server', 'Client' or 'Sender'. Other than these,
	 *			   only one more parameter must be added. When a message of the mandatory type parameter is
	 *			   received, this method is invoked
	 * @param parentObjProvider provider of object on which method will be invoked. Can be null if the method is static
	 * @throws WrongHandlerMethodException if the method has incorrect parameters
	 */
	private void addHandler(Method method, Provider<?> parentObjProvider) throws WrongHandlerMethodException {
		handlers.compute(getHandlerClass(method), (k, v) -> {
			List<Tuple<Provider<?>, Method>> list = v;
			if (list == null) {
				list = new ArrayList<>();
			}

			list.add(Tuple.of(parentObjProvider, method));
			return list;
		});
	}

	Map<Class<?>, List<Tuple<Provider<?>, Method>>> getHandlers() {
		return handlers;
	}

	private Class<?> getHandlerClass(Method method) throws WrongHandlerMethodException {
		List<Class<?>> parameters = Lists.newArrayList(method.getParameterTypes());
		parameters.removeAll(
				Lists.newArrayList(UUID.class, MessageHeader.class, Server.class, Client.class, Sender.class));

		if (parameters.size() != 1) {
			throw new WrongHandlerMethodException(method);
		}
		return parameters.get(0);
	}
}