package edu.u_tokyo.kmjlab.liu.videoquery;

import edu.u_tokyo.kmjlab.liu.model.videoquery.GramMatrix;
import Jama.Matrix;

public class MatrixCalculation
{
	static public float getInconsistency(GramMatrix m1, GramMatrix m2)
	{
		if(m1 == null || m2 == null)
		{
			return 100000;
		}

		double[][] m12Array = new double [3][3];
		m12Array[0][0] = m1.getM11() + m2.getM11();
		m12Array[0][1] = m1.getM12() + m2.getM12();
		m12Array[0][2] = m1.getM13() + m2.getM13();
		m12Array[1][0] = m1.getM21() + m2.getM21();
		m12Array[1][1] = m1.getM22() + m2.getM22();
		m12Array[1][2] = m1.getM23() + m2.getM23();
		m12Array[2][0] = m1.getM31() + m2.getM31();
		m12Array[2][1] = m1.getM32() + m2.getM32();
		m12Array[2][2] = m1.getM33() + m2.getM33();
		
		Matrix m12 = new Matrix(m12Array);
		
		float rankIncrease12 = getRankIncrease(m12);
		
		float min = Math.min(m1.getRankIncrease(), m2.getRankIncrease());
		if(min == 0)
		{
			return 100000000;
		}
		
		return rankIncrease12 / min;
	}
	
	
	static public float getRankIncrease(Matrix m)
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
		
		return (float)(det / (detSub * normF));
	}
}
