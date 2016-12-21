package com.lhh.core.exception;

public class LhhCoreException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public static final String MSG_TIMEOUT = "验证码超时";
	public static final String MSG_NOTNULL = "验证码不能为空";
	public static final String MSG_ERROR = "验证码不正确";
	public static final String MSG_FREEZE_USER="用户已冻结";
	public static final String MSG_ERROR_USERNAME_OR_PWD="用户名或密码错误";

	public LhhCoreException(String msg) {
		super(msg);
	}
}
