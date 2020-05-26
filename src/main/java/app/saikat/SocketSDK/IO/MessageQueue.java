package app.saikat.SocketSDK.IO;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import app.saikat.Annotations.DIManagement.GenParam;
import app.saikat.Annotations.DIManagement.Generate;
import app.saikat.Annotations.DIManagement.Generator;
import app.saikat.Annotations.DIManagement.Provides;
import app.saikat.Annotations.SocketSDK.DefaultQueue;
import app.saikat.DIManagement.Impl.Repository.Repository;
import app.saikat.PojoCollections.CommonObjects.Either;
import app.saikat.PojoCollections.CommonObjects.Tuple;
import app.saikat.SocketSDK.CommonFiles.Message;
import app.saikat.SocketSDK.GenricServerClient.Client;
import app.saikat.SocketSDK.GenricServerClient.Server;
import app.saikat.ThreadManagement.impl.CustomThreadFactory;
import app.saikat.ThreadManagement.interfaces.ThreadPoolManager;

public class MessageQueue {

	private static CustomThreadFactory threadFactory = new CustomThreadFactory("queue");

	// Main event-loop thread
	private Thread eventLoopThread;

	private final List<Tuple<Either<Server, Client>, Message>> messageQueue;

	private final Set<Handler<?>> handlers;
	private ThreadPoolManager threadPoolManager;

	private Logger logger = LogManager.getLogger(this.getClass());

	@Generate
	public MessageQueue(@GenParam Set<Handler<?>> handlers, ThreadPoolManager threadPoolManager) {
		messageQueue = Collections.synchronizedList(new LinkedList<>());
		this.handlers = handlers;

		logger.debug("Handlers: {}", handlers);

		this.threadPoolManager = threadPoolManager;

		eventLoopThread = threadFactory.newThread(this::process);
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
			Set<Handler<?>> handlersToExecute = handlers.parallelStream()
					.filter(h -> h.getHandlerType()
							.equals(objectClass)
							&& h.handlesSenderType(toProcess.first.apply(s -> Server.class, c -> Client.class)))
					.collect(Collectors.toSet());

			if (handlersToExecute == null || handlersToExecute.isEmpty()) {
				logger.warn("No handler found for handling websocket message of type {}", objectClass.getName());
				return;
			}

			for (Handler<?> handler : handlersToExecute) {
				threadPoolManager.execute(
						() -> handler.invokeMessageHandler(toProcess.second, toProcess.first.apply(s -> s, c -> c)));
			}
		}

	}

	@Provides
	@DefaultQueue
	private static MessageQueue getDefaultQueue(Repository repository, Generator<MessageQueue> queueGenerator) {
		Set<Handler<?>> handlers = repository.getBeanManagerOfType(MessageHandlerBeanManager.class)
				.getHandlers()
				.parallelStream()
				.filter(h -> h.getQualifiedMessageQueues().contains(DefaultQueue.class))
				.collect(Collectors.toSet());
		return queueGenerator.generate(handlers);
	}
}