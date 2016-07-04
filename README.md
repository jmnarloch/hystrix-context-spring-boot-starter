# Hystrix Context Spring Boot starter

> A Spring Boot starter for making Hystrix context aware

[![Build Status](https://travis-ci.org/jmnarloch/hystrix-context-spring-boot-starter.svg?branch=master)](https://travis-ci.org/jmnarloch/hystrix-context-spring-boot-starter)
[![Coverage Status](https://coveralls.io/repos/jmnarloch/hystrix-context-spring-boot-starter/badge.svg?branch=master&service=github)](https://coveralls.io/github/jmnarloch/hystrix-context-spring-boot-starter?branch=master)

## Setup

Add the Spring Boot starter to your project:

```xml
<dependency>
  <groupId>io.jmnarloch</groupId>
  <artifactId>hystrix-context-spring-boot-starter</artifactId>
  <version>1.0.0</version>
</dependency>
```

## Features

Have you ever wondered why when running your Hystrix Commands the executing thread losses access to the ThreadLocal
dependant functionality?

The simple answer is that by default Hystrix will spawn a new thread to execute your code, completely unaware of the
"outer" thread context.

For instance fallowing code with fail with an exception because it's going to be executed in thread completely
unaware of the Servlet request.

```java
new HystrixCommand<Object>(commandKey()) {
    @Override
    protected Object run() throws Exception {
        return RequestContextHolder.currentRequestAttributes().getAttribute("RequestId", SCOPE_REQUEST);
    }
}.execute();
```

The same rules applies to your [hystrix-javanica](https://github.com/Netflix/Hystrix/tree/master/hystrix-contrib/hystrix-javanica) `@HystrixCommand` annotated methods.

This extension tries to solve this by allowing to register custom Callable wrappers that will be executed prior spawning
new worker thread.

### Usage

In order to use this feature all you need to do is register a instance of `HystrixCallableWrapper`.
You may register as many of them as you like, they will be stacked up.

Example:

```java
@Component
public class RequestAttributeAwareCallableWrapper implements HystrixCallableWrapper {

    @Override
    public <T> Callable<T> wrapCallable(Callable<T> callable) {
        return new RequestAttributeAwareCallable<>(callable, RequestContextHolder.currentRequestAttributes());
    }

    private static class RequestAttributeAwareCallable<T> implements Callable<T> {

        private final Callable<T> callable;
        private final RequestAttributes requestAttributes;

        public RequestAttributeAwareCallable(Callable<T> callable, RequestAttributes requestAttributes) {
            this.callable = callable;
            this.requestAttributes = requestAttributes;
        }

        @Override
        public T call() throws Exception {

            try {
                RequestContextHolder.setRequestAttributes(requestAttributes);
                return callable.call();
            } finally {
                RequestContextHolder.resetRequestAttributes();
            }
        }
    }
}
```

This will make your Hystrix command aware of the execution context.

## Properties

The only supported property is `hystrix.wrappers.enabled` which allows to disable this extension.

```
hystrix.wrappers.enabled=true # true by default
```

## License

Apache 2.0
