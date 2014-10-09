package edu.u_tokyo.kmjlab.liu.videotest;

import java.io.File;
import java.io.FilenameFilter;

public class BmpFilter implements FilenameFilter
{
	@Override
	public boolean accept(File dir, String fileName)
	{
		if (fileName.toLowerCase().endsWith(".bmp"))
		{
			return true;
		}
		else
		{
			return false;
		}  
	}

}
