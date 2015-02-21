package edu.u_tokyo.kmjlab.liu.videoquery;

import java.util.List;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.opencv_core.IplImage;

import Jama.Matrix;

// size = 7 x 7 x 3
public class StPatch
{
	public static final int PATCH_WIDTH = 7;
	public static final int PATCH_HEIGHT = 7;
	public static final int PATCH_LENGTH = 3;
	
	public Matrix generateGramMatrix(List<IplImage> list, int x, int y, int frame)
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
		double[][] gradientMatrix = new double[matrixHeight][3];
		int matrixColumn = 0;
		for(int k = 0; k < PATCH_LENGTH; k++)
		{
			IplImage image = list.get(frame + k);
			IplImage prevImage = list.get(frame + k - 1);
			IplImage nextImage = list.get(frame + k + 1);
			BytePointer data = image.arrayData();
			
			for(int j = 0; j < PATCH_HEIGHT; j++)
			{
				for(int i = 0; i < PATCH_WIDTH; i++)
				{
					int leftPixel = (data.get((y + j) * width + x + i - 1)) & 0xFF;
					int rightPixel = (data.get((y + j) * width + x + i + 1)) & 0xFF;
					
					int upPixel = (data.get((y + j - 1) * width + x + i)) & 0xFF;
					int downPixel = (data.get((y + j + 1) * width + x + i)) & 0xFF;
					
					int prevPixel = (prevImage.arrayData().get((y + j) * width + x + i) & 0xFF);
					int nextPixel = (nextImage.arrayData().get((y + j) * width + x + i) & 0xFF);
					
					gradientMatrix[matrixColumn][0] = rightPixel - leftPixel;
					gradientMatrix[matrixColumn][1] = downPixel - upPixel;
					gradientMatrix[matrixColumn][2] = nextPixel - prevPixel;
					matrixColumn++;
				}
			}
		}
		Matrix gMatrix = new Matrix(gradientMatrix);
		Matrix m = gMatrix.transpose().times(gMatrix);
		return m;
	}
}
