package com.reactor.sample.phase.one;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.Random;

public class DemoDisposable {
    static Logger LOGGER = LoggerFactory.getLogger(DemoDisposable.class);

    public static void main(String[] args) {
//        oldUsage();

        Flux<Object> ioRequestResults = Flux.using(
            Connection::newConnection,
            connection -> Flux.fromIterable(connection.getData()),
            Connection::close
//            ,false
        );
        ioRequestResults.subscribe(
            data -> LOGGER.info("data: {}", data),
            e -> LOGGER.error("Error:", e),
            () -> LOGGER.info("Stream finished.")
        );
    }

    private static void oldUsage() {
        try(Connection conn = Connection.newConnection()) {
            conn
                .getData().forEach(System.out::println);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

class Connection implements AutoCloseable {

    static Logger LOGGER = LoggerFactory.getLogger(Connection.class);

    @Override
    public void close() {
        LOGGER.info("IO connection closed");
    }

    final Random random = new Random();

    public Iterable<String> getData() {
        if (random.nextInt(5) < 3) {
            throw new RuntimeException("Communication error");
        }
        return Arrays.asList("Some", "Data");
    }

    public static Connection newConnection() {
        LOGGER.info("IO Connection created.");
        return new Connection();
    }
}


