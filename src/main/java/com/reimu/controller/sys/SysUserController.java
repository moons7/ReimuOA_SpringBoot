package com.reimu.controller.sys;


import io.swagger.annotations.Api;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.reimu.helper.response.HttpDataContainer;
import com.reimu.constant.RoleStatus;
import com.reimu.dao.dto.ListQueryModel;
import com.reimu.dao.pojo.sys.SysUserEnt;
import com.reimu.service.sys.SysOrganService;
import com.reimu.shiro.SecurityHelper;
import com.reimu.shiro.principal.Principal;
import com.reimu.shiro.service.ShiroUserInfoService;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(value = "/sys/user")
@Validated
@Api(tags = "用户个人")
public class SysUserController {

    @Autowired
    private ShiroUserInfoService shiroUserInfoService;

    @Autowired
    private SysOrganService sysOrganService;


    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public HttpDataContainer userInfo() {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("roles", SecurityHelper.getRole());
        return HttpDataContainer.create(HttpDataContainer.STATUS_SUCCESS, dataMap);
    }

    @RequiresRoles({RoleStatus.ADMIN})
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public HttpDataContainer userList(@Valid @ModelAttribute ListQueryModel listQueryModel) {
        return HttpDataContainer.create(HttpDataContainer.STATUS_SUCCESS,
                shiroUserInfoService.getUserList(listQueryModel.getPage(), listQueryModel.getSize()));
    }

    @RequiresRoles({RoleStatus.ADMIN})
    @RequestMapping(value = "/password/update", method = RequestMethod.POST)
    public HttpDataContainer updatePassword(@Valid @ModelAttribute SysUserEnt sysUserEnt) {
        shiroUserInfoService.updatePassword(sysUserEnt);
        return HttpDataContainer.create(HttpDataContainer.STATUS_SUCCESS);
    }

    @RequiresAuthentication
    @RequestMapping(value = "/password/ownupdate", method = RequestMethod.POST)
    public HttpDataContainer updateOwnPassword(@RequestParam String oldpwd,
                                           @RequestParam String newpwd) {
        Optional<Principal> principal = SecurityHelper.getPrincipal();
        if (principal.isPresent()) {
            return HttpDataContainer.create(HttpDataContainer.STATUS_SUCCESS, shiroUserInfoService.updatePassword(principal.get().getUid(), oldpwd, newpwd));
        } else {
            return HttpDataContainer.create(-1, "非法操作");
        }
    }

    @RequiresRoles({RoleStatus.ADMIN})
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public HttpDataContainer createUser(@Valid @ModelAttribute SysUserEnt sysUserEnt) {
        String username = sysUserEnt.getUsername().toLowerCase().trim();
        sysUserEnt.setUsername(username);
        if (shiroUserInfoService.checkReplyUserName(username, 0)) {
            return HttpDataContainer.create(-1, "已经使用的用户名");
        }
        shiroUserInfoService.createUser(sysUserEnt);
        return HttpDataContainer.create(HttpDataContainer.STATUS_SUCCESS);
    }

    @RequiresRoles({RoleStatus.ADMIN})
    @RequestMapping(value = "/create/and/organ", method = RequestMethod.POST)
    public HttpDataContainer createUser(@Valid @ModelAttribute SysUserEnt sysUserEnt,
                                    @RequestParam Integer organID,
                                    @RequestParam(required = false) Integer roles) {
        String username = sysUserEnt.getUsername().toLowerCase().trim();
        sysUserEnt.setUsername(username);
        if (shiroUserInfoService.checkReplyUserName(username, 0)) {
            return HttpDataContainer.create(-1, "已经使用的用户名");
        }
        sysUserEnt.setOrganizationId(organID);
        shiroUserInfoService.createUser(sysUserEnt);
        sysOrganService.updateOrganUserRoles(sysUserEnt.getUserId(),organID,roles);
        SecurityHelper.deleteAllUserAuthorizationInfo(); // 更新授权缓存
        return HttpDataContainer.create(HttpDataContainer.STATUS_SUCCESS);
    }


    /**
     * 管理员更新用户资料
     * @param sysUserEnt
     * @return
     */
    @RequiresRoles({RoleStatus.ADMIN})
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public HttpDataContainer updateUser(@Valid @ModelAttribute SysUserEnt sysUserEnt) {
        sysUserEnt.setUsername(sysUserEnt.getUsername().toLowerCase().trim());
        if (shiroUserInfoService.checkReplyUserName(sysUserEnt.getUsername(), sysUserEnt.getUserId())) {
            return HttpDataContainer.create(-1, "已经使用的用户名");
        }
        sysUserEnt.setPassword(null); //手动清空防止自动注入
        shiroUserInfoService.updateUser(sysUserEnt);
        return HttpDataContainer.create(HttpDataContainer.STATUS_SUCCESS);
    }

    /**
     * 用户自我更新资料
     * @param realname
     * @return
     */
    @RequiresAuthentication
    @RequestMapping(value = "/update/own", method = RequestMethod.POST)
    public HttpDataContainer updateOwn(@RequestParam String realname) {
        int userID = SecurityHelper.getUserID();
        if (userID == -1) {
            return HttpDataContainer.create(-1, "非法参数");
        }
        SysUserEnt sysUserEnt = new SysUserEnt();
        sysUserEnt.setUserId(userID);
        sysUserEnt.setRealname(realname.trim());
        shiroUserInfoService.updateUser(sysUserEnt);
        return HttpDataContainer.create(HttpDataContainer.STATUS_SUCCESS);
    }


    @RequiresRoles({RoleStatus.ADMIN})
    @RequestMapping(value = "/del", method = RequestMethod.POST)
    public HttpDataContainer deleteUser(@Valid @ModelAttribute SysUserEnt sysUserEnt) {
        shiroUserInfoService.delUser(sysUserEnt);
        return HttpDataContainer.create(HttpDataContainer.STATUS_SUCCESS);
    }
}
