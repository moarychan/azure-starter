package com.reactor.sample.phase.one;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.stream.IntStream;

public class DemoCreateToStream {
    static Logger LOGGER = LoggerFactory.getLogger(DemoCreateToStream.class);

    public static void main(String[] args) {
        Flux.create(emitter -> {
            // this is different from push method
            emitter.onDispose(() -> LOGGER.info("Disposed"));
            IntStream.range(2000, 3000).forEach(emitter::next);

        })
            .delayElements(Duration.ofMillis(1))
            .subscribe(s -> LOGGER.info("onNext: {}", s));

        LOGGER.info("Waiting for computing.");
        try {
            Thread.sleep(16000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOGGER.info("End.");
    }
}
