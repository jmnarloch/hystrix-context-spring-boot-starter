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

import io.jmnarloch.spring.boot.hystrix.context.HystrixCallableWrapper;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.concurrent.Callable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Tests the {@link HystrixContextAwareConcurrencyStrategy} class.
 *
 * @author Jakub Narloch
 */
public class HystrixContextAwareConcurrencyStrategyTest {

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentException() {

        // when
        new HystrixContextAwareConcurrencyStrategy(null);
    }

    @Test
    public void shouldNotWrapCallable() {

        // given
        final Callable callable = Mockito.mock(Callable.class);

        // and
        final HystrixContextAwareConcurrencyStrategy strategy = new HystrixContextAwareConcurrencyStrategy(
                Collections.<HystrixCallableWrapper>emptyList()
        );

        // when
        final Callable result = strategy.wrapCallable(callable);

        // then
        assertEquals(callable, result);
    }

    @Test
    public void shouldWrapCallable() {

        // given
        final Callable callable = Mockito.mock(Callable.class);

        // and
        final HystrixContextAwareConcurrencyStrategy strategy = new HystrixContextAwareConcurrencyStrategy(
                Collections.<HystrixCallableWrapper>singletonList(new SimpleHystrixCallableWrapper())
        );

        // when
        final Callable result = strategy.wrapCallable(callable);

        // then
        assertNotEquals(callable, result);
    }

    private static class SimpleHystrixCallableWrapper implements HystrixCallableWrapper {

        @Override
        public <T> Callable<T> wrapCallable(final Callable<T> callable) {
            return new Callable<T>() {
                @Override
                public T call() throws Exception {
                    return callable.call();
                }
            };
        }
    }
}