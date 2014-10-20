package edu.u_tokyo.kmjlab.liu.model.features;

import java.util.Date;

/**
 * CuboidFeature entity. @author MyEclipse Persistence Tools
 */

public class CuboidFeature
{
	private Integer id;
	private Integer videoId;
	private Integer positionX;
	private Integer positionY;
	private Integer positionFrame;
	private String descriptor;
	private Date createTime;
	private Float sigma;
	private Float tao;

	// Constructors

	/** default constructor */
	public CuboidFeature()
	{
	}

	/** full constructor */
	public CuboidFeature(Integer videoId, Integer positionX, Integer positionY,
			Integer positionFrame, String descriptor, Date createTime,
			Float sigma, Float tao)
	{
		this.videoId = videoId;
		this.positionX = positionX;
		this.positionY = positionY;
		this.positionFrame = positionFrame;
		this.descriptor = descriptor;
		this.createTime = createTime;
		this.sigma = sigma;
		this.tao = tao;
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

	public Integer getVideoId()
	{
		return this.videoId;
	}

	public void setVideoId(Integer videoId)
	{
		this.videoId = videoId;
	}

	public Integer getPositionX()
	{
		return this.positionX;
	}

	public void setPositionX(Integer positionX)
	{
		this.positionX = positionX;
	}

	public Integer getPositionY()
	{
		return this.positionY;
	}

	public void setPositionY(Integer positionY)
	{
		this.positionY = positionY;
	}

	public Integer getPositionFrame()
	{
		return this.positionFrame;
	}

	public void setPositionFrame(Integer positionFrame)
	{
		this.positionFrame = positionFrame;
	}

	public String getDescriptor()
	{
		return this.descriptor;
	}

	public void setDescriptor(String descriptor)
	{
		this.descriptor = descriptor;
	}

	public Date getCreateTime()
	{
		return this.createTime;
	}

	public void setCreateTime(Date createTime)
	{
		this.createTime = createTime;
	}

	public Float getSigma()
	{
		return this.sigma;
	}

	public void setSigma(Float sigma)
	{
		this.sigma = sigma;
	}

	public Float getTao()
	{
		return this.tao;
	}

	public void setTao(Float tao)
	{
		this.tao = tao;
	}

}