package com.reactor.sample.phase.one;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Random;

public class DemoFlatOperation {
    static Logger LOGGER = LoggerFactory.getLogger(DemoFlatOperation.class);
    public static void main(String[] args) throws InterruptedException {
        Flux.just("user-1","user-2", "user-3")
            .flatMap(u -> requestBook(u)
                .map(b -> u + "/" + b))
            .subscribe(r -> LOGGER.info("onNext: " + r));
//        requestBook(null).subscribe(r -> System.out.println("onNext: " + r));
        LOGGER.info("Waiting for computing.");
        Thread.sleep(10000);
        LOGGER.info("End.");
    }

    public static Flux<String> requestBook(String user) {
        LOGGER.info("Get user {} books.", user);
        return Flux.range(1, new Random().nextInt(3) + 1)
            .map(i -> "book-" + i)
            .delayElements(Duration.ofMillis(3));
    }
}
