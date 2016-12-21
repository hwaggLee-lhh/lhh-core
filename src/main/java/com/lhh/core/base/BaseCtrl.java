package com.lhh.core.base;

import javax.annotation.Resource;

/**
 * ctrl基类
 * 提供JSONConvert对象
 * @author hwaggLee
 * @createDate 2016年12月16日
 */
public class BaseCtrl extends BaseLogger{
	
	@Resource
    protected JSONConvert jsonConvert;
    
}
