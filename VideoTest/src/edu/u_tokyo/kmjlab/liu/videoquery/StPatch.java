package edu.u_tokyo.kmjlab.liu.videoquery;

import java.util.List;

import org.bytedeco.javacpp.opencv_core.IplImage;

// size = 7 x 7 x 3
public class StPatch
{
	private static final int PATCH_WIDTH = 7;
	private static final int PATCH_HEIGHT = 7;
	private static final int PATCH_LENGTH = 3;
	
	public static void main(String[] args)
	{
		
	}
	
	public double[][] generateGradientMatrix(List<IplImage> list, int x, int y, int frame)
	{
		if(list == null || list.size() == 0 || x <= 0 || y <= 0 || frame <= 0)
		{
			return null;
		}
		IplImage firstImage = list.get(0);
		int length = list.size();
		int width = firstImage.width();
		int height = firstImage.height();
		if(frame + PATCH_LENGTH >= length || x + PATCH_WIDTH >= width || y + PATCH_HEIGHT >= height)
		{
			return null;
		}
		
		int matrixHeight = PATCH_WIDTH * PATCH_HEIGHT * PATCH_LENGTH;
		double[][] gradientMatrix = new double[3][matrixHeight];
		int matrixColumn = 0;
		for(int k = 0; k < PATCH_LENGTH; k++)
		{
			IplImage image = list.get(frame + k);
			IplImage prevImage = list.get(frame + k - 1);
			IplImage nextImage = list.get(frame + k + 1);
			for(int i = 0; i < PATCH_WIDTH; i++)
			{
				for(int j = 0; j < PATCH_HEIGHT; j++)
				{
					int pixel = (image.arrayData().get(i * width + j) & 0xFF);
					
					int leftPixel = (image.arrayData().get(i * width + j - 1) & 0xFF);
					int rightPixel = (image.arrayData().get(i * width + j + 1) & 0xFF);
					
					int upPixel = (image.arrayData().get((i - 1) * width + j) & 0xFF);
					int downPixel = (image.arrayData().get((i + 1) * width + j) & 0xFF);
					
					int prevPixel = (prevImage.arrayData().get(i * width + j) & 0xFF);
					int nextPixel = (nextImage.arrayData().get(i * width + j) & 0xFF);
				}
			}
		}
		
		return null;
	}
}
