package com.reactor.sample.phase.one;

import reactor.core.publisher.Flux;

import java.util.LinkedList;

public class DemoBatchProcessing {
    public static void main(String[] args) {
        //        demoBuffer();
        //        demoWindowed();
        // another method, buffer or groupBy
        //        demoGroupBy();
    }

    private static void demoGroupBy() {
        Flux.range(1, 7)
            .groupBy(e -> e % 2 == 2 ? "Even" : "Odd")
            //            .subscribe(flux -> flux.subscribe(System.out::println));
            .subscribe(groupFlux -> groupFlux
                .scan(new LinkedList<>(), (list, item) -> {
                    list.add(item);
                    if (list.size() > 2) {
                        list.remove(0);
                    }
                    return list;
                })
                .filter(arr -> !arr.isEmpty())
                .subscribe(data -> System.out.println("key: " + groupFlux.key() + " data: " + data))
            );
    }

    private static void demoWindowed() {
        Flux<Flux<Integer>> windowedFlux = Flux.range(101, 20)
                                               .windowUntil(DemoBatchProcessing::isPrime, true);
        windowedFlux.subscribe(window -> window.collectList()
                                               .subscribe(e -> System.out.println("onNext: " + e)));
    }

    public static boolean isPrime(Integer n) {
        if (n < 2) {
            return false;
        }

        if (n == 2) {
            return true;
        }

        if (n % 2 == 0) {
            return false;
        }

        for (int i = 3; i * i <= n; i += 2) {
            if (n % i == 0) {
                return false;
            }
        }

        return true;
    }

    private static void demoBuffer() {
        Flux.range(1, 13)
            .buffer(4)
            .subscribe(e -> System.out.println("onNext: " + e));
    }
}
