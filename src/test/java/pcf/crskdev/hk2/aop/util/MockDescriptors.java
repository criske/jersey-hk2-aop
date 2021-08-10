package pcf.crskdev.hk2.aop.util;

import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.mockito.Mockito;

public final class MockDescriptors {

    private MockDescriptors() {
        throw new IllegalStateException();
    }

    public static Descriptor create(Class<?> clazz) {
        return BuilderHelper.createConstantDescriptor(
            Mockito.mock(clazz),
            null
            , clazz
        );
    }

}
