package com.reactor.sample.phase.one;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.util.Random;
import java.util.function.Function;

public class DemoCompose {
    static Logger LOGGER = LoggerFactory.getLogger(DemoCompose.class);

    public static void main(String[] args) {
        Function<Flux<String>, Flux<String>> logUserinfo = stream -> {
            if (new Random().nextBoolean()) {
                return stream
                    .doOnNext(d -> LOGGER.info("A user: {}", d));
            } else {
                return stream
                    .doOnNext(d -> LOGGER.info("B user: {}", d));
            }
        };

        Flux<String> publisher = Flux.just("1", "2")
                                   .compose(logUserinfo);
        // 每次都会执行流转换
        publisher.subscribe();
        publisher.subscribe();
    }
}
