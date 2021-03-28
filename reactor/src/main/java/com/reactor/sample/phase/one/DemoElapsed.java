package com.reactor.sample.phase.one;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class DemoElapsed {
    static Logger LOGGER = LoggerFactory.getLogger(DemoElapsed.class);

    public static void main(String[] args) throws InterruptedException {
        Flux.range(0, 5)
            .delayElements(Duration.ofMillis(100))
//            .subscribe(d -> LOGGER.info("onNext: {}", d));
            .elapsed()
            .subscribe(e -> LOGGER.info("Elapsed {} ms {}", e.getT1(), e.getT2()));

        LOGGER.info("Waiting for computing.");
        Thread.sleep(5000);
        LOGGER.info("End.");
        // ScheduledExecutorService
    }
}
