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

import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.eventnotifier.HystrixEventNotifier;
import com.netflix.hystrix.strategy.executionhook.HystrixCommandExecutionHook;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisher;
import com.netflix.hystrix.strategy.properties.HystrixPropertiesStrategy;
import io.jmnarloch.spring.boot.hystrix.context.HystrixCallableWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Performs configuration of the Hystrix concurency strategy registering {@link HystrixCallableWrapper} instances that
 * has been registered in application context.
 *
 * @author Jakub Narloch
 * @see HystrixCallableWrapper
 * @see HystrixContextAwareConcurrencyStrategy
 */
@Configuration
@ConditionalOnProperty(value = "hystrix.wrappers.enabled", matchIfMissing = true)
public class HystrixContextAutoConfiguration {

    @Autowired(required = false)
    private List<HystrixCallableWrapper> wrappers = new ArrayList<>();

    @PostConstruct
    public void configureHystrixConcurencyStrategy() {
        if (!wrappers.isEmpty()) {
            resetPlugins();

            HystrixPlugins.getInstance().registerConcurrencyStrategy(
                    new HystrixContextAwareConcurrencyStrategy(wrappers)
            );
        }
    }

    private void resetPlugins() {
        HystrixMetricsPublisher metricsPublisher = HystrixPlugins.getInstance().getMetricsPublisher();
        HystrixEventNotifier eventNotifier = HystrixPlugins.getInstance().getEventNotifier();
        HystrixPropertiesStrategy propertiesStrategy = HystrixPlugins.getInstance().getPropertiesStrategy();
        HystrixCommandExecutionHook commandExecutionHook = HystrixPlugins.getInstance().getCommandExecutionHook();
        HystrixPlugins.reset();
        HystrixPlugins.getInstance().registerMetricsPublisher(metricsPublisher);
        HystrixPlugins.getInstance().registerEventNotifier(eventNotifier);
        HystrixPlugins.getInstance().registerPropertiesStrategy(propertiesStrategy);
        HystrixPlugins.getInstance().registerCommandExecutionHook(commandExecutionHook);
    }
}
