package edu.u_tokyo.kmjlab.liu.model.features;

import java.util.Date;

/**
 * CuboidFeatures entity. @author MyEclipse Persistence Tools
 */

public class CuboidFeature
{
	private Integer id;
	private String videoName;
	private Integer width;
	private Integer height;
	private Integer length;
	private String descriptor;
	private Integer type;
	private Date createTime;
	private Float param1;			// Gaussian sigma
	private Float param2;			// Gabor tao
	private Float param3;			// blank now

	// Constructors

	/** default constructor */
	public CuboidFeature()
	{
	}

	/** full constructor */
	public CuboidFeature(Integer id, String videoName, Integer width, Integer height,
			Integer length, String descriptor, Integer type,
			Date createTime, Float param1, Float param2, Float param3)
	{
		this.id = id;
		this.videoName = videoName;
		this.width = width;
		this.height = height;
		this.length = length;
		this.descriptor = descriptor;
		this.type = type;
		this.createTime = createTime;
		this.param1 = param1;
		this.param2 = param2;
		this.param3 = param3;
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

	public Date getCreateTime()
	{
		return this.createTime;
	}

	public void setCreateTime(Date createTime)
	{
		this.createTime = createTime;
	}

	public Float getParam1()
	{
		return this.param1;
	}

	public void setParam1(Float param1)
	{
		this.param1 = param1;
	}

	public Float getParam2()
	{
		return this.param2;
	}

	public void setParam2(Float param2)
	{
		this.param2 = param2;
	}

	public Float getParam3()
	{
		return this.param3;
	}

	public void setParam3(Float param3)
	{
		this.param3 = param3;
	}

}