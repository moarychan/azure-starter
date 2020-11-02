package com.microsoft.cas.discovery.event.srv;

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
@Configuration
@EnableScheduling
public class ServiceBusConsumer {

    private final AtomicInteger counter = new AtomicInteger();

    @Bean
    Consumer<Message<SimpleEvent>> servicebus() {
        return msg -> {
            counter.incrementAndGet();
            System.out.println("Service bus, consumed message " + msg.getPayload());
        };
    }

    @Scheduled(cron = "*/15 * * * * *")
    private void count() {
        log.info("Service bus, consumed {} messages", counter);
    }
}
