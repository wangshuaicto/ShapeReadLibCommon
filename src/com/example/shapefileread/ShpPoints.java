package com.example.shapefileread;

public class ShpPoints
{
	double latitude;
	double longtitude;

	public ShpPoints(double latitude, double longtitude)
	{
		this.latitude = latitude;
		this.longtitude = longtitude;
	}

	public double getLatitude()
	{
		return latitude;
	}

	public double getLongtitude()
	{
		return longtitude;
	}

	@Override
	public String toString()
	{
		// TODO Auto-generated method stub
		return latitude + "," + longtitude;
	}
}
