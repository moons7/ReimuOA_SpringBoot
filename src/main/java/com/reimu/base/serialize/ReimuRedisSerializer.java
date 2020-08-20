package com.reimu.base.serialize;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import com.reimu.util.ObjectUtils;

public class ReimuRedisSerializer implements RedisSerializer<Object> {

    @Override
    public byte[] serialize(Object o) throws SerializationException {
        return ObjectUtils.serializeToByte(o);
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        return ObjectUtils.unSerialize(bytes);
    }
}
