package com.lhh.core.base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.metadata.ClassMetadata;
import org.springframework.util.Assert;

import com.lhh.base.LhhPage;
import com.lhh.base.LhhPageParam;
import com.lhh.format.cls.LhhUtilsModel;
import com.lhh.format.lang.LhhUtilsStr;

/**
 * manager 基类
 * @author hwaggLee
 * @createDate 2016年12月16日
 * @param <T>:处理数据库模型
 */
@SuppressWarnings("all")
public abstract class BaseManager<T extends BaseModelable> extends BaseLogger {
	/**数据源*/
	@Resource(name="sessionFactory")
	private SessionFactory sessionFactory;
	
    /**统一的ID的属性名称：id*/
    public static final String ID_NAME = "idStr";
    

    /**通过子类实现这个方法，得到Model的类型
     * @return Model的类型
     */
    public abstract Class<T> getModelClass();
    
    /**得到当前处理Model的class的simpleName
     * @return clazz.getSimpleName()
     * @see java.lang.Class#getSimpleName()
     */
    public String getSimpleClassName() {
    	//System.out.println(this.getClass().getComponentType());
        return ClassUtils.getShortClassName(getModelClass());
    }

	public Session getCurrentSession(){  
        return sessionFactory.getCurrentSession();  
    } 
	
    public void flush() {
    	getCurrentSession().flush();
    }
    
    /**得到一个初始化好的DetachedCriteria查询对象
     * @return DetachedCriteria查询对象
     * @see org.hibernate.criterion.DetachedCriteria#forClass(Class)
     */
    public DetachedCriteria getDetachedCriteria() {
        return DetachedCriteria.forClass(getModelClass());
    }
    
    /**根据hql得到一个查询对象
     * @param hql hql
     * @return Query
     * @see org.hibernate.Session#createQuery()
     */
    public Query getQuery(String hql) {
        return getCurrentSession().createQuery(hql);
    }
    
    /**得到一个Criteria查询对象
     * @return Criteria查询对象
     * @see org.hibernate.Session#getCriteria()
     */
    public Criteria getCriteria() {
        return getCurrentSession().createCriteria(getModelClass());
    }
    
    /**查询Model对应的表中的所有数据，测试专用
     * @return List<Model>
     */
	public List<T> findList() {
    	return getCriteria().list();
    }
    
    /**查询Model对应的表中的所有数据，测试专用
     * @param orderByName 排序字段
     * @return List<Model>
     */
    public List<T> findList(String orderByName) {
        DetachedCriteria dc = DetachedCriteria.forClass(getModelClass());
        dc.addOrder(Order.asc(orderByName));
        return findList(dc);
    }
    
    /**根据指定查询对象，查找数据库中的数据，如果符合条件的个数超过1个抛异常
     * @param dc 查询对象
     * @return Model
     * @see org.hibernate.Criteria#uniqueResult()
     */
    public Object findUniqueResult(DetachedCriteria dc) {
        return (Object)dc.getExecutableCriteria(getCurrentSession()).uniqueResult();
    }
    
    /**根据指定查询对象，查找数据库中的数据，取得第一个对象，collect为空的话返回null
     * @param dc 查询对象
     * @return Model
     */
    public T findUniqueResultWithOutException(DetachedCriteria dc) {
        return (T)dc.getExecutableCriteria(getCurrentSession()).setMaxResults(1).uniqueResult();
    }
    
    /**根据指定查询对象，查找数据库中的数据
     * @param dc 查询对象
     * @return List<Model>
     * @see org.springframework.orm.hibernate3.HibernateTemplate#findByCriteria(DetachedCriteria)
     */
    public List<T> findList(DetachedCriteria dc) {
    	return dc.getExecutableCriteria(getCurrentSession()).list();
    }
    
    /**根据指定查询对象，查找数据库中的数据，带分页参数
     * @param dc 查询对象
     * @param firstResult 第一条记录的位置
     * @param maxResults 最大记录数
     * @return List<Model>
     * @see org.springframework.orm.hibernate3.HibernateTemplate#findByCriteria(DetachedCriteria, int, int)
     */
    public List<T> findList(DetachedCriteria dc, int firstResult, int maxResults) {
    	return dc.getExecutableCriteria(getCurrentSession()).setMaxResults(maxResults).setFirstResult(firstResult).list();
    }
    
    /**根据指定查询对象，查找数据库中的数据，带分页参数
     * @param dc 查询对象
     * @param firstResult 第一条记录的位置
     * @param maxResults 最大记录数
     * @return List<Model>
     * @see org.springframework.orm.hibernate3.HibernateTemplate#findByCriteria(DetachedCriteria, int, int)
     */
    public List<T> findList(Criteria criteria, int firstResult, int maxResults) {
        criteria.setFirstResult(firstResult);
        criteria.setMaxResults(maxResults);
        return criteria.list();
    }
    
    /**添加一个对象
     * @param entity 需要添加的对象，添加后该游离对象的引用变成实体对象的引用
     * @return model包含了已有的idStr
     * @see org.springframework.orm.hibernate3.HibernateTemplate#save(Object)
     */
    public T save(T entity) {
    	getCurrentSession().save(entity);
        return entity;
    }
    
    /**根据对象id删除对象
     * @param id 对象id
     */
    public void deleteByIdStr(String id) {
        List<String> ids = new LinkedList<String>();
        ids.add(id);
        deleteByIdList(ids);
    }
    
    /**根据对象id列表删除对象列表，删除对象方式，要做级联删除
     * @param ids 对象id列表，逗号分隔
     * @return 删除的个数
     */
    public void deleteObjectByIds(String ids) {
    	for (Iterator iterator = LhhUtilsStr.str2List(ids).iterator(); iterator.hasNext();) {
			String id = (String) iterator.next();
			delete(findById(id));
		}
    }
    
    /**
     * 根据对象id列表删除对象列表
     * @param idList 对象id列表
     * @return 删除的个数
     */
    public void deleteByIdList(Collection<String> idList) {
        if (idList == null || idList.size() == 0) {
            return;
        }
        for (String id : idList) {
        	delete(findById(id));
		}
    }
    /**删除一个实体对象，如果能够事先得到对象id的话，推荐deleteById方法
     * @param entity
     * @see org.springframework.orm.hibernate3.HibernateTemplate#delete(Object)
     */
    public void delete(Object entity) {
    	getCurrentSession().delete(entity);
    }
    
    /**删除一个Collection集合
     * @param entity
     * @see org.springframework.orm.hibernate3.HibernateTemplate#delete(Object)
     */
    private void deleteAll(Collection<T> entities) {  
        if (entities == null)  
            return;  
        for (Object entity : entities) {  
            getCurrentSession().delete(entity);  
        }  
    }  
    
    /**更新一个持久化对象，如果只需要更新部分属性的话，推荐updateByParam方法
     * 例：更新一个字段为null，当然数据中该字段允许为空
     * User user = (User) userDao.findObjectById("1");
     * user.setName(null);
     * userDao.update(user);
     * @param entity 需要更新的持久化对象
     * @see org.springframework.orm.hibernate3.HibernateTemplate#update(Object)
     */
    public void update(T entity) {
    	getCurrentSession().update(entity);
    }
    
    /**根据BaseModel，取得需要更新对象的id，只更新model中非null的属性，有一句findById操作
     * 如果需要往数据库中更新null属性的话，只能用update方法，如果要对对应关联清缓存的话，也建议最终调用update方法
     * 本方法支持级联，但是使用时要注意，级联一对多的collection为null和size为0处理方式不一样的：
     * null不处理，但是size为0是有实际语义的，表示一对多的部分全部删除
     * 例：
     * User user = new User();
     * user.setId("1");
     * user.setName("刘博宇");
     * userDao.updateByParam(user);
     * 在上面这个例子中，会更新id为1的记录中的name为刘博宇，但是不会更新其它字段
     * @param param
     */
    public void updateByParam(BaseModelable param) {
        Object entity = findById(param.getIdStr());
        LhhUtilsModel.transferValue(param, entity);
        getCurrentSession().update(entity);
    }
    
    /**根据指定id查询对象，如果该id的数据在数据库中不存在，返回null，测试专用
     * 与loadById区别为返回结果为持久化对象
     * @param id 指定id
     * @return model
     * @see org.springframework.orm.hibernate3.HibernateTemplate#load(Class, java.io.Serializable)
     */
    public T findById(Serializable id) {
    	T t = (T)getCurrentSession().get(getModelClass(), id);
    	if(t!=null) getCurrentSession().evict(t);
    	return t;
    }
    /**根据指定id查询对象，如果该id的数据在数据库中不存在，返回null
     * 与findById区别为返回结果为持久化对象
     * @param id 指定id
     * @return model
     * @see org.springframework.orm.hibernate3.HibernateTemplate#load(Class, java.io.Serializable)
     */
    public T loadById(Serializable id) {
    	return (T)getCurrentSession().get(getModelClass(), id);
    }
    
    public Query createQuery(String hql, Collection values) {
        return createQuery(hql, values.toArray());
    }
    
    /**创建Query对象. 对于需要first,max,fetchsize,cache,cacheRegion等诸多设置的函数,可以在返回Query后自行设置.
     * @param values Object[].
     * @return Query
     */
    public Query createQuery(String hql, Object[] values) {
        Assert.hasText(hql);
        if (values == null) {
            values = new Object[] {};
        }
        Query query = getQuery(hql);
        for (int i = 0; i < values.length; i++) {
            query.setParameter(i, values[i]);
        }
        return query;
    }

    /**分页查询函数，使用已设好查询条件与排序的Criteria
     * @param criteria 查询对象
     * @param pageParam 页面查询参数
     * @return Page(totalProperty, list)
     */
    public LhhPage<T> findPage(Criteria criteria, LhhPageParam pageParam) {
        Assert.notNull(criteria);
        Assert.notNull(pageParam);
        CriteriaImpl impl = (CriteriaImpl) criteria;
        // 先把Projection和OrderBy条件取出来,清空两者来执行Count操作
        Projection projection = impl.getProjection();
        //List<CriteriaImpl.OrderEntry>
        List orderEntries = (List) LhhUtilsModel.forceGetProperty(impl, "orderEntries");
        LhhUtilsModel.forceSetProperty(impl, "orderEntries", new ArrayList());
        criteria.setProjection(Projections.rowCount());
        // 执行查询
        int totalProperty = ((Long) criteria.uniqueResult()).intValue();
        
        // 将之前的Projection和OrderBy条件重新设回去
        criteria.setProjection(projection);
        

//        List listTemp = criteria.list();
//        int totalProperty = 0;
//        if(listTemp!=null && listTemp.size()>0) totalProperty = listTemp.size();
        
        if (projection == null) {
            criteria.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
        }
        if (StringUtils.isNotEmpty(pageParam.getSort())) {
            if (pageParam.getDir().equals(LhhPageParam.DIR[0])) {
                criteria.addOrder(Order.asc(pageParam.getSort()));
            } else {
                criteria.addOrder(Order.desc(pageParam.getSort()));
            }
        } else {
            LhhUtilsModel.forceSetProperty(impl, "orderEntries", orderEntries);
        }
        criteria.setFirstResult(pageParam.getStart());
        criteria.setMaxResults(pageParam.getLimit());
        List list = criteria.list();
        return new LhhPage<T>(totalProperty, list);
    }
    
    
    /**分页查询函数，使用已设好查询条件与排序的DetachedCriteria
     * @param dc DetachedCriteria
     * @param pageParam 页面查询参数
     * @return Page(totalProperty, list)
     */
    public LhhPage<T> findPage(DetachedCriteria dc, LhhPageParam pageParam) {
        return findPage(dc.getExecutableCriteria(getCurrentSession()), pageParam);
    }
    

    /**
     * 分页查询，查询结果自己设定去重情况
     * @param criteria
     * @param pageParam
     * @return
     */
    public LhhPage<T> findPageByUniquery(Criteria criteria, LhhPageParam pageParam) {
    	Assert.notNull(criteria);
    	Assert.notNull(pageParam);
    	CriteriaImpl impl = (CriteriaImpl) criteria;
    	
    	List orderEntries = (List) LhhUtilsModel.forceGetProperty(impl, "orderEntries");
    	// 执行查询
    	int totalProperty = (criteria.list()).size();
    	
    	if (StringUtils.isNotEmpty(pageParam.getSort())) {
    		if (pageParam.getDir().equals(LhhPageParam.DIR[0])) {
    			criteria.addOrder(Order.asc(pageParam.getSort()));
    		} else {
    			criteria.addOrder(Order.desc(pageParam.getSort()));
    		}
    	} else {
    		LhhUtilsModel.forceSetProperty(impl, "orderEntries", orderEntries);
    	}
    	criteria.setFirstResult(pageParam.getStart());
    	criteria.setMaxResults(pageParam.getLimit());
    	List list = criteria.list();
    	return new LhhPage<T>(totalProperty, list);
    }
    /**
     * 分页查询，查询结果自己设定去重情况
     * @param dc
     * @param pageParam
     * @return
     */
    public LhhPage<T> findPageByUniquery(DetachedCriteria dc, LhhPageParam pageParam) {
        return findPage(dc.getExecutableCriteria(getCurrentSession()), pageParam);
    }
    
    /**判断对象某些属性的值在数据库中是否唯一
     * @param entity 如果有id认为是update
     * @param uniquePropertyName 在POJO里不能重复的属性
     * @param ex 如果这个参数为空的话，不返回false直接抛异常。
     * @return boolean
     */
    public boolean isUnique(BaseModelable entity, String uniquePropertyName, RuntimeException ex) {
        List uniquePropertyNames = new LinkedList();
        uniquePropertyNames.add(uniquePropertyName);
        return isUnique(true, entity, uniquePropertyNames, ex);
    }
    
    /**判断对象某些属性的值在数据库中是否唯一
     * @param isAnd true表示uniquePropertyNames中的参数采用and的查询条件组合，反之表示采用or的查询条件组合
     * @param entity 如果有id认为是update
     * @param uniquePropertyNames 在POJO里不能重复的属性列表
     * @param ex RuntimeException 如果不为空，而且返回值是false的话抛出此异常
     * @return boolean
     */
    public boolean isUnique(boolean isAnd, BaseModelable entity, List uniquePropertyNames, RuntimeException ex) {
        DetachedCriteria dc = getDetachedCriteria();
        dc.setProjection(Projections.rowCount());
        Junction junction = null;
        if (isAnd) {
            junction = Restrictions.conjunction();
        } else {
            junction = Restrictions.disjunction();
            
        }
        for (Iterator iterator = uniquePropertyNames.iterator(); iterator.hasNext();) {
            String name = (String) iterator.next();
            Object object = LhhUtilsModel.getProperty(entity, name);
            if(object==null) {
            	junction.add(Restrictions.isNull(name));
            } else {
            	junction.add(Restrictions.eq(name, object));
            }
        }
        dc.add(junction);
        // 以下代码为了如果是update的情况,排除entity自身.
        
        String idStr = entity.getIdStr();

        if (idStr != null) {
        	dc.add(Restrictions.ne(ID_NAME, idStr));
        }
        boolean ret = ((Long) findUniqueResult(dc)) == 0;
        if (ret == false && ex != null) {
            throw ex;
        }
        return ret;
    }
    
    /**取得对象的主键名
     * @return idName
     */
    public String getIdName() {
        Class clazz = getModelClass();
        ClassMetadata meta = sessionFactory.getClassMetadata(clazz);
        Assert.notNull(meta, "Class " + clazz.getName() + " not define in hibernate session factory.");
        String idName = meta.getIdentifierPropertyName();
        Assert.hasText(idName, clazz.getName() + " has no identifier property define.");
        return idName;
    }
    
    
    public List findListByHql(String hql) {
    	return getCurrentSession().createQuery(hql).list();
    }
}
