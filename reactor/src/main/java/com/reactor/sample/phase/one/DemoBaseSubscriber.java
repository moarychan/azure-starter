package com.reactor.sample.phase.one;

import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;

public class DemoBaseSubscriber<T> extends BaseSubscriber<T> {

    @Override
    protected void hookOnSubscribe(Subscription subscription) {
        System.out.println("Subscribed");
        request(1);
    }

    @Override
    protected void hookOnNext(T value) {
        System.out.println("Value: " + value);
        request(1);
    }
}
