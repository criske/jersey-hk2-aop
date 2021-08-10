package pcf.crskdev.hk2.aop;

import java.util.Set;

public enum AdviceType {
    AFTER, AROUND, BEFORE;

    public static Set<AdviceType> all() {
        return Set.of(AdviceType.values());
    }
}
