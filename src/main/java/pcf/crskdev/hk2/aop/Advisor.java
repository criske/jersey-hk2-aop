package pcf.crskdev.hk2.aop;

public interface Advisor {

    default void before(JoinPoint joinPoint) {
    }

    default void after(JoinPoint joinPoint) {
    }

    default Object around(ProceedingJointPoint jointPoint) {
        return jointPoint.selfProceed();
    }

}
