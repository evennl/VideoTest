package edu.u_tokyo.kmjlab.liu.base.persistent;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class HibernateSessionFactory
{
	private static final ThreadLocal<Session> threadLocal = new ThreadLocal<Session>();
	private static final ThreadLocal<Transaction> trThreadLocal = new ThreadLocal<Transaction>();
	private static Configuration configuration = new Configuration();
	private static org.hibernate.SessionFactory sessionFactory;
 
	static
	{
		try
		{
			configuration.configure("hibernate.cfg.xml");
			StandardServiceRegistryBuilder standardServiceRegistryBuilder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
			StandardServiceRegistry standardServiceRegistry = standardServiceRegistryBuilder.build();
			sessionFactory = configuration.buildSessionFactory(standardServiceRegistry);
			
		}
		catch (Exception e)
		{
			System.err.println("%%%% Error Creating SessionFactory %%%%");
			e.printStackTrace();
		}
	}
 
	/**
	 * Returns the ThreadLocal Session instance. Lazy initialize the
	 * <code>SessionFactory</code> if needed.
	 * 
	 * @return Session
	 * @throws HibernateException
	 */
	public static Session getSession() throws HibernateException
	{
		Session session = (Session) threadLocal.get();
		if(session == null || !session.isOpen())
		{
			if (sessionFactory == null)
			{
				rebuildSessionFactory();
			}
			session = (sessionFactory != null) ? sessionFactory.openSession() : null;
			threadLocal.set(session);
		}
		return session;
	}
 
	/**
     * Rebuild hibernate session factory
     */
	public static void rebuildSessionFactory()
	{
		try
		{
			configuration.configure("hibernate.cfg.xml");
			StandardServiceRegistryBuilder standardServiceRegistryBuilder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
			StandardServiceRegistry standardServiceRegistry = standardServiceRegistryBuilder.build();
			sessionFactory = configuration.buildSessionFactory(standardServiceRegistry);
		}
		catch(Exception e)
		{
			System.err.println("%%%% Error Creating SessionFactory %%%%");
			e.printStackTrace();
		}
	}
 
	/**
	 * Close the single hibernate session instance.
	 * 
	 * @throws HibernateException
	 */
	public static void closeSession() throws HibernateException
	{
		Session session = (Session) threadLocal.get();
		threadLocal.set(null);
		if (session != null)
		{
			session.close();
		}
	}

	/**
	 * return hibernate configuration
	 */
	public static Configuration getConfiguration()
	{
        return configuration;
    }

    public static void openTransaction()
    {
        Transaction tr = trThreadLocal.get();
        if (tr == null)
        {
            tr = getSession().beginTransaction();
            trThreadLocal.set(tr);
        }
    }

    public static void commitTransaction()
    {
        Transaction tr = trThreadLocal.get();
        if (tr != null && !tr.wasCommitted() && !tr.wasRolledBack())
        {
            tr.commit();
            trThreadLocal.set(null);
        }
    }

    public static void RollbackTransaction()
    {
        Transaction tr = trThreadLocal.get();
        if (tr != null && !tr.wasCommitted() && !tr.wasRolledBack())
        {
            tr.rollback();
            trThreadLocal.set(null);
        }
    }
}
