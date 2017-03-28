package com.nauticbits.gpsrepeater;

import android.app.Activity;
import android.os.Bundle;

//activity for the app settings menu
public class HomePreferencesActivity extends Activity {

	@Override
	 protected void onCreate(Bundle savedInstanceState) {
	  
	  super.onCreate(savedInstanceState);
	  //load the setting menu
	  getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();
	 }
}
