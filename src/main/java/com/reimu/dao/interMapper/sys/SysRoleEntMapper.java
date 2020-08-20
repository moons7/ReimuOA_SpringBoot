package com.reimu.dao.interMapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.reimu.dao.pojo.sys.SysRoleEnt;

import java.util.List;

public interface SysRoleEntMapper extends BaseMapper<SysRoleEnt> {
    @Select("select b.* from sys_user_role a, sys_role b WHERE a.role_id=b.role_id  and a.user_id=#{userID}")
    List<SysRoleEnt> selectRolesByUserID(@Param("userID") int userID);
}