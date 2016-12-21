package com.lhh.core.core.service.impl;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

import com.lhh.base.LhhConfigFile;
import com.lhh.core.core.service.DictionaryFactory;

/**
 * 字典数据加载缓存
 * 不能在缓存加载之前使用
 * @author hwaggLee
 * @createDate 2016年12月16日
 */
public class DictionaryFactoryDB extends DictionaryFactory {

	private static final long serialVersionUID = 1L;
	private LhhConfigFile config;
	private DataSource dataSource;
	private String properties = "properties/dictionary-db.properties";
	private String encoding = "UTF-8";
	private String sqlSubfix = ".sql";
	private String sqlDictApplication = "dictapplication.sql";

	/**
	 * 配置文件地址
	 * @param properties
	 */
	public void setConfig(String properties) {
		if (StringUtils.isBlank(properties))
			return;
		this.properties = properties.trim();
	}

	/**
	 * 编码
	 * @param encoding
	 */
	public void setEncoding(String encoding) {
		if (StringUtils.isBlank(encoding))
			return;
		this.encoding = encoding.trim();
	}
	/**
	 * 数据源
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		if(dataSource == null )
			return;
		this.dataSource = dataSource;
	}
	/**
	 * sql属性名称后缀
	 * @param sqlSubfix
	 */
	public void setSqlSubfix(String sqlSubfix){
		if (StringUtils.isBlank(sqlSubfix))
			return;
		this.sqlSubfix = sqlSubfix;
	}
	/**
	 * 无其他配置时默认查询全部
	 * @param sqlDictApplication
	 */
	public void setSqlDictApplication(String sqlDictApplication){
		if (StringUtils.isBlank(sqlDictApplication))
			return;
		this.sqlSubfix = sqlDictApplication;
	}


	/**
	 * 读取字段数据
	 * @param dictCode：可为空
	 * @return
	 */
	protected List<Map<String, Object>> getData(String dictCode) {
		Assert.notNull(dataSource, "lhh core DictionaryFactoryDB dataSource is null");
		init();
		Assert.notNull(this.config, "lhh core DictionaryFactoryDB config is null");
		String sql = getSql(dictCode);
		Assert.hasLength(sql, "lhh core DictionaryFactoryDB sql is not found");
		return new JdbcTemplate(dataSource).queryForList(sql);
	}
	
	/**
	 * 初始化配置信息
	 */
	private void init() {
		this.config = new LhhConfigFile(properties, encoding);
	}
	
	/**
	 * 获取配置的sql
	 * @param key
	 * @return
	 */
	private String getSql(String key){
		String sql = config.getString(key+sqlSubfix);
		/* 首先判断单独业务字典的sql语句有没有，没有的话说明属于系统字典 */
		if (StringUtils.isEmpty(sql)) {
			sql = config.getString(sqlDictApplication);
		}
		return sql;
	}


}
