package com.reimu.shiro;


import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.reimu.dao.pojo.sys.SysUserEnt;
import com.reimu.shiro.principal.Principal;
import com.reimu.shiro.service.ShiroUserInfoService;
import com.reimu.util.EmptyUtils;

import java.util.List;

/**
 * 系统安全认证实现类
 */
public class SystemAuthorizingRealm extends AuthorizingRealm {

    private final static Logger logger = LoggerFactory.getLogger(SystemAuthorizingRealm.class);

    private final static String Realm_Name = "SYS_DEFAULT_REALM";

    private final static String USER_STATUS_NORMAL = "normal";

    /**
     * 认证回调函数, 登录时调用
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(
            AuthenticationToken authcToken) {
        UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
        // 校验用户名密码
        SysUserEnt user = ShiroUserInfoService.findUserInfoByUserName(token.getUsername());
        if (EmptyUtils.isEmpty(user)) {
            throw new IncorrectCredentialsException("用户名或密码错误！");
        } else if (!USER_STATUS_NORMAL.equals(user.getStatus())) {
            throw new AuthenticationException("该已帐号禁止登录!");
        }
        token.setPassword(HashEncryptService.sha512Encrypt(token.getPassword(), user.getSalt()).toCharArray());
        String passWord = user.getPassword();
        int organID = ShiroUserInfoService.findUserOrganID(user.getUserId());    //获取部门ID
        int rootOrganID = ShiroUserInfoService.findRootOrganByOrganID(organID); //获取最顶级部门ID 如果为0则表示该部门就是一级部门
        Principal principal = new Principal(user.getUserId(), user.getUsername(), organID, rootOrganID);
        return new SimpleAuthenticationInfo(principal, passWord, Realm_Name);
    }

    /**
     * 授权查询回调函数, 进行鉴权但缓存中无用户的授权信息时调用
     *
     * @Note 1.如果该用户授权已被redis缓存过, 则不进入该方法
     * 2.如果木有触发相关鉴权的判断操作,则也不会触发该操作
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(
            PrincipalCollection principals) {
        Principal principal = (Principal) getAvailablePrincipal(principals); //获取当前活动的principal
        logger.info("授权用户  {}", principal.getUsername());
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        List<String> roleList = SecurityHelper.getRole();
        for (String role : roleList) {
            info.addRole(role);
        }
        return info;
    }

    /**
     * 删除当前角色的授权信息
     */
    public void deleteCurrentUserAuthorizationInfo() {
        this.doClearCache(SecurityUtils.getSubject().getPrincipals());
    }

    /**
     * 删除所有角色的授权信息
     */
    public void deleteAllUserAuthorizationInfo() {
        this.getAuthorizationCache().clear();
    }


    @Override
    protected void assertCredentialsMatch(AuthenticationToken token, AuthenticationInfo info)
            throws AuthenticationException {
        //表单中的密码
        String tokenCredentials = String.valueOf((char[]) token.getCredentials());
        //数据库中的密码
        String accountCredentials = (String) info.getCredentials();

        //认证失败，抛出密码密码不正确的异常
        if (!accountCredentials.equals(tokenCredentials)) {
            String msg = "Submitted credentials for token [" + token + "] did not match the expected credentials.";
            throw new IncorrectCredentialsException(msg);
        }
    }

}
