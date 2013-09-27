package com.example.shapefileread;

import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity
{

	public ShapeReadLauncher shapeReadLauncher;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		shapeReadLauncher = new ShapeReadLauncher();
		
//		shapeReadLauncher.setShapeFilePath("/mnt/sdcard/shapTest");
		shapeReadLauncher.setShapeFilePath("/mnt/sdcard/SingleShapTest");
		
		/***************Test*****************/ 
		  
//		ShpInfo shpTest = shapeReadLauncher.shpInfoList.get(0);
//		Log.v("shape", "shp ≤‚ ‘  ‰≥ˆ£∫ "+shpTest.PolygonDataList.get(0).XRightPoint);
		
//		DbfInfo dbfTest = shapeReadLauncher.dbfInfoList.get(0);
//		
//		Log.v("shape","dbf≤‚ ‘ ‰≥ˆ£∫ "+ dbfTest.recordName.get(0).recordName);
//		Log.v("shape","dbf≤‚ ‘ ‰≥ˆValue£∫ "+ dbfTest.recordData.get(0).toString());
//		List<String[]> test = dbfTest.recordData;
//		Iterator<String[]> iter = test.iterator();
//		while(iter.hasNext())
//		{
//			String[] temp = iter.next();
//			for(String value:temp)
//			{
//				Log.v("shape", "dbfValue≤‚ ‘ :  "+value);
//			}
//		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
