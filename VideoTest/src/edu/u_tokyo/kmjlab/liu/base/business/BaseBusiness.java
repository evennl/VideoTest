package edu.u_tokyo.kmjlab.liu.base.business;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import edu.u_tokyo.kmjlab.liu.base.persistent.BasePer;

public class BaseBusiness<T>
{
	protected BasePer<T> basePer;
	public boolean cacheable = false;
	
	public boolean isCacheable()
	{
		return cacheable;
	}

	public void setCacheable(boolean cacheable)
	{
		this.cacheable = cacheable;
	}

	public BaseBusiness(T t)
	{
		this.basePer = new BasePer<T>(t);
	}
	
	public int getCount()
	{
		Criteria criteria = basePer.getNewCriteria();
		criteria.setCacheable(cacheable);
		return basePer.getCount(criteria);
	}
	
	public int getCount(Criteria criteria)
	{
		criteria.setCacheable(cacheable);
		return basePer.getCount(criteria);
	}

	public Long getMaxId()
	{
		Criteria criteria = basePer.getNewCriteria();

		criteria.setProjection(Projections.projectionList().add(Projections.max("id")));
		
		Long maxId = (Long) basePer.getRaw(criteria);
		if(maxId == null)
		{
			return 0L;
		}
		return maxId;
		
	}
	
	public Long getMaxEid()
	{
		Criteria criteria = basePer.getNewCriteria();

		criteria.setProjection(Projections.projectionList().add(Projections.max("eid")));
		
		Long maxId = (Long) basePer.getRaw(criteria);
		if(maxId == null)
		{
			return 0L;
		}
		return maxId;
		
	}
	
	/**
	 * 无条件、无翻页查询
	 */
	public List<T> getList(String orderby, boolean isAsc)
	{
	    Criteria criteria = basePer.getNewCriteria();
	    
        if(orderby != null && !orderby.equals(""))
        {
        	if(isAsc)
        	{
        		criteria.addOrder(Order.asc(orderby));
        	}
        	else
        	{
        		criteria.addOrder(Order.desc(orderby));
        	}
        }
        
        criteria.setCacheable(cacheable);
		return basePer.getList(criteria);
	}
	
	
	/**
	 * 无条件、有翻页查询
	 */
	public List<T> getList(int size, int page, String orderby, boolean isAsc)
	{
	    Criteria criteria = basePer.getNewCriteria();
	    
	    int first = (page - 1) * size;
	    int max = size;
	    criteria = criteria.setFirstResult(first);
        criteria.setMaxResults(max);
        
        if(orderby != null && !orderby.equals(""))
        {
        	if(isAsc)
        	{
        		criteria.addOrder(Order.asc(orderby));
        	}
        	else
        	{
        		criteria.addOrder(Order.desc(orderby));
        	}
        }
        
        criteria.setCacheable(cacheable);
		return basePer.getList(criteria);
	}
	
	public T get(Long id)
	{
		if(id != null)
		{
			Criteria criteria = basePer.getNewCriteria();
			Criterion likeCri = Restrictions.eq("id", id);
			criteria.add(likeCri);
			criteria.setCacheable(cacheable);
			return basePer.get(criteria);
		}
		return null;
	}
	
	public T get(Integer id)
	{
		if(id != null)
		{
			Criteria criteria = basePer.getNewCriteria();
			Criterion likeCri = Restrictions.eq("id", id);
			criteria.add(likeCri);
			criteria.setCacheable(cacheable);
			return basePer.get(criteria);
		}
		return null;
	}
	
	public T get(String id)
	{
		if(id != null)
		{
			Criteria criteria = basePer.getNewCriteria();
			Criterion likeCri = Restrictions.eq("id", id);
			criteria.add(likeCri);
			criteria.setCacheable(cacheable);
			return basePer.get(criteria);
		}
		return null;
	}
	
	/**
	 * @param id  值
	 * @param col 主键列
	 * @return
	 */
	public T get(String id, String col)
	{
		if(id != null)
		{
			Criteria criteria = basePer.getNewCriteria();
			Criterion likeCri = Restrictions.eq(col, id);
			criteria.add(likeCri);
			criteria.setCacheable(cacheable);
			return basePer.get(criteria);
		}
		return null;
	}	
	
	
	
	public List<T> getList(String orderby, boolean isAsc, String[] fieldNames)
	{
		basePer.setFieldNames(fieldNames);
		return getList(orderby, isAsc);
	}
	
	public List<T> getList(int size, int page, String orderby, boolean isAsc, String[] fieldNames)
	{
		basePer.setFieldNames(fieldNames);
		return getList(size, page, orderby, isAsc);
	}
	
	public T get(Long id, String[] fieldNames)
	{
		basePer.setFieldNames(fieldNames);
		return get(id);
	}
	
	public T get(String id, String[] fieldNames)
	{
		basePer.setFieldNames(fieldNames);
		return get(id);
	}
	
	public boolean isExist(Long id)
	{
		T t = get(id);
		if(t == null)
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	public boolean isExist(String id)
	{
		T t = get(id);
		if(t == null)
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	public void save(T t)
	{
		basePer.save(t);
	}
	public void update(T t)
	{
		basePer.update(t);
	}
	public void saveOrUpdate(T t)
	{
		basePer.saveOrUpdate(t);
	}
	public void delete(T t)
	{
		basePer.delete(t);
	}
	public void delete(Long id)
	{
		T t = get(id);
		basePer.delete(t);
	}
	public void delete(String id)
	{
		T t = get(id);
		basePer.delete(t);
	}
	
	public void bulkSave(List<T> tList)
	{
		basePer.bulkSave(tList);
	}
	
	public void bulkDelete(List<T> tList)
	{
		basePer.bulkDelete(tList);
	}
}