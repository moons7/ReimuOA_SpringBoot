package com.reimu.shiro.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reimu.dao.interMapper.sys.SysOrgEntMapper;
import com.reimu.dao.interMapper.sys.SysUserEntMapper;
import com.reimu.dao.pojo.sys.SysOrgEnt;
import com.reimu.dao.pojo.sys.SysUserEnt;
import com.reimu.shiro.HashEncryptService;
import com.reimu.util.EmptyUtils;
import com.reimu.util.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


@Service
@Transactional
public class ShiroUserInfoService {

    private static SysUserEntMapper sysUserEntMapper;

    private static SysOrgEntMapper sysOrgEntMapper;


    //@Note 利用Spring进行注入
    @Autowired
    public void setUserAccessor(SysUserEntMapper sysUserEntMapper) {
        ShiroUserInfoService.sysUserEntMapper = sysUserEntMapper;
    }

    @Autowired
    public void setSysOrgEntMapper(SysOrgEntMapper sysOrgEntMapper) {
        ShiroUserInfoService.sysOrgEntMapper = sysOrgEntMapper;
    }
    

    public static SysUserEnt findUserInfoByUserName(String userName) {
        return sysUserEntMapper.findUserByUserName(userName);
    }


    public static int findUserOrganID(int userID) {
        return  sysUserEntMapper.selectById(userID).getOrganizationId();
    }

    public static int findRootOrganByOrganID(int organID) {
        SysOrgEnt ent = sysOrgEntMapper.selectById(organID);
        if (ent == null) return -1;
        return ent.getRootId().intValue() == 0 ? ent.getOrganizationId() : ent.getRootId();
    }


    @Transactional(readOnly = true)
    public Object getUserList(int pageIndex, int pageSize) {
        var page = new Page<>(pageIndex,pageSize);
        return sysUserEntMapper.selectAllUser(page);
    }


    @Transactional
    public void updateUser(SysUserEnt sysUserEnt) {
        sysUserEntMapper.updateById(sysUserEnt);
    }


    @Transactional
    public void createUser(SysUserEnt sysUserEnt) {
        sysUserEnt.setDeleted("false");
        sysUserEnt.setCreateTime(new Date());
        String salt = RandomUtils.getRandomNumbersAndLetters(12);
        sysUserEnt.setSalt(salt);
        String password = HashEncryptService.sha512Encrypt(sysUserEnt.getPassword().toCharArray(), salt);
        sysUserEnt.setPassword(password);
        sysUserEnt.setUserId(null);
        sysUserEntMapper.insert(sysUserEnt);
    }


    @Transactional(readOnly = true)
    public boolean checkReplyUserName(String userName, int userID) {
        List<SysUserEnt> userEntList = sysUserEntMapper.selectUsersByUserName(userName);
        if (EmptyUtils.isEmpty(userEntList)) return false;
        else if (userEntList.size() == 1) {
            return userEntList.get(0).getUserId().intValue() == userID ? false : true;
        }
        return true;
    }

    @Transactional
    public void delUser(SysUserEnt sysUserEnt) {
        sysUserEnt.setStatus("lock");
        sysUserEnt.setDeleted("true");
        sysUserEnt.setOrganizationId(0);
        sysUserEntMapper.updateById(sysUserEnt);
    }

    @Transactional
    public void updatePassword(SysUserEnt sysUserEnt) {
        SysUserEnt newEnt = sysUserEntMapper.selectById(sysUserEnt.getUserId());
        newEnt.setPassword(HashEncryptService.sha512Encrypt(sysUserEnt.getPassword().toCharArray(), newEnt.getSalt()));
        sysUserEntMapper.updateById(newEnt);
    }

    @Transactional
    public boolean updatePassword(int userID, String oldPwd, String newPwd) {
        SysUserEnt ent = sysUserEntMapper.selectById(userID);
        String passWord = HashEncryptService.sha512Encrypt(oldPwd.toCharArray(), ent.getSalt());
        if (!ent.getPassword().equals(passWord)) return false;
        ent.setPassword(HashEncryptService.sha512Encrypt(newPwd.toCharArray(), ent.getSalt()));
        sysUserEntMapper.updateById(ent);
        return true;
    }
}
