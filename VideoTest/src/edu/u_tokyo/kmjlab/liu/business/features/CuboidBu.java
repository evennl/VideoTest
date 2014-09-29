package edu.u_tokyo.kmjlab.liu.business.features;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import edu.u_tokyo.kmjlab.liu.base.business.BaseBusiness;
import edu.u_tokyo.kmjlab.liu.model.features.CuboidFeatures;
import edu.u_tokyo.kmjlab.liu.util.CommonUtils;

public class CuboidBu extends BaseBusiness<CuboidFeatures>
{
	public CuboidBu()
	{
		super(new CuboidFeatures());
	}
	
	public List<CuboidFeatures> listByVideoName(String videoName)
	{
		if(videoName == null)
		{
			return new ArrayList<CuboidFeatures> ();
		}
		Criteria criteria = basePer.getNewCriteria();
		criteria.add(Restrictions.eq("videoName", videoName));
		return basePer.getList(criteria);
	}
	
	
	
	
	public List<CuboidFeatures> listByTime(String startTime, String endTime)
	{
		if(startTime == null && endTime == null)
		{
			return new ArrayList<CuboidFeatures> ();
		}
		else
		{
			Criteria criteria = basePer.getNewCriteria();
			if(startTime != null)
			{
				Date date = CommonUtils.String2Date(startTime + " 00:00:00");
				if(date == null)
				{
					return new ArrayList<CuboidFeatures> ();
				}
				criteria.add(Restrictions.ge("timestamp", date));
			}
			if(endTime != null)
			{
				Date date = CommonUtils.String2Date(endTime + " 23:59:59");
				if(date == null)
				{
					return new ArrayList<CuboidFeatures> ();
				}
				criteria.add(Restrictions.le("timestamp", date));
			}
			return basePer.getList(criteria);
		}
	}
}
