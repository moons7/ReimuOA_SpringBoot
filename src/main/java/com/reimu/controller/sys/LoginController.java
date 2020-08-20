package com.reimu.controller.sys;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.reimu.helper.response.HttpDataContainer;
import com.reimu.shiro.SecurityHelper;
import com.reimu.shiro.filter.CookieAuthenticationFilter;
import com.reimu.util.StringUtils;

import javax.servlet.http.HttpServletRequest;


/**
 * 路由控制由前端完成,后端shiro只负责认证与授权,只返回JSON字符串与cookie
 */
@Controller
public class LoginController {

    /**
     * 登录失败，真正登录的POST请求由Filter完成
     */
    @ResponseBody
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public HttpDataContainer login(HttpServletRequest request) {
        //  错误直接由shiro抛出,如果第一次认证正确并不会进入该流程*/
       String exception = (String) request
               .getAttribute(CookieAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);
        String message = (String) request
                .getAttribute(CookieAuthenticationFilter.DEFAULT_MESSAGE_PARAM);
        return HttpDataContainer.create(StringUtils.isBlank(exception) ? HttpDataContainer.STATUS_SUCCESS : -1,message);
    }

    /**
     * 后台登出
     *
     */
    @ResponseBody
    @RequestMapping(value = "/login/logout", method = RequestMethod.GET)
    public HttpDataContainer logout()  {
        SecurityHelper.getSubject().logout();
        return HttpDataContainer.create(HttpDataContainer.STATUS_SUCCESS);
    }

}
