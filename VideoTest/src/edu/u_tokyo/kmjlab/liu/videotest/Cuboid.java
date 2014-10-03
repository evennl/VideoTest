package edu.u_tokyo.kmjlab.liu.videotest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameGrabber.Exception;

import edu.u_tokyo.kmjlab.liu.business.features.CuboidBu;
import edu.u_tokyo.kmjlab.liu.model.features.CuboidFeature;

public class Cuboid
{
	public void extractCuboidFeatures(String videoFullFileName, float gaussianSigma, float gaborTao)
	{
		if(videoFullFileName == null || gaussianSigma <= 0 || gaborTao <= 0 || gaborTao >= 1)
		{
			return;
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

		IplImage grayFrame = null;
		IplImage gaussianImage = null;
		
		int i = 1;
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
			i++;
		}
		
		float[][][] response = cuboidResponse(processImageList, gaborTao);
		processImageList.clear();
		List<int[]> maximaPosition = findMaxima(response);
		response = null;
		
		CuboidBu cuboidBu = new CuboidBu();
		List<CuboidFeature> cuboidList = new ArrayList<CuboidFeature> ();
		i = 0;
		for(int[] position : maximaPosition)
		{
			CuboidFeature cuboidFeature = new CuboidFeature();
			cuboidFeature.setVideoName(videoFullFileName);
			cuboidFeature.setWidth(position[2]);
			cuboidFeature.setHeight(position[1]);
			cuboidFeature.setLength(position[0]);
			cuboidFeature.setParam1(gaussianSigma);
			cuboidFeature.setParam2(gaborTao);
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
	
	
	private float[][][] cuboidResponse(List<IplImage> gaussianList, double tao)
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
		
		double[] gaborKernelEven = generateGaborKernel(true, tao);
		double[] gaborKernelOdd = generateGaborKernel(false, tao);
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
				kernel[i + length] = -Math.sin(2 * Math.PI * omega * i) * Math.exp(-Math.pow(((double)i) / tao, 2));
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
