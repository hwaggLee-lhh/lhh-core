package com.lhh.core.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.lhh.LhhUtilsErrorMsg;
import com.lhh.base.LhhPage;
import com.lhh.base.LhhPageParam;
import com.lhh.core.LhhCore;
import com.lhh.core.base.JSONConvert;
import com.lhh.format.lang.LhhUtilsDateTime;

/**
 * 页面类中需要用到的工具方法
 * 
 * @author hwaggLee
 * @createDate 2016年12月16日
 */
@SuppressWarnings("rawtypes")
public class LhhCoreUtilsCtrl {

	/**
	 * 页面视图化
	 * @author hwaggLee
	 * @createDate 2016年12月16日
	 */
	public static class CoreModelAndView{
		public static ModelAndView getModelAndView(String subPath, String fileName) {
			return getModelAndView(subPath, fileName, null);
		}
		
		public static ModelAndView getModelAndView(String subPath, String fileName,
				Map<String, Object> map) {
			if (map == null) {
				map = new HashMap<String, Object>(1);
			}
			map.put("subPath", subPath);
			map.put("fileName", fileName);
			return new ModelAndView(subPath + "/" + fileName, map);
		}
	}
	
	
	/**
	 * 分页数据json化
	 * @author hwaggLee
	 * @createDate 2016年12月16日
	 */
	public static class CorePageToJson{
		/**
		 * 后台分页
		 * @param jsonConvert
		 * @param page
		 * @param jsonAwareList
		 * @param res
		 */
		public static void putJSONPage(JSONConvert jsonConvert, LhhPage page,List jsonAwareList, HttpServletResponse res) {
			JSONArray jsonArray = jsonConvert.modelCollect2JSONArray(page.getDatas(), jsonAwareList);
			putJSONArray(page.getTotalProperty(), jsonArray, res);
		}
		
		/**
		 * 用于List已经全部拿到，只需要转换部分分页数据
		 * @param jsonConvert
		 * @param list
		 * @param jsonAwareList
		 * @param req
		 * @param res
		 */
		public static void putJSONList(JSONConvert jsonConvert, List list,List jsonAwareList, HttpServletRequest req, HttpServletResponse res) {
			LhhPageParam pageParam = getPageParamZore(req);
			JSONArray jsonArray = jsonConvert.modelCollect2JSONArray(list.subList(pageParam.getStart(),Math.min(list.size(), pageParam.getEnd())),jsonAwareList);
			putJSONArray(list.size(), jsonArray, res);
		}
	}

	
	/**
	 * 数据josn化
	 * @author hwaggLee
	 * @createDate 2016年12月16日
	 */
	public static class CoreObjectToJson{
		
		/**
		 * list数据json化
		 * @param jsonConvert
		 * @param list
		 * @param jsonAwareList
		 * @param res
		 */
		public static void putJSONList(JSONConvert jsonConvert, List list,List jsonAwareList, HttpServletResponse res) {
			JSONArray jsonArray = jsonConvert.modelCollect2JSONArray(list,jsonAwareList);
			putJSONArray(jsonArray.size(), jsonArray, res);
		}
		

		/**
		 * 实体数据转换成json
		 * @param jsonConvert
		 * @param model
		 * @param jsonAwareList
		 * @param res
		 */
		@SuppressWarnings("unchecked")
		public static void putJSONListByModel(JSONConvert jsonConvert,Object object, List jsonAwareList, HttpServletResponse res) {
			List list = new LinkedList();
			list.add(object);
			JSONArray jsonArray = jsonConvert.modelCollect2JSONArray(list,jsonAwareList);
			putJSONModel(jsonArray, res, null);
		}

		
		public static void putJSONModel(JSONArray jsonArray,HttpServletResponse res, Map<String, Object> map) {
			if (map == null) {
				map = new HashMap<String, Object>(2);
			}
			map.put("success", new Boolean(true));
			map.put("data", jsonArray);
			putMapToJson(map, res);
		}

		public static void putJSONResult(boolean isSuccess, Object data,HttpServletResponse res) {
			Map<String, Object> map = new HashMap<String, Object>(2);
			if (isSuccess) {
				map.put("success", "true");
			} else {
				map.put("failure", "true");
			}
			map.put("data", data);
			putMapToJson(map, res);
		}
	}

	/**
	 * json数据输出
	 * @param totalProperty
	 * @param jsonArray
	 * @param res
	 */
	public static void putJSONArray(int totalProperty, JSONArray jsonArray,HttpServletResponse res) {
		putJSONArray(totalProperty, jsonArray, res, null);
	}

	/**
	 * json数据输出
	 * @param totalProperty
	 * @param jsonArray
	 * @param res
	 * @param map
	 */
	public static void putJSONArray(int totalProperty, JSONArray jsonArray,HttpServletResponse res, Map<String, Object> map) {
		if (map == null) {
			map = new HashMap<String, Object>(2);
		}
		map.put("total", "" + totalProperty);
		map.put("rows", jsonArray);
		putMapToJson(map, res);
	}


	/**
	 * jsonp转换
	 * @author hwaggLee
	 * @createDate 2016年12月16日
	 */
	public static class CoreJsonpToJson{
		public static void putJsonp(String callback, Map map,HttpServletResponse res) {
			putJSONP(callback, JSONObject.fromObject(map), res);
		}
		
		/**
		 * 去除字包含，防止hibernate的延迟加载报错 实际是对model处理多层次包含处理 解决There is a cycle in the hierarchy!问题
		 * @param callback
		 * @param map
		 * @param res
		 * @author huage
		 */
		public static void putJsonpCDSY(String callback, Map map,HttpServletResponse res) {
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
			putJSONP(callback, JSONObject.fromObject(map, jsonConfig), res);
		}
		
		public static void putJSONP(String callback, JSONObject jsonObject,HttpServletResponse res) {
			writeJsonp(callback, jsonObject.toString(), res);
		}
	}
	
	/**
	 * 数据错误时提示
	 * @param res
	 */
	public static void putErrorJSON(HttpServletResponse res,LhhUtilsErrorMsg error){
		if( error == null )error = LhhUtilsErrorMsg.e_0001;
		putErrorJSON(res, error.name());
	}
	/**
	 * 数据错误时提示
	 * @param res
	 */
	public static void putErrorJSON(HttpServletResponse res,String errorCode){
		if(StringUtils.isBlank(errorCode)) errorCode = LhhUtilsErrorMsg.e_0001.name();
		JSONObject object = new JSONObject();
		object.put(LhhCore.responseErrorCodeKey,errorCode);
		putJSON(object, res);
	}


	/**
	 * Map数据传输
	 * @param map
	 * @param res
	 */
	public static void putMapToJson(Map map, HttpServletResponse res) {
		putJSON(JSONObject.fromObject(map), res);
	}

	/**
	 * jsonobject数据传输
	 * @param jsonObject
	 * @param res
	 */
	public static void putJSON(JSONObject jsonObject, HttpServletResponse res) {
		writeStr(jsonObject.toString(), res);
	}

	/**
	 * jsonArray数据传输
	 * @param jsonArray
	 * @param res
	 */
	public static void putJSON(JSONArray jsonArray, HttpServletResponse res) {
		writeStr(jsonArray.toString(), res);
	}

	/**
	 * 数据传递
	 * @param jsonStr
	 * @param res
	 */
	private static void writeStr(String jsonStr, HttpServletResponse res) {
		res.setContentType("text/html; charset=UTF-8");
		try {
			res.getWriter().write(jsonStr);
		} catch (IOException ex) {
			throw new IllegalArgumentException(ex.getMessage());
		}
	}

	/**
	 * jsonp数据传递
	 * @param callback
	 * @param jsonStr
	 * @param res
	 */
	private static void writeJsonp(String callback, String jsonStr,HttpServletResponse res) {
		try {
			if (callback != null && !"".equals(callback.trim())) {
				res.setContentType("text/javascript");
				StringBuffer sb = new StringBuffer(callback);
				sb.append("(");
				sb.append(jsonStr);
				sb.append(");");
				res.getWriter().write(sb.toString());
			} else {
				res.setContentType("text/html; charset=UTF-8");
				res.getWriter().write(jsonStr);
			}
		} catch (IOException ex) {
			throw new IllegalArgumentException(ex.getMessage());
		}
	}

	/**
	 * 从req读取固定分页类型，page默认从0开始
	 * 
	 * @param req
	 * @return
	 */
	public static LhhPageParam getPageParamZore(HttpServletRequest req) {
		LhhPageParam pageParam = new LhhPageParam();
		pageParam.setLimit(ServletRequestUtils.getIntParameter(req,LhhCore.pageRowKeyName, LhhCore.pageRowNumber));
		int page = ServletRequestUtils.getIntParameter(req,LhhCore.pageKeyName, LhhCore.pageStartNumber);
		pageParam.setStart(page * pageParam.getLimit());
		pageParam.setSort(ServletRequestUtils.getStringParameter(req,LhhCore.pageSortName, ""));
		pageParam.setDir(ServletRequestUtils.getStringParameter(req,LhhCore.pageDirName, ""));
		return pageParam;
	}

	
	
	/**
	 * 模型数据搭建
	 * @author hwaggLee
	 * @createDate 2016年12月16日
	 */
	public static class EditorUtils {

		/**
		 * 从request中转换出模型
		 * @param req
		 * @param t
		 */
		public static <T> void convertObj(HttpServletRequest req, T t) {
			ServletRequestDataBinder binder = getBinder(t);
			binder.bind(req);
		}

		/**
		 * 从request中转换出模型(日期转换yyyy-MM-dd)
		 * @param req
		 * @param t
		 */
		public static <T> void convertObjWithDate(HttpServletRequest req, T t) {
			ServletRequestDataBinder binder = getBinder(t);
			converWithFormatToDate(binder, LhhUtilsDateTime.FORMAT_yyyy_MM_dd);
			binder.bind(req);
		}

		/**
		 * 从request中转换出模型(日期转换yyyy-MM-dd HH:mm:ss)
		 * @param req
		 * @param t
		 */
		public static <T> void convertObjWithDateTime(HttpServletRequest req,T t) {
			ServletRequestDataBinder binder = getBinder(t);
			converWithFormatToDate(binder, LhhUtilsDateTime.FORMAT_yyyy_MM_dd_HH_mm_ss);
			binder.bind(req);
		}
		
		/**
		 * 转换日期
		 * @param binder
		 * @param format
		 */
		private static <T> void converWithFormatToDate(ServletRequestDataBinder binder,String format) {
			SimpleDateFormat dateFormat = new SimpleDateFormat(format);
			dateFormat.setLenient(false);
			binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
		}
		

		private static <T> ServletRequestDataBinder getBinder(T t) {
			return new ServletRequestDataBinder(t);
		}
	}

}
