package pcf.crskdev.hk2.aop;

public interface ProceedingJointPoint extends JoinPoint {

    Object proceed(Object[] arguments);

    default Object selfProceed() {
        return this.proceed(this.getArguments());
    }
}
