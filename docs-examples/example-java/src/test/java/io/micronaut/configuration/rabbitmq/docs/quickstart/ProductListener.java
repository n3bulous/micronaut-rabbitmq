package io.micronaut.configuration.rabbitmq.docs.quickstart;

// tag::imports[]
import io.micronaut.configuration.rabbitmq.annotation.Queue;
import io.micronaut.configuration.rabbitmq.annotation.RabbitListener;
import io.micronaut.context.annotation.Requires;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
// end::imports[]

@Requires(property = "spec.name", value = "QuickstartSpec")
// tag::clazz[]
@RabbitListener // <1>
public class ProductListener {

    List<String> messageLengths = Collections.synchronizedList(new ArrayList<>());

    @Queue("product") // <2>
    public void receive(byte[] data) { // <3>
        messageLengths.add(new String(data));
        System.out.println("Java received " + data.length + " bytes from RabbitMQ");
    }
}
// end::clazz[]
