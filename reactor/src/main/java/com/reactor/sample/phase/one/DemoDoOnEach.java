package com.reactor.sample.phase.one;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

public class DemoDoOnEach {
    static Logger LOGGER = LoggerFactory.getLogger(DemoDoOnEach.class);

    public static void main(String[] args) {

        // doOnNext
        // doOnComplete
        // doOnCancel
        // doOnSubscribe

        Flux.just(1,2,3)
            .concatWith(Flux.error(new RuntimeException("Conn error")))
            .doOnEach(s -> LOGGER.debug("Signal: {}", s))
            .subscribe();
    }
}
