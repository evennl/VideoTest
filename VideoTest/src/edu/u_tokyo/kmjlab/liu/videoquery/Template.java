package edu.u_tokyo.kmjlab.liu.videoquery;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_highgui;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacpp.opencv_core.IplImage;

import edu.u_tokyo.kmjlab.liu.videotest.BmpFilter;

import Jama.Matrix;

public class Template
{
	private Map<String, Matrix> map = null;
	private List<IplImage> list;
	public int length;
	public int width;
	public int height;
	
	public Template(String path)
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
		IplImage firstTemplateImage = opencv_highgui.cvLoadImage(path + fileNameList.get(0));
		width = firstTemplateImage.width();
		height = firstTemplateImage.height();
	}
	
	public Matrix generateGramMatrix(int x, int y, int frame)
	{
		if(map == null)
		{
			map = new HashMap<String, Matrix>(length * width * height);
		}
		String key = x + " " + y + " " + frame;
		Matrix m = map.get(key);
		
		if(m == null)
		{
			StPatch stPatch = new StPatch();
			m = stPatch.generateGramMatrix(list, x, y, frame);
			map.put(key, m);
		}
		return m;
	}
}
