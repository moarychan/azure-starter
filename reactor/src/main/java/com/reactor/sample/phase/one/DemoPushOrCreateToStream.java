package com.reactor.sample.phase.one;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.stream.IntStream;

public class DemoPushOrCreateToStream {
    static Logger LOGGER = LoggerFactory.getLogger(DemoPushOrCreateToStream.class);

    public static void main(String[] args) {
        Flux.push(emitter -> IntStream.range(2000, 3000).forEach(emitter::next))
            .delayElements(Duration.ofMillis(1))
            .subscribe(s -> LOGGER.info("onNext: {}", s));

//        Flux.create(emitter -> {
//                // this is different from push method
//                emitter.onDispose(() -> LOGGER.info("Disposed"));
//                IntStream.range(2000, 3000).forEach(emitter::next);
//            })
        LOGGER.info("Waiting for computing.");
        try {
            Thread.sleep(16000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOGGER.info("End.");
    }
}
