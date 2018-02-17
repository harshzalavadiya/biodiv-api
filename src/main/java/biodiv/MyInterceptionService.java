package biodiv;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import org.aopalliance.intercept.ConstructorInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.Filter;
import org.jvnet.hk2.annotations.Service;

import com.google.common.collect.Lists;

@Service
public class MyInterceptionService implements org.glassfish.hk2.api.InterceptionService {

	@Inject
	private Provider<ResourceInterceptor> interceptorProvider;

	//private static final ResourceInterceptor RESOURCE_INTERCEPTOR = new ResourceInterceptor();

	//private static final List<MethodInterceptor> RESOURCE_METHOD_INTERCEPTORS = Collections
	//		.<MethodInterceptor>singletonList(RESOURCE_INTERCEPTOR);

	@Override
	public Filter getDescriptorFilter() {
		return new Filter() {
			@Override
			public boolean matches(final Descriptor d) {
				final String clazz = d.getImplementation();
				return clazz.startsWith("biodiv") || clazz.startsWith("biodiv.common");
			}
		};
	}

	@Override
	public List<MethodInterceptor> getMethodInterceptors(final Method method) {
		List<MethodInterceptor> ret = Lists.newArrayList();

		if (method.isAnnotationPresent(Transactional.class)) {
			ret.add(interceptorProvider.get());
		}
		return ret;
	}

	@Override
	public List<ConstructorInterceptor> getConstructorInterceptors(Constructor<?> constructor) {
		return Lists.newArrayList();
	}

}
