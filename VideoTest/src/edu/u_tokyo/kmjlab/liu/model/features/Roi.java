package edu.u_tokyo.kmjlab.liu.model.features;

import java.util.Date;

/**
 * Roi entity. @author MyEclipse Persistence Tools
 */

public class Roi
{
	private Integer id;
	private Integer videoId;
	private Integer x1;
	private Integer x2;
	private Integer y1;
	private Integer y2;
	private Integer frame;
	private Date createTime;

	// Constructors

	/** default constructor */
	public Roi()
	{
	}

	/** minimal constructor */
	public Roi(Integer id)
	{
		this.id = id;
	}

	/** full constructor */
	public Roi(Integer id, Integer videoId, Integer x1, Integer x2, Integer y1,
			Integer y2, Integer frame, Date createTime)
	{
		this.id = id;
		this.videoId = videoId;
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
		this.frame = frame;
		this.createTime = createTime;
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

	public Integer getX1()
	{
		return this.x1;
	}

	public void setX1(Integer x1)
	{
		this.x1 = x1;
	}

	public Integer getX2()
	{
		return this.x2;
	}

	public void setX2(Integer x2)
	{
		this.x2 = x2;
	}

	public Integer getY1()
	{
		return this.y1;
	}

	public void setY1(Integer y1)
	{
		this.y1 = y1;
	}

	public Integer getY2()
	{
		return this.y2;
	}

	public void setY2(Integer y2)
	{
		this.y2 = y2;
	}

	public Integer getFrame()
	{
		return this.frame;
	}

	public void setFrame(Integer frame)
	{
		this.frame = frame;
	}

	public Date getCreateTime()
	{
		return this.createTime;
	}

	public void setCreateTime(Date createTime)
	{
		this.createTime = createTime;
	}

}