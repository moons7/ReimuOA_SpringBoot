package com.reimu.service.sys;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.reimu.dao.interMapper.sys.SysOrgEntMapper;
import com.reimu.dao.interMapper.sys.SysRoleEntMapper;
import com.reimu.dao.interMapper.sys.SysUser2RoleEntMapper;
import com.reimu.dao.interMapper.sys.SysUserEntMapper;
import com.reimu.dao.pojo.sys.SysOrgEnt;
import com.reimu.dao.pojo.sys.SysRoleEnt;
import com.reimu.dao.pojo.sys.SysUser2RoleEnt;
import com.reimu.dao.pojo.sys.SysUserEnt;
import com.reimu.util.EmptyUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class SysOrganService {

    @Autowired
    private SysOrgEntMapper sysOrgEntMapper;

    @Autowired
    private SysUserEntMapper sysUserEntMapper;

    @Autowired
    private SysRoleEntMapper sysRoleEntMapper;

    @Autowired
    private SysUser2RoleEntMapper sysUser2RoleEntMapper;

    @Transactional
    public void addOrganUser(int userID, int organID) {
        var userEnt = new SysUserEnt();
        userEnt.setOrganizationId(organID);
        userEnt.setUserId(userID);
        sysUserEntMapper.updateById(userEnt);
    }

    @Transactional
    public void delOrganUser(int userID) {
        var userEnt = new SysUserEnt();
        userEnt.setOrganizationId(0);
        userEnt.setUserId(userID);
        sysUserEntMapper.updateById(userEnt);
    }

    @Transactional(readOnly = true)
    public List<?> selectSysOrgList(int parentID) {
        List<?> list;
        list = sysOrgEntMapper.selectSysOrgList(parentID);
        return list;
    }


    @Transactional(readOnly = true)
    public Object selectOrgUserList(int pageIndex, int pageSize, int organID) {
        var page = new Page<SysUserEnt>(pageIndex, pageSize);
        IPage<Map<String, Object>> usersList;
        if (organID > 0) {
            usersList = sysOrgEntMapper.selectSysUserByOrganID(organID, page);
        } else {
            usersList = sysOrgEntMapper.selectSysUserByALLOrgan(page);
        }
        for (Map<String, Object> userMap : usersList.getRecords()) {
            List<SysRoleEnt> sysRoleEnts = sysRoleEntMapper.selectRolesByUserID(((Long) userMap.get("user_id")).intValue());
            userMap.put("roles", sysRoleEnts);
        }
        return usersList;
    }

    public Object selectOrgUserListNotInOrgan(int pageIndex, int pageSize, int organID) {
        var page = new Page<SysUserEnt>(pageIndex, pageSize);
        return sysOrgEntMapper.selectUserNotInOrgan(organID, page);
    }


    @Transactional(readOnly = true)
    public String getOrganName(int organID) {
        return sysOrgEntMapper.selectById(organID).getOrganizationName();
    }

    @Transactional(readOnly = true)
    public boolean checkOrganIsSystem(int organID) {
        return sysOrgEntMapper.selectById(organID).getIsSystem();
    }


    @Transactional
    public void updateOrgan(int organID, String organName, String organDes) {
        SysOrgEnt ent = sysOrgEntMapper.selectById(organID);
        ent.setOrganizationName(organName);
        ent.setDescription(organDes);
        sysOrgEntMapper.updateById(ent);
    }

    @Transactional
    public SysOrgEnt addOrgan(int parentID, String organName, int rootID, String organDes) {
        SysOrgEnt ent = new SysOrgEnt();
        ent.setOrganizationName(organName);
        ent.setDescription(organDes);
        ent.setParentId(parentID);
        ent.setRootId(rootID);
        ent.setHasChildren(false);
        ent.setCreateTime(new Date());
        ent.setIsSystem(false);
        ent.setPathLength(0);
        sysOrgEntMapper.insert(ent);
        return ent;
    }

    @Transactional
    public boolean delOrgan(int organID) {
        SysOrgEnt ent = sysOrgEntMapper.selectById(organID);
        if (EmptyUtils.isEmpty(ent)) return false;
        //todo  update 一次用户表就行了
        List<SysUserEnt> userList = sysOrgEntMapper.selectAllSysUserByOrg(organID);
        for (SysUserEnt userEnt : userList) {
            userEnt.setOrganizationId(0);
            sysUserEntMapper.updateById(userEnt);
        }
        sysOrgEntMapper.deleteById(organID);
        return true;
    }

    public boolean checkUserIsInMultipleOrgan(int userID) {
        return sysUser2RoleEntMapper.selectCountOrganByUserID(userID) > 0 ? true : false;
    }

    /**
     * 检测添加组织是否重名
     *
     * @param parentID
     * @return
     */
    @Transactional(readOnly = true)
    public boolean checkOrganIsReplayName(int parentID, String name) {
        return sysOrgEntMapper.countOrganIsReplay(parentID, name, 0) > 0 ? true : false;
    }

    @Transactional
    public void updateOrganUserRoles(int userID, int organID, Integer role) {
        var userEnt = new SysUserEnt();
        userEnt.setUserId(userID);
        userEnt.setOrganizationId(organID);
        sysUserEntMapper.updateById(userEnt);
        var queryWrapper = new QueryWrapper<SysUser2RoleEnt>();
        queryWrapper.eq("user_id",userID);
         var roleEnt =sysUser2RoleEntMapper.selectOne(queryWrapper);
         if(EmptyUtils.isEmpty(roleEnt)){
             roleEnt = new SysUser2RoleEnt();
             roleEnt.setUserId(userID);
             roleEnt.setRoleId(role);
             sysUser2RoleEntMapper.insert(roleEnt);
         }else {
             roleEnt.setRoleId(role);
             sysUser2RoleEntMapper.updateById(roleEnt);
         }
    }

}
