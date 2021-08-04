package pcf.crskdev.hk2.aop;

import java.util.Map;

public interface Aspect extends Pointcut, Advisor {

    @Override
    default Map<AdviceType, MethodFilter> methodFilters() {
        return Map.of(
            AdviceType.BEFORE, MethodFilter.ANY,
            AdviceType.AROUND, MethodFilter.ANY,
            AdviceType.AFTER, MethodFilter.ANY
        );
    }
}
