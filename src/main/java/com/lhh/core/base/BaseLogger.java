package com.lhh.core.base;

import org.apache.log4j.Logger;

/**
 * base公共类
 * @author hwaggLee
 * @createDate 2016年12月16日
 */
public abstract class BaseLogger {
	protected Logger logger = Logger.getLogger(this.getClass());
	protected String logname = this.getClass().getSimpleName();
	
}
