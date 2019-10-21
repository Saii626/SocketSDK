package app.saikat.SocketSDK.IO.Providers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import app.saikat.DIManagement.DIManager;
import app.saikat.DIManagement.Provides;
import app.saikat.DIManagement.Exceptions.ClassNotUnderDIException;
import app.saikat.LogManagement.Logger;
import app.saikat.LogManagement.LoggerFactory;
import app.saikat.SocketSDK.Exceptions.WrongHandlerMethodException;
import app.saikat.SocketSDK.IO.MessageHandlers;

public class MessageHandlersProvider {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Provides
    public MessageHandlers getMessageHandlers(@MethodHandlers List<Method> handlers) {
        MessageHandlers messageHandlers = new MessageHandlers();

        handlers.forEach(m -> {
            Class<?> parentCls = m.getDeclaringClass();
            Class<? extends Annotation> parentClsAnnotation = DIManager.getQualifierAnnotation(parentCls);

            try {
                Object obj = DIManager.get(parentCls, parentClsAnnotation);
                messageHandlers.addHandler(m, obj);
            } catch (ClassNotUnderDIException | WrongHandlerMethodException e) {
                logger.error(e);
            }
        });
        return messageHandlers;
    }
}