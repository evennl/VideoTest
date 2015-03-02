package edu.u_tokyo.kmjlab.liu.model.videoquery;

/**
 * GramMatrix entity. @author MyEclipse Persistence Tools
 */

public class GramMatrix
{
	private Integer id;
	private Integer videoId;
	private Integer m11;
	private Integer m12;
	private Integer m13;
	private Integer m21;
	private Integer m22;
	private Integer m23;
	private Integer m31;
	private Integer m32;
	private Integer m33;
	private Integer x;
	private Integer y;
	private Integer frame;
	private Float rankIncrease;

	// Constructors

	/** default constructor */
	public GramMatrix()
	{
	}

	/** full constructor */
	public GramMatrix(Integer videoId, Integer m11, Integer m12, Integer m13,
			Integer m21, Integer m22, Integer m23, Integer m31, Integer m32,
			Integer m33, Integer x, Integer y, Integer frame, Float rankIncrease)
	{
		this.videoId = videoId;
		this.m11 = m11;
		this.m12 = m12;
		this.m13 = m13;
		this.m21 = m21;
		this.m22 = m22;
		this.m23 = m23;
		this.m31 = m31;
		this.m32 = m32;
		this.m33 = m33;
		this.x = x;
		this.y = y;
		this.frame = frame;
		this.rankIncrease = rankIncrease;
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

	public Integer getM11()
	{
		return this.m11;
	}

	public void setM11(Integer m11)
	{
		this.m11 = m11;
	}

	public Integer getM12()
	{
		return this.m12;
	}

	public void setM12(Integer m12)
	{
		this.m12 = m12;
	}

	public Integer getM13()
	{
		return this.m13;
	}

	public void setM13(Integer m13)
	{
		this.m13 = m13;
	}

	public Integer getM21()
	{
		return this.m21;
	}

	public void setM21(Integer m21)
	{
		this.m21 = m21;
	}

	public Integer getM22()
	{
		return this.m22;
	}

	public void setM22(Integer m22)
	{
		this.m22 = m22;
	}

	public Integer getM23()
	{
		return this.m23;
	}

	public void setM23(Integer m23)
	{
		this.m23 = m23;
	}

	public Integer getM31()
	{
		return this.m31;
	}

	public void setM31(Integer m31)
	{
		this.m31 = m31;
	}

	public Integer getM32()
	{
		return this.m32;
	}

	public void setM32(Integer m32)
	{
		this.m32 = m32;
	}

	public Integer getM33()
	{
		return this.m33;
	}

	public void setM33(Integer m33)
	{
		this.m33 = m33;
	}

	public Integer getX()
	{
		return this.x;
	}

	public void setX(Integer x)
	{
		this.x = x;
	}

	public Integer getY()
	{
		return this.y;
	}

	public void setY(Integer y)
	{
		this.y = y;
	}

	public Integer getFrame()
	{
		return this.frame;
	}

	public void setFrame(Integer frame)
	{
		this.frame = frame;
	}

	public Float getRankIncrease()
	{
		return this.rankIncrease;
	}

	public void setRankIncrease(Float rankIncrease)
	{
		this.rankIncrease = rankIncrease;
	}
}