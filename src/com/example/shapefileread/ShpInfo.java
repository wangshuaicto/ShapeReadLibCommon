package com.example.shapefileread;

import java.util.List;

public class ShpInfo
{
	/**SHP头文件的信息**/
	public int fileCode;
	public int fileLength;
	public int version;
	public int shapeType;
	public double Xmin;
	public double Ymin;
	public double Xmax;
	public double Ymax;
	
	//点的
	public List<RecordPointContent> PointDataList = null;
	//面的
	public List<RecordPolygonContent> PolygonDataList = null;
	
	public ShpInfo()
	{
		
	}
}


