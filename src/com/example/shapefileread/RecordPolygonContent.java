package com.example.shapefileread;
import java.util.List;

public class RecordPolygonContent
{
		public int recordNumber;
		public int contentLength;
		
		/**左上***/
		public double XLeftPoint;
		public double YLeftPoint;
		/**右下**/
		public double XRightPoint;
		public double YRightPoint;
		
		public int NumPartspolygon;
		
		public int NumPointspolygon;
		
		/**
		 * 起始位置数组
		 */
		public int[] startPosArr;
		
//		public int startPos;
		
		public List<ShpPoints> PolygonPointList = null;
		
//		public void recordNumbe
}
