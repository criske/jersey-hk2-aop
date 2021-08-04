package pcf.crskdev.hk2.aop.filter;

import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.Filter;

public interface HK2Filter extends Filter, GenericFilter<Descriptor> {

    default Filter unwrap() {
        GenericFilter<Descriptor> dis = this;
        return dis::matches;
    }

    static HK2Filter generify(Filter filter) {
        return filter::matches;
    }
}
