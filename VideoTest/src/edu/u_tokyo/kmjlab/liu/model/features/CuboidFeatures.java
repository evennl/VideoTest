package edu.u_tokyo.kmjlab.liu.model.features;

import java.util.Date;

/**
 * CuboidFeatures entity. @author MyEclipse Persistence Tools
 */

public class CuboidFeatures
{
	private Integer id;
	private String videoName;
	private Integer width;
	private Integer height;
	private Integer length;
	private String descriptor;
	private Integer type;
	private Date timestamp;

	// Constructors

	/** default constructor */
	public CuboidFeatures()
	{
	}

	/** full constructor */
	public CuboidFeatures(Integer id, String videoName, Integer width, Integer height,
			Integer length, String descriptor, Integer type, Date timestamp)
	{
		this.id = id;
		this.videoName = videoName;
		this.width = width;
		this.height = height;
		this.length = length;
		this.descriptor = descriptor;
		this.type = type;
		this.timestamp = timestamp;
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

	public String getDescriptor()
	{
		return this.descriptor;
	}

	public void setDescriptor(String descriptor)
	{
		this.descriptor = descriptor;
	}

	public Integer getType()
	{
		return this.type;
	}

	public void setType(Integer type)
	{
		this.type = type;
	}

	public Date getTimestamp()
	{
		return this.timestamp;
	}

	public void setTimestamp(Date timestamp)
	{
		this.timestamp = timestamp;
	}

}