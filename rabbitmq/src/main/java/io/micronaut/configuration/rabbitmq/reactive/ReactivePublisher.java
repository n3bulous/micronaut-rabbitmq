/*
 * Copyright 2017-2018 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.micronaut.configuration.rabbitmq.reactive;

import com.rabbitmq.client.AMQP;

/**
 * A generic contract for publishing RabbitMQ messages reactively.
 *
 * @param <T> The reactive type
 * @author James Kleeh
 * @since 1.1.0
 */
public interface ReactivePublisher<T> {

    /**
     * Publish the message with the provided arguments and return
     * a reactive type that completes successfully when the broker
     * acknowledged the message.
     *
     * @param exchange The exchange
     * @param routingKey The routing key
     * @param properties The properties
     * @param body The body
     *
     * @return The reactive type to subscribe to
     */
    T publish(String exchange, String routingKey, AMQP.BasicProperties properties, byte[] body);

}
