package com.example.shapefileread;

import java.util.List;

public class ShpInfo
{
	/**SHPͷ�ļ�����Ϣ**/
	public int fileCode;
	public int fileLength;
	public int version;
	public int shapeType;
	public double Xmin;
	public double Ymin;
	public double Xmax;
	public double Ymax;
	
	//���
	public List<RecordPointContent> PointDataList = null;
	//���
	public List<RecordPolygonContent> PolygonDataList = null;
	
	public ShpInfo()
	{
		
	}
}


