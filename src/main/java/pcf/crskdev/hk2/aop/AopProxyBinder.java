package pcf.crskdev.hk2.aop;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.IterableProvider;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.hk2.utilities.binding.ScopedBindingBuilder;
import org.glassfish.hk2.utilities.binding.ServiceBindingBuilder;
import pcf.crskdev.hk2.aop.impl.AopInvocationHandler;

import javax.inject.Inject;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.List;

public final class AopProxyBinder extends AbstractBinder {

    private final ServiceLocator serviceLocator;

    private final IterableProvider<Aspect> aspects;

    @Inject
    public AopProxyBinder(ServiceLocator serviceLocator,
                          IterableProvider<Aspect> aspects) {
        this.serviceLocator = serviceLocator;
        this.aspects = aspects;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    protected void configure() {

        for (Aspect aspect : aspects) {

            List<ActiveDescriptor<?>> descriptors =
                serviceLocator.getDescriptors(aspect.typeFilter());

            for (ActiveDescriptor<?> descriptor : descriptors) {

                ActiveDescriptor<?> reifiedDescriptor =
                    this.serviceLocator.reifyDescriptor(descriptor);
                Class<?> componentClass = reifiedDescriptor
                    .getImplementationClass();

                String componentName = reifiedDescriptor.getName();
                String targetName = "target." + componentName;
                Class<?>[] interfaces = componentClass.getInterfaces();
                if (interfaces.length == 0) {
                    continue;
                }

                Object proxy = Proxy.newProxyInstance(
                    componentClass.getClassLoader(),
                    interfaces,
                    new AopInvocationHandler(
                        this.serviceLocator,
                        targetName,
                        interfaces[0],
                        aspect,
                        Collections.unmodifiableMap(aspect.methodFilters())
                    )
                );

                ServiceLocatorUtilities.removeOneDescriptor(
                    this.serviceLocator,
                    descriptor
                );

                ScopedBindingBuilder<Object> scopedBindingBuilder =
                    super.bind(proxy);
                for (Class interfaze : interfaces) {
                    scopedBindingBuilder.to(interfaze);
                }
                if (componentName != null) {
                    scopedBindingBuilder.named(componentName);
                }

                ServiceBindingBuilder<?> serviceBindingBuilder =
                    super.bind(reifiedDescriptor.getImplementationClass());
                for (Class interfaze : interfaces) {
                    serviceBindingBuilder
                        .to(interfaze)
                        .in(reifiedDescriptor.getScopeAnnotation())
                        .named(targetName);
                }
            }
        }
    }
}
