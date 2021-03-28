package com.reactor.sample.phase.one;

import reactor.core.publisher.Flux;

public class DemoThenMany {
    public static void main(String[] args) {
        Flux.just(1,2,3)
            .thenMany(Flux.just(4,5))
            .subscribe(System.out::println);
        // only 4, 5 will be displayed
    }
}
