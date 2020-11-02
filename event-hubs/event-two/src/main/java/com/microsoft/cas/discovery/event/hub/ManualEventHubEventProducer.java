package com.microsoft.cas.discovery.event.hub;

import com.microsoft.cas.discovery.event.model.event.SimpleEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.Message;
import org.springframework.scheduling.annotation.EnableScheduling;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;

import java.util.function.Supplier;

/**
 * This event producer will allow sending events via Controller endpoints
 */
@Slf4j
@Configuration
@EnableScheduling
//@Profile("manual")
public class ManualEventHubEventProducer {

    @Bean
    public EmitterProcessor<Message<SimpleEvent>> emitter() {
        return EmitterProcessor.create();
    }

    @Bean
    Supplier<Flux<Message<SimpleEvent>>> eventhub(
            EmitterProcessor<Message<SimpleEvent>> emitter) {
        return () -> Flux.from(emitter)
                .doOnError(t -> log.warn("Encountered an error ({}), retrying", t.getMessage()));
    }

}
