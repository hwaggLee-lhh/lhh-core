package com.lhh.core.core.model;

import java.util.LinkedHashMap;

import com.lhh.core.base.BaseObject;
/**
 * 抽象字典元素
 * @author hwaggLee
 * @createDate 2016年12月16日
 */
public class LhhCoreDictionary extends BaseObject {
	
	private static final long serialVersionUID = 1L;
	private String code;
	private String name;
	/**元素容器，LinkedHashMap是为了保证顺序*/
	private LinkedHashMap<String, LhhCoreEnumeration> enumMap = new LinkedHashMap<String, LhhCoreEnumeration>();
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public LhhCoreDictionary() {
		
	}
	public LhhCoreDictionary(String code, String name) {
		this.code = code;
		this.name = name;
	}
	public String getEnumName(String enumCode) {
		LhhCoreEnumeration enumeration = getEnumeration(enumCode);
		if(enumeration==null) {
			return null;
		}
		return enumeration.getName();
	}
	public LhhCoreEnumeration getEnumeration(String key) {
		return enumMap.get(key);
	}
	public void addEnum(LhhCoreEnumeration enums) {
		if(enums==null) return;
		enumMap.put(enums.getCode(), enums);
	}

    /**
     * 设置字典中的元素
     * @param key
     * @param enumeration
     */
    public void addEnum(String key, LhhCoreEnumeration enumeration) {
        enumMap.put(key, enumeration);
    }

    public void addEnum(String enumCode, String enumName) {
        enumMap.put(enumCode, new LhhCoreEnumeration(enumCode, enumName));
    }
    
    public void remove(String enumCode) {
        enumMap.remove(enumCode);
    }

    public LinkedHashMap<String, LhhCoreEnumeration> getEnumMap() {
        return enumMap;
    }

    public void setEnumMap(LinkedHashMap<String, LhhCoreEnumeration> enumMap) {
        this.enumMap = enumMap;
    }

    public Object clone() {
        LhhCoreDictionary dictionary = (LhhCoreDictionary) super.clone();
        if (enumMap != null) {
            dictionary.setEnumMap(enumMap);
        }
        return dictionary;
    }
}
