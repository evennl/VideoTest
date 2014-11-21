package edu.u_tokyo.kmjlab.liu.videotest;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_highgui;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameGrabber.Exception;

import edu.u_tokyo.kmjlab.liu.business.features.CuboidBu;
import edu.u_tokyo.kmjlab.liu.model.features.CuboidFeature;

public class Cuboid
{
	public void extractCuboidFeaturesFromVideo(File videoFile, float gaussianSigma, float gaborOmega)
	{
		if(videoFile == null || gaussianSigma <= 0 || gaborOmega <= 0 || gaborOmega >= 1)
		{
			return;
		}
		
		FrameGrabber grabber = new FFmpegFrameGrabber(videoFile);
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

		IplImage grayFrame = null;
		IplImage gaussianImage = null;
		List<IplImage> processImageList = new ArrayList<IplImage> ();

		while (true)
		{
			try
			{
				IplImage frame = grabber.grab();
				if(frame == null)
				{
					break;
				}
				
				grayFrame = IplImage.create(opencv_core.cvGetSize(frame), opencv_core.IPL_DEPTH_8U, 1);
				opencv_imgproc.cvCvtColor(frame, grayFrame, opencv_imgproc.CV_BGR2GRAY);
				
				gaussianImage = IplImage.create(opencv_core.cvGetSize(frame), opencv_core.IPL_DEPTH_8U, 1);
				opencv_imgproc.cvSmooth(grayFrame, gaussianImage, opencv_imgproc.CV_GAUSSIAN, 0, 0, gaussianSigma, gaussianSigma);
				
				processImageList.add(gaussianImage);
				grayFrame = null;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		extractCuboidFeaturesFromIplImage(processImageList, gaussianSigma, gaborOmega, videoFile);
	}
	
	public void extractCuboidFeaturesFromBmp(File bmpDir, float gaussianSigma, float gaborOmega)
	{
		if(bmpDir == null || gaussianSigma <= 0 || gaborOmega <= 0 || gaborOmega >= 1)
		{
			return;
		}
		if(!bmpDir.exists() || !bmpDir.isDirectory())
		{
			return;
		}
		
		List<String> fileNameList = Arrays.asList(bmpDir.list(new BmpFilter()));
		Collections.sort(fileNameList, new Comparator<String>()
		{
		    @Override
		    public int compare(String fileName1, String fileName2)
		    {
		        return fileName1.toLowerCase().compareTo(fileName2.toLowerCase());
		    }
		});
		
		IplImage grayFrame = null;
		IplImage gaussianImage = null;
		List<IplImage> processImageList = new ArrayList<IplImage> ();
		String path = bmpDir.getAbsolutePath() + "\\";

		for(String fileName : fileNameList)
		{
			IplImage image = opencv_highgui.cvLoadImage(path + fileName);
			
			grayFrame = IplImage.create(opencv_core.cvGetSize(image), opencv_core.IPL_DEPTH_8U, 1);
			opencv_imgproc.cvCvtColor(image, grayFrame, opencv_imgproc.CV_BGR2GRAY);
			
			gaussianImage = IplImage.create(opencv_core.cvGetSize(image), opencv_core.IPL_DEPTH_8U, 1);
			opencv_imgproc.cvSmooth(grayFrame, gaussianImage, opencv_imgproc.CV_GAUSSIAN, 0, 0, gaussianSigma, gaussianSigma);
			
			processImageList.add(gaussianImage);
			
			grayFrame = null;
		}
		
		extractCuboidFeaturesFromIplImage(processImageList, gaussianSigma, gaborOmega, bmpDir);
	}
	
	private void extractCuboidFeaturesFromIplImage(List<IplImage> list, float gaussianSigma, float gaborOmega, File videoFile)
	{
		float[][][] response = cuboidResponse(list, gaborOmega);
		list.clear();
		List<int[]> maximaPosition = findMaxima(response);
		response = null;
		
		CuboidBu cuboidBu = new CuboidBu();
		List<CuboidFeature> cuboidList = new ArrayList<CuboidFeature> ();
		int i = 0;
		String videoName = videoFile.getName().split("\\.")[0];
		Integer videoId = VideoNameList.getId(videoName);
		for(int[] position : maximaPosition)
		{
			CuboidFeature cuboidFeature = new CuboidFeature();
			cuboidFeature.setVideoId(videoId);
			cuboidFeature.setPositionX(position[2]);
			cuboidFeature.setPositionY(position[1]);
			cuboidFeature.setPositionFrame(position[0]);
			cuboidFeature.setSigma(gaussianSigma);
			cuboidFeature.setOmega(gaborOmega);
			cuboidFeature.setCreateTime(new Date());
			cuboidList.add(cuboidFeature);
			i++;
			if(i >= 1000)
			{
				cuboidBu.bulkSave(cuboidList);
				cuboidList.clear();
				i = 0;
			}
		}
		
		if(cuboidList.size() > 0)
		{
			cuboidBu.bulkSave(cuboidList);
		}
	}
	
	private float[][][] cuboidResponse(List<IplImage> gaussianList, double omega)
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
		
		double[] gaborKernelEven = generateGaborKernel(true, omega);
		double[] gaborKernelOdd = generateGaborKernel(false, omega);
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
					
					if(i == 17 && j == 364 && k == 157)
					{
						System.out.println();
					}
					
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
	
	private double[] generateGaborKernel(boolean isEven, double omega)
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
				kernel[i + length] = -Math.sin(2 * Math.PI * omega * i / 29) * Math.exp(-Math.pow(((double)i) / tao / 29, 2));
			}
		}
		
		return kernel;
	}
	
	private List<int[]> findMaxima(float[][][] response)
	{
		int length = response.length;
		int height = response[0].length;
		int width = response[0][0].length;
		List<int[]> maximaPosition = new ArrayList<int []> ();
		
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
						temp > response[i - 1][j][k + 1]
					)
					{
						maximaPosition.add(new int[] {i + 1, j + 1, k + 1});
					}
					/*
					else if(temp < response[i][j][k - 1] &&
							temp < response[i][j][k + 1] &&
							temp < response[i][j - 1][k - 1] &&
							temp < response[i][j - 1][k] &&
							temp < response[i][j - 1][k + 1] &&
							temp < response[i][j + 1][k - 1] &&
							temp < response[i][j + 1][k] &&
							temp < response[i][j + 1][k + 1] &&
							temp < response[i + 1][j + 1][k - 1] &&
							temp < response[i + 1][j + 1][k] &&
							temp < response[i + 1][j + 1][k + 1] &&
							temp < response[i + 1][j - 1][k - 1] &&
							temp < response[i + 1][j - 1][k] &&
							temp < response[i + 1][j - 1][k + 1] &&
							temp < response[i + 1][j][k - 1] &&
							temp < response[i + 1][j][k] &&
							temp < response[i + 1][j][k + 1] &&
							temp < response[i - 1][j - 1][k - 1] &&
							temp < response[i - 1][j - 1][k] &&
							temp < response[i - 1][j - 1][k + 1] &&
							temp < response[i - 1][j + 1][k - 1] &&
							temp < response[i - 1][j + 1][k] &&
							temp < response[i - 1][j + 1][k + 1] &&
							temp < response[i - 1][j][k - 1] &&
							temp < response[i - 1][j][k] &&
							temp < response[i - 1][j][k + 1]
					)
					{
						maximaPosition.add(new int[] {i + 1, j + 1, k + 1});
					}
					*/
				}
			}
		}
		return maximaPosition;
	}
	
	private void closeGrabber(FrameGrabber grabber)
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
