package com.microsoft.cas.discovery.event.transformer;

//import com.microsoft.azure.spring.integration.core.api.reactor.Checkpointer;
import com.azure.spring.integration.core.api.reactor.Checkpointer;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.integration.config.GlobalChannelInterceptor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

import static com.azure.spring.integration.core.AzureHeaders.CHECKPOINTER;

//import static com.microsoft.azure.spring.integration.core.AzureHeaders.CHECKPOINTER;

/**
 * Channel interceptor for the incoming messages.
 * {@link #afterSendCompletion(Message, MessageChannel, boolean, Exception)}
 * will be invoked _after_ the message was sent through the output channel.
 * If the message was not sent through the output channel (either exception or
 * a method returning null) 'sent' will be false.
 *
 * When a message is received the order of calls will be (IN - incoming channel
 * OUT - outgoing channel):
 *
 *  IN.preSend -> {@link EventHubEventTransformer#transform()} (businness logic)
 *  -> OUT.preSend -> (Send message to next channel) -> OUT.postSend ->
 *  OUT.afterSendCompletion -> IN.postSend -> IN.afterSendCompletion
 */
@Slf4j
@Component
@GlobalChannelInterceptor(patterns = {"uppercase-in-*"})
public class EventHubTransformerChannelInterceptor implements ChannelInterceptor {

    private static final String ID_HEADER = "id";

    private final AtomicInteger totalSent;

    public EventHubTransformerChannelInterceptor() {
        this.totalSent = new AtomicInteger();
    }

    @Override
    public void afterSendCompletion(@NotNull Message<?> message,
                                    @NotNull MessageChannel channel,
                                    boolean sent,
                                    Exception ex) {
        Object id = message.getHeaders().get(ID_HEADER);
        Checkpointer checkpointer = checkpointer(message);
        if (checkpointer == null) {
            log.error("Checkpointer is null in channel='{}', id=''{}", channel, id);
        } else {
            if (sent) {
                log.debug("Checkpoint success, channel='{}', id=''{}", channel, id);
                totalSent.incrementAndGet();
                checkpointer.success()
                        .doOnSuccess(s -> log.debug("Message sent and marked: '{}'", id))
                        .doOnError(e -> log.debug("Message sent, failed to mark: '{}'", id, e))
                        .subscribe();
            } else {
                log.info("Checkpoint failure,  channel='{}', id=''{}", channel, id);
                checkpointer.failure()
                        .doOnSuccess(s -> log.debug("Message not sent, marked as failure: '{}'", id))
                        .doOnError(e -> log.debug("Message not sent, failed to mark: '{}'", id, e))
                        .subscribe();
            }
        }
    }

    private Checkpointer checkpointer(Message<?> message) {
        return (Checkpointer) message.getHeaders().get(CHECKPOINTER);
    }

    @Scheduled(cron = "*/15 * * * * *")
    private void count() {
        log.info("Transformed {} messages", totalSent);
    }

}
