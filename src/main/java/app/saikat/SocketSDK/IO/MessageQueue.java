package app.saikat.SocketSDK.IO;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import app.saikat.LogManagement.Logger;
import app.saikat.LogManagement.LoggerFactory;

import app.saikat.CommonLogic.Threads.ThreadPoolManager;
import app.saikat.PojoCollections.CommonObjects.Either;
import app.saikat.PojoCollections.CommonObjects.Tuple;
import app.saikat.SocketSDK.CommonFiles.MessageHeader;
import app.saikat.SocketSDK.CommonFiles.SessionData;
import app.saikat.SocketSDK.GenricServerClient.Client;
import app.saikat.SocketSDK.GenricServerClient.Server;
import app.saikat.SocketSDK.GenricServerClient.interfaces.Sender;

public class MessageQueue {
	
	// Main event-loop thread
	private Thread eventLoopThread;

	private final List<Tuple<Either<Server, Client>, Message>> messageQueue;

	private MessageHandlers messageHandlers;
	private ThreadPoolManager threadPoolManager;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public MessageQueue(MessageHandlers handlers, ThreadPoolManager threadPoolManager) {
		messageQueue = Collections.synchronizedList(new LinkedList<>());
		messageHandlers = handlers;

		this.threadPoolManager = threadPoolManager;

		eventLoopThread = new Thread(this::process);
		eventLoopThread.setName("input_thread");
		eventLoopThread.start();
	}

	public void addObjectToInputQueue(Message message, Server server) {

		synchronized (messageQueue) {
			messageQueue.add(Tuple.of(Either.left(server), message));
			messageQueue.notifyAll();
		}
	}

	public void addObjectToInputQueue(Message message, Client client) {

		synchronized (messageQueue) {
			messageQueue.add(Tuple.of(Either.right(client), message));
			messageQueue.notifyAll();
		}
	}

	private void process() {
		while (true) {

			Tuple<Either<Server, Client>, Message> toProcess;
			synchronized (messageQueue) {
				while (messageQueue.isEmpty()) {
					try {
						messageQueue.wait();
					} catch (InterruptedException e) {
						logger.error("Error: {}", e);
					}
				}

				toProcess = messageQueue.remove(0);
			}

			Class<?> objectClass = toProcess.second.second.getObject()
					.getClass();
			List<Tuple<Object, Method>> handlerList = messageHandlers.getHandlers()
					.get(objectClass);

			if (handlerList == null || handlerList.isEmpty()) {
				logger.warn("No handler found for handling websocket message of type {}", objectClass.getName());
				return;
			}

			for (Tuple<Object, Method> entry : handlerList) {
				
				threadPoolManager.execute(() -> executeMethod(entry.second, toProcess, entry.first));

					// object = DIManager.get(entry.first);

					// List<Method> declaredMethods = Lists.newArrayList(entry.first.getClass()
					//		 .getDeclaredMethods());

					// // Invoke methods in different executor threads
					// declaredMethods.stream()
					//		 .filter(m -> m.getName()
					//				 .equals(entry.second))
					//		 .filter(m -> this.filterBasedOnParameters(m, objectClass, toProcess.first))
					//		 .forEach(m -> threadPoolManager.execute(() -> executeMethod(m, toProcess, object)));
			}
		}

	}

	private void executeMethod(Method method, Tuple<Either<Server, Client>, Message> data, Object parentObject) {
		SessionData.setCurentSession(data.second.first.getSession());
		List<Class<?>> parameters = Lists.newArrayList(method.getParameterTypes());

		List<Object> params = parameters.stream()
				.map(paramCls -> {
					if (paramCls.equals(UUID.class)) {
						return data.second.first.getSession();
					} else if (paramCls.equals(MessageHeader.class)) {
						return data.second.first;
					} else if (paramCls.equals(Server.class)) {
						return data.first.getLeft()
								.get();
					} else if (paramCls.equals(Client.class)) {
						return data.first.getRight()
								.get();
					} else if (paramCls.equals(Sender.class)) {
						return data.first.apply(s -> s, c -> c);
					} else {
						return data.second.second.getObject();
					}
				})
				.collect(Collectors.toList());

		try {
			method.invoke(parentObject, params.toArray());
		} catch (Exception e) {
			logger.error("Error: {}", e);
		} finally {
			SessionData.removeSession();
		}
	}

	// private boolean filterBasedOnParameters(Method m, Class<?> objClass, Either<Server, Client> source) {
	//	 List<Class<?>> parameters = Lists.newArrayList(m.getParameterTypes());

	//	 if (parameters.contains(objClass)) {
	//		 if (parameters.contains(Server.class)) {
	//			 return source.containsLeft();
	//		 } else if (parameters.contains(Client.class)) {
	//			 return source.containsRight();
	//		 }
	//		 return true;
	//	 } else {
	//		 return false;
	//	 }
	// }
}