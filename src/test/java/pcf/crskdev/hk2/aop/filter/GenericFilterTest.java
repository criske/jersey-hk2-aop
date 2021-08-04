package pcf.crskdev.hk2.aop.filter;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

public final class GenericFilterTest {

    @Test
    public void shouldComposeFilters() {
        GenericFilter<String> filter = (d) -> true;
        MatcherAssert.assertThat(
            filter
                .and((d) -> false)
                .or(d -> true)
                .not()
                .matches(""),
            Matchers.is(false)
        );
    }

}