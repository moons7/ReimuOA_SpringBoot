package com.reimu.boot;


import com.reimu.shiro.SystemAuthorizingRealm;
import com.reimu.shiro.cache.RedisCacheManager;
import com.reimu.shiro.filter.CookieAuthenticationFilter;
import com.reimu.shiro.filter.KickoutSessionControlFilter;
import com.reimu.shiro.filter.ReimuAnonymousFilter;
import com.reimu.shiro.session.RedisSessionDAO;
import com.reimu.shiro.session.SessionManager;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionFactory;
import org.apache.shiro.session.mgt.SimpleSessionFactory;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Bean 不指定名字的话，自动创建一个方法名第一个字母小写的bean * @Bean(name = "securityManager")
 */
@Configuration
public class ShiroConfig {

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        //静态代理了ShiroFilterFactoryBean
        DelegatingFilterProxy delegatingFilterProxy = new DelegatingFilterProxy("shiroFilter");
        delegatingFilterProxy.setTargetFilterLifecycle(false);
        delegatingFilterProxy.setTargetBeanName("shiroFilter");
        FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
        filterRegistration.setFilter(delegatingFilterProxy);
        filterRegistration.setEnabled(true);
        filterRegistration.addUrlPatterns("/*");
        filterRegistration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ERROR, DispatcherType.FORWARD);
        return filterRegistration;
    }

    /**
     * Shiro的Web过滤器Factory 命名:shiroFilter<br /> * * @param securityManager * @return
     */
    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean shiroFilterFactoryBean(@Value("${project.authorize}") boolean authorize,
                                                         SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        //Shiro的核心安全接口,这个属性是必须的
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        //要求登录时的链接(可根据项目的URL进行替换),非必须的属性,默认会自动寻找Web工程根目录下的"/login.jsp"页面
        shiroFilterFactoryBean.setLoginUrl("/login");
        // 定义shiro过滤器,例如实现自定义的FormAuthenticationFilter，需要继承FormAuthenticationFilter */
        Map<String, Filter> filters = new LinkedHashMap<>();
        filters.put(CookieAuthenticationFilter.FILTER_NAME, new CookieAuthenticationFilter());
        filters.put(KickoutSessionControlFilter.FILTER_NAME, new KickoutSessionControlFilter());
        filters.put(ReimuAnonymousFilter.FILTER_NAME, new ReimuAnonymousFilter());
        shiroFilterFactoryBean.setFilters(filters);
        /*定义shiro过滤链 Map结构 * Map中key(xml中是指value值)的第一个'/'代表的路径是相对于HttpServletRequest.getContextPath()的值来的 * anon：它对应的过滤器里面是空的,什么都没做,这里.do和.jsp后面的*表示参数,比方说login.jsp?main这种 * authc：该过滤器下的页面必须验证后才能访问,它是Shiro内置的一个拦截器org.apache.com.reimu.shiro.web.filter.authc.CookieAuthenticationFilter */
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        // 配置退出过滤器,其中的具体的退出代码Shiro已经替我们实现了
        // <!-- 过滤链定义，从上向下顺序执行,一旦符合则停止匹配,一般将 /**放在最为下边 -->
        if (authorize) {
            filterChainDefinitionMap.put("/", ReimuAnonymousFilter.FILTER_NAME);
            filterChainDefinitionMap.put("/index.html", ReimuAnonymousFilter.FILTER_NAME);
            filterChainDefinitionMap.put("/web/**", ReimuAnonymousFilter.FILTER_NAME);  //放行 静态资源
            filterChainDefinitionMap.put("/login/logout", "logout");
//          filterChainDefinitionMap.put("/sys/user/", "kickout,cookieAuthFilter"); //TODO 优化判断绑定至登陆接口 防止恶意跳过此接口验证
            filterChainDefinitionMap.put("/**", CookieAuthenticationFilter.FILTER_NAME); // 为了性能,不在全局执行kickout逻辑
        } else {
            filterChainDefinitionMap.put("/**", ReimuAnonymousFilter.FILTER_NAME);
        }
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }


    @Bean
    public SessionManager sessionManager(RedisSessionDAO sessionDAO, SessionFactory sessionFactory) {
        SessionManager sessionManager = new SessionManager();
        sessionManager.setSessionDAO(sessionDAO);
        sessionManager.setGlobalSessionTimeout(1800000);
        sessionManager.setSessionValidationInterval(180000);
        sessionManager.setSessionValidationSchedulerEnabled(true);
        sessionManager.setSessionIdCookieEnabled(true);
        sessionManager.setSessionFactory(sessionFactory);
        SimpleCookie simpleCookie = new SimpleCookie("reimu.session.id");
        simpleCookie.setHttpOnly(false);//TODO 通过登录接口返回也可,设置为true更安全
        simpleCookie.setMaxAge(-1);//delete this cookie when browser be closed
        sessionManager.setSessionIdCookie(simpleCookie);
        return sessionManager;
    }

    @Bean
    public SessionFactory sessionFactory() {
        return new SimpleSessionFactory();
    }

    /**
     * 核心安全管理器
     */
    @Bean
    public SecurityManager securityManager(SessionManager sessionManager, CacheManager cacheManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        SystemAuthorizingRealm systemAuthorizingRealm = new SystemAuthorizingRealm();
        securityManager.setRealm(systemAuthorizingRealm);
        securityManager.setSessionManager(sessionManager);
        securityManager.setCacheManager(cacheManager);
        return securityManager;
    }

    /**
     * 核心安全管理器
     */
    @Bean
    public CacheManager cacheManager(RedisTemplate redisTemplate) {
        RedisCacheManager cacheManager = new RedisCacheManager();
        cacheManager.setRedisTemplate(redisTemplate);
        return cacheManager;
    }


    /**
     * 核心安全管理器
     */
    @Bean
    public RedisSessionDAO sessionDAO() {
        RedisSessionDAO sessionDAO = new RedisSessionDAO();
        sessionDAO.setSessionIdGenerator(new JavaUuidSessionIdGenerator());
        return sessionDAO;
    }

    /**
     * Shiro生命周期处理器
     */
    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * 开启Shiro的注解(如@RequiresRoles,@RequiresPermissions),需借助SpringAOP扫描使用Shiro注解的类,并在必要时进行安全逻辑验证 * 配置以下两个bean(DefaultAdvisorAutoProxyCreator(可选)和AuthorizationAttributeSourceAdvisor)即可实现此功能 * @return
     */
    @Bean
    @DependsOn({"lifecycleBeanPostProcessor"})
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }
}
