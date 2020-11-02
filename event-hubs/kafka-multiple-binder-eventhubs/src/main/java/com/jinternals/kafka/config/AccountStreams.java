package com.jinternals.kafka.config;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

@Primary
public interface AccountStreams {

    public static String INPUT = "account";

    public static String OUTPUT = "accountOutput";

    @Input(INPUT)
    public SubscribableChannel subscribableChannel();

    @Output(OUTPUT)
    MessageChannel accountOutput();
}
