package com.lhh.core;


public class LhhCore {
	//系统
	public static final String systemName = "lhhCore";
	//缓存
	public static final String ehcacheDict = systemName+"DictCache";
	public static final String ehcacheSms = systemName+"SendSmsCache";
	
	//session
	public static final String sessionUserid = systemName+"SessionUserid";
	public static final String sessionUsername = systemName+"SessionUsername";
	public static final String sessionUser = systemName+"SessionUser";
	//页面
    public static final String corePagePath = "/pages_core";
    //分页
    public static final String pageRowKeyName = "rows";
    public static final int pageRowNumber = 20;
    public static final String pageKeyName = "page";
    public static final int pageStartNumber = 0;
    public static final String pageSortName = "sort";
    public static final String pageDirName = "dir";
    //权限
    public static final String systemrole = "ROLE_系统管理员";
    
    //request,response 固定参数
    public static final String responseErrorCodeKey = "errorCode";
    
    //数据字典
    public static final String dictCode = "dictcode";
    public static final String dictName = "dictname";
    public static final String enumCode = "enumcode";
    public static final String enumName = "enumname";
    
    

    
}
