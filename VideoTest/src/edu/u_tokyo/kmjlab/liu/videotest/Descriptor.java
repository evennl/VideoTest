package edu.u_tokyo.kmjlab.liu.videotest;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_features2d.DescriptorExtractor;
import org.bytedeco.javacpp.opencv_features2d.KeyPoint;
import org.bytedeco.javacpp.opencv_highgui;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_nonfree.SIFT;

public class Descriptor
{
	public static void main(String[] args)
	{
		int x = 45, y = 277;
		float sigma = 1.67f;
		
		double scaled = ((sigma - 0.8) / 0.3 + 1) * 2;
		int scale = (int) scaled / 2;
		scale = 2 * scale + 1;
		
		System.out.println(scale);
		
		
		String dir = "D:/cuts/jouon_04_27_03_27_10_resize/003.bmp";
		IplImage imageTemp = opencv_highgui.cvLoadImage(dir);
		IplImage image = IplImage.create(opencv_core.cvGetSize(imageTemp), opencv_core.IPL_DEPTH_8U, 1);
		opencv_imgproc.cvCvtColor(imageTemp, image, opencv_imgproc.CV_BGR2GRAY);
		Mat imageMat = new Mat(image);
		
		float angle = 0;
		KeyPoint keypoint = new KeyPoint((float) x, (float) y, (float) scale);
		keypoint.put(keypoint);
		
		SIFT sift = new SIFT();
		Mat descriptors = new Mat();
		sift.apply(imageMat, null, keypoint, descriptors, true);
	}
}
