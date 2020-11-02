package com.microsoft.cas.discovery.event.hub;

import com.microsoft.cas.discovery.event.model.event.SimpleEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Slf4j
//@Configuration
//@EnableScheduling
public class EventHubsConsumer {

    private final AtomicInteger counter = new AtomicInteger();

//    @Bean
    public Consumer<Message<SimpleEvent>> eventhub() {
        // Turn on debug to see the messages, might be spammy
        return message -> {
            counter.incrementAndGet();
            log.debug("Event hubs, consumed '{}'", message.getPayload());
        };
    }

//    @Scheduled(cron = "*/15 * * * * *")
    private void count() {
        log.info("Consumed {} messages", counter);
    }

}
