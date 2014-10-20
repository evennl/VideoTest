package edu.u_tokyo.kmjlab.liu.business.features;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import edu.u_tokyo.kmjlab.liu.base.business.BaseBusiness;
import edu.u_tokyo.kmjlab.liu.model.features.Roi;

public class RoiBu extends BaseBusiness<Roi>
{
	public RoiBu()
	{
		super(new Roi());
	}
	
	public List<Roi> listByFrame(Integer videoId, int frameNumber)
	{
		Criteria criteria = basePer.getNewCriteria();
		criteria.add(Restrictions.eq("videoId", videoId));
		criteria.add(Restrictions.eq("frame", frameNumber));
		
		return basePer.getList(criteria); 
	}
}
