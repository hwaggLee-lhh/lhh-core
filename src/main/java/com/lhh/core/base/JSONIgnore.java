package com.lhh.core.base;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 忽略不Json序列化
 * @author hwaggLee
 * @createDate 2016年12月16日
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface JSONIgnore {

}
