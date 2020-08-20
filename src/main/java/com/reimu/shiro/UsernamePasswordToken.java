package com.reimu.shiro;

/**
 * 用户和密码令牌类
 * TODO 加入验证码
 * @author ThinkGem
 * @version 2013-5-19
 */
public class UsernamePasswordToken extends org.apache.shiro.authc.UsernamePasswordToken {

	private static final long serialVersionUID = 1L;

	private String captcha;

	public String getCaptcha() {
		return captcha;
	}

	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}

	public UsernamePasswordToken() {
		super();
	}

	public UsernamePasswordToken(String username, String password,
                                 boolean rememberMe, String host, String captcha) {
		super(username.toLowerCase().trim(), password, rememberMe, host); //用户名全部小写去空格处理
		this.captcha = captcha;
	}
}