package edu.u_tokyo.kmjlab.liu.business.features;

import java.util.List;

import org.hibernate.Criteria;

import edu.u_tokyo.kmjlab.liu.base.business.BaseBusiness;
import edu.u_tokyo.kmjlab.liu.model.features.VideoName;

public class VideoNameBu extends BaseBusiness<VideoName>
{
	public VideoNameBu()
	{
		super(new VideoName());
	}

	public List<VideoName> listAllVideoName()
	{
		Criteria criteria = basePer.getNewCriteria();
		return basePer.getList(criteria);
	}
}
