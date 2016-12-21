package com.lhh.core.base;

import java.util.Collection;
import java.util.List;

/**
 * services
 * @author hwaggLee
 * @createDate 2016年12月19日
 * @param <T>
 */
public interface BaseService<T> {
	T save(T t);
	void update(T t);
	void updateByParam(T t);
	void deleteById(String id);
	List<T> findList();
	void deleteByIdList(Collection<String> idList);
	T findById(String idStr);
	T loadById(String idStr);
	String getSimpleClassName();
	
}
