package name.lizhe.splitter;

import org.springframework.integration.splitter.AbstractMessageSplitter;
import org.springframework.messaging.Message;

public class MySplitService extends AbstractMessageSplitter{

    @Override
    protected Object splitMessage(Message<?> message) {
        System.out.println(message);
        return message.getPayload().toString().split(",");
    }
}