package edu.u_tokyo.kmjlab.liu.base.persistent;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;

import edu.u_tokyo.kmjlab.liu.util.CommonUtils;

public class BasePer<T>
{
	private T t;
    protected Session session;
    private enum OperFlag {SAVE, DELETE, UPDATE,SAVE_UPDATE}
    
 // 需强制加载的字段名
    private String[] fieldNames = null;
	   
	public String[] getFieldNames() {
		return fieldNames;
	}

	public void setFieldNames(String[] fieldNames) {
		this.fieldNames = fieldNames;
	}

	public BasePer(T t)
	{
		this.t = t;
		session = HibernateSessionFactory.getSession();
        this.session.setFlushMode(FlushMode.AUTO);
    }
	
	public Criteria getNewCriteria()
	{
		session = HibernateSessionFactory.getSession();
		return session.createCriteria(t.getClass());
	}
	
	public int getCount(Criteria criteria)
	{
		Transaction tx = session.beginTransaction();
		int count = 0;
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.rowCount());
		try
		{
			Long temp =(Long) criteria.setProjection(projList).uniqueResult();
			count = Integer.parseInt(temp.toString());
		}
		catch(Exception e)
		{
			System.out.println(CommonUtils.date2String(new Date()));
			e.printStackTrace();
			count = 0;
		}
		finally
		{
			session.flush();
	        tx.commit();
			session.close();
		}
        return count;
	}
	
	/**蟇ｹ迚ｹ螳壼ｭ玲ｮｵ隶｡謨ｰ�悟叙莉｣count(*)
	 * @param criteria
	 * @param countCol
	 * @return
	 */
	public int getCount(Criteria criteria, String countCol)
	{
		Transaction tx = session.beginTransaction();
		int count = 0;
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.count(countCol));
		try
		{
			Long temp =(Long) criteria.setProjection(projList).uniqueResult();
			count = Integer.parseInt(temp.toString());
		}
		catch(Exception e)
		{
			System.out.println(CommonUtils.date2String(new Date()));
			e.printStackTrace();
			count = 0;
		}
		finally
		{
			session.flush();
	        tx.commit();
			session.close();
		}
        return count;
	}
	
	public int getDistinctCount(Criteria criteria)
	{
		Transaction tx = session.beginTransaction();
		int count = 0;
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.countDistinct("id"));
		try
		{
			Long temp =(Long) criteria.setProjection(projList).uniqueResult();
			count = Integer.parseInt(temp.toString());
		}
		catch(Exception e)
		{
			System.out.println(CommonUtils.date2String(new Date()));
			e.printStackTrace();
			count = 0;
		}
		finally
		{
			session.flush();
	        tx.commit();
			session.close();
		}
        return count;
	}
	
	@SuppressWarnings("unchecked")
	public List<T> getList(Criteria criteria)
	{
		Transaction tx = session.beginTransaction();
		List<T> list = new ArrayList<T>();
		try
		{
			list = criteria.list();
			if(fieldNames != null)
			{
				initialize(list);
			}
		}
		catch(Exception e)
		{
			System.out.println(CommonUtils.date2String(new Date()));
			e.printStackTrace();
			list = new ArrayList<T>();
		}
		finally
		{
			session.flush();
	        tx.commit();
	        session.close();
		}
        return list;
	}
	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getRawList(Criteria criteria)
	{
		Transaction tx = session.beginTransaction();
		List list = new ArrayList();
		try
		{
			list = criteria.list();
			if(fieldNames != null)
			{
				initialize(list);
			}
		}
		catch(Exception e)
		{
			System.out.println(CommonUtils.date2String(new Date()));
			e.printStackTrace();
			list = new ArrayList();
		}
		finally
		{
			session.flush();
	        tx.commit();
	        session.close();
		}
        return list;
	}
	
	@SuppressWarnings("unchecked")
	public T get(Criteria criteria)
	{
		Transaction tx = session.beginTransaction();
		T entity = null;
		try
		{
			entity = (T) criteria.uniqueResult();
			if(fieldNames != null && entity != null)
			{
				initialize(entity);
			}
		}
		catch(Exception e)
		{
			System.out.println(CommonUtils.date2String(new Date()));
			e.printStackTrace();
			entity = null;
		}
		finally
		{
			session.flush();
	        tx.commit();
	        session.close();
		}
        return entity;
	}
	
	public Object getRaw(Criteria criteria)
	{
		Transaction tx = session.beginTransaction();
		Object entity = null;
		try
		{
			entity = criteria.uniqueResult();
			if(fieldNames != null && entity != null)
			{
				initialize(entity);
			}
		}
		catch(Exception e)
		{
			System.out.println(CommonUtils.date2String(new Date()));
			e.printStackTrace();
			entity = null;
		}
		finally
		{
			session.flush();
	        tx.commit();
	        session.close();
		}
        return entity;
	}
	
	/**
     * 增删改操作
     */
	public void save(T t)
    {
        operation(t, OperFlag.SAVE);
    }
	public void saveOrUpdate(T t)
    {
        operation(t, OperFlag.SAVE_UPDATE);
    }
	public void delete(T t)
    {
        operation(t, OperFlag.DELETE);
    }
	public void update(T t)
    {
        operation(t, OperFlag.UPDATE);
    }
	
	public void bulkSave(List<T> tList)
	{
		if(tList != null && tList.size() > 0)
		{
			try
	    	{
	    		HibernateSessionFactory.openTransaction();
	    		session = HibernateSessionFactory.getSession();
	    		int i = 1;
	    		for(T t : tList)
	    		{
	    			session.save(t);
	    			if(i == 500)
	    			{
	    				HibernateSessionFactory.commitTransaction();
	    				HibernateSessionFactory.openTransaction();
	    				i = 0;
	    			}
	    			i++;
	    		}
	    		HibernateSessionFactory.commitTransaction();
	    	}
			catch(Exception e)
	    	{
				System.out.println(CommonUtils.date2String(new Date()));
	    		e.printStackTrace();
	    		HibernateSessionFactory.RollbackTransaction();	
	    	}
	    	finally
	    	{
	    		HibernateSessionFactory.closeSession();
	        }
		}
	}
	
	public void bulkDelete(List<T> tList)
	{
		if(tList != null && tList.size() > 0)
		{
			try
	    	{
	    		HibernateSessionFactory.openTransaction();
	    		session = HibernateSessionFactory.getSession();
	    		int i = 1;
	    		for(T t : tList)
	    		{
	    			session.delete(t);
	    			if(i == 500)
	    			{
	    				HibernateSessionFactory.commitTransaction();
	    				HibernateSessionFactory.openTransaction();
	    			}
	    			i++;
	    		}
	    		HibernateSessionFactory.commitTransaction();
	    	}
			catch(Exception e)
	    	{
				System.out.println(CommonUtils.date2String(new Date()));
				e.printStackTrace();
	    		HibernateSessionFactory.RollbackTransaction();	
	    	}
	    	finally
	    	{
	    		HibernateSessionFactory.closeSession();
	        }
		}
	}
	
    private void operation(T t, OperFlag flag)
    {
    	try
    	{
    		HibernateSessionFactory.openTransaction();
    		session = HibernateSessionFactory.getSession();
    		switch (flag)
    		{
    			case SAVE:
    			{
    				session.save(t);
    				break;
    			}
    			case UPDATE:
    			{
    				session.merge(t);
    				break;
    			}
    			case DELETE:
    			{
    				session.delete(t);
    				break;
    			}
    			case SAVE_UPDATE:
    			{
    				session.saveOrUpdate(t);
    				break;
    			}
    			default:
    			{
    				break;
    			}
    		}
    		HibernateSessionFactory.commitTransaction();
    	}
    	catch(Exception e)
    	{
    		System.out.println(CommonUtils.date2String(new Date()));
			e.printStackTrace();
    		HibernateSessionFactory.RollbackTransaction();	
    	}
    	finally
    	{
    		HibernateSessionFactory.closeSession();
        }
    }
    
    /**
     * 以下两个initialize方法，用于Hibernate的强制加载。
     * 当Hibernate配置为延迟加载（lazy="true"或lazy="proxy"或默认配置），但有些属性（关联表的内容）又需要读取时，
     * 可调用以下两个方法的其中一个。
     * 使用之前先用setFieldNames方法设置好需要加载的属性名，再正常调用get或getList方法即可。
     * @param entity
     * @throws Exception 
     */
    private void initialize(Object entity) throws Exception
    {
    	if(fieldNames != null)
    	{
	    	int filedNumber = fieldNames.length;
			String[] getters = new String[filedNumber];
			int i;
			for(i = 0; i < filedNumber; i++)
			{
				getters[i] = "get" + fieldNames[i].substring(0, 1).toUpperCase() + fieldNames[i].substring(1);
			}
			for(i = 0; i < filedNumber; i++)
			{
				Hibernate.initialize(entity.getClass().getMethod(getters[i]).invoke(entity));
			}
    	}
    }
    
    private void initialize(List<T> list) throws Exception
    {
    	if(fieldNames != null)
    	{
	    	int filedNumber = fieldNames.length;
			String[] getters = new String[filedNumber];
			int i;
			for(i = 0; i < filedNumber; i++)
			{
				getters[i] = "get" + fieldNames[i].substring(0, 1).toUpperCase() + fieldNames[i].substring(1);
			}
			
			Iterator<T> iterator = list.iterator();
			while(iterator.hasNext())
			{
				T entity = iterator.next();
				for(i = 0; i < filedNumber; i++)
				{
					Hibernate.initialize(entity.getClass().getMethod(getters[i]).invoke(entity));		
				}
			}
    	}
    }
    
    public void truncate(String tablename)
    {
    	session = HibernateSessionFactory.getSession();
    	Transaction tx = session.beginTransaction();
    	try
    	{
    		session.createSQLQuery("truncate table " + tablename).executeUpdate();  
    		tx.commit();  
    	}
    	catch (Exception e)
    	{  
    		e.printStackTrace();
    		HibernateSessionFactory.RollbackTransaction();
    	}
    	finally
    	{  
    		HibernateSessionFactory.closeSession();
    	}
    }
}
