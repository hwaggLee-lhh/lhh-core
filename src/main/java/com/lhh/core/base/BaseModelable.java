package com.lhh.core.base;

import java.io.Serializable;

/**
 * 统一模型规范
 * @author hwaggLee
 * @createDate 2016年12月16日
 */
public interface BaseModelable extends JSONNotAware,Serializable, Cloneable {
	/**
	 * 统一规范主键
	 * @return
	 */
    String getIdStr();
}
