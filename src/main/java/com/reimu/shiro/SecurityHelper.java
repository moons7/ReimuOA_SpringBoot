package com.reimu.shiro;


import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.reimu.dao.interMapper.sys.SysRoleEntMapper;
import com.reimu.dao.pojo.sys.SysRoleEnt;
import com.reimu.shiro.principal.Principal;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * 用户常用辅助类，与Shiro相关
 */
@Component
public class SecurityHelper {

    private static final Logger log = LoggerFactory.getLogger(SecurityHelper.class);

    private static CacheManager cacheManager;

    private static SysRoleEntMapper sysRoleEntMapper;

    @Autowired
    public void setCacheManager(CacheManager cacheManager) {
        SecurityHelper.cacheManager = cacheManager;
    }

    @Autowired
    public void setSysRoleEntMapper(SysRoleEntMapper sysRoleEntMapper) {
        SecurityHelper.sysRoleEntMapper = sysRoleEntMapper;
    }


    /**
     * 获取Shiro中授权的主要对象
     */
    public static Subject getSubject() {
        return SecurityUtils.getSubject();
    }

    /**
     * 获取当前登录者对象
     */
    public static Optional<Principal> getPrincipal() {
        Subject subject = SecurityUtils.getSubject();
        Principal principal = (Principal) subject.getPrincipal();
        return Optional.ofNullable(principal);
    }

    /**
     * Shiro中获取的session等同于Http servlet中获取的session，该session的ID同时被写入cookie中
     *
     * @return Session
     */
    public static Session getSession(boolean create) {
        try {
            Subject subject = SecurityUtils.getSubject();
            Session session = subject.getSession(create);
            if (session == null) {
                session = subject.getSession();
            }
            return session;
        } catch (InvalidSessionException e) {
            if (log.isErrorEnabled()) {
                log.error("Function of Get Session occurred a error : " + e.getMessage());
            }
        }
        return null;
    }

    public static List<String> getRole() {
        int userID = SecurityHelper.getUserID();
        List<String> roles = new ArrayList<>();
        List<SysRoleEnt> roleEntList = sysRoleEntMapper.selectRolesByUserID(userID);
        if (roleEntList != null) {
            for (SysRoleEnt ent : roleEntList) {
                roles.add(ent.getRoleValue());
            }
        }
        if (roles.isEmpty()) {
            roles.add("none");
        }
        return roles;
    }

    public static int getOrganID() {
        Optional<Principal> principal = SecurityHelper.getPrincipal();
        if (principal.isPresent()) {
            return principal.get().getOrganid();
        }
        throw new UnauthorizedException();
    }

    public static int getUserID() {
        Optional<Principal> principal = SecurityHelper.getPrincipal();
        if (principal.isPresent()) {
            return principal.get().getUid();
        }
        throw new UnauthorizedException();
    }

    //todo Fix Hard Code
    public static void deleteAllUserAuthorizationInfo() {
        cacheManager.getCache("org.reimu.shiro.SystemAuthorizingRealm.authorizationCache").clear();
    }
}
