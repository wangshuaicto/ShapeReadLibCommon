package com.example.shapefileread;

import java.util.List;

public class DbfInfo
{
	public DbfInfo()
	{
		
	}
	public  byte version;
	
	public byte YY;
	
	public byte MM;
	
	public byte DD;
	
	public int recordNum;
	
	public short byteNum;
	
	public short onebyteLength;
	
	public byte Language_driver_ID;
	
	public List<DbfRecordDescription> recordName;
	
	public List<String[]> recordData;
	
}
