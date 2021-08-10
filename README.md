A proof of concept for Jersey as an alternative way to create aop proxies, inspired by Spring.

Supports only JDK dynamic proxies for now.

There are plans to include CGLib for class proxies and AspectJ pointcut expressions for filters.

```java
interface SomeService {
    void foo(String arg);
}

class SomeServiceImpl implements SomeService {
    //...
}
```

```java
class SomeServiceAspect implements Aspect {

    @Override
    public Filter typeFilter() {
        return BuilderHelper.createContractFilter(SomeService.class.getName());
    }

    @Override
    public Map<AdviceType, MethodFilter> methodFilters() {
        return Map.of(AdviceType.BEFORE, MethodFilter.ANY);
    }

    @Override
    public void before(JoinPoint joinPoint) {
        //intercepting foo
        System.out.println("BEFORE method "
            + joinPoint.getDescriptor().getName()
            + " with arg "
            + joinPoint.getArguments()[0]);
        ;
    }
}
```

It also supports AspectJ pointcut expressions:

```java
public interface Dummy {

    void hello();

    void bye();

}

public static class MyPointExpressionAspect extends PointcutExpressionAspect {
    @Override
    public String expression() {
        return "execution(* pcf.crskdev.hk2.aop.PointcutExpressionAspectTest.Dummy+.*(..))";
    }

    //when not overridden it will execute all advice types.
    @Override
    public Set<AdviceType> adviceTypes() {
        return Set.of(AdviceType.BEFORE);
    }

    @Override
    public void before(JoinPoint joinPoint) {
        System.out.println("BEFORE method "
            + joinPoint.getDescriptor().getName()
            + " with arg "
            + joinPoint.getArguments()[0]);
        ;
    }
}
```

Register the aop proxy binder and aspect(s) via Jersey.
```java
ResourceConfig config = new ResourceConfig();
config.register(SomeServiceImpl.class, SomeService.class);
config.register(new AbstractBinder() {
    @Override
    protected void configure() {
        bind(SomeServiceAspect.class).to(Aspect.class).in(Singleton.class);
    }
);
config.register(AopProxyBinder.class);
```