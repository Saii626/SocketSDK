package app.saikat.SocketSDK.TestQueues;

import java.util.stream.Collectors;

import javax.inject.Singleton;

import app.saikat.Annotations.DIManagement.Generator;
import app.saikat.Annotations.DIManagement.Provides;
import app.saikat.DIManagement.Impl.Repository.Repository;
import app.saikat.SocketSDK.IO.MessageHandlerBeanManager;
import app.saikat.SocketSDK.IO.MessageQueue;

@Singleton
public class MessageQueueCreator {
	
	@Provides
	@Q1
	private static MessageQueue getQ1Queue(Repository repository, Generator<MessageQueue> queueGenerator) {
		return queueGenerator.generate(repository.getBeanManagerOfType(MessageHandlerBeanManager.class)
				.getHandlers()
				.parallelStream()
				.filter(h -> h.getQualifiedMessageQueues().contains(Q1.class))
				.collect(Collectors.toSet()));
	}

	@Provides
	@Q2
	private static MessageQueue getQ2Queue(Repository repository, Generator<MessageQueue> queueGenerator) {
		return queueGenerator.generate(repository.getBeanManagerOfType(MessageHandlerBeanManager.class)
				.getHandlers()
				.parallelStream()
				.filter(h -> h.getQualifiedMessageQueues().contains(Q2.class))
				.collect(Collectors.toSet()));
	}
}