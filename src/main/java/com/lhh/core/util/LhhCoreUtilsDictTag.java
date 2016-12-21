package com.lhh.core.util;

import java.util.Collection;
import java.util.LinkedHashMap;

import net.sf.json.util.JSONUtils;

import com.lhh.core.core.model.LhhCoreDictionary;
import com.lhh.core.core.model.LhhCoreEnumeration;
import com.lhh.format.lang.LhhUtilsJSON;

/**
 * 标签工具类
 * @author hwaggLee
 * @createDate 2016年12月16日
 */
public class LhhCoreUtilsDictTag {

	/**
	 * 构造JSON数组
	 * 
	 * @param enumeration
	 * @param needTwoArray
	 *            是否需要二维数组
	 * @return
	 */
	public static String assembleArrayEnum(LhhCoreEnumeration enumeration,
			boolean needTwoArray) {
		if (needTwoArray) {
			return LhhUtilsJSON.assembleArrayEnum(enumeration.getCode(),enumeration.getName());
		} else {
			return JSONUtils.quote(enumeration.getName());
		}
	}

	/**
	 * 构造JSON二维数组
	 * 
	 * @param dictionary
	 * @param needTwoArray
	 *            是否需要二维数组
	 * @return
	 */
	public static String assembleArrayArray(LhhCoreDictionary dictionary,
			boolean needTwoArray) {
		if (dictionary == null) {
			return "[]";
		}
		StringBuffer buf = new StringBuffer();
		for (LhhCoreEnumeration enumObj : dictionary.getEnumMap().values()) {
			buf.append(',' + assembleArrayEnum(enumObj, needTwoArray));
		}
		// 删除第一个逗号
		if (buf.length() != 0) {
			buf.deleteCharAt(0);
		}
		return '[' + buf.toString() + ']';
	}

	/**
	 * 构造适合页面用的三维大数组 大数组例： [[["1001","类别1"],[["2010","scama"],["2020","中文项目"]]],
	 * [["1002","类别2"],[["3010","权证"],["3020","smg"]]] ]
	 * 
	 * @param collect
	 *            Dictionary列表
	 * @return
	 */
	public static String assembleArray2Level4Dict(Collection<LhhCoreDictionary> collect) {
		StringBuffer buf = new StringBuffer(100);
		for (LhhCoreDictionary dictionary : collect) {
			buf.append(",["
					+ LhhUtilsJSON.assembleArrayEnum(dictionary.getCode(),dictionary.getName()) + ','
					+ assembleArrayArray(dictionary, true) + ']');
		}
		// 删除第一个逗号
		if (buf.length() != 0) {
			buf.deleteCharAt(0);
		}
		return '[' + buf.toString() + ']';
	}

	/**
	 * 把1级Dictionary中的枚举转换成2级数据字典列表，2级联动数据需要
	 * 
	 * @param dictionary
	 * @return LinkedHashMap<dictCode, Dictionary>
	 */
	public static LinkedHashMap<String, LhhCoreDictionary> convert(
			LhhCoreDictionary dictionary) {
		Collection<LhhCoreEnumeration> collect = dictionary.getEnumMap().values();
		LinkedHashMap<String, LhhCoreDictionary> map = new LinkedHashMap<String, LhhCoreDictionary>(
				collect.size());
		for (LhhCoreEnumeration enumeration : collect) {
			LhhCoreDictionary dictionaryTemp = new LhhCoreDictionary(enumeration.getCode(),
					enumeration.getName());
			map.put(dictionaryTemp.getCode(), dictionaryTemp);
		}
		return map;
	}
}
