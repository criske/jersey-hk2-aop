package pcf.crskdev.hk2.aop.util;

import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.IterableProvider;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.internal.ActiveDescriptorBuilderImpl;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.mockito.Mockito;
import pcf.crskdev.hk2.aop.AopProxyBinder;
import pcf.crskdev.hk2.aop.AopProxyBinderTest;
import pcf.crskdev.hk2.aop.Aspect;

import javax.inject.Singleton;
import java.util.List;

public final class ServiceLocatorAopProxyBinderSetup {

    private final ServiceLocator serviceLocator;

    private final AdviceCalledInspector inspector;

    @SuppressWarnings("unchecked")
    public ServiceLocatorAopProxyBinderSetup(Class<? extends Aspect> aspect,
                                             Descriptor... targetDescriptors) {
        serviceLocator =
            ServiceLocatorUtilities.createAndPopulateServiceLocator();
        inspector = Mockito.mock(AdviceCalledInspector.class);

        //inject aspect inspector
        ServiceLocatorUtilities.addOneConstant(
            serviceLocator,
            inspector,
            null,
            AdviceCalledInspector.class
        );


        //inject the aspect
        ServiceLocatorUtilities.addOneDescriptor(
            serviceLocator,
            new ActiveDescriptorBuilderImpl(aspect)
                .in(Singleton.class)
                .asType(aspect)
                .to(Aspect.class)
                .build()
        );

        //inject target classes that will be intercepted by aspect.
        for (Descriptor descriptor : targetDescriptors) {
            ServiceLocatorUtilities.addOneDescriptor(serviceLocator, descriptor);
        }

        //inject aop proxy binder
        IterableProvider<Aspect> aspects = Mockito.mock(IterableProvider.class);
        Mockito.when(aspects.iterator()).thenReturn(
            List.of(
                serviceLocator.getService(Aspect.class)
            ).iterator()
        );
        ServiceLocatorUtilities.bind(
            serviceLocator,
            new AopProxyBinder(serviceLocator, aspects)
        );

    }

    public ServiceLocator getServiceLocator() {
        return serviceLocator;
    }

    public AdviceCalledInspector getInspector() {
        return inspector;
    }
}
