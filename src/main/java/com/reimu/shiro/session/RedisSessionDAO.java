package com.reimu.shiro.session;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.CachingSessionDAO;
import org.springframework.stereotype.Component;
import com.reimu.util.EmptyUtils;
import com.reimu.util.ServletsUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
 * 自定义授权会话管理类 主要使用Redis的hashMap缓存对象
 *
 * @author CX
 * @note 所有的cacheKeyName 使用StringUtils 转换为byte[] 进行操作
 * 所有的key与value,使用ObjectUtils 序列化为byte[] 进行操作
 * 避免使用redis的String类型相关接口
 * @attention 该类是session为本系统自定义的ReimuSession
 */
@Component
public class RedisSessionDAO extends CachingSessionDAO {

    private Cache cache() {
        Cache<Object, Object> cache = getCacheManager().getCache(this.getClass().getName());
        return cache;
    }


    @Override
    public void update(Session session){
        HttpServletRequest request = ServletsUtils.getRequest();
        if (!EmptyUtils.isEmpty(request)&&ServletsUtils.isStaticFile(request.getServletPath())) {
            return;
        }
        super.update(session);
    }

    @Override
    protected void doUpdate(Session session) {
    }

    @Override
    protected void doDelete(Session session) {
        cache().remove(session.getId().toString());
    }


    @Override
    protected Serializable doCreate(Session session) {
        HttpServletRequest request = ServletsUtils.getRequest();
        if (request != null) {
            String uri = request.getServletPath();
            if (ServletsUtils.isStaticFile(uri)) {
                return null;
            }
        }
        Serializable sessionId = this.generateSessionId(session);
        this.assignSessionId(session, sessionId);
        this.update(session);
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable serializable) {
            return super.getCachedSession(serializable);
    }

}
