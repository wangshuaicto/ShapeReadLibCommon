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
	 * 存取shapInfo信息
	 */
	public List<ShpInfo> shpInfoList = null;
	/**
	 * 存取dbfInfo信息
	 */
	public List<DbfInfo> dbfInfoList = null;
	
	//设置路径
		public void setShapeFilePath(String filePath)
		{
			//测试语句
			
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
	 * 读取DBF
	 * @param file
	 */
		public DbfInfo readdbf(File file) 
		{
			DbfInfo dbfinfo = new DbfInfo();
			try
			{
				/**********************表示当前的版本信息*******************/
				DataInputStream dis = new DataInputStream(new FileInputStream(file));
				byte currentVersion = dis.readByte();
				Log.v("shape", "当前版本信息: "+currentVersion);
				dbfinfo.version = currentVersion;
				/************最近的更新日期 3个字节*****************/
				byte YY = dis.readByte();
				dbfinfo.YY = YY;
				byte MM = dis.readByte();
				dbfinfo.MM = MM;
				byte DD = dis.readByte();
				dbfinfo.DD = DD;
				Log.v("shape", "最近更新日期："+YY+":"+MM+":"+DD);
				/************文件中的记录条数**************/
				int recordNum = readIntLittleToBig(dis);
				Log.v("shape", "dbf文件记录条数： "+recordNum);
				dbfinfo.recordNum = recordNum;
				/*******************文件头中的字节数*****************/
				short byteNum = readShortLittleToBig(dis);
				Log.v("shape", "文件中的字节数："+byteNum);
				dbfinfo.byteNum = byteNum;
				/***********************一条记录中的字节长度**************/
				short onebyteLength = readShortLittleToBig(dis);
				Log.v("shape", "一条记录中的字节长度:"+onebyteLength);
				dbfinfo.onebyteLength = onebyteLength;
				/**********保留字节，用于以后添加新的说明性信息时使用，这里用 0 来填写***/
				dis.skipBytes(2);
				/**************表示未完成的操作**********/
				byte undoCode = dis.readByte();
				Log.v("shape", "未完成的操作："+undoCode);
				/*********************dBASE IV 编密码标记*********/
				byte dbaseIV = dis.readByte();
				Log.v("shape", "dBASE IV:"+dbaseIV);
				/******************保留12字节，用于多用户处理时使用。**************/
				dis.skipBytes(12);
				/**********************DBF 文件的 MDX 标识*************************/
				byte MDX = dis.readByte();
				Log.v("shape", "DBF 文件的 MDX 标识:"+MDX);
				/**************************Language driver ID*******************/
				byte Language_driver_ID = dis.readByte();
				Log.v("shape", "Language driver ID:"+Language_driver_ID);
				dbfinfo.Language_driver_ID = Language_driver_ID;
				/*****保留字节，用于以后添加新的说明性信息时使用，这里用 0 来填写**/
				dis.skipBytes(2);
				/****
				 * 记录项信息描述数组。 n 表示记录项的个数。
				 * 这个数组的结构在表 2.8 中有详细的解释。
				 */
//				dbfinfo.re
				List<DbfRecordDescription> dbfRecordDescriptionsList = new ArrayList<DbfRecordDescription>();
				int fieldCount = (byteNum-32)/32;
				Log.v("shape", "fieldCount: "+fieldCount);
				for(int i = 0; i < fieldCount;i++)
				{
					DbfRecordDescription dbfRecordDescription = new DbfRecordDescription();
					/**记录项名称，是 ASCII 码值**/
					byte[] recordNameArray = new byte[11];
					int flag = -1;
					for(int j = 0; j < 11; j++)
					{
						byte temp = dis.readByte();
						recordNameArray[j] = temp;
//						Log.v("shape","测试： "+ Byte.toString(temp));
						if(temp == 0)
						{
							flag = j;
							dis.skipBytes(11-(j+1));
							break;
						}
					}
					String recordName = new String(recordNameArray, 0, flag);
					dbfRecordDescription.recordName = recordName;
					Log.v("shape", "记录项名称: "+recordName);
					/**
					 * 记录项的数据类型，是 ASCII 码值。（ B 、 C 、 D 、 G 、 L 、 M 和 N ，具体的解释见表 2.9 ）
					 */
					char recordType = (char) dis.readByte();
					dbfRecordDescription.recordType = recordType;
					Log.v("shape", "记录项的数据类型: "+recordType);
					/**
					 * 保留字节，用于以后添加新的说明性信息时使用，这里用 0 来填写  4个。
					 */
					dis.skipBytes(4);
					/**记录项长度，二进制型 **/
					byte recordLength = dis.readByte();
					dbfRecordDescription.recordLength = recordLength;
					Log.v("shape", "记录项长度： "+recordLength);
					/**记录项的精度，二进制型。**/
					byte recordAccuracy = dis.readByte()  ;
					dbfRecordDescription.recordAccuracy = recordAccuracy;
					Log.v("shape", "记录项的精度: "+recordAccuracy);
					/**保留字节，用于以后添加新的说明性信息时使用，这里用 0 来填写     2个字节**/
					dis.skipBytes(2);
					/**工作区 ID**/
					byte workspaceId = dis.readByte();
					dbfRecordDescription.workspaceId=workspaceId;
					Log.v("shape", "工作区ID: "+workspaceId);
					/**
					 * 保留字节，用于以后添加新的说明性信息时使用，这里用 0 来填写  10个字节
					 */
					dis.skipBytes(10);
					/**MDX 标识。如果存在一个 MDX 格式的索引文件，那么这个记录项为真，否则为空
					 */
					byte MDXFlag = dis.readByte();
					Log.v("shape", "MDX 标识 : "+MDXFlag);
					dbfRecordDescription.MDXFlag=MDXFlag;
					dbfRecordDescriptionsList.add(dbfRecordDescription);
				}
				dbfinfo.recordName = dbfRecordDescriptionsList;
				
				
				Log.v("shape", "还剩多少字节： "+dis.available());
				
				/**
				 * 作为记录项终止标识
				 */
				 dis.skip(1);
				
				/**
				 * 循环读取记录实体信息部分  254 个字节
				 */
				 
				 List<String[]> dbfData = new ArrayList<String[]>();
				   
				//循环  recordNum个字段
				for(int i = 0; i<recordNum;i++)
				{
					Log.v("shape", "******************************"+i+"*******************************************");
//					testarr[i] = 
					/**
					 * 删除标识符
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
//							Log.v("shape","查看读入的字节是什么：  "+ Byte.toString(testarr[i]));
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
								// Log.v("shape","查看读入的字节是什么：  "+
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
	 * 读取SHP
	 */
	
	public ShpInfo readSHPHeader(File file)
	{
		try
		{
			ShpInfo shaInfo= new ShpInfo();
			DataInputStream dis = new DataInputStream(new FileInputStream(file));
			//读取前100个字节头文件
			int FileCode = dis.readInt();
			shaInfo.fileCode = FileCode;
			System.out.println("FileCode: "+FileCode);
			//保留的5个 Integer类型  一个Integer含有4个字节 
			dis.skipBytes(20);
			/************************文件实际长度***********************/
			int filelength = dis.readInt();
			shaInfo.fileLength = filelength;
			Log.v("shape", "filelength: "+filelength);
//			Log.v
			/**********版本号********************/
			int version = readIntLittleToBig(dis);
			shaInfo.version = version;
			Log.v("shape", "version: "+version);
			/**************shapetype类型(判断文件是点、线、面shp文件)*********************/
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
			/**************头文件结束*********************/
			
			switch(shapetype)
			{
			case 0:// Null Shape
				break;
			case 1:// Point
				shaInfo.PointDataList = readPointSHP(dis);
				
				Log.v("shape","是否为null: "+ (shaInfo.PointDataList == null));
				
				break;
			case 3:// PolyLine
				Log.v("shape", "***************PolyLine************************");
				/*************坐标范围Box  4个Double(little)***********************/
				double box1 = readDoubleLittleToBig(dis);
				double box2 = readDoubleLittleToBig(dis);
				double box3 = readDoubleLittleToBig(dis);
				double box4 = readDoubleLittleToBig(dis);
				Log.v("shape", "坐标范围Box: "+box1+","+box2+";"+box3+","+box4);
				/*******************子线段个数*************************/
				int NumParts  = readIntLittleToBig(dis);
				Log.v("shape", "子线段个数NumParts ： "+NumParts );
				/**********************坐标点数**************************
				 * 表示构成当前线目标所包含的坐标点个数
				 */
				int NumPoints = readIntLittleToBig(dis);
				Log.v("shape", "坐标点数NumPoints： "+NumPoints);
				/************************Part数组********************
				 * 记录了每个子线段的坐标在 Points 数组中的起始位置
				 */
				int[] Parts = new int[NumParts];
				for(int i = 0;i<Parts.length;i++)
				{
					Parts[i] = readIntLittleToBig(dis);
					Log.v("shape", "起始位置："+Parts[i]);
				}
				/******************Points数组*******************
				 * 记录了所有的坐标信息
				 */
				ShpPoints[] PointsArray = new ShpPoints[NumPoints];
				for(int i = 0;i<PointsArray.length;i++)
				{
					PointsArray[i] = new ShpPoints(readDoubleLittleToBig(dis), readDoubleLittleToBig(dis));
					Log.v("shape", "坐标信息"+(i+1)+": "+PointsArray[i].toString());
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
	
	
	/********读取点SHP
	 * @throws IOException ****************/
	public List<RecordPointContent> readPointSHP(DataInputStream dis) throws IOException
	{
//		while(dis.)
		List<RecordPointContent> recordPointContentList = new ArrayList<RecordPointContent>();
		while(dis.available() != 0)
		{
			RecordPointContent recordpointContent = new RecordPointContent();
			Log.v("shape", "***************Point************************");
			//读取记录头
			/********************Record Number*************************/
			int recordNumber = dis.readInt();
			Log.v("shape", "Record Number: "+recordNumber);
			recordpointContent.recordNumber = recordNumber;
			/********************Content Length*************************/
			int contentlength = dis.readInt();
			Log.v("shape", "Content Length: "+contentlength);
			recordpointContent.contentLength = contentlength;
			//读取记录内容
			/**************shapetype类型*********************/
			int shapetypee = readIntLittleToBig(dis);
			Log.v("shape", "shapetype: "+shapetypee);
			
			/*********************X方向坐标***********************/
			double X = readDoubleLittleToBig(dis);
			recordpointContent.XPoint = X;
			double Y = readDoubleLittleToBig(dis);
			recordpointContent.YPoint = Y;
			Log.v("shape", "字节剩余测试 ： "+dis.available());
			Log.v("shape", "X,Y: "+X+","+Y);
			recordPointContentList.add(recordpointContent);
		}
		return recordPointContentList;
	}
	
	/**读取面shp**/
	
	public List<RecordPolygonContent> readPolygonSHP(DataInputStream dis) throws IOException
	{
		List<RecordPolygonContent> recordPolygonContentsList = new ArrayList<RecordPolygonContent>();
		while(dis.available() != 0)
		{
			RecordPolygonContent recordPolygonContent = new RecordPolygonContent();
			Log.v("shape", "***************Polygon************************");
			//读取记录头
			/********************Record Number*************************/
			int recordNumberpolygon = dis.readInt();
			Log.v("shape", "Record Number: "+recordNumberpolygon);
			recordPolygonContent.recordNumber = recordNumberpolygon;
			/********************Content Length*************************/
			int contentlengthpolygon = dis.readInt();
			Log.v("shape", "Content Length: "+contentlengthpolygon);
			recordPolygonContent.contentLength = contentlengthpolygon;
			//读取记录内容
			/**************shapetype类型*********************/
			int shapetypeepolygon = readIntLittleToBig(dis);
			Log.v("shape", "shapetype: "+shapetypeepolygon);
			
			/*************坐标范围Box  4个Double(little)***********************/
			double box1polygon = readDoubleLittleToBig(dis);
			recordPolygonContent.XLeftPoint = box1polygon;
			double box2polygon = readDoubleLittleToBig(dis);
			recordPolygonContent.YLeftPoint = box2polygon;
			double box3polygon = readDoubleLittleToBig(dis);
			recordPolygonContent.XRightPoint = box3polygon;
			double box4polygon = readDoubleLittleToBig(dis);
			recordPolygonContent.YRightPoint = box4polygon;
			Log.v("shape", "坐标范围Box: "+box1polygon+","+box2polygon+";"+box3polygon+","+box4polygon);
			/*******************子线段个数************************
			 *表示构成当前面状目标的子环的个数
			 * */
			int NumPartspolygon  = readIntLittleToBig(dis);
			Log.v("shape", "子线段个数NumPartspolygon ： "+NumPartspolygon  );
			recordPolygonContent.NumPartspolygon = NumPartspolygon;
			/**********************坐标点数**************************
			 * 表示构成当前面状目标所包含的坐标点个数
			 */
			int NumPointspolygon = readIntLittleToBig(dis);
			Log.v("shape", "坐标点数NumPointspolygon： "+NumPointspolygon);
			recordPolygonContent.NumPointspolygon = NumPointspolygon;
			/************************Part数组********************
			 * 记录了每个子环的坐标在 Points 数组中的起始位置
			 */
			int[] Partspolygon = new int[NumPartspolygon];
			recordPolygonContent.startPosArr = Partspolygon;
			for(int i = 0;i<Partspolygon.length;i++)
			{
				Partspolygon[i] = readIntLittleToBig(dis);
				Log.v("shape", "起始位置："+Partspolygon[i]);
			}
			/******************Points数组*******************
			 * 记录了所有的坐标信息
			 */
//			recordPolygonContent.PolygonPointList
			List<ShpPoints> shpPList = new ArrayList<ShpPoints>();
			
			ShpPoints[] PointsArraypolygon = new ShpPoints[NumPointspolygon];
			
			for(int i = 0;i<PointsArraypolygon.length;i++)
			{
				PointsArraypolygon[i] = new ShpPoints(readDoubleLittleToBig(dis), readDoubleLittleToBig(dis));
				shpPList.add(PointsArraypolygon[i]);
				Log.v("shape", "坐标信息"+(i+1)+": "+PointsArraypolygon[i].toString());
			}
			recordPolygonContent.PolygonPointList = shpPList;
			recordPolygonContentsList.add(recordPolygonContent);
		}
		return recordPolygonContentsList;
	}
	
	
	/************LittleToBig**********************/
	
	/*
	 * 读取little类型的Integer  字节调换顺序  1、4互换 2、3互换组成新的4字节
	 * 假设字节排练顺序 1、2、3、4
	 * byte1汉字语意表示"字节1"
	 * java默认读入Big-endian类型
	 */
	public int readIntLittleToBig(DataInputStream dis) throws IOException
	{
		int translate = 255;
		//temp保存读入的四个字节
		int temp = dis.readInt();
		//先取低八位并移动到1
		int byte4 = temp<<24;  //4 0 0 0  
		//取高八位并移动到4
		int byte1 = temp>>>24; //0 0 0 1
		//取2 并且移动到3
		int byte2 = (temp&(translate<<16))>>8;//0 0 2 0
		//取3并且移动到2
		int byte3 = (temp&(translate<<8))<<8;//0 3 0 0
		//重新组装
		int result = byte1|byte2|byte3|byte4;
		return result;
	}
	/*
	 * 读取little类型的Double  
	 * 假设高字节到低字节的顺序为 1、2、3、4、5、6、7、8
	 * Double类型不支持移位、与、或操作 
	 */
	public double readDoubleLittleToBig(DataInputStream dis) throws IOException
	{
	    //惟一成功的方法
		byte[] byteArray = new byte[8];
		for(int i = 0;i<byteArray.length;i++)
		{
			byteArray[i]=dis.readByte();
		}
		ByteBuffer byteBuffer = ByteBuffer.allocate(8);
		byteBuffer.put(byteArray);
//		System.out.println("此缓冲区的字节顺序： "+byteBuffer.order());
		byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
//		System.out.println("修改后的字节顺序： "+byteBuffer.order());
		byteBuffer.limit(byteBuffer.position()).position(0);
		DoubleBuffer db = byteBuffer.asDoubleBuffer();
		double result= db.get();
		return result;
	}
	/***
	 * 将读入的2个字节进行字节序的排列有BIG_ENDIAN转为LITTLE_ENDIAN
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
//		System.out.println("此缓冲区的字节顺序： "+byteBuffer.order());
		byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
//		System.out.println("修改后的字节顺序： "+byteBuffer.order());
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
//		System.out.println("此缓冲区的字节顺序： "+byteBuffer.order());
		byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
//		System.out.println("修改后的字节顺序： "+byteBuffer.order());
		byteBuffer.limit(byteBuffer.position()).position(0);
		CharBuffer sb = byteBuffer.asCharBuffer();
		char result= sb.get();
		return result;
	}
}
