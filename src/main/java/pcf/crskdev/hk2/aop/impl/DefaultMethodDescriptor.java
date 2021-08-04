package pcf.crskdev.hk2.aop.impl;

import pcf.crskdev.hk2.aop.MethodDescriptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;

public final class DefaultMethodDescriptor implements MethodDescriptor {

    private final Method method;

    public DefaultMethodDescriptor(Method method) {
        this.method = method;
    }

    @Override
    public TypeVariable<?>[] getTypeParameters() {
        return this.method.getTypeParameters();
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return this.method.getAnnotation(annotationClass);
    }

    @Override
    public Annotation[] getAnnotations() {
        return this.method.getAnnotations();
    }

    @Override
    public Annotation[] getDeclaredAnnotations() {
        return this.method.getDeclaredAnnotations();
    }

    @Override
    public Class<?> getDeclaringClass() {
        return this.method.getDeclaringClass();
    }

    @Override
    public String getName() {
        return this.method.getName();
    }

    @Override
    public int getModifiers() {
        return this.method.getModifiers();
    }

    @Override
    public boolean isSynthetic() {
        return this.method.isSynthetic();
    }

    @Override
    public Method getMethod() {
        return this.method;
    }
}
