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
package io.jmnarloch.spring.boot.hystrix;

import com.netflix.hystrix.HystrixCommand;
import io.jmnarloch.spring.boot.hystrix.context.HystrixCallableWrapper;
import io.jmnarloch.spring.boot.hystrix.wrapper.MdcAwareCallableWrapper;
import io.jmnarloch.spring.boot.hystrix.wrapper.RequestAttributeAwareCallableWrapper;
import org.jboss.logging.MDC;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

import static io.jmnarloch.spring.boot.hystrix.utils.HystrixUtils.commandGroup;
import static org.junit.Assert.assertEquals;
import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;

/**
 * Demonstrates the usage of this component
 *
 * @author Jakub Narloch
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Demo.Application.class)
@WebIntegrationTest(randomPort = true)
public class Demo {

    private static final String GROUP_KEY = "TestGroup";
    private static final String REQUEST_ID = "RequestId";

    private String requestId;

    @Before
    public void setUp() throws Exception {

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
        requestId = UUID.randomUUID().toString();
    }

    @Test
    public void shouldPropagateRequestAttributes() {

        // given
        RequestContextHolder.currentRequestAttributes().setAttribute(REQUEST_ID, requestId, SCOPE_REQUEST);

        // when
        final Object result = new HystrixCommand<Object>(commandGroup("TestGroup")) {
            @Override
            protected Object run() throws Exception {
                return RequestContextHolder.currentRequestAttributes().getAttribute(REQUEST_ID, SCOPE_REQUEST);
            }
        }.execute();

        // then
        assertEquals(requestId, result);
    }

    @Test
    public void shouldPropagateMdcContext() {

        // given
        MDC.put(REQUEST_ID, requestId);

        // when
        final Object result = new HystrixCommand<Object>(commandGroup(GROUP_KEY)) {
            @Override
            protected Object run() throws Exception {
                return MDC.get(REQUEST_ID);
            }
        }.execute();

        // then
        assertEquals(requestId, result);
    }

    @Configuration
    @EnableAutoConfiguration
    public static class Application {

        @Bean
        public HystrixCallableWrapper mdcAwareCallableWrapper() {
            return new MdcAwareCallableWrapper();
        }

        @Bean
        public HystrixCallableWrapper requestAttributeAwareCallableWrapper() {
            return new RequestAttributeAwareCallableWrapper();
        }
    }
}
