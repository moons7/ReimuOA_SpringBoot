package com.reimu.boot;


import com.reimu.base.serialize.ReimuRedisSerializer;
import com.reimu.util.EmptyUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @Bean 不指定名字的话，自动创建一个方法名第一个字母小写的bean * @Bean(name = "securityManager")
 */
@Configuration
public class RedisConfig {

    @Bean(name = "redisTemplate")
    public RedisTemplate redisTemplate(@Value("${project.redis.host}") String host,
                                       @Value("${project.redis.password}") String password,
                                       @Value("${project.redis.port}") Integer port) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setDatabase(8);
        config.setPort(port);
        config.setHostName(host);
        if(!EmptyUtils.isEmpty(password)) config.setPassword(password);
        LettuceConnectionFactory factory = new LettuceConnectionFactory(config);
        factory.setTimeout(10000);
        factory.afterPropertiesSet(); // 如果不是由spring自动装配必须显式调用该函数,由InitializingBean接口继承
        return buildRedisTemplate(factory);
    }

    protected RedisTemplate buildRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate template = new RedisTemplate();
        template.setConnectionFactory(connectionFactory);
        ReimuRedisSerializer reimuRedisSerializer = new ReimuRedisSerializer();
        template.setValueSerializer(reimuRedisSerializer);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(reimuRedisSerializer);
        return template;
    }

}
