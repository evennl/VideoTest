package edu.u_tokyo.kmjlab.liu.videotest;

import java.util.Date;
import java.util.List;

import edu.u_tokyo.kmjlab.liu.business.features.CuboidBu;
import edu.u_tokyo.kmjlab.liu.model.features.CuboidFeatures;

public class DatabaseTest
{
	public static void main(String[] args)
	{
		CuboidBu cuboidBu = new CuboidBu();
		//CuboidFeatures cuboid = new CuboidFeatures(null, "test", 1, 2, 3, "22222222", null, new Date());
		//cuboidBu.save(cuboid);
		List<CuboidFeatures> list = cuboidBu.listByTime("2014-09-27", null);
		System.out.println(list.size());
		
		list = cuboidBu.listByTime("2014-09-29", null);
		System.out.println(list.size());
		list = cuboidBu.listByTime("2014-09-30", null);
		System.out.println(list.size());
		
		
		list = cuboidBu.listByTime(null, "2014-09-28");
		System.out.println(list.size());
		list = cuboidBu.listByTime(null, "2014-09-29");
		System.out.println(list.size());
		list = cuboidBu.listByTime(null, "2014-09-30");
		System.out.println(list.size());
		
		
		list = cuboidBu.listByTime("2014-09-28", "2014-09-29");
		System.out.println(list.size());
		list = cuboidBu.listByTime("2014-09-30", "2014-09-30");
		System.out.println(list.size());
	}

}
