package com.reactor.sample.phase.one;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;

import java.util.function.Function;

public class DemoTransform {
    static Logger LOGGER = LoggerFactory.getLogger(DemoTransform.class);

    public static void main(String[] args) {
        Function<Flux<String>, Flux<String>> logUserinfo = stream -> stream
            .index()
            .doOnNext(tp -> LOGGER.info("{} user: {}", tp.getT1(), tp.getT2()))
            .map(Tuple2::getT2);

        Flux.range(1000, 3)
            .map(i -> "user-" + i)
            .transform(logUserinfo)
            .subscribe(d -> LOGGER.info("onNext: {}", d));
    }
}
