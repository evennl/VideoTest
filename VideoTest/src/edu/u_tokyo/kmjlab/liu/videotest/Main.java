package edu.u_tokyo.kmjlab.liu.videotest;

import java.util.ArrayList;
import java.util.List;

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
		IplImage grayFrame = null;
		IplImage gaussianImage = null;
		String destFileName = "D:/1/";
		int i = 1;
		List<IplImage> processImageList = new ArrayList<IplImage> ();
		List<IplImage> originalImageList = new ArrayList<IplImage> ();
		
		System.out.println("Start!");
		
		
		
		long memory1 = Runtime.getRuntime().freeMemory();
		
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
				
				grayFrame = IplImage.create(opencv_core.cvGetSize(frame), opencv_core.IPL_DEPTH_8U, 1);
				opencv_imgproc.cvCvtColor(frame, grayFrame, opencv_imgproc.CV_BGR2GRAY);
				
				gaussianImage = IplImage.create(opencv_core.cvGetSize(frame), opencv_core.IPL_DEPTH_8U, 1);
				opencv_imgproc.cvSmooth(grayFrame, gaussianImage, opencv_imgproc.CV_GAUSSIAN, 13, 13, 2f, 2f);
				
				processImageList.add(gaussianImage);
				originalImageList.add(frame);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			i++;
		}
		
		
		long memory2 = Runtime.getRuntime().freeMemory();
		System.out.println((memory1 - memory2) / 1024);
		
		cuboidResponse(processImageList);
		findMaxima(processImageList, originalImageList);
		
		
		for(i = 0; i < originalImageList.size(); i++)
		{
			opencv_highgui.cvSaveImage(destFileName + i + ".jpg", originalImageList.get(i));
		}
	}
	
	
	
	
	
	static private double[] gabor(boolean isEven, double omega)
	{
		if(omega <= 0 || omega >= 1)
		{
			return null;
		}
		double tao = 4 / omega;
		
		int length = (int) (Math.ceil(6 * tao));
		length = length / 2;
		double[] kernel = new double[length * 2 + 1];
		
		if(isEven)
		{
			for(int i = -length; i <= length; i++)
			{
				kernel[i + length] = -Math.cos(2 * Math.PI * omega * i) * Math.exp(-Math.pow(((double)i) / tao, 2));
			}
		}
		else
		{
			for(int i = -length; i <= length; i++)
			{
				kernel[i + length] = -Math.sin(2 * Math.PI * omega * i) * Math.exp(-Math.pow(((double)i) / tao, 2));
			}
		}
		return kernel;
	}
	
	static private void cuboidResponse(List<IplImage> gaussianList)
	{
		
	}
	
	static private void findMaxima(List<IplImage> responseList, List<IplImage> originalList)
	{
		
	}
}
