package edu.u_tokyo.kmjlab.liu.videotest;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_highgui;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacpp.opencv_imgproc;

public class Main
{
	public static void main(String[] args)
	{
		FrameGrabber grabber = new FFmpegFrameGrabber("D:/person01_handwaving_d1_uncomp.avi");
		try
		{
			grabber.start();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		IplImage frame = null;
		IplImage gaussianImage = null;
		String destFileName = "D:/1/";
		int i = 1;
		
		System.out.println("Start!");
		while (true)
		{
			System.out.println(i);
			try
			{
				frame = grabber.grab();
				if(frame == null)
				{
					System.out.println("End of Video");
					break;
				}
				
				gaussianImage = IplImage.create(opencv_core.cvGetSize(frame), opencv_core.IPL_DEPTH_8U, 3);
				opencv_imgproc.cvSmooth(frame, gaussianImage, opencv_imgproc.CV_GAUSSIAN, 7, 7, 1f, 1f);
				
				
				opencv_highgui.cvSaveImage(destFileName + i + ".jpg", gaussianImage);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			i++;
		}
	}
}
