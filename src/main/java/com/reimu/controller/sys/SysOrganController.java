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
import com.reimu.dao.pojo.sys.SysOrgEnt;
import com.reimu.service.sys.SysOrganService;
import com.reimu.shiro.SecurityHelper;
import com.reimu.shiro.service.ShiroUserInfoService;

import javax.validation.Valid;
import java.util.List;


@Controller
@RequestMapping(value = "/sys/organ")
@Validated
@Api(tags = "用户组织")
public class SysOrganController {

    @Autowired
    private SysOrganService sysOrganService;


    /**
     * 获取该部门下的子部门，并不包括子部门的下一级
     *
     * @param parentid
     * @return
     */
    @ResponseBody
    @RequiresAuthentication
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public HttpDataContainer organlist(@RequestParam Integer parentid) {
        List<?> list = sysOrganService.selectSysOrgList(parentid);
        return HttpDataContainer.create(HttpDataContainer.STATUS_SUCCESS, list);
    }

    @ResponseBody
    @RequiresAuthentication
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public HttpDataContainer user(@Valid @ModelAttribute ListQueryModel listQueryModel,
                              @RequestParam Integer organid) {
        Object pageInfoContainer = sysOrganService.selectOrgUserList(listQueryModel.getPage(), listQueryModel.getSize(), organid);
        return HttpDataContainer.create(HttpDataContainer.STATUS_SUCCESS, pageInfoContainer);
    }


    @ResponseBody
    @RequiresAuthentication
    @RequestMapping(value = "/user/notin", method = RequestMethod.GET)
    public HttpDataContainer notin(@Valid @ModelAttribute ListQueryModel listQueryModel,
                               @RequestParam Integer organid) {
        Object pageInfoContainer = sysOrganService.selectOrgUserListNotInOrgan(listQueryModel.getPage(), listQueryModel.getSize(), organid);
        return HttpDataContainer.create(HttpDataContainer.STATUS_SUCCESS, pageInfoContainer);
    }


    @ResponseBody
    @RequiresAuthentication
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public HttpDataContainer add(@RequestParam Integer parentid,
                             @RequestParam String organname,
                             @RequestParam(required = false) String organdes) {
        int rootID = 0;
        if (parentid > 0) {
            rootID = ShiroUserInfoService.findRootOrganByOrganID(parentid);
            if(rootID ==0)  return HttpDataContainer.create(-1, "禁止自主添加一级部门");
            rootID = rootID == 0 ? parentid : rootID; //rootID为0表示自身为第一级根节点
            if (rootID == RoleStatus.ORGAN_ADMIN) {
                return HttpDataContainer.create(-1, "该部门禁止自主添加");
            }
        }
        if(sysOrganService.checkOrganIsReplayName(parentid,organname)){
            return HttpDataContainer.create(-1, "同级别已有同名部门！");
        }
        SysOrgEnt ent = sysOrganService.addOrgan(parentid, organname, rootID, organdes);
        return HttpDataContainer.create(HttpDataContainer.STATUS_SUCCESS, ent.getOrganizationId());
    }

    /**
     * 部门添加用户
     *
     * @param userid
     * @param organid
     * @return
     */
    @ResponseBody
    @RequiresRoles({RoleStatus.ADMIN})
    @RequestMapping(value = "/user/add", method = RequestMethod.POST)
    public HttpDataContainer addUser(@RequestParam Integer userid,
                                 @RequestParam Integer organid) {
        if (sysOrganService.checkUserIsInMultipleOrgan(userid)) {
            return HttpDataContainer.create(-1, "禁止用户进入多个部门");
        }
        sysOrganService.addOrganUser(userid, organid);
        SecurityHelper.deleteAllUserAuthorizationInfo(); // 更新授权缓存
        return HttpDataContainer.create(HttpDataContainer.STATUS_SUCCESS);
    }

    /**
     * 部门删除用户
     *
     * @param userid
     * @param organid
     * @return
     */
    @ResponseBody
    @RequiresRoles({RoleStatus.ADMIN})
    @RequestMapping(value = "/user/del", method = RequestMethod.POST)
    public HttpDataContainer delUser(@RequestParam Integer userid,
                                 @RequestParam Integer organid) {
        sysOrganService.delOrganUser(userid);
        SecurityHelper.deleteAllUserAuthorizationInfo();// 更新授权缓存
        return HttpDataContainer.create(HttpDataContainer.STATUS_SUCCESS);
    }

    /**
     * 删除部门
     *
     * @param organid
     * @return
     */
    @ResponseBody
    @RequiresRoles({RoleStatus.ADMIN})
    @RequestMapping(value = "/del", method = RequestMethod.POST)
    public HttpDataContainer del(@RequestParam Integer organid) {
        if (sysOrganService.checkOrganIsSystem(organid)) {
            return HttpDataContainer.create(-2, "该部门由于系统保留,无法更改");
        }
        boolean success = sysOrganService.delOrgan(organid);
        return HttpDataContainer.create(success ? HttpDataContainer.STATUS_SUCCESS : -1, success ? "" : "删除失败");
    }

    /**
     * 更新部门信息
     *
     * @param organid
     * @param organname
     * @param organdes
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public HttpDataContainer update(@RequestParam Integer organid,
                                @RequestParam String organname,
                                @RequestParam(required = false) String organdes) {
        if (sysOrganService.checkOrganIsSystem(organid)) {
            return HttpDataContainer.create(-2, "该部门由于系统保留,无法更改");
        }
        sysOrganService.updateOrgan(organid, organname, organdes);
        return HttpDataContainer.create(HttpDataContainer.STATUS_SUCCESS);
    }

    /**
     * 更新部门人员的角色信息
     * @param organid
     * @param userid
     * @param roles
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/role/update", method = RequestMethod.POST)
    public HttpDataContainer updateOrganAuth(@RequestParam Integer organid,
                                         @RequestParam Integer userid,
                                         @RequestParam(required = false) Integer roles) {
        sysOrganService.updateOrganUserRoles(userid,organid,roles);
        SecurityHelper.deleteAllUserAuthorizationInfo();// 更新授权缓存
        return HttpDataContainer.create(HttpDataContainer.STATUS_SUCCESS);
    }


}
