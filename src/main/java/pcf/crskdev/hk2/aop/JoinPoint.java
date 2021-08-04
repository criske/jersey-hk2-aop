package pcf.crskdev.hk2.aop;

public interface JoinPoint {

    MethodDescriptor getDescriptor();

    Object getProxy();

    Object getTarget();

    Object[] getArguments();

}
