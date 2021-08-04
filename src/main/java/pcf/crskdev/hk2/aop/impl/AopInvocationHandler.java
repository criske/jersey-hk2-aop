package pcf.crskdev.hk2.aop.impl;

import org.glassfish.hk2.api.ServiceLocator;
import pcf.crskdev.hk2.aop.AdviceType;
import pcf.crskdev.hk2.aop.Advisor;
import pcf.crskdev.hk2.aop.MethodDescriptor;
import pcf.crskdev.hk2.aop.MethodFilter;
import pcf.crskdev.hk2.aop.ProceedingJointPoint;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

public final class AopInvocationHandler implements InvocationHandler {

    private final ServiceLocator serviceLocator;

    private final String targetName;

    private final Advisor advisor;

    private final Class<?> classImplOrContract;

    private final Map<AdviceType, MethodFilter> methodFilters;

    public AopInvocationHandler(
        ServiceLocator serviceLocator,
        String targetName,
        Class<?> classImplOrContract,
        Advisor advisor,
        Map<AdviceType, MethodFilter> methodFilters
    ) {
        this.serviceLocator = serviceLocator;
        this.targetName = targetName;
        this.classImplOrContract = classImplOrContract;
        this.advisor = advisor;
        this.methodFilters = methodFilters;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        Object realService = this.serviceLocator.getService(
            this.classImplOrContract,
            this.targetName
        );
        ProceedingJointPoint joinPoint = new DefaultProceedingJointPoint(
            method,
            realService,
            proxy,
            args
        );

        MethodDescriptor methodDescriptor = new DefaultMethodDescriptor(method);
        final Object returnVal;
        if (methodFilters.isEmpty()) {
            returnVal = joinPoint.selfProceed();
        } else {

            MethodFilter beforeFilter = methodFilters.get(AdviceType.BEFORE);
            if (beforeFilter != null && beforeFilter.matches(methodDescriptor)) {
                this.advisor.before(joinPoint);
            }

            MethodFilter aroundFilter = methodFilters.get(AdviceType.AROUND);
            if (aroundFilter != null && aroundFilter.matches(methodDescriptor)) {
                returnVal = this.advisor.around(joinPoint);
            } else {
                returnVal = joinPoint.selfProceed();
            }

            MethodFilter after = methodFilters.get(AdviceType.AFTER);
            if (after != null && after.matches(methodDescriptor)) {
                this.advisor.after(joinPoint);
            }

        }
        return returnVal;
    }
}
