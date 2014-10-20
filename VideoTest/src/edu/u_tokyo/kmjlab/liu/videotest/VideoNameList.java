package edu.u_tokyo.kmjlab.liu.videotest;

import java.util.List;

import edu.u_tokyo.kmjlab.liu.business.features.VideoNameBu;
import edu.u_tokyo.kmjlab.liu.model.features.VideoName;

public class VideoNameList
{
	static private List<VideoName> videoNameList = null;

	static public String getName(int id)
	{
		if(videoNameList == null)
		{
			VideoNameBu videoNameBu = new VideoNameBu();
			videoNameList = videoNameBu.listAllVideoName();
		}
		for(VideoName videoName : videoNameList)
		{
			Integer videoId = videoName.getId();
			if(videoId != null && videoId.equals(id))
			{
				return videoName.getVideoName();
			}
		}
		return null;
	}
	
	static public Integer getId(String name)
	{
		if(name == null)
		{
			return null;
		}
		if(videoNameList == null)
		{
			VideoNameBu videoNameBu = new VideoNameBu();
			videoNameList = videoNameBu.listAllVideoName();
		}
		for(VideoName videoName : videoNameList)
		{
			String vName = videoName.getVideoName();
			if(vName != null && vName.equals(name))
			{
				return videoName.getId();
			}
		}
		return null;
	}
}
