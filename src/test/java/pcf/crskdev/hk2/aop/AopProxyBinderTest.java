package pcf.crskdev.hk2.aop;

import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import pcf.crskdev.hk2.aop.util.AdviceCalledInspector;
import pcf.crskdev.hk2.aop.util.MockDescriptors;
import pcf.crskdev.hk2.aop.util.ServiceLocatorAopProxyBinderSetup;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public final class AopProxyBinderTest {

    @Test
    public void shouldInjectAopProxy() {

        var setup = new ServiceLocatorAopProxyBinderSetup(
            DummyAspect.class,
            MockDescriptors.create(Dummy.class)
        );
        ServiceLocator serviceLocator = setup.getServiceLocator();
        var inspector = setup.getInspector();

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

    static class DummyAspect implements Aspect {

        @Inject
        private AdviceCalledInspector inspector;

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