package com.reimu.dao.pojo.sys;

import com.baomidou.mybatisplus.annotation.TableName;

@TableName("sys_role")
public class SysRoleEnt {
    private Integer roleId;

    private String roleName;

    private String roleValue;

    private String roleType;

    private Boolean proteced;

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName == null ? null : roleName.trim();
    }

    public String getRoleValue() {
        return roleValue;
    }

    public void setRoleValue(String roleValue) {
        this.roleValue = roleValue == null ? null : roleValue.trim();
    }

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType == null ? null : roleType.trim();
    }

    public Boolean getProteced() {
        return proteced;
    }

    public void setProteced(Boolean proteced) {
        this.proteced = proteced;
    }
}