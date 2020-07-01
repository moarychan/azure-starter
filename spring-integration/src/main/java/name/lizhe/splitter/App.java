package name.lizhe.splitter;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

/**
 * Main entry-point into the test application
 */
public class App {

    // http://lizhe.name/node/139
    public static void main(String args[]) {
        String cfg = "classpath:integration/applicationContext.xml";
        ApplicationContext context = new ClassPathXmlApplicationContext(cfg);
        MessageChannel channel = context.getBean("inputChannel", MessageChannel.class);
        Message<String> message1 = MessageBuilder.withPayload("a,b,c,d,e,f").build();
        channel.send(message1);
    }

}