package com.reimu.dao.interMapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.reimu.dao.pojo.sys.SysOrgEnt;
import com.reimu.dao.pojo.sys.SysUserEnt;

import java.util.List;
import java.util.Map;

public interface SysOrgEntMapper extends BaseMapper<SysOrgEnt> {

    @Select("select * from sys_organization where parent_id=#{parentID}")
    List<SysOrgEnt> selectSysOrgList(int parentID);

    @Select("select a.*,b.organization_name from sys_user a,sys_organization b where a.status='normal' and a.organization_id=b.organization_id and  a.organization_id=#{organID}  ")
    IPage<Map<String,Object>> selectSysUserByOrganID(@Param("organID") int organID, Page page);

    @Select("select a.*,b.organization_name from sys_user a,sys_organization b where a.status='normal' and a.organization_id=b.organization_id")
    IPage<Map<String,Object>> selectSysUserByALLOrgan(Page page);

    @Select("select a.* from sys_user a where a.status='normal'  and  a.organization_id=#{organID}  ")
    List<SysUserEnt>  selectAllSysUserByOrg(@Param("organID") int organID);

    @Select("select count(*) from sys_organization  where parent_id=#{organID} ")
    int selectIsParent(@Param("organID") int organID);

    @Select("select user_id,username,realname from sys_user WHERE deleted='false' and organ_state='SINGLE' and user_id not in (SELECT a.user_id from sys_user a LEFT JOIN  sys_user_organization b on a.user_id=b.user_id WHERE b.organization_id=#{organID}) ")
    IPage<Map<String, Object>> selectUserNotInOrgan(@Param("organID") int organID, Page page);

    @Select("select count(*) from  sys_organization  where organization_id!=#{organID} and parent_id=#{parentID} and organization_name=#{organName}")
    int countOrganIsReplay(@Param("parentID") int parentID, @Param("organName") String organName, @Param("organID") int organID);

}