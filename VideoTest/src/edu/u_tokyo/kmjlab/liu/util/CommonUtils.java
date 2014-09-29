package edu.u_tokyo.kmjlab.liu.util;

import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class CommonUtils
{
	public static final String FORMAT = "yyyy-MM-dd";
	public static final String FORMAT_FULL = "yyyy-MM-dd HH:mm:ss";
	public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    
	/** 将字符串转换成ISO-8859-1
     * @param s 输入串
     * @return 转后的字符串
     */
    public static String to8859(String s)
    {
    	String ret = "";
    	try
    	{
    		ret = new String(s.getBytes("GBK"), "8859_1");
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return ret;
    }
    
    public static String date2String(Date date)
	{
		if(date != null)
		{
			SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_FULL);
			return  sdf.format(date).toString();
		}
		else
		{
			return "";
		}
	}
    
    public static String date2String(Date date, String format)
	{
		if(date != null)
		{
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return  sdf.format(date).toString();
		}
		else
		{
			return "";
		}
	}
	
    public static String long2String(Long number)
	{
		if(number != null)
		{
			return number.toString();
		}
		else
		{
			return "";
		}
	}
    
    public static Long String2Long(String number)
    {
    	Long longNumber = null;
    	if(number != null)
    	{
    		try
    		{
    			longNumber =  Long.parseLong(number);
    		}
    		catch(NumberFormatException e)
    		{
    			return null;
    		}
    	}
    	else
    	{
    		return null;
    	}
    	return longNumber;
    }
    
    public static Date String2Date(String strDate)
	{
    	if(strDate == null || strDate.equals(""))
    	{
    		return null;
    	}
    	else
    	{
			DateFormat format = new SimpleDateFormat(FORMAT_FULL);
			Date date;
			try
			{
				date = (Date)format.parse(strDate);
			}
			catch(ParseException e)
			{
				// TODO Auto-generated catch block
				return null;
			}
			return date;
    	}
	}
    
    public static Date String2Date(String strDate, String format)
	{
    	if(strDate == null || strDate.equals(""))
    	{
    		return null;
    	}
    	else
    	{
			DateFormat dateFormat = new SimpleDateFormat(format);
			Date date;
			try
			{
				date = (Date)dateFormat.parse(strDate);
			}
			catch(ParseException e)
			{
				// TODO Auto-generated catch block
				return null;
			}
			return date;
    	}
	}
}


