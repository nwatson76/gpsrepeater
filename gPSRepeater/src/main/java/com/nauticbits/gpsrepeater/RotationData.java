package com.nauticbits.gpsrepeater;

// class not implemented with this app. Original idea was to use this class to calculated and return the angle of the device
 
public class RotationData {

	
	private String pitch;
	private String roll;
	private String azmuith;
	
	public RotationData(float fAzmuith, float fPitch, float fRoll)
	{
		// class not implemented with this app
		
		azmuith = String.format("%.0f",fAzmuith); 
		pitch = String.format("%.0f",fPitch);
		roll = String.format("%.0f",fRoll);  
	}
	
	public String getAzmuith()
	{
		return azmuith; 
	}
	
	public String getPitch()
	{
		return pitch; 
	}
	
	public String getRoll()
	{
		return roll; 
	}
	

}
