package com.lhh.core.base;

import org.springframework.util.StringUtils;

/**
 * 模型基类
 * 所有持久化对象继承此类
 * @author hwaggLee
 * @createDate 2016年12月19日
 */
public abstract class BaseModel extends BaseObject implements BaseModelable {
	
	private static final long serialVersionUID = 1L;

	public int hashCode() {
        String idStr = getIdStr();
        return StringUtils.isEmpty(idStr)?super.hashCode():idStr.hashCode();
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        /*因为字节码增强的关系，getClass()不能用作判断的依据*/
        if (getClass().getPackage() != other.getClass().getPackage()) {
            return false;
        }
        if (hashCode() == other.hashCode()) {
            return true;
        }
        return false;
    }
}
