package com.reactor.sample.phase.one;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Random;

public class DemoUsingWhen {
    static Logger LOGGER = LoggerFactory.getLogger(DemoUsingWhen.class);

    public static void main(String[] args) {
        // @Deprecated
        /*Flux.usingWhen(
            Transaction.beginTransaction(),
            trans -> trans.insertRow(Flux.just("A", "B", "C")),
            Transaction::commit,
            Transaction::rollback
        ).subscribe(
            d -> LOGGER.info("onNext: {}", d),
            e -> LOGGER.error("onError: ", e),
            () -> LOGGER.info("onComplete")
        );*/

        Flux.usingWhen(
            Transaction.beginTransaction(),
            trans -> trans.insertRow(Flux.just("A", "B", "C")),
            Transaction::commit,
            (ret, e) -> {
                LOGGER.error("onError: ", e);
                return ret.rollback();
            },
            Transaction::cancel
        ).subscribe(
            d -> LOGGER.info("onNext: {}", d),
            e -> LOGGER.error("onError: ", e),
            () -> LOGGER.info("onComplete")
        );

        LOGGER.info("Waiting for computing.");
        try {
            Thread.sleep(12000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOGGER.info("End.");
    }

}

class Transaction {
    static Logger LOGGER = LoggerFactory.getLogger(Transaction.class);

    private static final Random random = new Random();
    private final int id;

    public Transaction (int id) {
        this.id = id;
        LOGGER.info("[T: {}] created", id);
    }

    public static Mono<Transaction> beginTransaction() {
        return Mono.defer(() -> Mono.just(new Transaction(random.nextInt(1000))));
    }

    public Flux<String> insertRow(Publisher<String> rows) {
        return Flux.from(rows)
            .delayElements(Duration.ofMillis(100))
            .flatMap(r -> {
                if (random.nextInt(10) < 2) {
                    return Mono.error(new RuntimeException("Insert error: " + r));
                } else {
                    return Mono.just(r);
                }
            });
    }

    public Mono<Void> commit() {
        return Mono.defer(() -> {
            LOGGER.info("[T: {}] commit.", id);
            if (random.nextBoolean()) {
                return Mono.empty();
            } else {
                return Mono.error(new RuntimeException("Commit error."));
            }
        });
    }

    public Mono<Void> rollback() {
        return Mono.defer(() -> {
            LOGGER.info("[T: {}] rollback.", id);
            if (random.nextBoolean()) {
                return Mono.empty();
            } else {
                return Mono.error(new RuntimeException("Rollback error."));
            }
        });
    }

    public Mono<Void> cancel() {
        return Mono.defer(() -> {
            LOGGER.info("[T: {}] cancel.", id);
            if (random.nextBoolean()) {
                return Mono.empty();
            } else {
                return Mono.error(new RuntimeException("Cancel error."));
            }
        });
    }
}
