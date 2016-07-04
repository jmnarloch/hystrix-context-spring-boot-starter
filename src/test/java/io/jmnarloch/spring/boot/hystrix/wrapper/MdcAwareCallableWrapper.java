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
package io.jmnarloch.spring.boot.hystrix.wrapper;

import io.jmnarloch.spring.boot.hystrix.context.HystrixCallableWrapper;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * An example of propagating the {@link MDC} context map.
 *
 * @author Jakub Narloch
 */
public final class MdcAwareCallableWrapper implements HystrixCallableWrapper {

    @Override
    public <T> Callable<T> wrapCallable(Callable<T> callable) {
        return new MdcAwareCallable<>(callable, MDC.getCopyOfContextMap());
    }

    private class MdcAwareCallable<T> implements Callable<T> {

        private final Callable<T> callable;

        private final Map<String, String> contextMap;

        public MdcAwareCallable(Callable<T> callable, Map<String, String> contextMap) {
            this.callable = callable;
            this.contextMap = contextMap != null ? contextMap : new HashMap<String, String>();
        }

        @Override
        public T call() throws Exception {
            try {
                MDC.setContextMap(contextMap);
                return callable.call();
            } finally {
                MDC.clear();
            }
        }
    }
}
