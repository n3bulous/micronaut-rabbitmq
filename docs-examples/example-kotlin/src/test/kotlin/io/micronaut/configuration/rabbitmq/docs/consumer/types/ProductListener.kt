package io.micronaut.configuration.rabbitmq.docs.consumer.types

// tag::imports[]
import com.rabbitmq.client.BasicProperties
import com.rabbitmq.client.Envelope
import io.micronaut.configuration.rabbitmq.annotation.Queue
import io.micronaut.configuration.rabbitmq.annotation.RabbitListener
import io.micronaut.context.annotation.Requires

import java.util.ArrayList
import java.util.Collections
// end::imports[]

@Requires(property = "spec.name", value = "TypeBindingSpec")
// tag::clazz[]
@RabbitListener
class ProductListener {

    val messages: MutableList<String> = Collections.synchronizedList(ArrayList())

    @Queue("product")
    fun receive(data: ByteArray,
                envelope: Envelope, // <1>
                basicProperties: BasicProperties) { // <2>
        messages.add("exchange: [${envelope.exchange}], routingKey: [${envelope.routingKey}], contentType: [${basicProperties.contentType}]")
    }
}
// end::clazz[]
