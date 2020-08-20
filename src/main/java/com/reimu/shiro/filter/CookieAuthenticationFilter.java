package com.reimu.shiro.filter;


import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.pam.UnsupportedTokenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.reimu.base.response.HttpDataContainer;
import com.reimu.helper.response.ResponseHelper;
import com.reimu.shiro.SecurityHelper;
import com.reimu.shiro.UsernamePasswordToken;
import com.reimu.util.ServletsUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * Override method @redirectToLogin and @issueSuccessRedirect to cancel page redirect and only return Json data
 * it also could be rewritten for validation page by using redirect function
 * or can use org.apache.com.reimu.shiro.web.filter.authc.FormAuthenticationFilter directly
 */
public class CookieAuthenticationFilter extends org.apache.shiro.web.filter.authc.FormAuthenticationFilter {

    private static final Logger logger = LoggerFactory.getLogger(CookieAuthenticationFilter.class);

    public static final String FILTER_NAME = "cookieAuthFilter";
    public static final String DEFAULT_MESSAGE_PARAM = "message";


    //@NOTE:与 @link SystemAuthorizingRealm中的doGetAuthenticationInfo 返回的Token进行比对 成功则进入下一流程 失败则抛出异常并调用本类的onLoginFailure 函数
    //错误异常由本父类的 DEFAULT_ERROR_KEY_ATTRIBUTE_NAME 定义
    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
        String username = getUsername(request);
        String password = getPassword(request);
        boolean rememberMe = isRememberMe(request);
        String host = ServletsUtils.getRemoteAddr((HttpServletRequest) request);
        //TODO 验证码逻辑
        return new UsernamePasswordToken(username, password, rememberMe, host,null);
    }

    /**
     * @param request
     * @param response
     */
    @Override
    protected void redirectToLogin(ServletRequest request, ServletResponse response) {
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    /**
     * @param request
     * @param response
     */
    @Override
    protected void issueSuccessRedirect(ServletRequest request,
                                        ServletResponse response) {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        //已经在这里生成Cookie id
        ResponseHelper.createBody(httpServletResponse).createDataContainer(HttpDataContainer.create(0, SecurityHelper.getSession(true).getId())).printOut();
    }

    /**
     * 只有在Realm中失败抛出异常后该事件才会被调用
     */
    @Override
    protected boolean onLoginFailure(AuthenticationToken token,
                                     AuthenticationException exception, ServletRequest request, ServletResponse response) {
        String className = exception.getClass().getName(), message;
        if (IncorrectCredentialsException.class.getName().equals(className)
                || UnknownAccountException.class.getName().equals(className)) {
            message = "用户或密码错误, 请重试.";
        } else if (UnsupportedTokenException.class.getName().equals(className)) {
            message = "请输入正确的验证码";
        } else {
            message = "系统出现问题，请稍后再试！";
        }
        request.setAttribute(getFailureKeyAttribute(), className);
        request.setAttribute(DEFAULT_MESSAGE_PARAM, message);
        return true;
    }





    @Override
    public void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
//        httpServletResponse.addHeader("Access-Control-Allow-Origin", "*");
//        httpServletResponse.addHeader("Access-Control-Allow-Headers", "*");
//        httpServletResponse.addHeader("Access-Control-Allow-Credentials","true");
        httpServletResponse.addHeader("X-XSS-Protection", "1; mode=block");
        httpServletResponse.addHeader("X-Content-Type-Options", "nosniff");
        httpServletResponse.addHeader("X-Frame-Options", "SAMEORIGIN");
        httpServletResponse.addHeader("If-Modified-Since","0"); //防止IE缓存AJAX结果
        httpServletResponse.addHeader("Cache-Control","no-cache");
        httpServletResponse.setCharacterEncoding("UTF-8");
        super.doFilterInternal(request, response, chain);
    }



}