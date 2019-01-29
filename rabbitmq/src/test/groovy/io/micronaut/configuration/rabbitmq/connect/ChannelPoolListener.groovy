package io.micronaut.configuration.rabbitmq.connect

import com.rabbitmq.client.Channel
import io.micronaut.configuration.rabbitmq.connect.ChannelPool
import io.micronaut.context.event.BeanCreatedEvent
import io.micronaut.context.event.BeanCreatedEventListener

import javax.inject.Singleton

@Singleton
class ChannelPoolListener implements BeanCreatedEventListener<ChannelPool> {

    @Override
    ChannelPool onCreated(BeanCreatedEvent<ChannelPool> event) {
        ChannelPool pool = event.getBean()
        try {
            Channel channel = pool.getChannel()
            channel.queueDeclare("abc", true, false, false, new HashMap<>())
            channel.queueDeclare("pojo", true, false, false, new HashMap<>())
            channel.queueDeclare("header", true, false, false, new HashMap<>())
            channel.queueDeclare("property", true, false, false, new HashMap<>())
            channel.queueDeclare("type", true, false, false, new HashMap<>())
            channel.queueDeclare("boolean", true, false, false, new HashMap<>())
            channel.queueDeclare("product", true, false, false, new HashMap<>())
            pool.returnChannel(channel)
        } catch (IOException e) {
            //no-op
        }
        pool
    }
}
