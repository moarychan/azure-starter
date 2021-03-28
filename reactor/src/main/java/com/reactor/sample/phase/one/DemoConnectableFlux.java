package com.reactor.sample.phase.one;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class DemoConnectableFlux {
    static Logger LOGGER = LoggerFactory.getLogger(DemoConnectableFlux.class);

    public static void main(String[] args) throws InterruptedException {
//        multicast();

//        cacheStream();

        shareStream();
    }

    private static void shareStream() throws InterruptedException {
        Flux<Integer> source = Flux.range(0, 5)
                                   .delayElements(Duration.ofMillis(100))
                                   .doOnSubscribe(s -> LOGGER.info("New sub for the cold publisher"));
        Flux<Integer> cacheSource = source.share();

        cacheSource.subscribe(e -> LOGGER.info("Sub 1 on Next: {}", e));

        LOGGER.info("Waiting for computing.");
        Thread.sleep(400);
        LOGGER.info("End.");

        // 只会订阅到尚未错过的事件
        cacheSource.subscribe(e -> LOGGER.info("Sub 2 on Next: {}", e));
        Thread.sleep(9000);
    }

    private static void cacheStream() throws InterruptedException {
        Flux<Integer> source = Flux.range(0, 3)
                                   .doOnSubscribe(s -> LOGGER.info("New sub for the cold publisher"));
        Flux<Integer> cacheSource = source.cache(Duration.ofSeconds(1));

        cacheSource.subscribe(e -> LOGGER.info("Sub 1 on Next: {}", e));
        cacheSource.subscribe(e -> LOGGER.info("Sub 2 on Next: {}", e));

        LOGGER.info("Waiting for computing.");
        Thread.sleep(3000);
        LOGGER.info("End.");

        cacheSource.subscribe(e -> LOGGER.info("Sub 3 on Next: {}", e));
    }

    // 多播流元素
    private static void multicast() {
        Flux<Integer> source = Flux.range(0, 3)
                                        .doOnSubscribe(s -> LOGGER.info("New sub for the cold publisher"));
        ConnectableFlux<Integer> conn = source.publish();

        conn.subscribe(e -> LOGGER.info("Sub 1 on Next: {}", e));
        conn.subscribe(e -> LOGGER.info("Sub 2 on Next: {}", e));
        LOGGER.info("all subs are ready, connecting");
        conn.connect();
    }
}
