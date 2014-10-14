package edu.u_tokyo.kmjlab.liu.videotest;

import java.io.File;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_highgui;
import org.bytedeco.javacpp.opencv_core.CvContour;
import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.CvSeq;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_core.CvFont;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.javacpp.opencv_video.BackgroundSubtractorMOG2;

public class test
{
    public static void main(String[] args) throws Exception
    {
            // Preload the opencv_objdetect module to work around a known

            CvSeq contour = new CvSeq(null);
            CvSeq ptr = new CvSeq();
            //Loader.load(opencv_objdetect.class);
            CvMemStorage storage = CvMemStorage.create();
            CanvasFrame frameInput = new CanvasFrame("Original");
            CanvasFrame frameOutput = new CanvasFrame("Foregroung");
            File f = new File("D:\\test\\jouon_01.avi");
            FrameGrabber grabber = new OpenCVFrameGrabber(f);
            grabber.start();
            IplImage grabbedImage = grabber.grab();
            grabber.start();

            IplImage foreground = null;
            BackgroundSubtractorMOG2 mog = new BackgroundSubtractorMOG2(30, 40, false);
            IplImage frame = grabbedImage.clone();

            int i = 1;
            while (frameInput.isVisible())
            {
                    if (foreground == null)
                    {
                            foreground = IplImage.create(frame.width(), frame.height(), opencv_core.IPL_DEPTH_8U, 1);
                    }

                    Mat mat_grabbedImage = new Mat(grabbedImage);
                    Mat mat_foreground = new Mat(foreground);
                    mog.apply(mat_grabbedImage, mat_foreground, -1);
                    
                    
                    opencv_highgui.cvSaveImage("D:\\test\\jouon_01_foreground\\" + i + ".bmp", foreground);
    				i++;
                    

                    // morph. schliessen
                    opencv_imgproc.cvDilate(foreground, foreground, null, 5);
                    opencv_imgproc.cvErode(foreground, foreground, null, 6);
                    opencv_imgproc.cvSmooth(foreground, foreground, opencv_imgproc.CV_MEDIAN, 3, 3, 2, 2);
                    opencv_imgproc.cvFindContours(foreground, storage, contour,
                                    Loader.sizeof(CvContour.class), opencv_imgproc.CV_RETR_LIST,
                                    opencv_imgproc.CV_CHAIN_APPROX_SIMPLE);
                    
                    
                    
                    // cvDilate(diff, diff, null, 3);
                    // cvErode(diff, diff, null, 3);

                    CvRect boundbox;

                    int cnt = 0;
                    for (ptr = contour; ptr != null; )
                    {
                    	try
                        {
                    		boundbox = opencv_imgproc.cvBoundingRect(ptr, 0);
                        }
                        catch(Exception e)
                        {
                        	break;
                        }

                            opencv_core.cvRectangle(
                                            grabbedImage,
                                            opencv_core.cvPoint(boundbox.x(), boundbox.y()),
                                            opencv_core.cvPoint(boundbox.x() + boundbox.width(), boundbox.y()
                                                            + boundbox.height()), opencv_core.CV_RGB(255, 0, 0), 1, 8,
                                            0);

                            //CvFont font = new CvFont(opencv_core.CV_FONT_HERSHEY_PLAIN);
                            CvFont font = new CvFont();
                            opencv_core.cvPutText(grabbedImage, " " + cnt,
                            		opencv_core.cvPoint(boundbox.x(), boundbox.y()), font, opencv_core.CvScalar.RED);

                            // Color randomColor = new Color(rand.nextFloat(),
                            // rand.nextFloat(), rand.nextFloat());
                            // CvScalar color = CV_RGB(randomColor.getRed(),
                            // randomColor.getGreen(), randomColor.getBlue());
                            // cvDrawContours(diff, ptr, color, CV_RGB(0, 0, 0), -1,
                            // CV_FILLED, 8, cvPoint(0, 0));
                            
                            try
                            {
                            	ptr = ptr.h_next();
                            }
                            catch(Exception e)
                            {
                            	break;
                            }
                    }

                    frameInput.showImage(grabbedImage);
                    frameOutput.showImage(foreground);
                    
                    try
                    {
                    	grabbedImage = grabber.grab();
                    }
                    catch(Exception e)
                    {
                    	break;
                    }
                    if(grabbedImage == null)
                    {
                    	break;
                    }
            }
            grabber.stop();
            frameInput.dispose();
            frameOutput.dispose();
    }

}
