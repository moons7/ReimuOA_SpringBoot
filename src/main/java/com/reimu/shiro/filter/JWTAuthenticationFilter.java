package com.reimu.shiro.filter;


import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.pam.UnsupportedTokenException;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.reimu.shiro.UsernamePasswordToken;
import com.reimu.util.ServletsUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * JSON Web Token (See https://tools.ietf.org/html/rfc7519 for more information)
 * Only use Http header for authentication,can avoid for CSRF attack
 */
public class JWTAuthenticationFilter extends org.apache.shiro.web.filter.authc.FormAuthenticationFilter {

    private static final Logger logger = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    public static final String FILTER_NAME = "JWTAuthFilter";
    public static final String DEFAULT_CAPTCHA_PARAM = "validateCode";
    public static final String DEFAULT_MESSAGE_PARAM = "message";

    private String captchaParam = DEFAULT_CAPTCHA_PARAM;
    private String messageParam = DEFAULT_MESSAGE_PARAM;

    //@NOTE:与 @link SystemAuthorizingRealm中的doGetAuthenticationInfo 返回的Token进行比对 成功则进入下一流程 失败则抛出异常并调用本类的onLoginFailure 函数
    //错误异常由本父类的 DEFAULT_ERROR_KEY_ATTRIBUTE_NAME 定义
    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
        String username = getUsername(request);
        String password = getPassword(request);
        boolean rememberMe = isRememberMe(request);
        String host = ServletsUtils.getRemoteAddr((HttpServletRequest) request);
        String captcha = getCaptcha(request);
        return new UsernamePasswordToken(username, password, rememberMe, host, captcha);

    }



    protected String getCaptcha(ServletRequest request) {
        return WebUtils.getCleanParam(request, captchaParam);
    }



    /**
     * @param request
     * @param response
     */
    @Override
    protected void redirectToLogin(ServletRequest request, ServletResponse response){
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
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse arg1, Object arg2)  {
        boolean accessAllowed = false;
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String jwt = httpRequest.getHeader("Authorization");
        if (jwt == null || !jwt.startsWith("Bearer ")) {
            return accessAllowed;
        }
        jwt = jwt.substring(jwt.indexOf(" "));
//        String username = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary("secret"))
//                .parseClaimsJws(jwt).getBody().getSubject();
        String username="test";
        String subjectName = (String) SecurityUtils.getSubject().getPrincipal();
        if (username.equals(subjectName)) {
            accessAllowed = true;
        }
        return accessAllowed;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest arg0, ServletResponse arg1) {
        HttpServletResponse response = (HttpServletResponse) arg1;
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return false;
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
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        } else {
            message = "系统出现问题，请稍后再试！";
        }
            request.setAttribute(getFailureKeyAttribute(), className);
            request.setAttribute(captchaParam, message);
            return true;
        }
}