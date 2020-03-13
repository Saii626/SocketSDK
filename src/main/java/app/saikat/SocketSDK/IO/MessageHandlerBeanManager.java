package app.saikat.SocketSDK.IO;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Provider;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.TypeToken;

import app.saikat.Annotations.DIManagement.Scan;
import app.saikat.Annotations.SocketSDK.MessageHandler;
import app.saikat.DIManagement.Exceptions.NotValidBean;
import app.saikat.DIManagement.Impl.BeanManagers.InjectBeanManager;
import app.saikat.DIManagement.Impl.BeanManagers.PostConstructBeanManager;
import app.saikat.DIManagement.Impl.DIBeans.ConstantProviderBean;
import app.saikat.DIManagement.Impl.DIBeans.DIBeanImpl;
import app.saikat.DIManagement.Impl.Helpers.DependencyHelper;
import app.saikat.DIManagement.Interfaces.DIBean;
import app.saikat.DIManagement.Interfaces.DIBeanManager;

public class MessageHandlerBeanManager extends DIBeanManager {

	private Set<Handler<?>> handlers = new HashSet<>();

	public Set<Handler<?>> getHandlers() {
		return this.handlers;
	}

	@Override
	public Map<Class<?>, Scan> addToScan() {
		return Collections.singletonMap(MessageHandler.class, createScanObject());
	}

	@Override
	public <T> void beanCreated(DIBean<T> bean) {
		super.beanCreated(bean);

		if (!bean.getProviderType()
				.equals(TypeToken.of(Void.TYPE))) {
			throw new NotValidBean(bean, "Message handler should not return value");
		}
	}

	@Override
	public boolean shouldResolveDependency() {
		return true;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> List<DIBean<?>> resolveDependencies(DIBean<T> target, Collection<DIBean<?>> alreadyResolved,
			Collection<DIBean<?>> toBeResolved, Collection<Class<? extends Annotation>> allQualifiers) {
		logger.debug("Resolving dependency of MessageHandler: {}", target);
		List<DIBean<?>> dependencies = DependencyHelper.scanAndSetDependencies((DIBeanImpl<T>) target, allQualifiers);

		List<Class<?>> params = dependencies.stream()
				.map(dep -> dep.getProviderType()
						.getRawType())
				.collect(Collectors.toList());
		params = params.subList(1, params.size());

		String error = Handler.validateParams(params);
		if (error != null) {
			logger.error("Error validating params for MessageHandler {}", target);
			logger.error(error);
			return dependencies;
		}
		
		DIBean<?> parentBean = dependencies.get(0);
		logger.debug("Unresolved parent dependency: {}", parentBean);

		target.getDependencies()
				.clear();
		target.getDependencies()
				.add(parentBean);

		DependencyHelper.resolveAndSetDependencies((DIBeanImpl<T>) target, alreadyResolved, toBeResolved);
		parentBean = target.getDependencies().get(0);
		logger.debug("Resolved parent dependency: {} with provider: {}", parentBean, parentBean.getProvider());

		Handler<T> handler = new Handler<>(target.getDependencies()
				.get(0), (Invokable) target.getInvokable(), params);
		logger.debug("Created handler {} for {}", handler, target);
		handlers.add(handler);

		return dependencies;
	}

	@Override
	public <T> ConstantProviderBean<Provider<T>> createProviderBean(DIBean<T> target,
			InjectBeanManager injectBeanManager, PostConstructBeanManager postConstructBeanManager) {
		return null;
	}

	@Override
	public void dependencyResolved() {
		// Make handlers immutable
		handlers = ImmutableSet.copyOf(handlers);
	}

}