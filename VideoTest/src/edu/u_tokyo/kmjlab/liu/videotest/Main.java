package edu.u_tokyo.kmjlab.liu.videotest;

import java.io.File;

import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_highgui;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameGrabber.Exception;

public class Main
{
	public static void main(String[] args)
	{
		final float gaussianSigma = 2f;
		final float gaborTao = 0.9f;
		
		String bmpDir = "D:/test/jouon_01/";
		String videoFullFileName = "D:/test/jouon_01.avi";
		
		//videoToBmp(videoFullFileName, bmpDir);
		Cuboid cuboid = new Cuboid();
		cuboid.extractCuboidFeatures(videoFullFileName, gaussianSigma, gaborTao);
	}
	
	
	static private void videoToBmp(String videoFullFileName, String bmpDir)
	{
		if(videoFullFileName == null || bmpDir == null)
		{
			return;
		}
		File dir = new File(bmpDir);
		if(!dir.exists() || !dir.isDirectory())
		{
			return;
		}
		if(!bmpDir.endsWith("/") || bmpDir.endsWith("\\"))
		{
			bmpDir = bmpDir + "/";
		}
		
		FrameGrabber grabber = new FFmpegFrameGrabber(videoFullFileName);
		try
		{
			grabber.start();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			closeGrabber(grabber);
			return;
		}
		
		int i = 1;
		while (true)
		{
			try
			{
				IplImage frame = grabber.grab();
				if(frame == null)
				{
					break;
				}
				
				opencv_highgui.cvSaveImage(bmpDir + i + ".bmp", frame);
				i++;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				closeGrabber(grabber);
				return;
			}
		}
		closeGrabber(grabber);
	}
	
	
	static private void closeGrabber(FrameGrabber grabber)
	{
		if(grabber != null)
		{
			try
			{
				grabber.release();
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
