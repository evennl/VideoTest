package edu.u_tokyo.kmjlab.liu.videoquery;

import java.awt.image.BufferedImage;
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

import javax.imageio.ImageIO;

import edu.u_tokyo.kmjlab.liu.business.videoquery.GramMatrixBu;
import edu.u_tokyo.kmjlab.liu.business.videoquery.VideoBu;
import edu.u_tokyo.kmjlab.liu.model.videoquery.GramMatrix;
import edu.u_tokyo.kmjlab.liu.model.videoquery.Video;
import edu.u_tokyo.kmjlab.liu.videotest.BmpFilter;

public class Main
{
	public static void main(String[] args)
	{
		String videoPath = "D:/cuts/0_resize_original_1/";
		String templatePath = "D:/cuts/2_template/";
		
		
		// Save data to database
		//VideoClip videoClip = new VideoClip(videoPath, false);
		//videoClip.storeGramMatrixToDatabase();
		
		
		
		
		
		
		VideoBu videoBu = new VideoBu();
		Video video = videoBu.getByName(videoPath);
		if(video == null)
		{
			System.out.println("This video does not exist in database.");
			return;
		}
		Video template = videoBu.getByName(templatePath);
		if(template == null)
		{
			System.out.println("This template does not exist in database.");
			return;
		}
		
		
		
		// Online calculation
		//float[][][] consistency = calculateBigConsistency(video, template);
		//outputConsistency(consistency, "D:/0.txt");
		
		
		
		// draw
		float[][][] consistency = inputConsistency("D:/0.txt", video, template);
		try
		{
			drawColor(consistency, videoPath, "D:/0/", template);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	static public GramMatrix[][][] convertListToArray(List<GramMatrix> list, Video video)
	{
		if(list == null || list.size() == 0 || video == null)
		{
			return null;
		}
		GramMatrix[][][] array = new GramMatrix[video.getLength() - VideoClip.PATCH_LENGTH - 1][video.getHeight() - VideoClip.PATCH_HEIGHT - 1][video.getWidth() - VideoClip.PATCH_WIDTH - 1];
		for(GramMatrix m : list)
		{
			array[m.getFrame() - VideoClip.PATCH_LENGTH / 2 - 1][m.getY() - VideoClip.PATCH_HEIGHT / 2 - 1][m.getX() - VideoClip.PATCH_WIDTH / 2 - 1] = m;
		}
		return array;
	}
	
	static public float[][][] calculateConsistency(Video video, Video template)
	{
		if(video == null || template == null || video.getId() == null || template.getId() == null ||
				video.getLength() - template.getLength() + 1 <= 0 ||
				video.getHeight() - template.getHeight() + 1 <= 0 ||
				video.getWidth() - template.getWidth() + 1 <= 0)
		{
			return null;
		}
		
		System.out.println("Start calculate consistency.");
		
		GramMatrixBu gramMatrixBu = new GramMatrixBu();
		List<GramMatrix> vList = gramMatrixBu.getListByVideoId(video.getId());
		List<GramMatrix> tList = gramMatrixBu.getListByVideoId(template.getId());
		
		
		GramMatrix[][][] vArray = convertListToArray(vList, video);
		GramMatrix[][][] tArray = convertListToArray(tList, template);
		
		float[][][] consistency = new float[video.getLength() - template.getLength() + 1][video.getHeight() - template.getHeight() + 1][video.getWidth() - template.getWidth() + 1];
		
		int size = consistency[0][0].length * consistency[0].length * consistency.length;
		int counter = 1;
		
		
		for(int k = 0; k < consistency.length; k++)
		{
			for(int j = 0; j < consistency[0].length; j++)
			{
				for(int i = 0; i < consistency[0][0].length; i++)
				{
					consistency[k][j][i] = getConsistency(i + template.getWidth() / 2, j + template.getHeight() / 2, k + template.getLength() / 2, vArray, tArray);
					System.out.println(counter + "/" + size);
					counter++;
				}
			}
		}
		
		return consistency;
	}
	
	
	
	static public float[][][] calculateBigConsistency(Video video, Video template)
	{
		if(video == null || template == null || video.getId() == null || template.getId() == null ||
				video.getLength() - template.getLength() + 1 <= 0 ||
				video.getHeight() - template.getHeight() + 1 <= 0 ||
				video.getWidth() - template.getWidth() + 1 <= 0)
		{
			return null;
		}
		
		System.out.println("Start calculate consistency.");
		
		GramMatrixBu gramMatrixBu = new GramMatrixBu();
		List<GramMatrix> tList = gramMatrixBu.getListByVideoId(template.getId());
		GramMatrix[][][] tArray = convertListToArray(tList, template);
		
		float[][][] consistency = new float[video.getLength() - template.getLength() + 1][video.getHeight() - template.getHeight() + 1][video.getWidth() - template.getWidth() + 1];
		
		int counter = 1;
		int size = consistency.length * consistency[0].length * consistency[0][0].length;
		for(int k = 0; k < consistency.length; k++)
		{
			int frameStart = k;
			int frameEnd = frameStart + tArray.length;
			
			List<GramMatrix> vList = gramMatrixBu.getList(video.getId(), frameStart, frameEnd);
			GramMatrix[][][] vArray = convertListToArray(vList, video);
			
			for(int j = 0; j < consistency[0].length; j++)
			{
				for(int i = 0; i < consistency[0][0].length; i++)
				{
					consistency[k][j][i] = getConsistency(i + template.getWidth() / 2, j + template.getHeight() / 2, k + template.getLength() / 2, vArray, tArray);
					System.out.println(counter + "/" + size);
					counter++;
				}
			}
			vList.clear();
			vArray = null;
			System.gc();
		}
		
		return consistency;
	}
	
	
	
	// (x, y, frame) is the position of video 
	static public float getConsistency(int x, int y, int frame, GramMatrix[][][] video, GramMatrix[][][] template)
	{
		int templateWidth = template[0][0].length + VideoClip.PATCH_WIDTH + 1;
		int templateHeight = template[0].length + VideoClip.PATCH_HEIGHT + 1;
		int templateLength = template.length + VideoClip.PATCH_LENGTH + 1;
		
		int xStart = x - templateWidth / 2;
		int yStart = y - templateHeight / 2;
		int frameStart = frame - templateLength / 2;
		
		float consistency = 0;
		for(int k = 0; k < template.length; k++)
		{
			for(int j = 0; j < template[0].length; j++)
			{
				for(int i = 0; i < template[0][0].length; i++)
				{
					GramMatrix m1 = video[frameStart + k][yStart + j ][xStart + i];
					GramMatrix m2 = template[k][j][i];
					
					float inconsistency = MatrixCalculation.getInconsistency(m1, m2);
					consistency += 1 / inconsistency;
				}
			}
		}
		
		return consistency;
	}
	
	static public void outputConsistency(float[][][] consistency, String path)
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
	
	static float[][][] inputConsistency(String path, Video video, Video template)
	{
		float[][][] consistency = new float[video.getLength() - template.getLength() + 1][video.getHeight() - template.getHeight() + 1][video.getWidth() - template.getWidth() + 1];
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
	            		consistency[k][j][i] = Float.valueOf(sb.toString());
	            		sb.setLength(0);
	            		i++;
            		}
                }
            	else if(c == ';')
            	{
            		consistency[k][j][i] = Float.valueOf(sb.toString());
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
            			consistency[k][j][i] = Float.valueOf(sb.toString());
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
	
	
	static public void drawColor(float[][][] consistency, String colorDir, String drawDir, Video template) throws IOException
	{
		File path = new File(drawDir);
		if(!path.exists())
		{
			path.mkdirs();
		}
		
		float maximum = 0;
		float minimum = Float.MAX_VALUE;
		
		for(int k = 0; k < consistency.length; k++)
		{
			for(int j = 0; j < consistency[0].length; j++)
			{
				for(int i = 0; i < consistency[0][0].length; i++)
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
		System.out.println(minimum + "~" + maximum);
		
		float threshold = (float) maximum * VideoClip.THRESHOLD;
		float threshold2 = (float) maximum * 0.7f;
		final int redColor = 16711680;
		
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
		
		List<BufferedImage> colorList = new ArrayList<BufferedImage>(fileNameList.size());
		
		for(String fileName : fileNameList)
		{
			BufferedImage image = ImageIO.read(new File(colorDir + fileName));
			colorList.add(image);
		}
		
		for(int k = 0; k < consistency.length; k++)
		{
			BufferedImage image = colorList.get(k + template.getLength() / 2);
			
			for(int j = 0; j < consistency[0].length; j++)
			{
				for(int i = 0; i < consistency[0][0].length; i++)
				{
					if(consistency[k][j][i] > threshold && consistency[k][j][i] < threshold2)
					{
						int s_height = j + template.getHeight() / 2;
						int s_width = i + template.getWidth() / 2;

						//s_height += 16;
						//s_width += 28;
						//image.setRGB(s_width, s_height, calculateRGB(threshold, maximum, consistency[k][j][i], image.getRGB(i, j)));
						image.setRGB(s_width, s_height, redColor);
					}
				}
			}
		}

		for(int i = 0; i < colorList.size(); i++)
		{
			BufferedImage image = colorList.get(i);
			ImageIO.write(image, "bmp", new File(drawDir + i + ".bmp"));
		}
	}
	
	static private int calculateRGB(float min, float max, float x, int rgb)
	{
		int level = (int) ((x - min) * 766 / (max - min));
		int r = 0;
		int g = 0;
		int b = 0;
		if(level <= 255)
		{
			b = level;
		}
		else if(level <= 510)
		{
			//b = 510 - level;
			g = level - 255;
		}
		else
		{
			r = level - 510;
			//g = 765 - level;
		}
		
		r = 255;
		b = 0;
		g = 0;
		
		int originalR = (rgb & 0xff0000) >> 16;
		int originalG = (rgb & 0x00ff00) >> 8;
		int originalB = rgb & 0x0000ff;
		
		float alpha = 0.7f;
		
		r = (int) (alpha * originalR + (1- alpha) * r);
		g = (int) (alpha * originalG + (1- alpha) * g);
		b = (int) (alpha * originalB + (1- alpha) * b);
		
		int newRgb = (r << 16) + (g << 8) + b;
		
		return newRgb;
	}
}