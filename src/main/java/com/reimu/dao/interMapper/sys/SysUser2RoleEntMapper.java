package com.reimu.dao.interMapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.reimu.dao.pojo.sys.SysUser2RoleEnt;

public interface SysUser2RoleEntMapper extends BaseMapper<SysUser2RoleEnt> {

    @Delete("delete from sys_user_role where user_id=#{userID} and organization_id=#{organID} ")
    int deleteRolesInOrgan(@Param("userID") int userID, @Param("organID") int organID);

    @Insert("insert into sys_user_role(user_id,organization_id,role_id) values (#{userID},#{organID},#{roleID}) ")
    int insertRolesInOrgan(@Param("userID") int userID, @Param("organID") int organID, @Param("roleID") int roleID);

    @Select("select count(*) from sys_user_role where  organization_id!=0 and user_id=#{userID} ")
    int selectCountOrganByUserID(@Param("userID") int userID);
}