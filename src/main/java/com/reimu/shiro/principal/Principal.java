package com.reimu.shiro.principal;

import java.io.Serializable;

/**
 * 登录后的用户信息
 */
public class Principal implements Serializable {

    private int uid; // 编号

    private String username; // 登录名

    private int organid; // 部门编号


    private int rootOrganid;

    public int getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public Principal(int uid, String username, int organid, int rootOrganid) {
        this.uid = uid;
        this.username = username;
        this.organid =organid;
        this.rootOrganid=rootOrganid;
    }

    public int getOrganid() {
        return organid;
    }

    public void setOrganid(int organid) {
        this.organid = organid;
    }


    public int getRootOrganid() {
        return rootOrganid;
    }

    public void setRootOrganid(int rootOrganid) {
        this.rootOrganid = rootOrganid;
    }


}
