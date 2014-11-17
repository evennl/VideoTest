package edu.u_tokyo.kmjlab.liu.videotest;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_highgui;
import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacpp.opencv_core.CvContour;
import org.bytedeco.javacpp.opencv_core.CvFont;
import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_core.CvSeq;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_video.BackgroundSubtractorMOG2;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.javacv.FrameGrabber.Exception;

import edu.u_tokyo.kmjlab.liu.model.features.CuboidFeature;

public class BackgroundSubtraction
{
	private File bmpDir = null;
	private List<String> fileNameList = null;
	private int fileIndex = 0;
	
	private IplImage foreground = null;
	private CvMemStorage storage = null;
	private CvSeq contour = null;
	private BackgroundSubtractorMOG2 mog = null;
	private final int areaThreshold = 200;
	private int width = 0;
	
	public BackgroundSubtraction()
	{
		
	}
	
	public BackgroundSubtraction(File bmpDir)
	{
		if(bmpDir.exists() && bmpDir.isDirectory())
		{
			this.bmpDir = bmpDir;
			this.fileNameList = Arrays.asList(bmpDir.list(new BmpFilter()));
			Collections.sort(fileNameList, new Comparator<String>()
			{
			    @Override
			    public int compare(String fileName1, String fileName2)
			    {
			        return fileName1.toLowerCase().compareTo(fileName2.toLowerCase());
			    }
			});
		}
	}
	
	public static void main(String[] args)
    {
		BackgroundSubtraction bs = new BackgroundSubtraction();
		bs.apply("D:\\test\\jouon_01.avi");
    }
	
	
	public void apply(String videoName)
	{
		if(videoName == null)
		{
			return;
		}
		File file = new File(videoName);
		if(!file.exists() || file.isDirectory())
		{
			return;
		}
		
	    FrameGrabber grabber = new OpenCVFrameGrabber(file);
	    IplImage grabbedImage = null;
	    
	    try
		{
			grabber.start();
			grabbedImage = grabber.grab();
		}
	    catch (Exception e)
		{
	    	closeGrabber(grabber);
	    	return;
		}
	    
	    IplImage foreground = null;
        BackgroundSubtractorMOG2 mog = new BackgroundSubtractorMOG2(30, 40, false);
        IplImage frame = grabbedImage.clone();
        
        CvSeq contour = new CvSeq();
	    CvSeq ptr = null;
	    CvMemStorage storage = CvMemStorage.create();
	    int i = 1;
	    
        while(grabbedImage != null)
        {
        	if(foreground == null)
        	{
        		foreground = IplImage.create(frame.width(), frame.height(), opencv_core.IPL_DEPTH_8U, 1);
        		width = frame.width();
        	}
        	mog.apply(new Mat(grabbedImage), new Mat(foreground), -1);
        	 
        	opencv_imgproc.cvDilate(foreground, foreground, null, 5);
        	opencv_imgproc.cvErode(foreground, foreground, null, 6);
        	opencv_imgproc.cvSmooth(foreground, foreground, opencv_imgproc.CV_MEDIAN, 3, 3, 2, 2);
        	opencv_imgproc.cvFindContours(foreground, storage, contour,
                             Loader.sizeof(CvContour.class), opencv_imgproc.CV_RETR_LIST,
                             opencv_imgproc.CV_CHAIN_APPROX_SIMPLE);
             
        	System.out.println(i);
        	for (ptr = contour; ptr != null; ptr = ptr.h_next())
        	{
        		if(ptr.address() == 0)
        		{
        			break;
        		}
        		CvRect boundbox = opencv_imgproc.cvBoundingRect(ptr, 0);
        		if(boundbox.width() * boundbox.height() > 900)
        		{
        			System.out.println("x : " + boundbox.x() + " ~ " + (boundbox.x() + boundbox.width() + 1) + " y : " + boundbox.y() + " ~ " + (boundbox.y() + boundbox.height() + 1));
        			
        			opencv_core.cvRectangle(grabbedImage,
                            opencv_core.cvPoint(boundbox.x(), boundbox.y()),
                            opencv_core.cvPoint(boundbox.x() + boundbox.width(), boundbox.y() + boundbox.height()),
                            opencv_core.CV_RGB(255, 0, 0), 1, 8, 0);
        			CvFont font = new CvFont();
                    opencv_core.cvPutText(grabbedImage, "0",
                    		opencv_core.cvPoint(boundbox.x(), boundbox.y()), font, opencv_core.CvScalar.RED);
        		}
        	}
        	opencv_highgui.cvSaveImage("D:\\test\\jouon_01_rect\\" + i + ".bmp", grabbedImage);
        	
        	System.out.println();
             
        	try
        	{
        		grabbedImage = grabber.grab();
        	}
        	catch (Exception e)
        	{
        		closeGrabber(grabber);
        		return;
        	}
        	i++;
        }
	}
	
	
	public void apply2()
	{
		if(bmpDir == null || fileNameList == null || fileNameList.size() == 0)
		{
			return;
		}
		String path = bmpDir.getAbsolutePath() + "\\";
		String fileName = fileNameList.get(fileIndex++);
		IplImage image = opencv_highgui.cvLoadImage(path + fileName);
		
		if(foreground == null)
		{
			foreground = IplImage.create(image.width(), image.height(), opencv_core.IPL_DEPTH_8U, 1);
		}
		if(mog == null)
		{
			mog = new BackgroundSubtractorMOG2(30, 40, false);
		}
        if(contour == null)
	    {
        	contour = new CvSeq();
	    }
	    if(storage == null)
	    {
	    	storage = CvMemStorage.create();
	    }
        
	    mog.apply(new Mat(image), new Mat(foreground), -1);
        opencv_imgproc.cvDilate(foreground, foreground, null, 5);
    	opencv_imgproc.cvErode(foreground, foreground, null, 6);
    	opencv_imgproc.cvSmooth(foreground, foreground, opencv_imgproc.CV_MEDIAN, 3, 3, 2, 2);
    	opencv_imgproc.cvFindContours(foreground, storage, contour,
                         Loader.sizeof(CvContour.class), opencv_imgproc.CV_RETR_LIST,
                         opencv_imgproc.CV_CHAIN_APPROX_SIMPLE);
    	
    	/*
    	for (CvSeq ptr = contour; ptr != null; ptr = ptr.h_next())
    	{
    		if(ptr.address() == 0)
    		{
    			break;
    		}
    		CvRect boundbox = opencv_imgproc.cvBoundingRect(ptr, 0);
    		if(boundbox.width() * boundbox.height() > areaThreshold)
    		{
    			System.out.println("x : " + boundbox.x() + " ~ " + (boundbox.x() + boundbox.width() + 1) + " y : " + boundbox.y() + " ~ " + (boundbox.y() + boundbox.height() + 1));
    			
    			opencv_core.cvRectangle(image,
                        opencv_core.cvPoint(boundbox.x(), boundbox.y()),
                        opencv_core.cvPoint(boundbox.x() + boundbox.width(), boundbox.y() + boundbox.height()),
                        opencv_core.CV_RGB(255, 0, 0), 1, 8, 0);
    			CvFont font = new CvFont();
                opencv_core.cvPutText(image, "0",
                		opencv_core.cvPoint(boundbox.x(), boundbox.y()), font, opencv_core.CvScalar.RED);
    		}
    	}
    	opencv_highgui.cvSaveImage("D:\\cuts\\jouon_04_27_03_27_10_resize_foreground\\" + fileName, image);
    	*/
	}
    
	
	public boolean contain(CuboidFeature feature)
	{
		final byte b127 = (byte) 127;
		for (CvSeq ptr = contour; ptr != null; ptr = ptr.h_next())
    	{
			if(ptr.address() == 0)
    		{
    			break;
    		}
			CvRect boundbox = opencv_imgproc.cvBoundingRect(ptr, 0);
    		//if(boundbox.width() * boundbox.height() > areaThreshold)
    		//{
    			if(feature.getPositionX() >= boundbox.x() && feature.getPositionX() <= boundbox.x() + boundbox.width() 
    				&& feature.getPositionY() >= boundbox.y() && feature.getPositionY() <= boundbox.y() + boundbox.height())
    			{
    				BytePointer bp = foreground.arrayData();
    				int index = (feature.getPositionY() - 1) * width + feature.getPositionX() - 1;
    				if(bp.get(index) < b127)
    				{
    					return true;
    				}
    			}
    		//}
    	}
		return false;
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
