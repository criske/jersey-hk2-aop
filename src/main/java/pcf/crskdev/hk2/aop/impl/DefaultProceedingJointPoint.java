package pcf.crskdev.hk2.aop.impl;

import pcf.crskdev.hk2.aop.MethodDescriptor;
import pcf.crskdev.hk2.aop.ProceedingJointPoint;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public final class DefaultProceedingJointPoint implements ProceedingJointPoint {

    private final Method method;

    private final Object target;

    private final Object proxy;

    private final MethodDescriptor descriptor;

    private final Object[] arguments;

    public DefaultProceedingJointPoint(
        Method method, Object target,
        Object proxy, Object[] arguments
    ) {
        this.method = method;
        this.target = target;
        this.proxy = proxy;
        this.descriptor = new DefaultMethodDescriptor(method);
        this.arguments = arguments != null
            ? Arrays.copyOf(arguments, arguments.length)
            : new Object[0];
    }


    @Override
    public MethodDescriptor getDescriptor() {
        return descriptor;
    }

    @Override
    public Object getProxy() {
        return this.proxy;
    }

    @Override
    public Object getTarget() {
        return this.target;
    }

    @Override
    public Object[] getArguments() {
        return Arrays.copyOf(arguments, arguments.length);
    }

    @Override
    public Object proceed(Object[] arguments) {
        try {
            return this.method.invoke(this.target, arguments);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
