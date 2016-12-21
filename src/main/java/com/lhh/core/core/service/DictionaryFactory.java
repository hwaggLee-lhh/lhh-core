package com.lhh.core.core.service;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import com.lhh.core.LhhCore;
import com.lhh.core.core.model.LhhCoreDictionary;
import com.lhh.core.core.model.LhhCoreEnumeration;

public abstract class DictionaryFactory implements Serializable {
	protected static Logger logger = Logger.getLogger(DictionaryFactory.class);
    
    private static final long serialVersionUID = 6417761806628943394L;
    
    public static final String DICT_CODE = LhhCore.dictCode;
    public static final String DICT_NAME = LhhCore.dictName;
    public static final String ENUM_CODE = "enumcode";
    public static final String ENUM_NAME = "enumname";
    
    //需注意不能在缓存加载之前使用(需要设置延迟加载)
	private static final CacheManager cacheManager ;
    private static Cache cache ;
	static {
		//System.out.println("----------------->?");
		cacheManager = CacheManager.create();
		String[] names = cacheManager.getCacheNames();
		if( names != null ){
			for (int i = 0; i < names.length; i++) {
				logger.info("init DictionaryFactory cache info["+i+"]="+names[i]);
			}
		}
		cache = cacheManager.getCache(LhhCore.ehcacheDict);
		if (cache == null) {
			try {
				System.setProperty(Cache.NET_SF_EHCACHE_USE_CLASSIC_LRU, "true");
			} finally {
			}
		}
	}
    
    /**得到所有的枚举数据，每个枚举的map中包含四个元素
     * ，DictionaryFactory.DICT_CODE
     * ，DictionaryFactory.DICT_NAME
     * ，DictionaryFactory.ENUM_CODE
     * ，DictionaryFactory.ENUM_NAME
     * @return List<Map<key, value>>
     */
    protected abstract List<Map<String, Object>> getData(String dictCode);

    public LhhCoreEnumeration getEnumByName(String dictName, String enumName) {
        Assert.hasText(dictName, "dictName is null");
        Assert.hasText(dictName, "dictName is null");
        LhhCoreEnumeration enumeration = new LhhCoreEnumeration(null, enumName);
        List<Map<String, Object>> list = getData(null);
        for (Map<String, Object> map : list) {
        	if (dictName.equals(map.get(DICT_NAME)) && enumName.equals(map.get(ENUM_NAME))) {
                enumeration = new LhhCoreEnumeration(map.get(ENUM_CODE).toString(), enumName);
            }
		}
        Assert.notNull(enumeration, "dictName:" + dictName + ",enumName:" + enumName + ",not found");
        return enumeration;
    }

    /**
     * 根据字典代码取字典
     * @param code
     * @return
     */
    public LhhCoreDictionary getDictionary(String dictCode) {
        if (dictCode == null) {
            return null;
        }
		net.sf.ehcache.Element ce = cache.get(dictCode);
		LhhCoreDictionary temp = ce==null?null:(LhhCoreDictionary)ce.getObjectValue();
		if(temp!=null) return temp;
        
        Map<String, LhhCoreDictionary> dictMap = assembleDictionary(dictCode);
        // 放入缓存中
        for (Map.Entry<String, LhhCoreDictionary> entry : dictMap.entrySet()) {
        	// 放入缓存中
			cache.put(new net.sf.ehcache.Element((String) entry.getKey(), entry.getValue()));
		}
        LhhCoreDictionary dictionary = (LhhCoreDictionary) dictMap.get(dictCode);
        Assert.notNull(dictionary, "invalid dictCode:" + dictCode);
        return dictionary;
    }
    /**
     * 装配字典
     * @param data
     * @return Map<dictCode,Dictionary>
     */
    private Map<String, LhhCoreDictionary> assembleDictionary(String dictionaryCode) {
        Map<String, LhhCoreDictionary> dictMap = new HashMap<String, LhhCoreDictionary>(1);
        List<Map<String, Object>> list = getData(dictionaryCode);
        for (Map<String, Object> map : list) {
        	String dictCode = map.get(DICT_CODE).toString().trim();
            String dictName = map.get(DICT_NAME).toString().trim();
            String enumCode = map.get(ENUM_CODE).toString().trim();
            String enumName = map.get(ENUM_NAME).toString().trim();
            LhhCoreEnumeration element = new LhhCoreEnumeration(enumCode, enumName);
            LhhCoreDictionary dict = (LhhCoreDictionary) dictMap.get(dictCode);
            // 判断在map中有没有dict
            if (dict != null) {
                // 更新字典
                dict.addEnum(element);
            } else {
                // 新建字典
                dict = new LhhCoreDictionary();
                dict.setCode(dictCode);
                dict.setName(dictName);
                dict.addEnum(element);
                dictMap.put(dictCode, dict);
            }
		}
        
        return dictMap;
    }


    /**
     * 根据元素代码取元素名称。
     * @param enumCode
     * @return String
     */
    public String getEnumerationName(String enumCode) {
        return getEnumeration(enumCode).getName();
    }
    
    /**业务数据字典专用
     * @param dictCode 业务数据字典名称
     * @param enumCode 编码
     * @return 名称
     */
    public String getEnumerationName(String dictCode, String enumCode) {
        LhhCoreDictionary dictionary = getDictionary(dictCode);
        if (dictionary == null) {
            return null;
        }
        return dictionary.getEnumName(enumCode);
    }
    
    /**
     * 根据元素中的代码设定元素中的名称
     * @param enumeration
     */
    public void setName(LhhCoreEnumeration enumeration) {
        if (enumeration == null) {
            return;
        }
        LhhCoreEnumeration temp = getEnumeration(enumeration.getCode());
        if (temp == null) {
            return;
        }
        enumeration.setName(temp.getName());
    }
    
    /**
     * 根据元素代码取元素对象。
     * @param enumCode
     * @return Enumeration
     */
    public LhhCoreEnumeration getEnumeration(String enumCode) {
        if (enumCode == null) {
            return null;
        }
        checkEnumCode(enumCode);
        String dictCode = enumCode.substring(0, enumCode.indexOf('-'));
        return getDictionary(dictCode).getEnumeration(enumCode);
    }
    
    
    protected void checkEnumCode(String enumCode) {
        Assert.isTrue(isValidEnumCode(enumCode), "invalid enumCode:" + enumCode);
    }
    
    protected boolean isValidEnumCode(String enumCode) {
        return enumCode.indexOf('-') != -1;
    }

    protected String getDictCodeFromEnumCode(String enumCode) {
        return enumCode.substring(0, enumCode.indexOf('-'));
    }

    public void removeCache(String dictCode) {
    	cache.remove(dictCode);
    }
}