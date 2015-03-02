package edu.u_tokyo.kmjlab.liu.videoquery;

import java.awt.image.BufferedImage;
import java.util.List;

import Jama.Matrix;

// size = 7 x 7 x 3
public class StPatch
{
	public static final int PATCH_WIDTH = 7;
	public static final int PATCH_HEIGHT = 7;
	public static final int PATCH_LENGTH = 3;
	private static final int GRADIENT_MATRIX_ROW = PATCH_WIDTH * PATCH_HEIGHT * PATCH_LENGTH;
	
	public Matrix generateGramMatrix(List<BufferedImage> list, int x, int y, int frame, int width, int height, int length)
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
				for(int i = patchFrameStart; i < patchFrameEnd; i++)
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
		return m;
	}
}
