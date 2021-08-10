package pcf.crskdev.hk2.aop;

import org.aspectj.weaver.tools.PointcutExpression;
import org.aspectj.weaver.tools.PointcutParser;
import org.aspectj.weaver.tools.PointcutPrimitive;
import org.aspectj.weaver.tools.ShadowMatch;
import org.glassfish.hk2.api.Filter;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class PointcutExpressionAspect implements Aspect {

    static final Set<PointcutPrimitive> SUPPORTED_PRIMITIVES = new HashSet<>();

    static {
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.EXECUTION);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.WITHIN);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_ANNOTATION);
    }

    @Override
    public final Map<AdviceType, MethodFilter> methodFilters() {
        Map<AdviceType, MethodFilter> methodFilterMap = new HashMap<>();
        this.adviceTypes().forEach((advice) -> methodFilterMap
            .put(advice, descriptor -> {
                PointcutExpression expression = this.resolveExpression();
                Method method = descriptor.getMethod();
                ShadowMatch match =
                    expression.matchesMethodExecution(method);
                return match.alwaysMatches() || match.maybeMatches();
            }));
        return methodFilterMap;
    }

    @Override
    public final Filter typeFilter() {
        return (descriptor) -> {
            try {
                PointcutExpression expression = this.resolveExpression();
                Class<?> aClass = Class.forName(descriptor.getImplementation());
                boolean matched = expression.couldMatchJoinPointsInType(aClass);
                return matched;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return false;
            }
        };
    }

    public abstract String expression();

    public Set<AdviceType> adviceTypes() {
        return AdviceType.all();
    }

    private PointcutExpression resolveExpression() {
        PointcutParser parser = PointcutParser
            .getPointcutParserSupportingSpecifiedPrimitivesAndUsingSpecifiedClassLoaderForResolution(
                SUPPORTED_PRIMITIVES,
                this.getClass().getClassLoader()
            );
        return parser.parsePointcutExpression(this.expression());
    }
}
