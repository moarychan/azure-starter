package com.microsoft.cas.discovery.event.transformer;

import com.microsoft.cas.discovery.event.binding.BindingSelector;
import com.microsoft.cas.discovery.event.model.event.SimpleEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.support.ErrorMessage;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.function.Function;

@Slf4j
@Configuration
@EnableScheduling
public class EventHubEventTransformer {

    private static final String SEND_TO = "spring.cloud.stream.sendto.destination";

    private static final String PREFIX = "uppercase-out-.*";

    private final BindingSelector selector;

    public EventHubEventTransformer(BindingSelector selector) {
        this.selector = selector;
    }

    @Bean
    Function<SimpleEvent, Message<SimpleEvent>> uppercase() {
        return event -> {
            Message<SimpleEvent> build = MessageBuilder
                .withPayload(event.toBuilder()
                                  .datum("Transformed, on it's way to the next Event Hub")
                                  .build())
                .setHeader(SEND_TO, selector.nextBinding(PREFIX, true))
                .build();
            log.info("Trans event hubs message.");
            return build;
        };
    }

    @ServiceActivator(inputChannel = "errorChannel")
    public void error(@Header("id") String id, ErrorMessage message) {
        log.error("Handling ERROR via errorChannel, id='{}', message='{}'", id, message);
    }

}
