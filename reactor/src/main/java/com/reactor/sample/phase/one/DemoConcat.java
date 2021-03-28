package com.reactor.sample.phase.one;

import reactor.core.publisher.Flux;

public class DemoConcat {
    public static void main(String[] args) {
        // merge
        // zip
        // combineLatest
        Flux.concat(Flux.range(1,3), Flux.range(4,2), Flux.range(6,5))
            .subscribe(e -> System.out.println("onNext: " + e));
    }
}
