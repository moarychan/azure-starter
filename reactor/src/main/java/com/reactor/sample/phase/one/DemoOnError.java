package com.reactor.sample.phase.one;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Random;

public class DemoOnError {
    static Logger LOGGER = LoggerFactory.getLogger(DemoOnError.class);

    public static void main(String[] args) throws InterruptedException {
        Flux.just("user-1")
            .flatMap(u -> requestBooks(u)
                // 不超过5此重试，从100毫秒的时序时间开始
                .retryBackoff(5, Duration.ofMillis(100))
                // 3秒后会触发一个超时异常
                .timeout(Duration.ofSeconds(3))
                // 如果出现任何错误，则返回默认数据集
                .onErrorResume(e -> Flux.just("The Martian")))
            .subscribe(
                r -> LOGGER.info("onNext: " + r),
                e -> LOGGER.error("onError: ", e),
                () -> LOGGER.info("onComplete")
            );
        LOGGER.info("Waiting for computing.");
        Thread.sleep(10000);
        LOGGER.info("End.");
    }

    public static Flux<String> requestBooks(String userId) {
        return Flux.defer(() ->
        {
            if (new Random().nextInt(10) < 7) {
                return Flux.<String>error(new RuntimeException("Error"))
                    .delaySequence(Duration.ofMillis(100));
            } else {
                return Flux.just("Blue", "Yellow").delayElements(Duration.ofMillis(3));

            }
        }).doOnSubscribe(s -> LOGGER.info("Request for {} ", userId));
    }
}
