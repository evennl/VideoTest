package edu.u_tokyo.kmjlab.liu.videoquery;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_highgui;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacpp.opencv_core.IplImage;

import Jama.Matrix;

public class Video
{
	private List<IplImage> list;
	public int length;
	public int width;
	public int height;
	
	private static final double THRESHOLD = 0.8;
	
	public Video(String path)
	{
		File dir = new File(path);
		String[] fileNameList = dir.list();
		list = new ArrayList<IplImage>(fileNameList.length);
		
		for(String fileName : fileNameList)
		{
			IplImage image = opencv_highgui.cvLoadImage(path + fileName);
			IplImage grayFrame = IplImage.create(opencv_core.cvGetSize(image), opencv_core.IPL_DEPTH_8U, 1);
			opencv_imgproc.cvCvtColor(image, grayFrame, opencv_imgproc.CV_BGR2GRAY);
			list.add(grayFrame);
		}
		length = list.size();
		IplImage firstTemplateImage = opencv_highgui.cvLoadImage(path + fileNameList[0]);
		width = firstTemplateImage.width();
		height = firstTemplateImage.height();
	}
	
	
	
	public static void main(String[] args)
	{
		Video video = new Video("D:/cuts/1/");
		Template template = new Template("D:/cuts/2/");
		
		double[][][] consistency = new double[3][3][3];
		int counter = 1;
		for(int k = 0; k < 3; k++)
		{
			for(int j = 0; j < 3; j++)
			{
				for(int i = 0; i < 3; i++)
				{
					consistency[k][j][i] = counter++;
				}
			}
		}
		video.findMatchedArea(template);
	}
	
	
	
	
	private Matrix generateGramMatrix(int x, int y, int frame)
	{
		StPatch stPatch = new StPatch();
		return stPatch.generateGramMatrix(list, x, y, frame);
	}
	
	
	private double getConsistency(int x, int y, int frame, Template template)
	{
		if(template == null)
		{
			return 0;
		}
		if(x <= 0 || y <= 0 || frame <= 0)
		{
			return 0;
		}
		if(frame + template.length + StPatch.PATCH_LENGTH >= length || x +  + template.width + StPatch.PATCH_WIDTH >= width || y + template.height + StPatch.PATCH_HEIGHT >= height)
		{
			return 0;
		}
		
		double consistency = 0;
		
		for(int k = 0; k < template.length; k++)
		{
			for(int j = 0; j < template.height; j++)
			{
				for(int i = 0; i < template.width; i++)
				{
					Matrix m1 = generateGramMatrix(x + i, y + j, frame + k);
					if(m1 == null)
					{
						return 0;
					}
					Matrix m2 = template.generateGramMatrix(i, j, k);
					if(m2 == null)
					{
						continue;
					}
					double inconsistency = MatrixCalculation.getInconsistency(m1, m2);
					consistency += 1 / inconsistency;
				}
			}
		}
		return consistency;
		//return consistency / (template.width * template.height * template.length);
	}
	
	public void findMatchedArea(Template template)
	{
		double maximum = 0;
		double minimum = Double.MAX_VALUE;
		double[][][] consistency = new double[length][height][width];
		int counter = 0;
		int size = length * height * width;
		
		for(int k = 0; k < length; k++)
		{
			for(int j = 0; j < height; j++)
			{
				for(int i = 0; i < width; i++)
				{
					counter++;
					
					consistency[k][j][i] = getConsistency(i, j, k, template);
					if(consistency[k][j][i] > maximum)
					{
						maximum = consistency[k][j][i];
					}
					if(consistency[k][j][i] > 0 && consistency[k][j][i] < minimum)
					{
						minimum = consistency[k][j][i];
					}
					System.out.println(counter + "/" + size + " " + consistency[k][j][i] + "/" + minimum + ":" + maximum);
				}
			}
		}
		outputConsistency(consistency);
		double threshold = minimum + (maximum - minimum) * THRESHOLD;
		byte b255 = (byte) 255;
		
		for(int k = 0; k < length; k++)
		{
			IplImage image = list.get(k);
			BytePointer data = image.arrayData();
			
			for(int j = 0; j < height; j++)
			{
				for(int i = 0; i < width; i++)
				{
					if(consistency[k][j][i] > threshold)
					{
						data.put(j * width + i, b255);  
					}
				}
			}
			opencv_highgui.cvSaveImage("D:/result/" + k + ".bmp", image);
		}
	}
	
	private void outputConsistency(double[][][] consistency)
	{
		if(consistency == null)
		{
			return;
		}
		int length = consistency.length;
		int height = consistency[0].length;
		int width = consistency[0][0].length;
		StringBuffer s = new StringBuffer();
		s.append("{");
		for(int k = 0; k < length; k++)
		{
			if(k > 0)
			{
				s.append(",");
			}
			s.append("{");
			
			for(int j = 0; j < height; j++)
			{
				for(int i = 0; i < width; i++)
				{
					s.append((long)consistency[k][j][i]);
					if(i < width - 1)
					{
						s.append(",");
					}
				}
				if(j < height - 1)
				{
					s.append(";");
				}
			}
			s.append("}");
		}
		s.append("}");
		File file = new File("D:/1.txt");
		try
		{
			BufferedWriter buffwrite = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));
			buffwrite.write(s.toString());
            buffwrite.flush();
            buffwrite.close();
		}
		catch (Exception e)
		{
            e.printStackTrace();
        }
	}
}
