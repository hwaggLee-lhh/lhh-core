package com.lhh.core.core.tag;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import com.lhh.core.base.LhhCoreBaseTag;
import com.lhh.core.core.model.LhhCoreDictionary;
import com.lhh.core.core.service.DictionaryFactory;
import com.lhh.core.util.LhhCoreUtilsDictTag;

public abstract class DictTag extends LhhCoreBaseTag {
    
	private static final long serialVersionUID = 1L;

	/**字典编号，在下拉列表中显示对应的枚举的值*/
    private String dictCode = "";
    
    /**数据字典工厂*/
    private DictionaryFactory dictionaryFactory;
    
    /**过滤器名称*/
    private String filterName = "";
    
    /**tag专用，缓存失效时间period单位是秒，0表示缓存永远有效，负数表示不放入缓存*/
    private String period;
    
    /**true表示需要二维数组，false表示只需要一维数组，只需要enums中的value*/
    private String needTwoArray;
    
    /**xbrlbf项目专用，过滤fundId的权限*/
    private String fundId = "";
    
    protected abstract String getDictionaryFactoryName();
    
    public int doStartTag() {
        dictionaryFactory = (DictionaryFactory) getWebApplicationContext().getBean(getDictionaryFactoryName());
        period = StringUtils.defaultIfEmpty(period, "-1");
        needTwoArray = StringUtils.defaultIfEmpty(needTwoArray, "true");
        return EVAL_BODY_INCLUDE;
    }
    
    public int doEndTag() {
        LhhCoreDictionary dictionary = null;
        //首先查询缓存中是否存在数据字典
        int periodNum = Integer.parseInt(period);
        if (periodNum == 0) {
            periodNum = Integer.MAX_VALUE;
        }
        if (dictionary == null) {//不使用缓存或者没有命中缓存periodNum <= 0 && needPutInCache
            //dictCode可以为空，完全靠filterDictionary得到显示数据
            if (StringUtils.isNotEmpty(dictCode)) {
                dictionary = dictionaryFactory.getDictionary(dictCode);
            } else {
                dictionary = new LhhCoreDictionary();
            }
        }
        Assert.notNull(dictionary);
        super.pageContextWrite(pageContext, LhhCoreUtilsDictTag.assembleArrayArray(dictionary, "true".equals(needTwoArray)));
        return EVAL_PAGE;
    }
    
    public String getKeyName() {
        return dictCode + filterName + fundId;
    }

    public void setDictCode(String dictCode) {
        this.dictCode = dictCode;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public void setNeedTwoArray(String needTwoArray) {
        this.needTwoArray = needTwoArray;
    }

    public void setFundId(String fundId) {
        this.fundId = fundId;
    }

}
