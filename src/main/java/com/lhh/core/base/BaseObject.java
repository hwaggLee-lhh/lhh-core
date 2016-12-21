package com.lhh.core.base;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 实体克隆
 * @author hwaggLee
 * @createDate 2016年12月19日
 */
public abstract class BaseObject extends BaseLogger implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;
   
    
    /**覆盖toString方法，ToStringStyle取值为ToStringStyle.SHORT_PREFIX_STYLE
     * ，调试的时候注意会自动取所有引用的值，会触发所有的延迟加载
     * @return String
     * @see org.apache.commons.lang.builder.ToStringBuilder#reflectionToString(Object, ToStringStyle)
     */
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
    /**
     * 提供默认的clone方法的实现，不支持深层复制
     * @return Object
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }
    }
}
