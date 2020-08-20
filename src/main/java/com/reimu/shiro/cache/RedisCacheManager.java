

package com.reimu.shiro.cache;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.Set;

/**
 * @attion 只针对授权
 * 自定义授权缓存管理类,使用Redis的hashMap缓存对象
 * @note 对授权信息进行缓存(鉴权无信息缓存) 实现在 @link JedisSessionDao
 *       所有的cacheKeyName 使用StringUtils 转换为byte[] 进行操作
 *       所有的key与value,使用ObjectUtils 序列化为byte[] 进行操作
 *       避免使用redis的String类型相关接口
 */
@SuppressWarnings("unchecked")
public class RedisCacheManager implements CacheManager {

    @Override
    public <K, V> Cache<K, V> getCache(String name) {
        return new SessionCache(cacheKeyPrefix + name);
    }

    private static final String cacheKeyPrefix = "shiro_cache_";

    private static final Logger logger = LoggerFactory.getLogger(RedisCacheManager.class);

    private RedisTemplate redisTemplate;

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 自定义授权缓存管理类
     *
     * @author ThinkGem
     * @version 2014-7-20
     */
    public class SessionCache<K, V> implements Cache<K, V> {

        private String cacheKeyName;

        public SessionCache(String cacheKeyName) {
            this.cacheKeyName = cacheKeyName;
        }


        @Override
        public V get(K key) throws CacheException {
            return  (V)redisTemplate.opsForHash().get(cacheKeyName,key);
        }

        @Override
        public V put(K key, V value) throws CacheException {
            redisTemplate.opsForHash().put(cacheKeyName,key,value);
            return value;
        }


        @Override
        public V remove(K key) throws CacheException {
            try {
                redisTemplate.opsForHash().delete(cacheKeyName,key);
            }catch (Exception e){
                logger.error("Shiro 删除发生错误.");
            }
            return null;
        }

        @Override
        public void clear() throws CacheException {
                redisTemplate.delete(cacheKeyName);
        }

        @Override
        public int size() {
           return redisTemplate.opsForHash().size(cacheKeyName).intValue();
        }


        @Override
        public Set<K> keys() {
            return  redisTemplate.opsForHash().keys(cacheKeyName);
        }


        @Override
        public Collection<V> values() {
            return  redisTemplate.opsForHash().values(cacheKeyName);
        }
    }
}
