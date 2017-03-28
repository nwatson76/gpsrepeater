package com.nauticbits.gpsrepeater;

//class used to store the device light sensor data
public class LightData {

	private String light;
	
	public LightData(float flight)
	{
		// set the float as a string with 5 significant digits
		light = String.format("%.5f",flight); 
	}
	
	public String getLight()
	{
		return light; 
	}

}
