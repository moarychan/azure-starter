// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.jinternals.kafka.output;

import com.jinternals.kafka.config.AccountStreams;
import com.jinternals.kafka.config.OrderStreams;
import com.jinternals.kafka.events.AccountEvent;
import com.jinternals.kafka.events.OrderEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class SourceExample {

    @Autowired
    private AccountStreams accountStreams;

    @Autowired
    private OrderStreams orderStreams;

    @PostMapping("/messages")
    public String postMessage(@RequestParam String message) {
        OrderEvent demoEvent = new OrderEvent();
        demoEvent.setId("1");
        demoEvent.setName(message);
        this.orderStreams.orderOutput().send(new GenericMessage<>(demoEvent));
        return message;
    }

    @PostMapping("/messages1")
    public String postMessage1(@RequestParam String message) {
        AccountEvent accountEvent = new AccountEvent();
        accountEvent.setId("1");
        accountEvent.setName(message);
        this.accountStreams.accountOutput().send(new GenericMessage<>(accountEvent));
        return message;
    }

}
