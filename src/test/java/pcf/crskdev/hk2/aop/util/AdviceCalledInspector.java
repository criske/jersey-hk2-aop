package pcf.crskdev.hk2.aop.util;

import pcf.crskdev.hk2.aop.AdviceType;

public interface AdviceCalledInspector {
    void called(AdviceType adviceType);
}
