package com.lhh.core.base;

import java.util.Collection;
import java.util.List;

/**
 * services
 * @author hwaggLee
 * @createDate 2016年12月19日
 * @param <T>
 */
public abstract class BaseServiceImpl<T extends BaseModelable> extends BaseLogger implements BaseService<T> {
	protected abstract BaseManager<T> getBaseManager();
	public T save(T entity) {
		return getBaseManager().save(entity);
	}
	public void update(T entity) {
		getBaseManager().update(entity);
	}
	public void updateByParam(T t) {
		getBaseManager().updateByParam(t);
	}
	public List<T> findList() {
		return getBaseManager().findList();
	}
	public void deleteById(String id) {
		getBaseManager().deleteByIdStr(id);
	}
	public void deleteByIdList(Collection<String> idList) {
		getBaseManager().deleteByIdList(idList);
	}
	public T findById(String idStr) {
		return getBaseManager().findById(idStr);
	}
	public T loadById(String idStr) {
		return getBaseManager().loadById(idStr);
	}
	public String getSimpleClassName(){
		return getBaseManager().getSimpleClassName();
	}
}
