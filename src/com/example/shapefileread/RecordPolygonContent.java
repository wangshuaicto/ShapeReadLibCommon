package com.example.shapefileread;
import java.util.List;

public class RecordPolygonContent
{
		public int recordNumber;
		public int contentLength;
		
		/**����***/
		public double XLeftPoint;
		public double YLeftPoint;
		/**����**/
		public double XRightPoint;
		public double YRightPoint;
		
		public int NumPartspolygon;
		
		public int NumPointspolygon;
		
		/**
		 * ��ʼλ������
		 */
		public int[] startPosArr;
		
//		public int startPos;
		
		public List<ShpPoints> PolygonPointList = null;
		
//		public void recordNumbe
}
