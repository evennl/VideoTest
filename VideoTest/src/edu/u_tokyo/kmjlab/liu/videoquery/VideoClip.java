package edu.u_tokyo.kmjlab.liu.videoquery;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.imageio.ImageIO;

import Jama.Matrix;

import edu.u_tokyo.kmjlab.liu.business.videoquery.GramMatrixBu;
import edu.u_tokyo.kmjlab.liu.business.videoquery.VideoBu;
import edu.u_tokyo.kmjlab.liu.model.videoquery.GramMatrix;
import edu.u_tokyo.kmjlab.liu.videotest.BmpFilter;
import edu.u_tokyo.kmjlab.liu.model.videoquery.Video;

public class VideoClip
{
	private List<BufferedImage> list;
	private int width;
	private int height;
	private int length;
	private int videoId;
	private boolean isTemplate;
	private String path;
	
	public static final int PATCH_WIDTH = 7;
	public static final int PATCH_HEIGHT = 7;
	public static final int PATCH_LENGTH = 3;
	private static final int GRADIENT_MATRIX_ROW = PATCH_WIDTH * PATCH_HEIGHT * PATCH_LENGTH;
	public static final float THRESHOLD = 0.4f;
	
	public VideoClip(String path, boolean isTemplate)
	{
		this.isTemplate = isTemplate;
		this.path = path;
		
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
		
		try
		{
			BufferedImage firstImage = ImageIO.read(new File(path + fileNameList.get(0)));
			width = firstImage.getWidth();
			height = firstImage.getHeight();
			
			list = new ArrayList<BufferedImage>(fileNameList.size());
			for(String fileName : fileNameList)
			{
				BufferedImage image = ImageIO.read(new File(path + fileName));
				BufferedImage grayImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
				Graphics g = grayImage.getGraphics();
				g.drawImage(image, 0, 0, null);
				g.dispose();
				list.add(grayImage);
			}
			length = list.size();
			
		}
		catch (IOException e)
		{
			e.printStackTrace();
			list = null;
			width = 0;
			height = 0;
			length = 0;
		}
	}
	
	private void setId()
	{
		VideoBu bu = new VideoBu();
		Video video = bu.getByName(path);
		if(video == null)
		{
			video = new Video();
			video.setWidth(width);
			video.setHeight(height);
			video.setLength(length);
			video.setVideoName(path);
			video.setIsTemplate(isTemplate);
			bu.save(video);
			videoId = video.getId();
		}
		else if(video.getWidth() != width || video.getHeight() != height || video.getLength() != length || 
				!video.getVideoName().equals(path) || video.getIsTemplate() != isTemplate)
		{
			video.setWidth(width);
			video.setHeight(height);
			video.setLength(length);
			video.setVideoName(path);
			video.setIsTemplate(isTemplate);
			bu.update(video);
			videoId = video.getId();
		}
		else
		{
			videoId = video.getId();
		}
	}
	
	private GramMatrix calculateGramMatrix(int x, int y, int frame)
	{
		int patchXStart = x - PATCH_WIDTH / 2;
		int patchXEnd = x + (PATCH_WIDTH + 1) / 2;
		int patchYStart = y - PATCH_HEIGHT / 2;
		int patchYEnd = y + (PATCH_HEIGHT + 1) / 2;
		int patchFrameStart = frame - PATCH_LENGTH / 2;
		int patchFrameEnd = frame + (PATCH_LENGTH + 1) / 2;
		
		if(list == null || list.size() == 0 || 
				patchXStart <= 0 || patchYStart <= 0 || patchFrameStart <= 0 ||
				patchXEnd > width - 1 || patchYEnd > height - 1 || patchFrameEnd > length - 1)
		{
			return null;
		}
		
		double[][] gradientMatrix = new double[GRADIENT_MATRIX_ROW][3];
		int matrixColumn = 0;
		for(int k = patchFrameStart; k < patchFrameEnd; k++)
		{
			BufferedImage image = list.get(k);
			BufferedImage prevImage = list.get(k - 1);
			BufferedImage nextImage = list.get(k + 1);
			
			for(int j = patchYStart; j < patchYEnd; j++)
			{
				for(int i = patchXStart; i < patchXEnd; i++)
				{
					int leftPixel = image.getRGB(i - 1, j) & 0xff;
					int rightPixel = image.getRGB(i + 1, j) & 0xff;
					int upPixel = image.getRGB(i, j - 1) & 0xff;
					int downPixel = image.getRGB(i, j + 1) & 0xff;
					int prevPixel = prevImage.getRGB(i, j) & 0xff;
					int nextPixel = nextImage.getRGB(i, j) & 0xff;
					
					gradientMatrix[matrixColumn][0] = rightPixel - leftPixel;
					gradientMatrix[matrixColumn][1] = downPixel - upPixel;
					gradientMatrix[matrixColumn][2] = nextPixel - prevPixel;
					matrixColumn++;
				}
			}
		}
		Matrix gMatrix = new Matrix(gradientMatrix);
		Matrix m = gMatrix.transpose().times(gMatrix);
		
		double rankIncrease = MatrixCalculation.getRankIncrease(m);
		double[][] mm = m.getArray();
		
		GramMatrix gramMatrix = new GramMatrix();
		gramMatrix.setM11((int) mm[0][0]);
		gramMatrix.setM12((int) mm[0][1]);
		gramMatrix.setM13((int) mm[0][2]);
		gramMatrix.setM21((int) mm[1][0]);
		gramMatrix.setM22((int) mm[1][1]);
		gramMatrix.setM23((int) mm[1][2]);
		gramMatrix.setM31((int) mm[2][0]);
		gramMatrix.setM32((int) mm[2][1]);
		gramMatrix.setM33((int) mm[2][2]);
		gramMatrix.setRankIncrease((float) rankIncrease);
		gramMatrix.setX(x);
		gramMatrix.setY(y);
		gramMatrix.setFrame(frame);
		gramMatrix.setVideoId(videoId);
		
		return gramMatrix;
	}
	
	public void storeGramMatrixToDatabase()
	{
		setId();
		
		System.out.println("Start to store.");
		int xStart = PATCH_WIDTH / 2 + 1;
		int xEnd = width - (PATCH_WIDTH + 1) / 2;
		int yStart = PATCH_HEIGHT / 2 + 1;
		int yEnd = height - (PATCH_HEIGHT + 1) / 2;
		int frameStart = PATCH_LENGTH / 2 + 1;;
		int frameEnd = length - (PATCH_LENGTH + 1) / 2;
		
		GramMatrixBu bu = new GramMatrixBu();
		List<GramMatrix> list = new ArrayList<GramMatrix>();
		int size = (xEnd - xStart) * (yEnd - yStart) * (frameEnd - frameStart);
		int counter = 0;
		for(int k = frameStart; k < frameEnd; k++)
		{
			for(int j = yStart; j < yEnd; j++)
			{
				for(int i = xStart; i < xEnd; i++)
				{
					GramMatrix gramMatrix = calculateGramMatrix(i, j, k);
					if(gramMatrix != null)
					{
						list.add(gramMatrix);
					}
					counter++;
					if(list.size() == 500)
					{
						bu.bulkSave(list);
						System.out.println(counter + "/" + size);
						list.clear();
					}
				}
			}
		}
		bu.bulkSave(list);
		System.out.println(counter + "/" + size);
	}
}
