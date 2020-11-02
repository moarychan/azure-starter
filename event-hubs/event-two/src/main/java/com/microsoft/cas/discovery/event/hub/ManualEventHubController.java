package com.microsoft.cas.discovery.event.hub;

import com.microsoft.cas.discovery.event.binding.BindingSelector;
import com.microsoft.cas.discovery.event.model.event.SimpleEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.EmitterProcessor;

import java.util.Random;
import java.util.UUID;

@Slf4j
@RestController
//@Profile("manual")
public class ManualEventHubController {

    private static final String SEND_TO = "spring.cloud.stream.sendto.destination";

    private static final String PREFIX = "eventhub-out-.*";

    private final BindingSelector bindingSelector;

    private final EmitterProcessor<Message<SimpleEvent>> emitter;

    private static final Random RND = new Random();

    private static final String[] QUOTES = {
        "Before software can be reusable it first has to be usable",
        "One man’s crappy software is another man’s full-time job",
        "Deleted code is debugged code",
        "If at first you don’t succeed; call it version 1.0",
        "It’s not a bug – it’s an undocumented feature",
        "There are only two industries that refer to their customers as 'users'",
        "Never trust a computer you can’t throw out a window",
        "The function of good software is to make the complex appear to be simple",
        "If Java had true garbage collection, most programs would delete themselves upon execution",
        "To err is human, but to really foul things up you need a computer"
    };

    public ManualEventHubController(
            BindingSelector bindingSelector,
            EmitterProcessor<Message<SimpleEvent>> emitter) {
        this.bindingSelector = bindingSelector;
        this.emitter = emitter;
    }

    @GetMapping("/send")
    public void delegateToEmitter() {
        SimpleEvent event = SimpleEvent.builder()
                                       .streamUid(UUID.randomUUID().toString()).tenantUid("my-tenant-id")
                                       .taskUid("my-task-id").index("my-index").datum(QUOTES[RND.nextInt(QUOTES.length)])
                                       .build();
        emitter.onNext(
                MessageBuilder
                        .withPayload(event)
                        .setHeader(SEND_TO, "uppercase-in-0") // bindingSelector.nextBinding(PREFIX, true)
                        .build()
        );
    }

}
