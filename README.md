A proof of concept for Jersey as an alternative way to create aop proxies, inspired by Spring.
```java
interface SomeService {
    void foo(String arg);
}

class SomeServiceImpl implements SomeService {
    //...
}

class SomeService implements Aspect {
    
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
            + joinPoint.getArguments()[0]);;
    }
}
```

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