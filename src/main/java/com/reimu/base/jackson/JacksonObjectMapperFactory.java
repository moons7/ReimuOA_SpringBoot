package com.reimu.base.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonObjectMapperFactory {

    private volatile static ObjectMapper objectMapper;

    private JacksonObjectMapperFactory (){}

    public static ObjectMapper getSingleton() {
        if (objectMapper == null) {
            synchronized (ObjectMapper.class) {
                if (objectMapper == null) {
                    objectMapper = new ObjectMapper();
                }
            }
        }
        return objectMapper;
    }
}
