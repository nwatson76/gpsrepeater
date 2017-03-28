package com.nauticbits.gpsrepeater;

import android.os.Bundle;
import android.preference.PreferenceFragment;

//the menu used for this app
public class PrefsFragment extends PreferenceFragment {

 @Override
 public void onCreate(Bundle savedInstanceState) {
  
  super.onCreate(savedInstanceState);
  
  // Load the preferences from the settings XML resource
        addPreferencesFromResource(R.xml.homemenu);
 }

}
