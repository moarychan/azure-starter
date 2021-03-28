package com.reactor.sample.phase.one;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

public class DemoMaterialize {
    static Logger LOGGER = LoggerFactory.getLogger(DemoMaterialize.class);

    public static void main(String[] args) {

        //物化和非物化
        Flux.range(1,3)
            .doOnNext(e -> LOGGER.info("data: {}", e))
            // convert to signal stream
            .materialize()
            .doOnNext(e -> LOGGER.info("signal: {}, type: {}, isOnNext(): {}, hasValue(): {}",
                e, e.getClass().getSimpleName(), e.isOnNext(), e.hasValue()))
//            .doOnNext(e -> LOGGER.info("Another: {}", e))
            .dematerialize()
            .log()
            .collectList()
            .subscribe(r -> LOGGER.info("result: {}", r));

    }
}
