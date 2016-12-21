package com.lhh.core.cache;

import java.io.IOException;

import net.sf.ehcache.CacheException;

import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.core.io.Resource;

/**
 * 缓存文件配置加载 在使用 CacheManager.create()时因配置文件未放在根目录而初始化了一个新的缓存池，本例为解决这个问题
 * 
 * @author hwaggLee
 * @createDate 2016年12月16日
 */
public class LhhCoreEhCacheManagerFactoryBean extends EhCacheManagerFactoryBean {

	protected Resource configLocation;
	protected boolean shared;
	protected String cacheManagerName;
	
	public void setConfigLocation(Resource resource) {
		this.configLocation = resource;
		super.setConfigLocation(resource);
	}

	/**
	 * 缓存创建的缓存文件
	 * @param shared
	 */
	public void setShared(boolean shared) {
		this.shared = shared;
		super.setShared(shared);
	}
	public void setCacheManagerName(String cacheManagerName) {
		this.cacheManagerName = cacheManagerName;
		super.setCacheManagerName(cacheManagerName);
	}

	/**
	 * 在需要使用到缓存文件中的内容时，其他地方不能比此处先加载并创建单列缓存。否则系统无法加载指定文件
	 * @throws IOException
	 * @throws CacheException
	 */
	public void afterPropertiesSet() throws IOException, CacheException {
		super.afterPropertiesSet();
	}

}
