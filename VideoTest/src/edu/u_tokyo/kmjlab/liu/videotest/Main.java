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
		
		FrameGrabber grabber = new FFmpegFrameGrabber("D:/test/person01_handwaving_d1_uncomp.avi");
		try
		{
			grabber.start();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		IplImage grayFrame = null;
		IplImage gaussianImage = null;
		String destFileName = "D:/1/";
		int i = 1;
		List<IplImage> processImageList = new ArrayList<IplImage> ();
		List<IplImage> originalImageList = new ArrayList<IplImage> ();
		
		System.out.println("Start!");
		
		/*
		String path = "D:/test/img2/";
		File dir = new File(path);
		String[] fileNames = dir.list();
		
		for(String fileName : fileNames)
		{
			IplImage image = opencv_highgui.cvLoadImage(path + fileName);
			
			grayFrame = IplImage.create(opencv_core.cvGetSize(image), opencv_core.IPL_DEPTH_8U, 1);
			opencv_imgproc.cvCvtColor(image, grayFrame, opencv_imgproc.CV_BGR2GRAY);
			
			gaussianImage = IplImage.create(opencv_core.cvGetSize(grayFrame), opencv_core.IPL_DEPTH_8U, 1);
			opencv_imgproc.cvSmooth(grayFrame, gaussianImage, opencv_imgproc.CV_GAUSSIAN, 13, 13, 2f, 2f);
			
			processImageList.add(gaussianImage);
			originalImageList.add(grayFrame);
		}
		*/
		
		
		while (true)
		{
			try
			{
				IplImage frame = grabber.grab();
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
				originalImageList.add(opencv_core.cvCloneImage(frame));
				grayFrame = null;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			i++;
		}
		
		
		float[][][] response = cuboidResponse(processImageList);
		processImageList.clear();
		findMaxima(response, originalImageList);
		
		
		for(i = 0; i < originalImageList.size(); i++)
		{
			opencv_highgui.cvSaveImage(destFileName + i + ".bmp", originalImageList.get(i));
		}
	}
	
	
	
	
	
	static private double[] getGaborKernel(boolean isEven, double omega)
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
	
	static private float[][][] cuboidResponse(List<IplImage> gaussianList)
	{
		if(gaussianList == null || gaussianList.size() == 0)
		{
			return null;
		}
		IplImage firstIamge = gaussianList.get(0);
		int width = firstIamge.width();
		int height = firstIamge.height();
		int length = gaussianList.size();
		
		float[][][] response = new float[length][height][width];
		
		double[] gaborKernelEven = getGaborKernel(true, 0.9);
		double[] gaborKernelOdd = getGaborKernel(false, 0.9);
		int kernelLength = gaborKernelEven.length;
		
		for(int i = 0; i < length; i++)
		{	
			int imgConvStartIndex = i - kernelLength / 2;
			int kernelConvStartIndex = imgConvStartIndex < 0 ? -imgConvStartIndex : 0;
			imgConvStartIndex = imgConvStartIndex < 0 ? 0 : imgConvStartIndex;
			
			int imgConvEndIndex = i + kernelLength / 2 + 1;
			imgConvEndIndex = imgConvEndIndex >= length ? length : imgConvEndIndex;
			
			
			for(int j = 0; j < height; j++)
			{
				for(int k = 0; k < width; k++)
				{
					double convResultEven = 0;
					double convResultOdd = 0;
					int kernelI = kernelConvStartIndex;
					for(int imgI = imgConvStartIndex; imgI < imgConvEndIndex; imgI++)
					{
						byte pixel = gaussianList.get(imgI).arrayData().get(j * width + k);
						
						convResultEven += (pixel & 0xFF) * gaborKernelEven[kernelI];
						convResultOdd += (pixel & 0xFF) * gaborKernelOdd[kernelI++];
					}
					response[i][j][k] = (float) (Math.pow(convResultEven, 2) + Math.pow(convResultOdd, 2));
				}
			}
		}
		return response;
	}
	
	static private void findMaxima(float[][][] response, List<IplImage> originalList)
	{
		int length = response.length;
		int height = response[0].length;
		int width = response[0][0].length;
		for(int i = 1; i < length - 1; i++)
		{
			for(int j = 1; j < height - 1; j++)
			{
				for(int k = 1; k < width - 1; k++)
				{
					float temp = response[i][j][k];
					if(temp > response[i][j][k - 1] &&
						temp > response[i][j][k + 1] &&
						temp > response[i][j - 1][k - 1] &&
						temp > response[i][j - 1][k] &&
						temp > response[i][j - 1][k + 1] &&
						temp > response[i][j + 1][k - 1] &&
						temp > response[i][j + 1][k] &&
						temp > response[i][j + 1][k + 1] &&
						temp > response[i + 1][j + 1][k - 1] &&
						temp > response[i + 1][j + 1][k] &&
						temp > response[i + 1][j + 1][k + 1] &&
						temp > response[i + 1][j - 1][k - 1] &&
						temp > response[i + 1][j - 1][k] &&
						temp > response[i + 1][j - 1][k + 1] &&
						temp > response[i + 1][j][k - 1] &&
						temp > response[i + 1][j][k] &&
						temp > response[i + 1][j][k + 1] &&
						temp > response[i - 1][j - 1][k - 1] &&
						temp > response[i - 1][j - 1][k] &&
						temp > response[i - 1][j - 1][k + 1] &&
						temp > response[i - 1][j + 1][k - 1] &&
						temp > response[i - 1][j + 1][k] &&
						temp > response[i - 1][j + 1][k + 1] &&
						temp > response[i - 1][j][k - 1] &&
						temp > response[i - 1][j][k] &&
						temp > response[i - 1][j][k + 1] &&
						originalList.get(i - 1).arrayData().get((j * width + k) * 3) != originalList.get(i).arrayData().get((j * width + k) * 3) &&
						originalList.get(i + 1).arrayData().get((j * width + k) * 3) != originalList.get(i).arrayData().get((j * width + k) * 3) &&
						originalList.get(i - 1).arrayData().get((j * width + k) * 3 + 1) != originalList.get(i).arrayData().get((j * width + k) * 3 + 1) &&
						originalList.get(i + 1).arrayData().get((j * width + k) * 3 + 1) != originalList.get(i).arrayData().get((j * width + k) * 3 + 1) &&
						originalList.get(i - 1).arrayData().get((j * width + k) * 3 + 2) != originalList.get(i).arrayData().get((j * width + k) * 3 + 2) &&
						originalList.get(i + 1).arrayData().get((j * width + k) * 3 + 2) != originalList.get(i).arrayData().get((j * width + k) * 3 + 2)
					)
					{
						originalList.get(i).arrayData().put((j * width + k) * 3, (byte) 0);
						originalList.get(i).arrayData().put((j * width + k) * 3 + 1, (byte) 0);
						originalList.get(i).arrayData().put((j * width + k) * 3 + 2, (byte) 255);
						
						//originalList.get(i).arrayData().put(j * width + k, (byte) 0);
					}
				}
			}
		}
	}
}
