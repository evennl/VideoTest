package edu.u_tokyo.kmjlab.liu.videotest;

import java.util.List;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_features2d.KeyPoint;
import org.bytedeco.javacpp.opencv_highgui;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_nonfree.SIFT;

import edu.u_tokyo.kmjlab.liu.business.features.CuboidBu;
import edu.u_tokyo.kmjlab.liu.model.features.CuboidFeature;

public class Descriptor
{
	public static void main(String[] args)
	{
		int x = 45, y = 277;
		float sigma = 1.67f;
		
		double scaled = ((sigma - 0.8) / 0.3 + 1) * 2;
		int scale = (int) scaled / 2;
		scale = 2 * scale + 1;
		
		String dir = "D:/cuts/jouon_04_27_03_27_10_resize/003.bmp";
		IplImage imageTemp = opencv_highgui.cvLoadImage(dir);
		IplImage image = IplImage.create(opencv_core.cvGetSize(imageTemp), opencv_core.IPL_DEPTH_8U, 1);
		opencv_imgproc.cvCvtColor(imageTemp, image, opencv_imgproc.CV_BGR2GRAY);
		Mat imageMat = new Mat(image);
		
		CuboidBu cuboidBu = new CuboidBu();
		List<CuboidFeature> list = cuboidBu.listByFrame(4, 3);
		
		SIFT sift = new SIFT();
		Mat descriptors = new Mat();
		for(CuboidFeature cuboidFeature : list)
		{
			Mat descriptor = new Mat();
			KeyPoint keypoint = new KeyPoint((float) (cuboidFeature.getPositionX() - 1), (float) (cuboidFeature.getPositionY() - 1), (float) scale, 0, 0, 0, 0);
			sift.apply(imageMat, new Mat(), keypoint, descriptor, true);
			
			StringBuffer strBuff = new StringBuffer();
			BytePointer bp = descriptor.datastart();
			BytePointer bpEnd = descriptor.dataend();
			for(int i = 0; !bp.equals(bpEnd); bp = descriptor.ptr(0, i))
			{
				if(i != 0)
				{
					strBuff.append(" ");
				}
				strBuff.append(bp.get(0));
				strBuff.append(" ");
				strBuff.append(bp.get(1));
				strBuff.append(" ");
				strBuff.append(bp.get(2));
				strBuff.append(" ");
				strBuff.append(bp.get(3));
				i++;
			}
			System.out.println(strBuff.length());
			descriptors.push_back(descriptor);
		}
		System.out.println(descriptors.cols());
	}
}
