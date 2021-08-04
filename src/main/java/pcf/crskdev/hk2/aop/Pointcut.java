package pcf.crskdev.hk2.aop;

import org.glassfish.hk2.api.Filter;

import java.util.Map;

public interface Pointcut {

    Filter typeFilter();

    Map<AdviceType, MethodFilter> methodFilters();
}
