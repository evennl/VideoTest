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
		final float gaussianSigma = 3f;
		final float gaborOmega = 0.9f;
		
		String bmpDir = "D:/cuts/jouon_08_44_06_44_15_resize/";
		File file = new File(bmpDir);
		
		
		
		//videoToBmp(videoFullFileName, bmpDir);
		//Cuboid cuboid = new Cuboid();
		//cuboid.extractCuboidFeaturesFromVideo(videoFullFileName, gaussianSigma, gaborOmega);
		//cuboid.extractCuboidFeaturesFromBmp(file, gaussianSigma, gaborOmega);
		
		//File bmpDirDst = new File("D:/test/test01_mark/");
		File bmpDirDst = new File("D:/cuts/jouon_08_44_06_44_15_resize_foregroundmark/");
		addMarkToBmp(file, bmpDirDst, true);
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
	
	static private void addMarkToBmp(File bmpDirSrc, File bmpDirDst, boolean useBackgroundSubtraction)
	{
		if(bmpDirSrc == null || !bmpDirSrc.exists() || !bmpDirSrc.isDirectory() || bmpDirDst == null)
		{
			return;
		}
		
		if(!bmpDirDst.exists())
		{
			bmpDirDst.mkdirs();
		}
		if(!bmpDirDst.isDirectory())
		{
			return;
		}
		
		List<String> fileNameList = Arrays.asList(bmpDirSrc.list(new BmpFilter()));
		Collections.sort(fileNameList, new Comparator<String>()
		{
		    @Override
		    public int compare(String fileName1, String fileName2)
		    {
		        return fileName1.toLowerCase().compareTo(fileName2.toLowerCase());
		    }
		});
		
		String bmpSrcPath = bmpDirSrc.getAbsolutePath() + "\\";
		String bmpDstPath = bmpDirDst.getAbsolutePath() + "\\";
		IplImage firstImage = opencv_highgui.cvLoadImage(bmpSrcPath + fileNameList.get(0));
		int width = firstImage.width();
		opencv_highgui.cvSaveImage(bmpDstPath + fileNameList.get(0), firstImage);
		
		
		int i = 1;
		int length = fileNameList.size();
		final byte b0 = (byte) 0;
		final byte b255 = (byte) 255;
		
		
		CuboidBu cuboidBu = new CuboidBu();
		Integer videoId = VideoNameList.getId(bmpDirSrc.getName());
		
		
		BackgroundSubtraction backgroundSubtraction = new BackgroundSubtraction(bmpDirSrc);
		
		for(i = 1; i < length; i++)
		{
			String fileName = fileNameList.get(i);
			IplImage image = opencv_highgui.cvLoadImage(bmpSrcPath + fileName);
				
			int frameNumber = Integer.parseInt(fileName.split("\\.")[0]);
			List<CuboidFeature> list = cuboidBu.listByFrame(videoId, frameNumber);
			
			
			if(useBackgroundSubtraction)
			{
				backgroundSubtraction.apply2();
			}
			
				
			if(list != null && list.size() > 0)
			{
				BytePointer bp = image.arrayData();
				for(CuboidFeature feature : list)
				{
					if(useBackgroundSubtraction && !backgroundSubtraction.contain(feature))
					{
						continue;
					}
					
					int index = ((feature.getPositionY() - 1) * width + feature.getPositionX() - 1) * 3;
					bp.put(index, b0);
					bp.put(index + 1, b0);
					bp.put(index + 2, b255);
				}
			}
			opencv_highgui.cvSaveImage(bmpDstPath + fileName, image);
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
