package com.reimu.service.sys;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.reimu.dao.interMapper.sys.SysRoleEntMapper;
import com.reimu.dao.pojo.sys.SysRoleEnt;

import java.util.List;

@Service
public class SysRoleService {


    @Autowired
    private SysRoleEntMapper sysRoleEntMapper;

    @Transactional(readOnly = true)
    public List<String> getUserRoles(int userID) {
       List<SysRoleEnt> roleEntList=sysRoleEntMapper.selectRolesByUserID(userID);
       return null;
    }

}
