package com.reactor.sample.phase.one;

import reactor.core.publisher.Flux;

import java.util.Arrays;

public class DemoScanSkip {
    public static void main(String[] args) {
        int bucketSize = 5;
        Flux.range(1, 500)
            .index()
            .scan(new int[bucketSize],
                (acc, ele) -> {
                    acc[(int)(ele.getT1() % bucketSize)] = ele.getT2();
                    return acc;
                })
            .skip(bucketSize * 2)
            .map(array -> Arrays.stream(array).sum() * 1.0 / bucketSize)
            .subscribe(av -> System.out.println("average: " + av));
    }
}
