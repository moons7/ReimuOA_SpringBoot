package com.reimu.dao.interMapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Select;
import com.reimu.dao.pojo.sys.SysUserEnt;

import java.util.List;
import java.util.Map;

public interface SysUserEntMapper extends BaseMapper<SysUserEnt> {


    @Select("select * from sys_user where deleted='false' and userName=#{userName} limit 1")
    SysUserEnt findUserByUserName(String userName);

    @Select("select user_id,username,realname,status from sys_user where deleted='false'")
    IPage<Map<String,Object>> selectAllUser(Page page);

    @Select("select * from sys_user where deleted='false' and username=#{username} ")
    List<SysUserEnt> selectUsersByUserName(String username);

}