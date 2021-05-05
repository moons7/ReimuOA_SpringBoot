package com.reimu.base.serialize;

import com.reimu.util.ObjectUtils;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

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
