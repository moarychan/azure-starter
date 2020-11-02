package com.reactor.sample.phase.one;

import reactor.core.Disposable;
import reactor.core.publisher.Flux;

public class DemoFluxRange {

    public static void main(String[] args) {
//        testsConsumer();
//        testErrorConsume();

//        testWithCompleteConsume();
//        withMySubscribe();

        Flux<Integer> ints = Flux.range(1, 4);
        SampleSubscribe<Integer> ss = new SampleSubscribe<>();
        ints.subscribe(i -> System.out.println(i), error -> System.out.println(error),
            () -> System.out.println("complete"),
            s -> ss.request(10)
        );
        ints.subscribe(ss);

    }

    private static void withMySubscribe() {
        Flux<Integer> ints = Flux.range(1, 4);
        SampleSubscribe<Integer> ss = new SampleSubscribe<>();
        ints.subscribe(i -> System.out.println(i), error -> System.out.println(error),
            () -> System.out.println("complete"),
            s -> ss.request(10)
        );
        ints.subscribe(ss);
    }

    private static void testWithCompleteConsume() {
        Flux<Integer> ints = Flux.range(1, 4);
        ints.subscribe(i -> System.out.println(i), error -> System.out.println(error), () -> System.out.println("complete"));
    }

    private static void testErrorConsume() {
        Flux<Integer> ints = Flux.range(1, 4).map(i -> {
           if (i <= 3) return i;
           throw new RuntimeException("Go to 4");
        });
        ints.subscribe(i -> System.out.println(i),
            error -> System.err.println("Error:" + error));
    }

    private static void testsConsumer() {
        Flux<Integer> ints = Flux.range(1, 3);
        /*Disposable subscribe = ints.subscribe();
        subscribe.dispose();*/
        //        ints.subscribe();

        ints.subscribe(i -> System.out.println(i));
    }
}
