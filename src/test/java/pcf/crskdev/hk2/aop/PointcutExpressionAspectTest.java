package pcf.crskdev.hk2.aop;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pcf.crskdev.hk2.aop.util.AdviceCalledInspector;
import pcf.crskdev.hk2.aop.util.MockDescriptors;
import pcf.crskdev.hk2.aop.util.ServiceLocatorAopProxyBinderSetup;

import javax.inject.Inject;
import java.util.Set;

class PointcutExpressionAspectTest {

    @Test
    public void shouldInterceptUsingAspectJPointcutExpression() {
        var setup = new ServiceLocatorAopProxyBinderSetup(
            MyPointExpressionAspect.class,
            MockDescriptors.create(Dummy.class)
        );

        var dummy = setup.getServiceLocator().getService(Dummy.class);
        var inspector = setup.getInspector();

        dummy.hello();
        dummy.bye();

        Mockito.verify(
            inspector,
            Mockito.times(2)
        ).called(Mockito.any());

    }

    public interface Dummy {

        void hello();

        void bye();

    }

    public static class MyPointExpressionAspect extends PointcutExpressionAspect {

        @Inject
        private AdviceCalledInspector inspector;

        @Override
        public String expression() {
            return "execution(* pcf.crskdev.hk2.aop.PointcutExpressionAspectTest.Dummy+" +
                ".*(..))";
        }

        @Override
        public Set<AdviceType> adviceTypes() {
            return Set.of(AdviceType.BEFORE);
        }

        @Override
        public void before(JoinPoint joinPoint) {
            inspector.called(AdviceType.BEFORE);
        }
    }

}