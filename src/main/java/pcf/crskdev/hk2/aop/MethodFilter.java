package pcf.crskdev.hk2.aop;

import pcf.crskdev.hk2.aop.filter.GenericFilter;

public interface MethodFilter extends GenericFilter<MethodDescriptor> {

    boolean matches(MethodDescriptor descriptor);

    MethodFilter ANY = GenericFilter
        .uncomposable((MethodFilter) descriptor -> true)
        .as(MethodFilter.class);

}
