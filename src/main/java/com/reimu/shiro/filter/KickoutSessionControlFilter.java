package com.reimu.shiro.filter;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.reimu.shiro.principal.Principal;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.Serializable;
import java.util.Collection;


/**
 * Override method @redirectToLogin and @issueSuccessRedirect to cancel page redirect and only return Json data
 * it also could be rewritten for validation page by using redirect function
 * or can use org.apache.com.reimu.shiro.web.filter.authc.FormAuthenticationFilter directly
 */
public class KickoutSessionControlFilter extends org.apache.shiro.web.filter.authc.FormAuthenticationFilter {

    private static final Logger logger = LoggerFactory.getLogger(KickoutSessionControlFilter.class);

    public static final String FILTER_NAME = "kickout";


    //保证总的执行链不中断
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        return false;
    }

    private boolean kickoutBefore = true; //踢出之前登录的/之后登录的用户 默认踢出之前登录的用户

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) {
        Subject subject = getSubject(request, response);
        if (!subject.isAuthenticated() && !subject.isRemembered()) {
            //如果没有登录，直接进行之后的流程
            return true;
        }
        Session userSession = subject.getSession();
        Principal user = (Principal) subject.getPrincipal();
        Serializable sessionId = userSession.getId();
        SessionDAO sessionDAO = ((DefaultSessionManager) ((DefaultSecurityManager) SecurityUtils
                .getSecurityManager()).getSessionManager()).getSessionDAO();
        Collection<Session> sessionsCollection = sessionDAO.getActiveSessions();
        sessionsCollection.stream().forEach(session -> {
            SimpleSession simpleSession = (SimpleSession) session;
            Object AUTHENTICATED = simpleSession.getAttribute(DefaultSubjectContext.AUTHENTICATED_SESSION_KEY);
            if (AUTHENTICATED == null) return;
            if (!(boolean) AUTHENTICATED) return;
            if (kickoutBefore && sessionId.equals(simpleSession.getId())) {
                return;
            }
            SimplePrincipalCollection principalCollection = (SimplePrincipalCollection) simpleSession.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
            Principal principal = (Principal) principalCollection.getPrimaryPrincipal();
            if (user.getUid() == principal.getUid()) {
                simpleSession.setTimeout(0);
                sessionDAO.update(simpleSession);
            }
        });
        return true;
    }
}