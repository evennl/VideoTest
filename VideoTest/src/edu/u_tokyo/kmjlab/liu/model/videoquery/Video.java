package edu.u_tokyo.kmjlab.liu.model.videoquery;

/**
 * Video entity. @author MyEclipse Persistence Tools
 */

public class Video
{
	private Integer id;
	private String videoName;
	private Integer width;
	private Integer height;
	private Integer length;
	private Boolean isTemplate;

	// Constructors

	/** default constructor */
	public Video()
	{
	}

	/** full constructor */
	public Video(String videoName, Integer width, Integer height,
			Integer length, Boolean isTemplate)
	{
		this.videoName = videoName;
		this.width = width;
		this.height = height;
		this.length = length;
		this.isTemplate = isTemplate;
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

	public Integer getWidth()
	{
		return this.width;
	}

	public void setWidth(Integer width)
	{
		this.width = width;
	}

	public Integer getHeight()
	{
		return this.height;
	}

	public void setHeight(Integer height)
	{
		this.height = height;
	}

	public Integer getLength()
	{
		return this.length;
	}

	public void setLength(Integer length)
	{
		this.length = length;
	}

	public Boolean getIsTemplate()
	{
		return this.isTemplate;
	}

	public void setIsTemplate(Boolean isTemplate)
	{
		this.isTemplate = isTemplate;
	}

}