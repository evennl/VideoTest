package edu.u_tokyo.kmjlab.liu.business.videoquery;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import edu.u_tokyo.kmjlab.liu.base.business.BaseBusiness;
import edu.u_tokyo.kmjlab.liu.model.videoquery.Video;

public class VideoBu extends BaseBusiness<Video>
{
	public VideoBu()
	{
		super(new Video());
	}
	
	public Video getByName(String name)
	{
		if(name == null || "".equals(name))
		{
			return null;
		}
		Criteria criteria = basePer.getNewCriteria();
		criteria.add(Restrictions.eq("videoName", name));
		return basePer.get(criteria); 
	}
}
