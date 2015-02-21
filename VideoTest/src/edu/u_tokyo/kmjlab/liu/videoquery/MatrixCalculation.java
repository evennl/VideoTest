package edu.u_tokyo.kmjlab.liu.videoquery;

import Jama.Matrix;

public class MatrixCalculation
{
	static public double getInconsistency(Matrix m1, Matrix m2)
	{
		if(m1 == null || m2 == null)
		{
			return 100000;
		}
		if(m1.getRowDimension() != 3 || m1.getColumnDimension() != 3 || m2.getRowDimension() != 3 || m2.getColumnDimension() != 3)
		{
			return 100000;
		}
		
		Matrix m12 = m1.plus(m2);
		
		double rankIncrease1 = getRankIncrease(m1);
		double rankIncrease2 = getRankIncrease(m2);
		double rankIncrease12 = getRankIncrease(m12);
		
		double min = Math.min(rankIncrease1, rankIncrease2);
		if(min == 0)
		{
			return 100000000;
		}
		
		return rankIncrease12 / min;
	}
	
	
	static private double getRankIncrease(Matrix m)
	{
		if(m == null)
		{
			return 0;
		}
		if(m.getColumnDimension() != 3 || m.getRowDimension() != 3)
		{
			return 0;
		}
		Matrix mSub = m.getMatrix(0, 1, 0, 1);
		
		double det = m.det();
		double detSub = mSub.det();
		double normF = m.normF();
		
		if(detSub == 0 || normF == 0)
		{
			return 100000000;
		}
		
		return det / (detSub * normF);
	}
}
