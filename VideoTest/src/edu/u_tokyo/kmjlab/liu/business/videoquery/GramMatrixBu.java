package edu.u_tokyo.kmjlab.liu.business.videoquery;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import edu.u_tokyo.kmjlab.liu.base.business.BaseBusiness;
import edu.u_tokyo.kmjlab.liu.model.videoquery.GramMatrix;

public class GramMatrixBu extends BaseBusiness<GramMatrix>
{
	public GramMatrixBu()
	{
		super(new GramMatrix());
	}
	
	public List<GramMatrix> getListByVideoId(int videoId)
	{
		Criteria criteria = basePer.getNewCriteria();
		criteria.add(Restrictions.eq("videoId", videoId));
		List<GramMatrix> list = basePer.getList(criteria);
		if(list == null)
		{
			list = new ArrayList<GramMatrix>();
		}
		return list;
	}
	
	public List<GramMatrix> getListByVideoId(int size, int page, int videoId)
	{
		Criteria criteria = basePer.getNewCriteria();
		
		int first = (page - 1) * size;
	    int max = size;
	    criteria = criteria.setFirstResult(first);
        criteria.setMaxResults(max);
		
		criteria.add(Restrictions.eq("videoId", videoId));
		List<GramMatrix> list = basePer.getList(criteria);
		if(list == null)
		{
			list = new ArrayList<GramMatrix>();
		}
		return list;
	}
	
	public int getCountByVideoId(int videoId)
	{
		Criteria criteria = basePer.getNewCriteria();
		criteria.add(Restrictions.eq("videoId", videoId));
		return basePer.getCount(criteria);
	}
	
	public List<GramMatrix> getList(int videoId, int frameStart, int frameEnd)
	{
		Criteria criteria = basePer.getNewCriteria();
		criteria.add(Restrictions.eq("videoId", videoId));
		criteria.add(Restrictions.ge("frame", frameStart));
		criteria.add(Restrictions.lt("frame", frameEnd));
		
		List<GramMatrix> list = basePer.getList(criteria);
		if(list == null)
		{
			list = new ArrayList<GramMatrix>();
		}
		return list;
	}
}
