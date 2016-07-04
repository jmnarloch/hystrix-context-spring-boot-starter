/**
 * Copyright (c) 2015-2016 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jmnarloch.spring.boot.hystrix.context;

import java.util.concurrent.Callable;

/**
 * The entry point to define the implementation specific logic for wrapping callable that is being executed
 * by {@code Hystrix}.
 *
 * Below is example application for passing the logging MDC context from the outer thread:
 *
 * <pre>
 * {@code
 * public class MdcAwareCallableWrapper implements HystrixCallableWrapper {
 *
 *      public <T> Callable<T> wrapCallable(Callable<T> callable) {
 *          return new MdcAwareCallable<>(callable, MDC.getCopyOfContextMap());
 *      }
 * }
 *
 * public class MdcAwareCallable<T> implements Callable<T> {
 *
 *      private final Callable<T> callable;
 *
 *      private final Map<String, String> contextMap;
 *
 *      public MdcAwareCallable(Callable<T> callable, Map<String, String> contextMap) {
 *          this.callable = callable;
 *          this.contextMap = contextMap;
 *      }
 *
 *      public T call() throws Exception {
 *          try {
 *              MDC.setContextMap(contextMap);
 *              return callable.call();
 *          } finally {
 *              MDC.clear();
 *          }
 *      }
 * }}
 * </pre>
 *
 * @author Jakub Narloch
 * @see com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy
 */
public interface HystrixCallableWrapper {

    /**
     * Wraps the passed callable instance.
     *
     * @param callable the callable to wrap
     * @param <T>      the result type
     * @return the wrapped callable
     */
    <T> Callable<T> wrapCallable(Callable<T> callable);
}
