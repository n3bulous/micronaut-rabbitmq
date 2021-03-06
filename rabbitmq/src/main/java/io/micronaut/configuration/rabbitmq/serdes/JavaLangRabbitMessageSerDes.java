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

package io.micronaut.configuration.rabbitmq.serdes;

import io.micronaut.configuration.rabbitmq.bind.RabbitConsumerState;
import io.micronaut.core.serialize.exceptions.SerializationException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Serializes and deserializes standard Java types.
 *
 * @author James Kleeh
 * @since 1.1.0
 */
@Singleton
public class JavaLangRabbitMessageSerDes implements RabbitMessageSerDes<Object> {

    /**
     * The order of this serDes.
     */
    public static final Integer ORDER = 100;

    protected final Map<Class, RabbitMessageSerDes> javaSerDes = new HashMap<>();

    /**
     * Default constructor.
     */
    public JavaLangRabbitMessageSerDes() {
        javaSerDes.put(String.class, getStringSerDes());
        javaSerDes.put(Short.class, getShortSerDes());
        javaSerDes.put(Integer.class, getIntegerSerDes());
        javaSerDes.put(Long.class, getLongSerDes());
        javaSerDes.put(Float.class, getFloatSerDes());
        javaSerDes.put(Double.class, getDoubleSerDes());
        javaSerDes.put(byte[].class, getByteArraySerDes());
        javaSerDes.put(ByteBuffer.class, getByteBufferSerDes());
        javaSerDes.put(UUID.class, getUUIDSerDes());
    }

    @Override
    public byte[] serialize(Object data) {
        return findSerDes(data.getClass()).serialize(data);
    }

    @Override
    public Object deserialize(RabbitConsumerState messageState, Class<Object> type) {
        return findSerDes(type).deserialize(messageState, type);
    }

    @Override
    public boolean supports(Class<Object> type) {
        return findSerDes(type) != null;
    }

    @Override
    public int getOrder() {
        return ORDER;
    }

    /**
     * Finds the correct serDes based on the type.
     *
     * @param type The java type
     * @return The serdes, or null if none can be found
     */
    @Nullable
    protected RabbitMessageSerDes findSerDes(Class type) {
        return javaSerDes.get(type);
    }

    /**
     * @return The serDes that handles {@link String}
     */
    @Nonnull
    protected RabbitMessageSerDes<String> getStringSerDes() {
        return new StringSerDes();
    }

    /**
     * @return The serDes that handles {@link Short}
     */
    @Nonnull
    protected RabbitMessageSerDes<Short> getShortSerDes() {
        return new ShortSerDes();
    }

    /**
     * @return The serDes that handles {@link Integer}
     */
    @Nonnull
    protected RabbitMessageSerDes<Integer> getIntegerSerDes() {
        return new IntegerSerDes();
    }

    /**
     * @return The serDes that handles {@link Long}
     */
    @Nonnull
    protected RabbitMessageSerDes<Long> getLongSerDes() {
        return new LongSerDes();
    }

    /**
     * @return The serDes that handles {@link Float}
     */
    @Nonnull
    protected RabbitMessageSerDes<Float> getFloatSerDes() {
        return new FloatSerDes();
    }

    /**
     * @return The serDes that handles {@link Double}
     */
    @Nonnull
    protected RabbitMessageSerDes<Double> getDoubleSerDes() {
        return new DoubleSerDes();
    }

    /**
     * @return The serDes that handles byte[]
     */
    @Nonnull
    protected RabbitMessageSerDes<byte[]> getByteArraySerDes() {
        return new ByteArraySerDes();
    }

    /**
     * @return The serDes that handles {@link ByteBuffer}
     */
    @Nonnull
    protected RabbitMessageSerDes<ByteBuffer> getByteBufferSerDes() {
        return new ByteBufferSerDes();
    }

    /**
     * @return The serDes that handles {@link UUID}
     */
    @Nonnull
    protected RabbitMessageSerDes<UUID> getUUIDSerDes() {
        return new UUIDSerDes();
    }

    /**
     * Serializes/deserializes a {@link String}.
     */
    static class StringSerDes implements RabbitMessageSerDes<String> {

        private static final Charset ENCODING = Charset.forName("UTF8");

        @Override
        public String deserialize(RabbitConsumerState messageState, Class<String> type) {
            byte[] data = messageState.getBody();
            if (data == null) {
                return null;
            } else {
                return new String(data, ENCODING);
            }
        }

        @Override
        public byte[] serialize(String data) {
            if (data == null) {
                return null;
            } else {
                return data.getBytes(ENCODING);
            }
        }

        @Override
        public boolean supports(Class<String> type) {
            return type == String.class;
        }
    }

    /**
     * Serializes/deserializes a {@link Short}.
     */
    static class ShortSerDes implements RabbitMessageSerDes<Short> {

        @Override
        public Short deserialize(RabbitConsumerState messageState, Class<Short> type) {
            byte[] data = messageState.getBody();
            if (data == null) {
                return null;
            } else if (data.length != 2) {
                throw new SerializationException("Incorrect message body size to deserialize to a Short");
            } else {
                short value = 0;
                for (byte b : data) {
                    value <<= 8;
                    value |= b & 0xFF;
                }
                return value;
            }
        }

        @Override
        public byte[] serialize(Short data) {
            if (data == null) {
                return null;
            } else {
                return new byte[] {
                        (byte) (data >>> 8),
                        data.byteValue()
                };
            }
        }

        @Override
        public boolean supports(Class<Short> type) {
            return type == Short.class;
        }
    }

    /**
     * Serializes/deserializes an {@link Integer}.
     */
    static class IntegerSerDes implements RabbitMessageSerDes<Integer> {

        @Override
        public Integer deserialize(RabbitConsumerState messageState, Class<Integer> type) {
            byte[] data = messageState.getBody();
            if (data == null) {
                return null;
            } else if (data.length != 4) {
                throw new SerializationException("Incorrect message body size to deserialize to an Integer");
            } else {
                int value = 0;
                for (byte b : data) {
                    value <<= 8;
                    value |= b & 0xFF;
                }
                return value;
            }
        }

        @Override
        public byte[] serialize(Integer data) {
            if (data == null) {
                return null;
            } else {
                return new byte[] {
                        (byte) (data >>> 24),
                        (byte) (data >>> 16),
                        (byte) (data >>> 8),
                        data.byteValue()
                };
            }
        }

        @Override
        public boolean supports(Class<Integer> type) {
            return type == Integer.class;
        }
    }

    /**
     * Serializes/deserializes a {@link Long}.
     */
    static class LongSerDes implements RabbitMessageSerDes<Long> {

        @Override
        public Long deserialize(RabbitConsumerState messageState, Class<Long> type) {
            byte[] data = messageState.getBody();
            if (data == null) {
                return null;
            } else if (data.length != 8) {
                throw new SerializationException("Incorrect message body size to deserialize to a Long");
            } else {
                long value = 0;
                for (byte b : data) {
                    value <<= 8;
                    value |= b & 0xFF;
                }
                return value;
            }
        }

        @Override
        public byte[] serialize(Long data) {
            if (data == null) {
                return null;
            } else {
                return new byte[]{
                        (byte) (data >>> 56),
                        (byte) (data >>> 48),
                        (byte) (data >>> 40),
                        (byte) (data >>> 32),
                        (byte) (data >>> 24),
                        (byte) (data >>> 16),
                        (byte) (data >>> 8),
                        data.byteValue()
                };
            }
        }

        @Override
        public boolean supports(Class<Long> type) {
            return type == Long.class;
        }
    }

    /**
     * Serializes/deserializes a {@link Float}.
     */
    static class FloatSerDes implements RabbitMessageSerDes<Float> {

        @Override
        public Float deserialize(RabbitConsumerState messageState, Class<Float> type) {
            byte[] data = messageState.getBody();
            if (data == null) {
                return null;
            } else if (data.length != 4) {
                throw new SerializationException("Incorrect message body size to deserialize to a Float");
            } else {
                int value = 0;
                for (byte b : data) {
                    value <<= 8;
                    value |= b & 0xFF;
                }
                return Float.intBitsToFloat(value);
            }
        }

        @Override
        public byte[] serialize(Float data) {
            if (data == null) {
                return null;
            } else {
                long bits = Float.floatToRawIntBits(data);
                return new byte[] {
                        (byte) (bits >>> 24),
                        (byte) (bits >>> 16),
                        (byte) (bits >>> 8),
                        (byte) bits
                };
            }
        }

        @Override
        public boolean supports(Class<Float> type) {
            return type == Float.class;
        }
    }

    /**
     * Serializes/deserializes a {@link Double}.
     */
    static class DoubleSerDes implements RabbitMessageSerDes<Double> {

        @Override
        public Double deserialize(RabbitConsumerState messageState, Class<Double> type) {
            byte[] data = messageState.getBody();
            if (data == null) {
                return null;
            } else if (data.length != 8) {
                throw new SerializationException("Incorrect message body size to deserialize to a Double");
            } else {
                long value = 0;
                for (byte b : data) {
                    value <<= 8;
                    value |= b & 0xFF;
                }
                return Double.longBitsToDouble(value);
            }
        }

        @Override
        public byte[] serialize(Double data) {
            if (data == null) {
                return null;
            } else {
                long bits = Double.doubleToLongBits(data);
                return new byte[] {
                        (byte) (bits >>> 56),
                        (byte) (bits >>> 48),
                        (byte) (bits >>> 40),
                        (byte) (bits >>> 32),
                        (byte) (bits >>> 24),
                        (byte) (bits >>> 16),
                        (byte) (bits >>> 8),
                        (byte) bits
                };
            }
        }

        @Override
        public boolean supports(Class<Double> type) {
            return type == Double.class;
        }
    }

    /**
     * Serializes/deserializes a byte[].
     */
    static class ByteArraySerDes implements RabbitMessageSerDes<byte[]> {

        @Override
        public byte[] deserialize(RabbitConsumerState messageState, Class<byte[]> type) {
            return messageState.getBody();
        }

        @Override
        public byte[] serialize(byte[] data) {
            return data;
        }

        @Override
        public boolean supports(Class<byte[]> type) {
            return type == byte[].class;
        }
    }

    /**
     * Serializes/deserializes a {@link ByteBuffer}.
     */
    static class ByteBufferSerDes implements RabbitMessageSerDes<ByteBuffer> {

        @Override
        public ByteBuffer deserialize(RabbitConsumerState messageState, Class<ByteBuffer> type) {
            byte[] data = messageState.getBody();
            if (data == null) {
                return null;
            } else {
                return ByteBuffer.wrap(data);
            }
        }

        @Override
        public byte[] serialize(ByteBuffer data) {
            if (data == null) {
                return null;
            } else {
                data.rewind();

                if (data.hasArray()) {
                    byte[] arr = data.array();
                    if (data.arrayOffset() == 0 && arr.length == data.remaining()) {
                        return arr;
                    }
                }

                byte[] ret = new byte[data.remaining()];
                data.get(ret, 0, ret.length);
                data.rewind();
                return ret;
            }
        }

        @Override
        public boolean supports(Class<ByteBuffer> type) {
            return type == ByteBuffer.class;
        }
    }

    /**
     * Serializes/deserializes a {@link UUID}.
     */
    static class UUIDSerDes implements RabbitMessageSerDes<UUID> {

        StringSerDes stringSerDes = new StringSerDes();

        @Override
        public UUID deserialize(RabbitConsumerState messageState, Class<UUID> type) {
            String uuid = stringSerDes.deserialize(messageState, String.class);
            if (uuid == null) {
                return null;
            } else {
                return UUID.fromString(uuid);
            }
        }

        @Override
        public byte[] serialize(UUID data) {
            if (data == null) {
                return null;
            } else {
                return stringSerDes.serialize(data.toString());
            }
        }

        @Override
        public boolean supports(Class<UUID> type) {
            return type == UUID.class;
        }
    }
}
