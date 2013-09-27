package com.example.shapefileread;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class ShapeReadLauncher
{
	private File file = null;
	
	/**
	 * ��ȡshapInfo��Ϣ
	 */
	public List<ShpInfo> shpInfoList = null;
	/**
	 * ��ȡdbfInfo��Ϣ
	 */
	public List<DbfInfo> dbfInfoList = null;
	
	//����·��
		public void setShapeFilePath(String filePath)
		{
			//�������
			
			file = new File(filePath);
			shpInfoList = new ArrayList<ShpInfo>();
			dbfInfoList = new ArrayList<DbfInfo>();
			if(file.isDirectory())
			{
				File[] files = file.listFiles();
				for(File file2:files)
				{
//					ShpInfo shapInfo = new ShpInfo();
					if(file2.getName().endsWith(".shp"))
					{
//						Log.v("shape", file2.getName());
//						shpInfoList.add(readSHPHeader(file2));
//						
					}
					else if(file2.getName().endsWith(".dbf"))
					{
						Log.v("shape", file2.getName());
						dbfInfoList.add(readdbf(file2));
					}
				}
			}
			else
			{
				return;
			}
		}
		
	/**
	 * ��ȡDBF
	 * @param file
	 */
		public DbfInfo readdbf(File file) 
		{
			DbfInfo dbfinfo = new DbfInfo();
			try
			{
				/**********************��ʾ��ǰ�İ汾��Ϣ*******************/
				DataInputStream dis = new DataInputStream(new FileInputStream(file));
				byte currentVersion = dis.readByte();
				Log.v("shape", "��ǰ�汾��Ϣ: "+currentVersion);
				dbfinfo.version = currentVersion;
				/************����ĸ������� 3���ֽ�*****************/
				byte YY = dis.readByte();
				dbfinfo.YY = YY;
				byte MM = dis.readByte();
				dbfinfo.MM = MM;
				byte DD = dis.readByte();
				dbfinfo.DD = DD;
				Log.v("shape", "����������ڣ�"+YY+":"+MM+":"+DD);
				/************�ļ��еļ�¼����**************/
				int recordNum = readIntLittleToBig(dis);
				Log.v("shape", "dbf�ļ���¼������ "+recordNum);
				dbfinfo.recordNum = recordNum;
				/*******************�ļ�ͷ�е��ֽ���*****************/
				short byteNum = readShortLittleToBig(dis);
				Log.v("shape", "�ļ��е��ֽ�����"+byteNum);
				dbfinfo.byteNum = byteNum;
				/***********************һ����¼�е��ֽڳ���**************/
				short onebyteLength = readShortLittleToBig(dis);
				Log.v("shape", "һ����¼�е��ֽڳ���:"+onebyteLength);
				dbfinfo.onebyteLength = onebyteLength;
				/**********�����ֽڣ������Ժ�����µ�˵������Ϣʱʹ�ã������� 0 ����д***/
				dis.skipBytes(2);
				/**************��ʾδ��ɵĲ���**********/
				byte undoCode = dis.readByte();
				Log.v("shape", "δ��ɵĲ�����"+undoCode);
				/*********************dBASE IV ��������*********/
				byte dbaseIV = dis.readByte();
				Log.v("shape", "dBASE IV:"+dbaseIV);
				/******************����12�ֽڣ����ڶ��û�����ʱʹ�á�**************/
				dis.skipBytes(12);
				/**********************DBF �ļ��� MDX ��ʶ*************************/
				byte MDX = dis.readByte();
				Log.v("shape", "DBF �ļ��� MDX ��ʶ:"+MDX);
				/**************************Language driver ID*******************/
				byte Language_driver_ID = dis.readByte();
				Log.v("shape", "Language driver ID:"+Language_driver_ID);
				dbfinfo.Language_driver_ID = Language_driver_ID;
				/*****�����ֽڣ������Ժ�����µ�˵������Ϣʱʹ�ã������� 0 ����д**/
				dis.skipBytes(2);
				/****
				 * ��¼����Ϣ�������顣 n ��ʾ��¼��ĸ�����
				 * �������Ľṹ�ڱ� 2.8 ������ϸ�Ľ��͡�
				 */
//				dbfinfo.re
				List<DbfRecordDescription> dbfRecordDescriptionsList = new ArrayList<DbfRecordDescription>();
				int fieldCount = (byteNum-32)/32;
				Log.v("shape", "fieldCount: "+fieldCount);
				for(int i = 0; i < fieldCount;i++)
				{
					DbfRecordDescription dbfRecordDescription = new DbfRecordDescription();
					/**��¼�����ƣ��� ASCII ��ֵ**/
					byte[] recordNameArray = new byte[11];
					int flag = -1;
					for(int j = 0; j < 11; j++)
					{
						byte temp = dis.readByte();
						recordNameArray[j] = temp;
//						Log.v("shape","���ԣ� "+ Byte.toString(temp));
						if(temp == 0)
						{
							flag = j;
							dis.skipBytes(11-(j+1));
							break;
						}
					}
					String recordName = new String(recordNameArray, 0, flag);
					dbfRecordDescription.recordName = recordName;
					Log.v("shape", "��¼������: "+recordName);
					/**
					 * ��¼����������ͣ��� ASCII ��ֵ���� B �� C �� D �� G �� L �� M �� N ������Ľ��ͼ��� 2.9 ��
					 */
					char recordType = (char) dis.readByte();
					dbfRecordDescription.recordType = recordType;
					Log.v("shape", "��¼�����������: "+recordType);
					/**
					 * �����ֽڣ������Ժ�����µ�˵������Ϣʱʹ�ã������� 0 ����д  4����
					 */
					dis.skipBytes(4);
					/**��¼��ȣ��������� **/
					byte recordLength = dis.readByte();
					dbfRecordDescription.recordLength = recordLength;
					Log.v("shape", "��¼��ȣ� "+recordLength);
					/**��¼��ľ��ȣ��������͡�**/
					byte recordAccuracy = dis.readByte()  ;
					dbfRecordDescription.recordAccuracy = recordAccuracy;
					Log.v("shape", "��¼��ľ���: "+recordAccuracy);
					/**�����ֽڣ������Ժ�����µ�˵������Ϣʱʹ�ã������� 0 ����д     2���ֽ�**/
					dis.skipBytes(2);
					/**������ ID**/
					byte workspaceId = dis.readByte();
					dbfRecordDescription.workspaceId=workspaceId;
					Log.v("shape", "������ID: "+workspaceId);
					/**
					 * �����ֽڣ������Ժ�����µ�˵������Ϣʱʹ�ã������� 0 ����д  10���ֽ�
					 */
					dis.skipBytes(10);
					/**MDX ��ʶ���������һ�� MDX ��ʽ�������ļ�����ô�����¼��Ϊ�棬����Ϊ��
					 */
					byte MDXFlag = dis.readByte();
					Log.v("shape", "MDX ��ʶ : "+MDXFlag);
					dbfRecordDescription.MDXFlag=MDXFlag;
					dbfRecordDescriptionsList.add(dbfRecordDescription);
				}
				dbfinfo.recordName = dbfRecordDescriptionsList;
				
				
				Log.v("shape", "��ʣ�����ֽڣ� "+dis.available());
				
				/**
				 * ��Ϊ��¼����ֹ��ʶ
				 */
				 dis.skip(1);
				
				/**
				 * ѭ����ȡ��¼ʵ����Ϣ����  254 ���ֽ�
				 */
				 
				 List<String[]> dbfData = new ArrayList<String[]>();
				   
				//ѭ��  recordNum���ֶ�
				for(int i = 0; i<recordNum;i++)
				{
					Log.v("shape", "******************************"+i+"*******************************************");
//					testarr[i] = 
					/**
					 * ɾ����ʶ��
					 */
					dis.skip(1);
					
					String[] data = new String[fieldCount];
					
					
					for(int j = 0; j<fieldCount; j++)
					{
//						dbfRecordDescriptionsList.get(j).recordName;
//						switch(dbfRecordDescriptionsList.get(j).recordType)
//						{
//						case 'C':
//							break;
//						}
						DbfRecordDescription dbfredes = dbfRecordDescriptionsList.get(j);
						if(dbfredes.recordType=='C')
						{
							byte[] testarr = new byte[254];
							
							for (int k = 0; k < testarr.length; k++)
							{
								testarr[k] = dis.readByte();
//							Log.v("shape","�鿴������ֽ���ʲô��  "+ Byte.toString(testarr[i]));
							}
							String valuetest = new String(testarr, 0, testarr.length-1,"gb2312");
							data[j] = valuetest;
							Log.v("shape", dbfredes.recordName+" : "+valuetest);
						}
						else
						{
							byte[] testarr = new byte[dbfredes.recordLength];
	
							for (int k = 0; k < testarr.length; k++)
							{
								testarr[k] = dis.readByte();
								// Log.v("shape","�鿴������ֽ���ʲô��  "+
								// Byte.toString(testarr[i]));
							}
							String valuetest = new String(testarr, 0,
									testarr.length - 1,"gb2312");
							data[j] = valuetest;
							Log.v("shape", dbfredes.recordName+" : " + valuetest);
						}
					}
					dbfData.add(data);
				}
				dbfinfo.recordData = dbfData;
				return dbfinfo;
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}
		
	/*
	 * ��ȡSHP
	 */
	
	public ShpInfo readSHPHeader(File file)
	{
		try
		{
			ShpInfo shaInfo= new ShpInfo();
			DataInputStream dis = new DataInputStream(new FileInputStream(file));
			//��ȡǰ100���ֽ�ͷ�ļ�
			int FileCode = dis.readInt();
			shaInfo.fileCode = FileCode;
			System.out.println("FileCode: "+FileCode);
			//������5�� Integer����  һ��Integer����4���ֽ� 
			dis.skipBytes(20);
			/************************�ļ�ʵ�ʳ���***********************/
			int filelength = dis.readInt();
			shaInfo.fileLength = filelength;
			Log.v("shape", "filelength: "+filelength);
//			Log.v
			/**********�汾��********************/
			int version = readIntLittleToBig(dis);
			shaInfo.version = version;
			Log.v("shape", "version: "+version);
			/**************shapetype����(�ж��ļ��ǵ㡢�ߡ���shp�ļ�)*********************/
			int shapetype = readIntLittleToBig(dis);
			Log.v("shape", "shapetype: "+shapetype);
			shaInfo.shapeType = shapetype;
			/*******************Xmin**********************/
			double Xmin = readDoubleLittleToBig(dis);
			Log.v("shape", "Xmin: "+Xmin);
			shaInfo.Xmin = Xmin;
			/********************Ymin*************************/
			double Ymin = readDoubleLittleToBig(dis);
			Log.v("shape", "Ymin: "+Ymin);
			shaInfo.Ymin = Ymin;
			/********************Xmax*************************/
			double Xmax = readDoubleLittleToBig(dis);
			Log.v("shape", "Xmax: "+Xmax);
			shaInfo.Xmax = Xmax;
			/********************Ymax*************************/
			double Ymax = readDoubleLittleToBig(dis);
			Log.v("shape", "Ymax: "+Ymax);
			shaInfo.Ymax = Ymax;
			/********************Zmin*************************/
			double Zmin = readDoubleLittleToBig(dis);
			Log.v("shape", "Zmin: "+Zmin);
			/********************Zmax*************************/
			double Zmax = readDoubleLittleToBig(dis);
			Log.v("shape", "Zmax: "+Zmax);
			/********************Mmin*************************/
			double Mmin = readDoubleLittleToBig(dis);
			Log.v("shape", "Mmin: "+Mmin);
			/********************Mmax*************************/
			double Mmax = readDoubleLittleToBig(dis);
			Log.v("shape", "Mmax: "+Mmax);
			/**************ͷ�ļ�����*********************/
			
			switch(shapetype)
			{
			case 0:// Null Shape
				break;
			case 1:// Point
				shaInfo.PointDataList = readPointSHP(dis);
				
				Log.v("shape","�Ƿ�Ϊnull: "+ (shaInfo.PointDataList == null));
				
				break;
			case 3:// PolyLine
				Log.v("shape", "***************PolyLine************************");
				/*************���귶ΧBox  4��Double(little)***********************/
				double box1 = readDoubleLittleToBig(dis);
				double box2 = readDoubleLittleToBig(dis);
				double box3 = readDoubleLittleToBig(dis);
				double box4 = readDoubleLittleToBig(dis);
				Log.v("shape", "���귶ΧBox: "+box1+","+box2+";"+box3+","+box4);
				/*******************���߶θ���*************************/
				int NumParts  = readIntLittleToBig(dis);
				Log.v("shape", "���߶θ���NumParts �� "+NumParts );
				/**********************�������**************************
				 * ��ʾ���ɵ�ǰ��Ŀ������������������
				 */
				int NumPoints = readIntLittleToBig(dis);
				Log.v("shape", "�������NumPoints�� "+NumPoints);
				/************************Part����********************
				 * ��¼��ÿ�����߶ε������� Points �����е���ʼλ��
				 */
				int[] Parts = new int[NumParts];
				for(int i = 0;i<Parts.length;i++)
				{
					Parts[i] = readIntLittleToBig(dis);
					Log.v("shape", "��ʼλ�ã�"+Parts[i]);
				}
				/******************Points����*******************
				 * ��¼�����е�������Ϣ
				 */
				ShpPoints[] PointsArray = new ShpPoints[NumPoints];
				for(int i = 0;i<PointsArray.length;i++)
				{
					PointsArray[i] = new ShpPoints(readDoubleLittleToBig(dis), readDoubleLittleToBig(dis));
					Log.v("shape", "������Ϣ"+(i+1)+": "+PointsArray[i].toString());
				}
				break;
			case 5://Polygon 
				shaInfo.PolygonDataList = readPolygonSHP(dis);
				break;
			case 8://MultiPoint 
				break;
			}
			return shaInfo;
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	
	/********��ȡ��SHP
	 * @throws IOException ****************/
	public List<RecordPointContent> readPointSHP(DataInputStream dis) throws IOException
	{
//		while(dis.)
		List<RecordPointContent> recordPointContentList = new ArrayList<RecordPointContent>();
		while(dis.available() != 0)
		{
			RecordPointContent recordpointContent = new RecordPointContent();
			Log.v("shape", "***************Point************************");
			//��ȡ��¼ͷ
			/********************Record Number*************************/
			int recordNumber = dis.readInt();
			Log.v("shape", "Record Number: "+recordNumber);
			recordpointContent.recordNumber = recordNumber;
			/********************Content Length*************************/
			int contentlength = dis.readInt();
			Log.v("shape", "Content Length: "+contentlength);
			recordpointContent.contentLength = contentlength;
			//��ȡ��¼����
			/**************shapetype����*********************/
			int shapetypee = readIntLittleToBig(dis);
			Log.v("shape", "shapetype: "+shapetypee);
			
			/*********************X��������***********************/
			double X = readDoubleLittleToBig(dis);
			recordpointContent.XPoint = X;
			double Y = readDoubleLittleToBig(dis);
			recordpointContent.YPoint = Y;
			Log.v("shape", "�ֽ�ʣ����� �� "+dis.available());
			Log.v("shape", "X,Y: "+X+","+Y);
			recordPointContentList.add(recordpointContent);
		}
		return recordPointContentList;
	}
	
	/**��ȡ��shp**/
	
	public List<RecordPolygonContent> readPolygonSHP(DataInputStream dis) throws IOException
	{
		List<RecordPolygonContent> recordPolygonContentsList = new ArrayList<RecordPolygonContent>();
		while(dis.available() != 0)
		{
			RecordPolygonContent recordPolygonContent = new RecordPolygonContent();
			Log.v("shape", "***************Polygon************************");
			//��ȡ��¼ͷ
			/********************Record Number*************************/
			int recordNumberpolygon = dis.readInt();
			Log.v("shape", "Record Number: "+recordNumberpolygon);
			recordPolygonContent.recordNumber = recordNumberpolygon;
			/********************Content Length*************************/
			int contentlengthpolygon = dis.readInt();
			Log.v("shape", "Content Length: "+contentlengthpolygon);
			recordPolygonContent.contentLength = contentlengthpolygon;
			//��ȡ��¼����
			/**************shapetype����*********************/
			int shapetypeepolygon = readIntLittleToBig(dis);
			Log.v("shape", "shapetype: "+shapetypeepolygon);
			
			/*************���귶ΧBox  4��Double(little)***********************/
			double box1polygon = readDoubleLittleToBig(dis);
			recordPolygonContent.XLeftPoint = box1polygon;
			double box2polygon = readDoubleLittleToBig(dis);
			recordPolygonContent.YLeftPoint = box2polygon;
			double box3polygon = readDoubleLittleToBig(dis);
			recordPolygonContent.XRightPoint = box3polygon;
			double box4polygon = readDoubleLittleToBig(dis);
			recordPolygonContent.YRightPoint = box4polygon;
			Log.v("shape", "���귶ΧBox: "+box1polygon+","+box2polygon+";"+box3polygon+","+box4polygon);
			/*******************���߶θ���************************
			 *��ʾ���ɵ�ǰ��״Ŀ����ӻ��ĸ���
			 * */
			int NumPartspolygon  = readIntLittleToBig(dis);
			Log.v("shape", "���߶θ���NumPartspolygon �� "+NumPartspolygon  );
			recordPolygonContent.NumPartspolygon = NumPartspolygon;
			/**********************�������**************************
			 * ��ʾ���ɵ�ǰ��״Ŀ������������������
			 */
			int NumPointspolygon = readIntLittleToBig(dis);
			Log.v("shape", "�������NumPointspolygon�� "+NumPointspolygon);
			recordPolygonContent.NumPointspolygon = NumPointspolygon;
			/************************Part����********************
			 * ��¼��ÿ���ӻ��������� Points �����е���ʼλ��
			 */
			int[] Partspolygon = new int[NumPartspolygon];
			recordPolygonContent.startPosArr = Partspolygon;
			for(int i = 0;i<Partspolygon.length;i++)
			{
				Partspolygon[i] = readIntLittleToBig(dis);
				Log.v("shape", "��ʼλ�ã�"+Partspolygon[i]);
			}
			/******************Points����*******************
			 * ��¼�����е�������Ϣ
			 */
//			recordPolygonContent.PolygonPointList
			List<ShpPoints> shpPList = new ArrayList<ShpPoints>();
			
			ShpPoints[] PointsArraypolygon = new ShpPoints[NumPointspolygon];
			
			for(int i = 0;i<PointsArraypolygon.length;i++)
			{
				PointsArraypolygon[i] = new ShpPoints(readDoubleLittleToBig(dis), readDoubleLittleToBig(dis));
				shpPList.add(PointsArraypolygon[i]);
				Log.v("shape", "������Ϣ"+(i+1)+": "+PointsArraypolygon[i].toString());
			}
			recordPolygonContent.PolygonPointList = shpPList;
			recordPolygonContentsList.add(recordPolygonContent);
		}
		return recordPolygonContentsList;
	}
	
	
	/************LittleToBig**********************/
	
	/*
	 * ��ȡlittle���͵�Integer  �ֽڵ���˳��  1��4���� 2��3��������µ�4�ֽ�
	 * �����ֽ�����˳�� 1��2��3��4
	 * byte1���������ʾ"�ֽ�1"
	 * javaĬ�϶���Big-endian����
	 */
	public int readIntLittleToBig(DataInputStream dis) throws IOException
	{
		int translate = 255;
		//temp���������ĸ��ֽ�
		int temp = dis.readInt();
		//��ȡ�Ͱ�λ���ƶ���1
		int byte4 = temp<<24;  //4 0 0 0  
		//ȡ�߰�λ���ƶ���4
		int byte1 = temp>>>24; //0 0 0 1
		//ȡ2 �����ƶ���3
		int byte2 = (temp&(translate<<16))>>8;//0 0 2 0
		//ȡ3�����ƶ���2
		int byte3 = (temp&(translate<<8))<<8;//0 3 0 0
		//������װ
		int result = byte1|byte2|byte3|byte4;
		return result;
	}
	/*
	 * ��ȡlittle���͵�Double  
	 * ������ֽڵ����ֽڵ�˳��Ϊ 1��2��3��4��5��6��7��8
	 * Double���Ͳ�֧����λ���롢����� 
	 */
	public double readDoubleLittleToBig(DataInputStream dis) throws IOException
	{
	    //Ωһ�ɹ��ķ���
		byte[] byteArray = new byte[8];
		for(int i = 0;i<byteArray.length;i++)
		{
			byteArray[i]=dis.readByte();
		}
		ByteBuffer byteBuffer = ByteBuffer.allocate(8);
		byteBuffer.put(byteArray);
//		System.out.println("�˻��������ֽ�˳�� "+byteBuffer.order());
		byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
//		System.out.println("�޸ĺ���ֽ�˳�� "+byteBuffer.order());
		byteBuffer.limit(byteBuffer.position()).position(0);
		DoubleBuffer db = byteBuffer.asDoubleBuffer();
		double result= db.get();
		return result;
	}
	/***
	 * �������2���ֽڽ����ֽ����������BIG_ENDIANתΪLITTLE_ENDIAN
	 * @param dis
	 * @return short
	 * @throws IOException
	 */
	public short readShortLittleToBig(DataInputStream dis) throws IOException
	{
		byte[] byteArray = new byte[2];
		for(int i = 0; i<byteArray.length; i++)
		{
			byteArray[i] = dis.readByte();
		}
		ByteBuffer byteBuffer = ByteBuffer.allocate(2);
		byteBuffer.put(byteArray);
//		System.out.println("�˻��������ֽ�˳�� "+byteBuffer.order());
		byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
//		System.out.println("�޸ĺ���ֽ�˳�� "+byteBuffer.order());
		byteBuffer.limit(byteBuffer.position()).position(0);
		ShortBuffer sb = byteBuffer.asShortBuffer();
		short result= sb.get();
		return result;
	}
	
	
	public char readCharLittleToBig(DataInputStream dis) throws IOException
	{
		byte[] byteArray = new byte[2];
		for(int i = 0; i<byteArray.length; i++)
		{
			byteArray[i] = dis.readByte();
		}
		ByteBuffer byteBuffer = ByteBuffer.allocate(2);
		byteBuffer.put(byteArray);
//		System.out.println("�˻��������ֽ�˳�� "+byteBuffer.order());
		byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
//		System.out.println("�޸ĺ���ֽ�˳�� "+byteBuffer.order());
		byteBuffer.limit(byteBuffer.position()).position(0);
		CharBuffer sb = byteBuffer.asCharBuffer();
		char result= sb.get();
		return result;
	}
}
