package app.saikat.SocketSDK.IO;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.reflect.Invokable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import app.saikat.DIManagement.Interfaces.DIBean;
import app.saikat.SocketSDK.CommonFiles.Message;
import app.saikat.SocketSDK.CommonFiles.MessageHeader;
import app.saikat.SocketSDK.GenricServerClient.Client;
import app.saikat.SocketSDK.GenricServerClient.Server;
import app.saikat.SocketSDK.GenricServerClient.interfaces.Sender;

public class Handler<T> {

	public static enum SenderType {
		SERVER, CLIENT, SENDER, DONT_CARE
	};

	private Class<T> handlerType;
	private final BiConsumer<Message, Sender> invokeHandler;
	private final Invokable<Object, Void> invokable;
	private final SenderType senderType;
	private final List<Class<? extends Annotation>> qualifiedMessageQueues;

	private final Logger logger = LogManager.getLogger(this.getClass());

	@SuppressWarnings("unchecked")
	public Handler(DIBean<?> parentBean, Invokable<Object, Void> invokable,
			List<Class<?>> parameterList, List<Class<? extends Annotation>> qualifiedMessageQueues) {
		this.invokable = invokable;

		if (parameterList.contains(Server.class)) {
			senderType = SenderType.SERVER;
		} else if (parameterList.contains(Client.class)) {
			senderType = SenderType.CLIENT;
		} else if (parameterList.contains(Sender.class)) {
			senderType = SenderType.SENDER;
		} else {
			senderType = SenderType.DONT_CARE;
		}

		List<BiFunction<Message, Sender, Object>> paramsExtractor = new ArrayList<>();

		for (Class<?> paramCls : parameterList) {
			if (paramCls.equals(UUID.class)) {
				paramsExtractor.add((m, s) -> m.first.getSession());
			} else if (paramCls.equals(MessageHeader.class)) {
				paramsExtractor.add((m, s) -> m.first);
			} else if (paramCls.equals(Server.class) || paramCls.equals(Client.class)
					|| paramCls.equals(Sender.class)) {
				paramsExtractor.add((m, s) -> s);
			} else {
				this.handlerType = (Class<T>) paramCls;
				paramsExtractor.add((m, s) -> m.second.getObject());
			}
		}

		invokeHandler = (m, s) -> {
			List<Object> params = paramsExtractor.stream()
					.map(e -> e.apply(m, s))
					.collect(Collectors.toList());

					
					logger.debug("Handler's parent bean provider is: {}", parentBean.getProvider());
			Object receiver = parentBean != null ? parentBean.getProvider().get() : null;

			try {
				invokable.invoke(receiver, params.toArray());
			} catch (InvocationTargetException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		};

		this.qualifiedMessageQueues = qualifiedMessageQueues;
	}

	public Class<T> getHandlerType() {
		return this.handlerType;
	}

	public SenderType getSenderType() {
		return senderType;
	}

	public List<Class<? extends Annotation>> getQualifiedMessageQueues() {
		return this.qualifiedMessageQueues;
	}

	public boolean handlesSenderType(Class<?> cls) {
		if (cls.equals(Server.class)) {
			return senderType == SenderType.SERVER || senderType == SenderType.SENDER;
		} else if (cls.equals(Client.class)) {
			return senderType == SenderType.CLIENT || senderType == SenderType.SENDER;
		} else if (cls.equals(Sender.class)) {
			return senderType == SenderType.SENDER;
		} else {
			return senderType == SenderType.DONT_CARE;
		}
	}

	public boolean invokeMessageHandler(Message message, Sender sender) {
		try {
			invokeHandler.accept(message, sender);
			return true;
		} catch (RuntimeException e) {
			logger.error("Error invoking MessageHandler of method: {}", invokable.getName());
			logger.error("Error: ", e);
		}
		return false;
	}

	@Override
	public String toString() {
		return String.format("[SenderType: %s, MessageType: %s]", senderType.toString(), handlerType.getName());
	}

	public static String validateParams(List<Class<?>> params) {
		List<Class<?>> copyParams = Lists.newArrayList(params);

		if (copyParams.size() == 0)
			return "No parameters provided for handler";

		boolean hasServer = copyParams.remove(Server.class);
		boolean hasClient = copyParams.remove(Client.class);
		boolean hasSender = copyParams.remove(Sender.class);

		if (hasServer && (hasClient || hasSender))
			return "Only one of Server, Client or Sender is allowed. Found Server and "
					+ (hasClient ? "Client" : "Sender");
		if (hasClient && (hasServer || hasSender))
			return "Only one of Server, Client or Sender is allowed. Found Client and "
					+ (hasServer ? "Server" : "Sender");

		copyParams.remove(UUID.class);
		copyParams.remove(MessageHeader.class);

		if (copyParams.size() != 1)
			return "Too many Message type specified. Only 1 should be provided. Specified types: "
					+ copyParams.toString();

		return null;

	}
}