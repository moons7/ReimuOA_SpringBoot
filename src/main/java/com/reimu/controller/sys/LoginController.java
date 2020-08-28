package com.reimu.controller.sys;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.reimu.helper.response.HttpDataContainer;
import com.reimu.shiro.SecurityHelper;
import com.reimu.shiro.filter.CookieAuthenticationFilter;
import com.reimu.util.StringUtils;

import javax.servlet.http.HttpServletRequest;


/**
 * 路由控制由前端完成,后端shiro只负责认证与授权,只返回JSON字符串与cookie
 */
@RestController
@Api(tags = "用户登录与注销")
public class LoginController {

    /**
     * 登录失败，真正登录的POST请求由Filter完成
     */
    @ApiOperation("用户登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username",value = "用户名",paramType = "query",dataType = "String",required = true),
            @ApiImplicitParam(name = "password",value = "密码",paramType = "query",dataType = "String",required = true)
    })
    @PostMapping("/login")
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
    @GetMapping("/login/logout")
    public HttpDataContainer logout()  {
        SecurityHelper.getSubject().logout();
        return HttpDataContainer.create(HttpDataContainer.STATUS_SUCCESS);
    }

}
