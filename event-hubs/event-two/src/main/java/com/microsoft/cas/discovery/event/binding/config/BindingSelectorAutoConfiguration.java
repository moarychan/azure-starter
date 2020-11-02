package com.microsoft.cas.discovery.event.binding.config;

import com.microsoft.cas.discovery.event.binding.BindingSelector;
import com.microsoft.cas.discovery.event.binding.RoundRobinBindingSelector;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.config.BindingServiceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(BindingServiceProperties.class)
public class BindingSelectorAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    BindingSelector defaultSelector(BindingServiceProperties bsp) {
        return new RoundRobinBindingSelector(bsp);
    }

}
