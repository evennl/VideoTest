package edu.u_tokyo.kmjlab.liu.business.features;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import edu.u_tokyo.kmjlab.liu.base.business.BaseBusiness;
import edu.u_tokyo.kmjlab.liu.model.features.CuboidFeature;
import edu.u_tokyo.kmjlab.liu.util.CommonUtils;

public class CuboidBu extends BaseBusiness<CuboidFeature>
{
	public CuboidBu()
	{
		super(new CuboidFeature());
	}
	
	public List<CuboidFeature> listByVideoName(String videoName)
	{
		if(videoName == null)
		{
			return new ArrayList<CuboidFeature> ();
		}
		Criteria criteria = basePer.getNewCriteria();
		criteria.add(Restrictions.eq("videoName", videoName));
		return basePer.getList(criteria);
	}
	
	public List<CuboidFeature> listByTime(String startTime, String endTime)
	{
		if(startTime == null && endTime == null)
		{
			return new ArrayList<CuboidFeature> ();
		}
		else
		{
			Criteria criteria = basePer.getNewCriteria();
			if(startTime != null)
			{
				Date date = CommonUtils.String2Date(startTime + " 00:00:00");
				if(date == null)
				{
					return new ArrayList<CuboidFeature> ();
				}
				criteria.add(Restrictions.ge("timestamp", date));
			}
			if(endTime != null)
			{
				Date date = CommonUtils.String2Date(endTime + " 23:59:59");
				if(date == null)
				{
					return new ArrayList<CuboidFeature> ();
				}
				criteria.add(Restrictions.le("timestamp", date));
			}
			return basePer.getList(criteria);
		}
	}
	
	public List<CuboidFeature> listByFrame(Integer videoId, int frameNumber)
	{
		Criteria criteria = basePer.getNewCriteria();
		criteria.add(Restrictions.eq("videoId", videoId));
		criteria.add(Restrictions.eq("positionFrame", frameNumber));
		
		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Projections.property("positionX"), "positionX");
		projectionList.add(Projections.property("positionY"), "positionY");
		
		criteria.setProjection(projectionList);
		criteria.setResultTransformer(Transformers.aliasToBean(CuboidFeature.class));
		
		return basePer.getList(criteria); 
	}
}
