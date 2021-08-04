package pcf.crskdev.hk2.aop;

import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.IterableProvider;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.internal.ActiveDescriptorBuilderImpl;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

public final class AopProxyBinderTest {

    @Test
    public void shouldInjectAopProxy() {
        DummyInspector inspector = Mockito.mock(DummyInspector.class);
        ServiceLocator serviceLocator =
            ServiceLocatorUtilities.createAndPopulateServiceLocator();
        ServiceLocatorUtilities.addOneConstant(
            serviceLocator,
            inspector,
            null,
            DummyInspector.class
        );
        ServiceLocatorUtilities.addOneConstant(
            serviceLocator,
            Mockito.mock(Dummy.class),
            null,
            Dummy.class
        );
        ServiceLocatorUtilities.addOneDescriptor(
            serviceLocator,
            new ActiveDescriptorBuilderImpl(DummyAspect.class)
                .in(Singleton.class)
                .asType(DummyAspect.class)
                .to(Aspect.class)
                .build()
        );

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

        Dummy dummy = serviceLocator.getService(Dummy.class);

        dummy.hello();

        ArgumentCaptor<AdviceType> argCaptor =
            ArgumentCaptor.forClass(AdviceType.class);
        Mockito.verify(inspector, Mockito.times(3)).called(argCaptor.capture());
        MatcherAssert.assertThat(
            argCaptor.getAllValues().stream().sorted().collect(Collectors.toList()),
            Matchers.is(
                List.of(
                    AdviceType.AFTER,
                    AdviceType.AROUND,
                    AdviceType.BEFORE
                )
            )
        );

    }


    public interface Dummy {
        void hello();
    }

    interface DummyInspector {
        void called(AdviceType adviceType);
    }

    static class DummyAspect implements Aspect {

        @Inject
        private DummyInspector inspector;

        @Override
        public Filter typeFilter() {
            return BuilderHelper.createContractFilter(Dummy.class.getName());
        }

        @Override
        public void before(JoinPoint joinPoint) {
            inspector.called(AdviceType.BEFORE);
        }

        @Override
        public void after(JoinPoint joinPoint) {
            inspector.called(AdviceType.AFTER);
        }

        @Override
        public Object around(ProceedingJointPoint jointPoint) {
            inspector.called(AdviceType.AROUND);
            return jointPoint.selfProceed();
        }
    }

}