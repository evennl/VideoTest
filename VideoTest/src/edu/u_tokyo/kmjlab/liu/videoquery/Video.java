package edu.u_tokyo.kmjlab.liu.videoquery;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_highgui;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacpp.opencv_core.IplImage;

import edu.u_tokyo.kmjlab.liu.videotest.BmpFilter;

import Jama.Matrix;

public class Video
{
	private List<IplImage> list;
	public int length;
	public int width;
	public int height;
	
	private static final double THRESHOLD = 0.89;
	
	public Video(String path)
	{
		File dir = new File(path);
		List<String> fileNameList = Arrays.asList(dir.list(new BmpFilter()));
		Collections.sort(fileNameList, new Comparator<String>()
		{
		    @Override
		    public int compare(String fileName1, String fileName2)
		    {
		        return fileName1.toLowerCase().compareTo(fileName2.toLowerCase());
		    }
		});
		
		list = new ArrayList<IplImage>(fileNameList.size());
		
		for(String fileName : fileNameList)
		{
			IplImage image = opencv_highgui.cvLoadImage(path + fileName);
			IplImage grayFrame = IplImage.create(opencv_core.cvGetSize(image), opencv_core.IPL_DEPTH_8U, 1);
			opencv_imgproc.cvCvtColor(image, grayFrame, opencv_imgproc.CV_BGR2GRAY);
			list.add(grayFrame);
		}
		length = list.size();
		IplImage firstImage = opencv_highgui.cvLoadImage(path + fileNameList.get(0));
		width = firstImage.width();
		height = firstImage.height();
	}
	
	
	
	public static void main(String[] args)
	{
		Video video = new Video("D:/cuts/5/");
		Template template = new Template("D:/cuts/2/");
		
		video.findMatchedArea(template);
		double[][][] consistency = video.inputConsistency("D:/1.txt");
		video.drawColor(consistency, "D:/cuts/color2/", "D:/cuts/draw2/", template);
		//video.draw(consistency, "D:/result2/", template);
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
		if(frame + template.length + StPatch.PATCH_LENGTH >= length || x + template.width + StPatch.PATCH_WIDTH >= width || y + template.height + StPatch.PATCH_HEIGHT >= height)
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
		outputConsistency(consistency, "D:/1.txt");
		System.out.println(minimum + ":" + maximum);
	}
	
	public void outputConsistency(double[][][] consistency, String path)
	{
		if(consistency == null)
		{
			return;
		}
		File file = new File(path);
		if(!file.exists())
		{
			try
			{
				file.createNewFile();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
	
	
	public double[][][] inputConsistency(String path)
	{
		double[][][] consistency = new double[length][height][width];
		File file = new File(path);
		if(!file.exists())
		{
			try
			{
				file.createNewFile();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		int i = 0, j = 0, k = 0;
		int tempchar;
		StringBuffer sb = new StringBuffer();
        Reader reader = null;
        try
        {
            reader = new InputStreamReader(new FileInputStream(file));
            
            tempchar = reader.read();
            
            while((tempchar = reader.read()) != -1)
            {
            	char c = (char) tempchar;
            	if(c == ',')
            	{
            		if(sb.length() != 0)
            		{
	            		consistency[k][j][i] = Double.valueOf(sb.toString());
	            		sb.setLength(0);
	            		i++;
            		}
                }
            	else if(c == ';')
            	{
            		consistency[k][j][i] = Double.valueOf(sb.toString());
            		j++;
            		i = 0;
            		sb.setLength(0);
                }
            	else if(c == '{')
            	{
            		continue;
                }
            	else if(c == '}')
            	{
            		if(sb.length() != 0)
            		{
            			consistency[k][j][i] = Double.valueOf(sb.toString());
            			k++;
                		j = 0;
                		i = 0;
                		sb.setLength(0);
            		}
                }
            	else
            	{
            		sb.append(c);
            	}
            }
            reader.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return consistency;
	}
	
	public void draw(double[][][] consistency, String dir, Template template)
	{
		File path = new File(dir);
		if(!path.exists())
		{
			path.mkdirs();
		}
		
		double maximum = 0;
		double minimum = Double.MAX_VALUE;
		
		for(int k = 0; k < length; k++)
		{
			for(int j = 0; j < height; j++)
			{
				for(int i = 0; i < width; i++)
				{
					if(consistency[k][j][i] > maximum)
					{
						maximum = consistency[k][j][i];
					}
					if(consistency[k][j][i] > 0 && consistency[k][j][i] < minimum)
					{
						minimum = consistency[k][j][i];
					}
				}
			}
		}
		double threshold = maximum * THRESHOLD;
		byte b255 = (byte) 255;
		
		for(int k = 0; k < length; k++)
		{
			
			if(k + template.length / 2 >= list.size())
			{
				break;
			}
			IplImage image = list.get(k + template.length / 2);
			BytePointer data = image.arrayData();
			
			for(int j = 0; j < height; j++)
			{
				for(int i = 0; i < width; i++)
				{
					if(consistency[k][j][i] > threshold)
					{
						int s_height = j + template.height / 2;
						int s_width = i + template.width / 2;
						if(s_width >= width)
						{
							s_width = width - 1;
						}
						if(s_height >= height)
						{
							s_height = height - 1;
						}
						int index = s_height * (width + 3) + s_width;
						data.put(index, b255);
					}
				}
			}
		}
		
		
		
		for(int i = 0; i < list.size(); i++)
		{
			IplImage image = list.get(i);
			opencv_highgui.cvSaveImage(dir + i + ".bmp", image);
		}
	}
	
	public void drawColor(double[][][] consistency, String colorDir, String drawDir, Template template)
	{
		File path = new File(drawDir);
		if(!path.exists())
		{
			path.mkdirs();
		}
		
		double maximum = 0;
		double minimum = Double.MAX_VALUE;
		
		for(int k = 0; k < length; k++)
		{
			for(int j = 0; j < height; j++)
			{
				for(int i = 0; i < width; i++)
				{
					if(consistency[k][j][i] > maximum)
					{
						maximum = consistency[k][j][i];
					}
					if(consistency[k][j][i] > 0 && consistency[k][j][i] < minimum)
					{
						minimum = consistency[k][j][i];
					}
				}
			}
		}
		double threshold = maximum * THRESHOLD;
		byte b255 = (byte) 255;
		byte b0 = (byte) 0;
		
		
		File colorPath = new File(colorDir);
		List<String> fileNameList = Arrays.asList(colorPath.list(new BmpFilter()));
		Collections.sort(fileNameList, new Comparator<String>()
		{
		    @Override
		    public int compare(String fileName1, String fileName2)
		    {
		        return fileName1.toLowerCase().compareTo(fileName2.toLowerCase());
		    }
		});
		
		
		
		List<IplImage> colorList = new ArrayList<IplImage>(fileNameList.size());
		
		for(String fileName : fileNameList)
		{
			IplImage image = opencv_highgui.cvLoadImage(colorDir + fileName);
			colorList.add(image);
		}
		IplImage firstImage = colorList.get(0);
		int bigWidth = firstImage.width();
		
		
		for(int k = 0; k < length; k++)
		{
			if(k + template.length / 2 >= colorList.size())
			{
				break;
			}
			IplImage image = colorList.get(k + template.length / 2);
			BytePointer data = image.arrayData();
			
			for(int j = 0; j < height; j++)
			{
				for(int i = 0; i < width; i++)
				{
					if(consistency[k][j][i] > threshold)
					{
						int s_height = j + template.height / 2;
						int s_width = i + template.width / 2;
						if(s_width >= width)
						{
							s_width = width - 1;
						}
						if(s_height >= height)
						{
							s_height = height - 1;
						}
						s_height += 16;
						s_width += 40;
						int index = s_height * (bigWidth) + s_width;
						data.put(index * 3 + j, b0);
						data.put(index * 3 + j + 1, b0);
						data.put(index * 3 + j + 2, b255);
					}
				}
			}
		}

		for(int i = 0; i < colorList.size(); i++)
		{
			IplImage image = colorList.get(i);
			opencv_highgui.cvSaveImage(drawDir + i + ".bmp", image);
		}
	}
}
