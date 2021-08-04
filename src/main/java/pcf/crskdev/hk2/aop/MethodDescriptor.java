package pcf.crskdev.hk2.aop;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

public interface MethodDescriptor extends AnnotatedElement,
    GenericDeclaration, Member {

    Method getMethod();
}
