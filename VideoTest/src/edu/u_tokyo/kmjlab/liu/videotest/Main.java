package edu.u_tokyo.kmjlab.liu.videotest;

import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameGrabber.Exception;

public class Main
{
	public static void main(String[] args)
	{
		System.out.println("Hello world!");
		System.out.println("Hello world2!");

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
		

		final int frameRate = 30;
		int frameWidth = 640;
		int frameHeight = 480;

		FFmpegFrameRecorder recorder = new FFmpegFrameRecorder("D:/2.mp4", frameWidth, frameHeight, 0);
		recorder.setFormat("mp4");
		recorder.setFrameRate(frameRate);

		try
		{
			recorder.start();
		}
		catch (org.bytedeco.javacv.FrameRecorder.Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		IplImage frame = null;
		int i = 1;
		while (true)
		{
			System.out.println(i++);
			try
			{
				frame = grabber.grab();
				if(frame == null)
				{
					System.out.println("End of Video");
					break;
				}
				recorder.record(frame);
			}
			catch (Exception | org.bytedeco.javacv.FrameRecorder.Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
}
