package com.nauticbits.gpsrepeater;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.widget.TextView;


//asynchronously update the user interface so that app is smooth and not choppy during UI data refresh
public class UIUpdate extends AsyncTask<Void, Void, Void> {
	
	private Activity act;
	private SharedPreferences settingsT;
	private GpsData gpsData;
	

	//instantiate and inject Activity, GpsData and SharePreferences
	public UIUpdate(Activity actt, GpsData gData, SharedPreferences settings) {
		this.act = actt;
		this.settingsT = settings;
		this.gpsData = gData;
	}

	//do nothing in the background
	protected Void doInBackground(Void... args) {
		return null;
	}
	
	//
	public void onPostExecute(Void... args) {
		
		FontFitTextView longitude;
		FontFitTextView longitude2;
		FontFitTextView latitude;
		FontFitTextView latitude2;
		FontFitTextView bearing;
		FontFitTextView bearing2;
		FontFitTextView bearing3;
		FontFitTextView altitude;
		FontFitTextView altitude2;
		
		FontFitTextView sog;
		FontFitTextView sog2;
		FontFitTextView sog3;
		
		TextView sogDesc;
		TextView sog2Desc;
		TextView sog3Desc;
		
		TextView latDesc;
		TextView latDesc2;
		TextView longDesc;
		TextView longDesc2;
		TextView altitudeDesc;
		TextView altitudeDesc2;
		
		sog = (FontFitTextView) act.findViewById(R.id.SOG);
		sog2 = (FontFitTextView) act.findViewById(R.id.SOG2);
		sog3 = (FontFitTextView) act.findViewById(R.id.SOG3);
		
		sogDesc = (TextView) act.findViewById(R.id.SOGDesc);
		sog2Desc = (TextView) act.findViewById(R.id.SOG2Desc);
		sog3Desc = (TextView) act.findViewById(R.id.SOG3Desc);
		
		latDesc = (TextView) act.findViewById(R.id.LatitudeDesc);
		latDesc2 = (TextView) act.findViewById(R.id.Latitude2Desc);
		longDesc = (TextView) act.findViewById(R.id.LongitudeDesc);
		longDesc2 = (TextView) act.findViewById(R.id.Longitude2Desc);
		
		altitudeDesc = (TextView) act.findViewById(R.id.altitudeDesc);
		altitudeDesc2 = (TextView) act.findViewById(R.id.altitude2Desc);
		
		altitude = (FontFitTextView) act.findViewById(R.id.altitude);
		altitude2 = (FontFitTextView) act.findViewById(R.id.altitude2);
		longitude = (FontFitTextView) act.findViewById(R.id.Longitude);
		latitude = (FontFitTextView) act.findViewById(R.id.Latitude); 
		longitude2 = (FontFitTextView) act.findViewById(R.id.Longitude2);
		latitude2 = (FontFitTextView) act.findViewById(R.id.Latitude2); 
		bearing = (FontFitTextView) act.findViewById(R.id.Bearing); 
		bearing2 = (FontFitTextView) act.findViewById(R.id.Bearing2);
		bearing3 = (FontFitTextView) act.findViewById(R.id.Bearing3);
		
		if(settingsT.getString("itemSpeedSetting", "N").equals("N")){
			sog.setText(gpsData.getSpeedKnots());
			sog2.setText(gpsData.getSpeedKnots());
			sog3.setText(gpsData.getSpeedKnots());
			altitude.setText(gpsData.getAltitude());
			altitude2.setText(gpsData.getAltitude());
			altitudeDesc.setText(R.string.altitude_n);
			altitudeDesc2.setText(R.string.altitude_n);
			sogDesc.setText(R.string.sog_n);
			sog2Desc.setText(R.string.sog_n);
			sog3Desc.setText(R.string.sog_n);
		}else if(settingsT.getString("itemSpeedSetting", "N").equals("M")){
			sog.setText(gpsData.getspeedKMH());
			sog2.setText(gpsData.getspeedKMH());
			sog3.setText(gpsData.getspeedKMH());
			altitude.setText(gpsData.getAltitude());
			altitude2.setText(gpsData.getAltitude());
			altitudeDesc.setText(R.string.altitude_m);
			altitudeDesc2.setText(R.string.altitude_m);
			sogDesc.setText(R.string.sog_m);
			sog2Desc.setText(R.string.sog_m);
			sog3Desc.setText(R.string.sog_m);
		}else{
			sog.setText(gpsData.getSpeedMPH());
			sog2.setText(gpsData.getSpeedMPH());
			sog3.setText(gpsData.getSpeedMPH());
			altitude.setText(gpsData.getAltitudeFeet());
			altitude2.setText(gpsData.getAltitudeFeet());
			altitudeDesc.setText(R.string.altitude_i);
			altitudeDesc2.setText(R.string.altitude_i);
			sogDesc.setText(R.string.sog_i);
			sog2Desc.setText(R.string.sog_i);
			sog3Desc.setText(R.string.sog_i);
		}

		bearing.setText(gpsData.getBearing()+ "°");
		bearing2.setText(gpsData.getBearing()+ "°");
		bearing3.setText(gpsData.getBearing()+ "°");
		
		if(settingsT.getString("itemCoordinateSetting", "DDMM").equals("DDMM")){
			latitude.setText(gpsData.getLatitudeDDMM());
			longitude.setText(gpsData.getLongitudeDDMM());
			latDesc.setText(R.string.latitude_ddmm);
			longDesc.setText(R.string.longitude_ddmm);
			latitude2.setText(gpsData.getLatitudeDDMM());
			longitude2.setText(gpsData.getLongitudeDDMM());
			latDesc2.setText(R.string.latitude_ddmm);
			longDesc2.setText(R.string.longitude_ddmm);
		}else if(settingsT.getString("itemCoordinateSetting", "DDMM").equals("DD")){
			latitude.setText(gpsData.getLatitudeDecimal());
			longitude.setText(gpsData.getLongitudeDecimal());
			latDesc.setText(R.string.latitude_dd);
			longDesc.setText(R.string.longitude_dd);
			latitude2.setText(gpsData.getLatitudeDecimal());
			longitude2.setText(gpsData.getLongitudeDecimal());
			latDesc2.setText(R.string.latitude_dd);
			longDesc2.setText(R.string.longitude_dd);
		}else{
			latitude.setText(gpsData.getLatitudeDMS());
			longitude.setText(gpsData.getLongitudeDMS());
			latDesc.setText(R.string.latitude_dms);
			longDesc.setText(R.string.longitude_dms);
			latitude2.setText(gpsData.getLatitudeDMS());
			longitude2.setText(gpsData.getLongitudeDMS());
			latDesc2.setText(R.string.latitude_dms);
			longDesc2.setText(R.string.longitude_dms);
		}
		
		if(settingsT.getBoolean("itemLogSetting", false)){
			java.util.Date currentDate = new java.util.Date();
			appendLog(currentDate.toString() + "," + sog.getText() + "," + gpsData.getBearing() + "," + String.valueOf(gpsData.getLatitudeDecimal()) + "," + String.valueOf(gpsData.getLongitudeDecimal()) + "," + altitude.getText());
   		}
		
	}
	
	//append the gps information to the log file
	public void appendLog(String text)
	{       
		File direct = new File(Environment.getExternalStorageDirectory() + "/gpsrepeater");
		
		//determine if the log directory is there
		if(!direct.exists())
	    {
	        
			//create the directory
			if(direct.mkdir()) 
			  {
			   //directory is created;
			  }
	
	    }

		
		File logFile = new File(Environment.getExternalStorageDirectory() + "/gpsrepeater/gps.log");

		//if the file doesn't exist
		if (!logFile.exists())
		{
	      try
	      {
	         //create new log file
			 logFile.createNewFile();
	         
			 //use BufferedWriter for performance, true to set append to file flag
	         BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));

			 //add header fields
	         buf.append("DATE,SOG,COG,LAT,LONG,ALT");
			 //add a new line and close it
	         buf.newLine();
	         buf.flush();
	         buf.close();
	      } 
	      catch (IOException e)
	      {
	    	  // TODO Auto-generated catch block
	         e.printStackTrace();
	      }
	   }
	   try
	   {
	      //use BufferedWriter for performance, true to set append to file flag
	      BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true)); 
	      //append comma separated GPS values and close file
		  buf.append(text);
	      buf.newLine();
	      buf.flush();
	      buf.close();
	   }
	   catch (IOException e)
	   {
	      //eat the bug and continue
	      e.printStackTrace();
	   }
	}
}





