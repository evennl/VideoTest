package edu.u_tokyo.kmjlab.liu.videoquery;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.bytedeco.javacpp.opencv_highgui;
import org.bytedeco.javacpp.opencv_core.IplImage;

public class CutImages
{
	public static void main(String[] args) throws FileNotFoundException
	{
		String path = "D:/1/";
		File dir = new File(path);
		String[] fileNameList = dir.list();
		
		int x = 112, y = 95, w = 88, h = 145;
		Iterator<ImageReader> iterator = ImageIO.getImageReadersByFormatName("bmp");
        ImageReader reader = (ImageReader)iterator.next();
        
        int i = 1;
		for(String fileName : fileNameList)
		{
			String src = path + fileName;
			String dest = path + "cut_" + fileName;
			ImageReadParam param = reader.getDefaultReadParam();
	        InputStream in;
			try
			{
				in = new FileInputStream(src);
				ImageInputStream iis = ImageIO.createImageInputStream(in);
				reader.setInput(iis, true);
				Rectangle rect = new Rectangle(x, y, w, h);
				param.setSourceRegion(rect);
				BufferedImage bi = reader.read(0, param);
				ImageIO.write(bi, "bmp", new File(dest));
			}
			catch (IOException  e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
			i++;
		}
		
		
		
		
        
	}
}
