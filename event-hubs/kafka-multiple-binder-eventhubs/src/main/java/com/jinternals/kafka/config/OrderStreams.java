package com.jinternals.kafka.config;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

@Primary
public interface OrderStreams {

    public static String INPUT = "order";

    public static String OUTPUT = "orderOutput";

    @Input(INPUT)
    public SubscribableChannel subscribableChannel();

    @Output(OUTPUT)
    MessageChannel orderOutput();
}
