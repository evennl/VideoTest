package edu.u_tokyo.kmjlab.liu.videotest;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_highgui;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameGrabber.Exception;

import edu.u_tokyo.kmjlab.liu.business.features.CuboidBu;
import edu.u_tokyo.kmjlab.liu.model.features.CuboidFeature;

public class Main
{
	public static void main(String[] args)
	{
		final float gaussianSigma = 2f;
		final float gaborTao = 0.9f;
		
		String bmpDir = "D:/cuts/Take directly_ENV01_end_01_3851_3902_resize/";
		//String videoFullFileName = "D:/test/jouon_01.avi";
		
		//videoToBmp(videoFullFileName, bmpDir);
		//Cuboid cuboid = new Cuboid();
		//cuboid.extractCuboidFeaturesFromVideo(videoFullFileName, gaussianSigma, gaborTao);
		//cuboid.extractCuboidFeaturesFromBmp(bmpDir, gaussianSigma, gaborTao);
		
		addMarkToBmp("D:\\cuts\\Take directly_ENV01_end_01_3851_3902_resize\\", "D:\\cuts\\Take directly_ENV01_end_01_3851_3902_resize\\", "D:\\cuts\\Take directly_ENV01_end_01_3851_3902_resize_mark\\");
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
		if(!bmpDir.endsWith("/") && !bmpDir.endsWith("\\"))
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
	
	static private void addMarkToBmp(String videoName, String bmpDirSrc, String bmpDirDst)
	{
		if(bmpDirSrc == null || videoName == null || bmpDirDst == null)
		{
			return;
		}
		File dirSrc = new File(bmpDirSrc);
		if(!dirSrc.exists() || !dirSrc.isDirectory())
		{
			return;
		}
		
		if(!bmpDirSrc.endsWith("/") && !bmpDirSrc.endsWith("\\"))
		{
			bmpDirSrc = bmpDirSrc + "/";
		}
		if(!bmpDirDst.endsWith("/") && !bmpDirDst.endsWith("\\"))
		{
			bmpDirDst = bmpDirDst + "/";
		}
		
		File dirDst = new File(bmpDirDst);
		if(!dirDst.exists())
		{
			dirDst.mkdirs();
		}
		
		List<String> fileNameList = Arrays.asList(dirSrc.list(new BmpFilter()));
		Collections.sort(fileNameList, new Comparator<String>()
		{
		    @Override
		    public int compare(String fileName1, String fileName2)
		    {
		        return fileName1.toLowerCase().compareTo(fileName2.toLowerCase());
		    }
		});
		
		
		IplImage firstImage = opencv_highgui.cvLoadImage(bmpDirSrc + fileNameList.get(0));
		int width = firstImage.width();
		opencv_highgui.cvSaveImage(bmpDirDst + fileNameList.get(0), firstImage);
		
		
		int i = 1;
		int length = fileNameList.size();
		final byte b0 = (byte) 0;
		final byte b255 = (byte) 255;
		
		
		CuboidBu cuboidBu = new CuboidBu();
		for(i = 1; i < length; i++)
		{
			String fileName = fileNameList.get(i);
			IplImage image = opencv_highgui.cvLoadImage(bmpDirSrc + fileName);
			
			int frameNumber = Integer.parseInt(fileName.split("\\.")[0]);
			List<CuboidFeature> list = cuboidBu.listByFrame(videoName, frameNumber);
			
			if(list != null && list.size() > 0)
			{
				BytePointer bp = image.arrayData();
				for(CuboidFeature feature : list)
				{
					int index = ((feature.getHeight() - 1) * width + feature.getWidth() - 1) * 3;
					bp.put(index, b0);
					bp.put(index + 1, b0);
					bp.put(index + 2, b255);
				}
			}
			opencv_highgui.cvSaveImage(bmpDirDst + fileName, image);
		}
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
