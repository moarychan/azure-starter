package com.reactor.sample.phase.one;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuples;

import java.time.Duration;

public class DemoGenerateToStream {
    static Logger LOGGER = LoggerFactory.getLogger(DemoGenerateToStream.class);

    public static void main(String[] args) {
        Flux.generate(
            () -> Tuples.of(0L, 1L),
            (state, sink) -> {
                LOGGER.info("generated value: {}", state.getT2());
                sink.next(state.getT2());
                long value = state.getT1() + state.getT2();
                return Tuples.of(state.getT2(), value);
            }).delayElements(Duration.ofMillis(1))
            .take(3) // try to comment this line
            .subscribe(s -> LOGGER.info("onNext: {}", s));

        LOGGER.info("Waiting for computing.");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOGGER.info("End.");
    }
}
