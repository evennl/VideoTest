package edu.u_tokyo.kmjlab.liu.model.features;

/**
 * VideoName entity. @author MyEclipse Persistence Tools
 */

public class VideoName
{
	private Integer id;
	private String videoName;

	// Constructors

	/** default constructor */
	public VideoName()
	{
	}

	/** minimal constructor */
	public VideoName(Integer id)
	{
		this.id = id;
	}

	/** full constructor */
	public VideoName(Integer id, String videoName)
	{
		this.id = id;
		this.videoName = videoName;
	}

	// Property accessors

	public Integer getId()
	{
		return this.id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public String getVideoName()
	{
		return this.videoName;
	}

	public void setVideoName(String videoName)
	{
		this.videoName = videoName;
	}

}