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
package io.jmnarloch.spring.boot.hystrix.support;

import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import io.jmnarloch.spring.boot.hystrix.context.HystrixCallableWrapper;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.Callable;

/**
 * Defines a custom {@link HystrixConcurrencyStrategy} that wraps the passed {@link Callable} using the predefined
 * {@link HystrixCallableWrapper} list.
 *
 * @author Jakub Narloch
 * @see HystrixCallableWrapper
 */
public class HystrixContextAwareConcurrencyStrategy extends HystrixConcurrencyStrategy {

    private final Collection<HystrixCallableWrapper> wrappers;

    /**
     * Creates new instance of {@link HystrixContextAwareConcurrencyStrategy} with the specific wrappers list.
     *
     * @param wrappers the list of wrappers
     */
    public HystrixContextAwareConcurrencyStrategy(Collection<HystrixCallableWrapper> wrappers) {
        Assert.notNull(wrappers, "Parameter 'wrappers' can not be null");
        this.wrappers = wrappers;
    }

    @Override
    public <T> Callable<T> wrapCallable(Callable<T> callable) {

        return new CallableWrapperChain<>(callable, wrappers.iterator())
                .wrapCallable();
    }

    private static class CallableWrapperChain<T> {

        private final Callable<T> callable;

        private final Iterator<HystrixCallableWrapper> wrappers;

        public CallableWrapperChain(Callable<T> callable, Iterator<HystrixCallableWrapper> wrappers) {
            this.callable = callable;
            this.wrappers = wrappers;
        }

        public Callable<T> wrapCallable() {
            Callable<T> result = callable;
            while (wrappers.hasNext()) {
                result = wrappers.next().wrapCallable(result);
            }
            return result;
        }
    }
}
