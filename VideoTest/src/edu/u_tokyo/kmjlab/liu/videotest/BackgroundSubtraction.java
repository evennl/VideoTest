package edu.u_tokyo.kmjlab.liu.videotest;

import java.io.File;

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

public class BackgroundSubtraction
{
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
        		if(boundbox.width() * boundbox.y() > 3000)
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
        		opencv_highgui.cvSaveImage("D:\\test\\jouon_01_rect\\" + i + ".bmp", grabbedImage);
        	}
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
