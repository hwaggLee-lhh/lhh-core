package com.lhh.core.base;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.hibernate.collection.internal.AbstractPersistentCollection;
import org.hibernate.proxy.LazyInitializer;
import org.springframework.stereotype.Component;

import com.lhh.core.core.model.LhhCoreEnumeration;
import com.lhh.core.core.service.DictionaryFactory;
import com.lhh.format.cls.LhhUtilsModel;
import com.lhh.format.lang.LhhUtilsDateTime;


/**
 * 转换数据
 * @author hwaggLee
 * @createDate 2016年12月16日
 */
@Component("jsonConvert")
@SuppressWarnings("rawtypes")
public class JSONConvert extends BaseLogger{

    @Resource
    private DictionaryFactory dictionaryFactory;
    private String dateFormat = LhhUtilsDateTime.FORMAT_yyyy_MM_dd;
    
    public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public JSONArray modelCollect2JSONArray(Collection collect) {
        return modelCollect2JSONArray(collect, null);
    }
    
    public JSONArray modelCollect2JSONArray(Collection collect, Collection jsonAwareCollect) {
        JSONArray jsonArray = new JSONArray();
        for (Iterator iter = collect.iterator(); iter.hasNext();) {
            Object object = iter.next();
            if (object instanceof Collection) {
                //object = modelCollect2JSONArray(collect, jsonAwareCollect);
                object = modelCollect2JSONArray((List)object, jsonAwareCollect);
            } else if (object instanceof Map ||
                object instanceof CharSequence ||
                object instanceof Number
                    ) {
                object = JSONObject.fromObject(object);
            } else {
                object = model2JSON(object, jsonAwareCollect);
            }
            jsonArray.add(object);
        }
        return jsonArray;
    }


    public JSONObject model2JSON(Object model) {
        return model2JSON(model, null);
    }
    
    /**
     * 把model转换为JSON对象，凡是实现JSONNotAware的不引入，按照get方法得到属性信息
     * 向量的判断暂时只有Collection，也就是说会对Collection中所有JSONNotAware的不引入，
     * 目的是减少不必要的延迟加载，需要引入的话把名字通过jsonAwareArray传入
     * @param model 需要转换的对象
     * @param jsonAwareArray 需要引入的变量
     * @return
     */
    public JSONObject model2JSON(Object model, Collection jsonAwareCollect) {
        JSONObject jsonObject = new JSONObject();
        Method[] methods = model.getClass().getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            /*BaseModel中不需要getClass*/
            if (method.getName().equals("getClass")) {
                continue;
            }
            if (!LhhUtilsModel.isGetMethod(method)) {
                continue;
            }
            Class returnType = method.getReturnType();
            /*不需要转换的类型*/
            if (returnType == Document.class || 
                returnType == byte[].class ||
                returnType == Logger.class ||
                returnType == LazyInitializer.class
               ) {
                continue;
            }
            String propertyName = LhhUtilsModel.getPropertyName(method.getName());
            Object getObj = LhhUtilsModel.invokeGetMethod(model, method);
            if (getObj == null) {
                continue;
            }
            Collection subCollect = getAwareSubList(jsonAwareCollect, propertyName);
            if (getObj instanceof LazyInitializer) {
                continue;
            } else if (getObj instanceof Date) {
                //时间转换为页面需要的格式，time还未考虑
                getObj = LhhUtilsDateTime.date2StrDate((Date) getObj, this.dateFormat);
            } else if (getObj instanceof LhhCoreEnumeration) {
                //数据字典转换，已知code得到name
            	LhhCoreEnumeration tempEnum = (LhhCoreEnumeration)getObj;
            	dictionaryFactory.setName(tempEnum);
            } else if (getObj instanceof JSONNotAware) {
                /*判断的时候，jsonAwareCollect优先级高于JSONNotAware*/
                if (subCollect == null) {
                    /*只有多对一的情况下会到此逻辑，如果没有指定加载这个对象，只加载idStr*/
                    Class clazz = getObj.getClass();
                    Method getIdStrMothod = LhhUtilsModel.getMethod(clazz,
                    		LhhUtilsModel.getGetMehodName(BaseManager.ID_NAME), new Class[0]);
                    JSONObject tempJSONObject = new JSONObject();
                    tempJSONObject.put(BaseManager.ID_NAME, LhhUtilsModel.invokeGetMethod(getObj, getIdStrMothod));
                    getObj = tempJSONObject;
                } else {
                    getObj = model2JSON(getObj, subCollect);
                }
            } else if (getObj instanceof AbstractPersistentCollection) {//Hibernate的List，可能触发延迟加载
                if (subCollect == null) {
                    continue;
                }
                JSONArray jsonArray = new JSONArray();
                for (Iterator iter = ((Collection) getObj).iterator(); iter.hasNext();) {
                    jsonArray.add(model2JSON(iter.next(), subCollect));
                }
                getObj = jsonArray;
            }
            jsonObject.put(propertyName, getObj);
        }
        return jsonObject;
    }
    
    /**
     * 从原始的jsonAwareList中查找前缀为name的，去除前缀后，放到新的List中返回
     * @param jsonAwareCollect 原始jsonAwareList
     * @param name 需要判断的属性名称
     * @return 如果连名字符合的都没有返回null，如果有符合的但是没有subList返回new LinkedList()
     */
    @SuppressWarnings("unchecked")
	private static Collection getAwareSubList(Collection jsonAwareCollect, String name) {
        if (jsonAwareCollect == null) {
            return null;
        }
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("name is empty");
        }
        boolean needAware = false;
        Collection awareSubCollect = new LinkedList();
        for (Iterator iter = jsonAwareCollect.iterator(); iter.hasNext();) {
            String element = (String) iter.next();
            if (StringUtils.isEmpty(element)) {
                throw new IllegalArgumentException("element is empty");
            }
            if (element.startsWith(name + ".")) {
                String subStr = element.substring(name.length() + 1);
                if (StringUtils.isEmpty(subStr)) {
                    throw new IllegalArgumentException("subStr is empty, element:" + element);
                }
                awareSubCollect.add(subStr);
                needAware = true;
            } else if (element.startsWith(name)) {
                needAware = true;
            }
        }
        if (!needAware) {
            return null;
        }
        return awareSubCollect;
    }

}
