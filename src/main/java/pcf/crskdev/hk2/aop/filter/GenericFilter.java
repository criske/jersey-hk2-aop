package pcf.crskdev.hk2.aop.filter;

public interface GenericFilter<T> {

    boolean matches(T toMatch);

    default GenericFilter<T> and(GenericFilter<T> other) {
        return new ComposableFilter<T>(this, other) {
            @Override
            public boolean compose(
                T toMatch,
                GenericFilter<T> left,
                GenericFilter<T> right
            ) {
                return left.matches(toMatch) && right.matches(toMatch);
            }
        };
    }

    default GenericFilter<T> or(GenericFilter<T> other) {
        return new ComposableFilter<T>(this, other) {
            @Override
            public boolean compose(
                T toMatch,
                GenericFilter<T> left,
                GenericFilter<T> right
            ) {
                return left.matches(toMatch) || right.matches(toMatch);
            }
        };
    }

    default GenericFilter<T> not() {
        final GenericFilter<T> dis = this;
        return toMatch -> !dis.matches(toMatch);
    }

    default <O extends GenericFilter<?>> O as(Class<O> castType) {
        return castType.cast(this);
    }

    static <T> GenericFilter<T> uncomposable(GenericFilter<T> filter) {
        return new GenericFilter<T>() {
            @Override
            public boolean matches(T toMatch) {
                return filter.matches(toMatch);
            }

            @Override
            public GenericFilter<T> and(GenericFilter<T> other) {
                throw new UnsupportedOperationException("Composition not " +
                    "allowed");
            }

            @Override
            public GenericFilter<T> or(GenericFilter<T> other) {
                throw new UnsupportedOperationException("Composition not " +
                    "allowed");
            }

            @Override
            public <O extends GenericFilter<?>> O as(Class<O> castType) {
                return filter.as(castType);
            }
        };
    }

    abstract class ComposableFilter<T> implements GenericFilter<T> {
        private final GenericFilter<T> left;
        private final GenericFilter<T> right;

        public ComposableFilter(GenericFilter<T> left, GenericFilter<T> right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public final boolean matches(T toMatch) {
            return this.compose(toMatch, this.left, this.right);
        }

        public abstract boolean compose(
            T toMatch,
            GenericFilter<T> left,
            GenericFilter<T> right
        );
    }
}
