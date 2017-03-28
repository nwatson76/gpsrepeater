package com.nauticbits.gpsrepeater;

import android.location.Location;

//class that recieves a Location object using Dependency Injection and provides all
//GPS metrics required by this app
public class GpsData {
	
	//instantiate variables for GPS metrics 
	private String mlatDecimal;
	private String mlongDecimal;
	private String mlatDMS;
	private String mlongDMS;
	private String mlatDDMM;
	private String mlongDDMM;
	private String mspeedKnots;
	private String mspeedKMH;
	private String mspeedMPH;
	private String mspeedMPS;
	private String maccuracy;
	private String mbearing;
	private String maltitude;
	private String mtime;
	private String mVMG;
	private String maltitudeFeet;
	
	public GpsData(Location location) {

		// calculate metrics from Location object

		//velocity made good
		mVMG = String.valueOf(String.format("%.2f",Math.cos(Double.parseDouble(Float.toString(location.getSpeed())))));
		
		//latitude and longitude in decimal degrees
		mlatDecimal = String.valueOf(String.format("%.5f",location.getLatitude()));
    	mlongDecimal = String.valueOf(String.format("%.5f",location.getLongitude())); 
    	
		//latitude and longitude in degrees minutes seconds
    	mlatDMS = String.valueOf(getDMS(location.getLatitude()));
    	mlongDMS = String.valueOf(getDMS(location.getLongitude()));
    	
		//Latitude and longitude in degrees and decimal minutes
    	mlatDDMM = String.valueOf(getDDMM(location.getLatitude()));
    	mlongDDMM = String.valueOf(getDDMM(location.getLongitude()));
    	
		//speed, knots, KMH, MPH, meters per second
    	mspeedKnots = String.valueOf(String.format("%.2f", location.getSpeed()*1.94384));
    	mspeedKMH = String.valueOf(String.format("%.0f", location.getSpeed()*3.6));
    	mspeedMPH = String.valueOf(String.format("%.0f", location.getSpeed()*2.23694));
    	mspeedMPS = String.valueOf(String.format("%.2f", location.getSpeed()));

		//
    	maccuracy = String.valueOf(location.getAccuracy());

		//bearing or heading
    	mbearing = String.valueOf(String.format("%.0f",location.getBearing()));
    	
		//altitude in meters and feet
    	maltitude = String.valueOf(String.format("%.0f",location.getAltitude()));
    	maltitudeFeet = String.valueOf(String.format("%.0f",location.getAltitude()*3.2808399));
    	
		//current time
    	mtime = String.valueOf(location.getTime());
		
	}
	
	//format double to degrees minutes and seconds format
	public String getDMS(Double decimalCoordinate){
		
		Boolean applyNegativeCorrection = false;
		
		//some coordiates can have negative numbers. The latitude is preceded by a minus sign ( – ) 
		//if it is south of the equator (a positive number implies north), and the longitude is preceded 
		//by a minus sign if it is west of the prime meridian (a positive number implies east);
		//make the number negative if necessary
		if (decimalCoordinate < 0)
		{
			applyNegativeCorrection = true;
		}
		
		//calculate each, degrees, minutes and seconds separately
		decimalCoordinate = Math.abs(decimalCoordinate);
		double dd = Math.floor(decimalCoordinate);
    	double mm = Math.floor((decimalCoordinate-dd)*60);
    	double ss = (((decimalCoordinate-dd)*60) - mm)*60;

		//
    	String mms = String.format("%.0f",mm).length()==1? "0" + String.format("%.0f",mm) : String.format("%.0f",mm);
    	String sss = String.format("%.0f",ss).length()==1? "0" + String.format("%.5f",ss) : String.format("%.5f",ss);
    	
		//check if number should be negative
    	if (applyNegativeCorrection)
    	{
    		dd = dd *-1;
    	}

		return String.format("%.0f",dd) + "°" +  mms + "‘" + sss + "\"";
	}
	

	//format double to degrees minutes format
	public String getDDMM(Double decimalCoordinate){
		
		Boolean applyNegativeCorrection = false;
		
		//some coordiates can have negative numbers. The latitude is preceded by a minus sign ( – ) 
		//if it is south of the equator (a positive number implies north), and the longitude is preceded 
		//by a minus sign if it is west of the prime meridian (a positive number implies east);
		//make the number negative if necessary
		if (decimalCoordinate < 0)
		{
			applyNegativeCorrection = true;
		}

		//calculate each, degrees, minutes
		decimalCoordinate = Math.abs(decimalCoordinate);
		Double dd = Math.floor(decimalCoordinate);
    	Double mm = Math.floor((decimalCoordinate-dd)*60);
    	Double mmmm = (((decimalCoordinate-dd)*60) - mm);
    	String mms = String.format("%.0f",mm).length()==1? "0" + String.format("%.3f",mm+mmmm) : String.format("%.3f",mm+mmmm);
    	
		//check if number should be negative
    	if (applyNegativeCorrection)
    	{
    		dd = dd *-1;
    	}

		return String.format("%.0f",dd) + "°" +  mms + "‘";
	}
	
	//velocity made good or speed
	public String getVMG()
	{
		return mVMG;
	}
	
	//latitude
	public String getLatitudeDecimal()
	{
		return mlatDecimal;
	}
	
	//longitude
	public String getLongitudeDecimal()
	{
		return mlongDecimal;
	}
	
	//latitude degrees minutes and seconds
	public String getLatitudeDMS()
	{
		return mlatDMS;
	}
	
	//longitude degrees minutes and seconds
	public String getLongitudeDMS()
	{
		return mlongDMS;
	}
	
	//latitude degrees minutes 
	public String getLatitudeDDMM()
	{
		return mlatDDMM;
	}
	
	//longitude degrees minutes
	public String getLongitudeDDMM()
	{
		return mlongDDMM;
	}
	
	//returns string of a decimal number
	public String Decimal()
	{
		return mlongDDMM;
	}
	
	//knots
	public String getSpeedKnots()
	{
		return mspeedKnots;
	}
	
	//KM/H
	public String getspeedKMH()
	{
		return mspeedKMH;
	}
	
	//MPH
	public String getSpeedMPH()
	{
		return mspeedMPH;
	}
	
	//meters per second
	public String getSpeedMPS()
	{
		return mspeedMPS;
	}
	
	//gps accuracy
	public String getAccuracy()
	{
		return maccuracy;
	}
	
	public String getBearing()
	{
		return mbearing;
	}
	
	//gps bearing
	public String getAltitude()
	{
		return maltitude;
	}
	
	//gps altitude
	public String getAltitudeFeet()
	{
		return maltitudeFeet;
	}
	
	//gps time
	public String getTime()
	{
		return mtime;
	}
	
	

}
