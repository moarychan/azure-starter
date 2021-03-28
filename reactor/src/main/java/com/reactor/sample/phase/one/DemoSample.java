package com.reactor.sample.phase.one;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class DemoSample {
    static Logger LOGGER = LoggerFactory.getLogger(DemoSample.class);
    
    public static void main(String[] args) {
        Flux.range(1, 100)
            .delayElements(Duration.ofMillis(1))
            .sample(Duration.ofMillis(20))
            .subscribe(e -> LOGGER.info("onNext: {}", e));

        LOGGER.info("Waiting for computing.");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOGGER.info("End.");
    }
}
