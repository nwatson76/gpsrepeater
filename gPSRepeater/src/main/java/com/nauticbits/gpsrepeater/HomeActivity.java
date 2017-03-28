package com.nauticbits.gpsrepeater;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.GestureDetector.OnGestureListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;


//Home activity class, the main activity of the app
public class HomeActivity extends FragmentActivity implements OnGestureListener, SensorEventListener {
	
	//instantiate objects used by the app
	ViewFlipper vf;
	boolean disableInteraction = false;
	GestureDetector gd;
	int activeFlip = 0;

	private RotationData rotationData;
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private Sensor orientSensor;
    private Sensor tempSensor;
    private Sensor lightSensor;

    private boolean autoDayNightAdjustment;

	//gps variables
	private boolean mUseFine = true;
    private static final String KEY_FINE = "use_fine";
    private static final int ONE_SECONDS = 2000;
    private static final int TEN_METERS = 2;

    private LightData lightData;
    private float totalLumensOverDuration = 0;
    private float lumenCount = 0;

	 private static final String TAG = "GPSRepeater";
	private static final boolean D = false;

	// Message types sent from the Handler
	public static final int MESSAGE_TOAST = 5;
	private static final int UPDATE_LATLNG = 6;

	//static variables
	private static final String NIGHT = "NIGHT";
	private static final String DAY = "DAY";
	private String currentDisplay = DAY;
	public static final String TOAST = "toast";

	//Handler used to update the UI
	private Handler mHandler;

	//
	private List<TableLayout> tableLayoutList;
	private List<TableRow> tableRowList;

	//custom object to hold all GPSData for this app
	private GpsData gpsData = null;
	
	//Toast notification
	private Toast mToast;

	//debugging variables
	private java.util.Date lastDayNightDisplay = new java.util.Date();
	private java.util.Date lastHeelDisplay = new java.util.Date();
	private String whatHappened;

	//android location manager
	private LocationManager mLocationManager2;

	//used for swipe and tap interaction of view flipper
	Animation slideLeftIn, slideLeftOut, slideRightIn, slideRightOut;

	//imageview for Day/Night and Lock and unlock icons
	private ImageView imageViewDay;
	private ImageView imageViewNight;
	private ImageView imageViewLock;
	private ImageView imageViewUnlock;
	private ImageView imageViewNightLock;
	private ImageView imageViewNightUnlock;

	//imageview for Setting icons
	private ImageView imageViewSettings;
	private ImageView imageViewNightSettings;

	private void updateUILocation(Location location) {
        try{
        	// We're sending the update to a handler which then updates the UI with the new location.
            Message.obtain(mHandler,
                    UPDATE_LATLNG,
                    location).sendToTarget();
        }catch(Exception ex){
			 Toast.makeText(getBaseContext(), ex.getMessage() + " updateUILocation", Toast.LENGTH_SHORT).show();
		}
    }

	private final LocationListener listener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            // A new location update is received.  Do something useful with it.  Update the UI with
            // the location update.
            Location gpsLocation = null;

            gpsLocation = requestUpdatesFromProvider(LocationManager.GPS_PROVIDER, R.string.not_support_gps);
            // Update the UI immediately if a location is obtained.
            if (gpsLocation != null) updateUILocation(gpsLocation);
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

	private void setup() {
		try
		{
			Location gpsLocation = null;
			mLocationManager2.removeUpdates(listener);
			// Get fine location updates only.
			if (mUseFine) {
				// Request updates from just the fine (gps) provider.
				gpsLocation = requestUpdatesFromProvider(LocationManager.GPS_PROVIDER, R.string.not_support_gps);
				// Update the UI immediately if a location is obtained.
				if (gpsLocation != null) updateUILocation(gpsLocation); else Toast.makeText(getBaseContext(), " Waiting for gps signal...", Toast.LENGTH_SHORT).show();
			}
		}catch(Exception ex){
			 Toast.makeText(getBaseContext(), ex.getMessage() + " onStart", Toast.LENGTH_SHORT).show();
		}

	}

	private void initializeSettings(){
		//Gets a SharedPreferences instance that points to the default file that is used by the preference framework in the given context 
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = settings.edit();

		//store settings for this app
		//set default coordinate setting
		if(!settings.contains("itemCoordinateSetting"))
			editor.putString("itemCoordinateSetting", "DDMM");

		//set default speed setting
		if(!settings.contains("itemSpeedSetting"))
			editor.putString("itemSpeedSetting", "N");

		//set default day night setting
		if(!settings.contains("itemAutoDayNightSetting"))
			editor.putBoolean("itemAutoDayNightSetting", false);

		//set default gps log setting
		if(!settings.contains("itemLogSetting"))
			editor.putBoolean("itemLogSetting", false);

		editor.commit();
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//debugging variables
		whatHappened = whatHappened + "onCreate fired,";
        if(D) Log.i(TAG, "What Happened: " + whatHappened);

		//remove the top title bar from the app
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		//set the view
		setContentView(R.layout.activity_home);

		//initialize default settings
		initializeSettings();

		//get references to system sensors
		mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		orientSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

		lightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

		//start listening to sensors
		mSensorManager.registerListener(this, orientSensor, SensorManager.SENSOR_DELAY_UI);
		mSensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_UI);

		//
		Context mContext = this.getApplication();
		
		//toast the UI
		mToast = Toast.makeText(getApplicationContext(), "" , Toast.LENGTH_LONG);

		//set the view flipper
		vf = (ViewFlipper)this.findViewById(R.id.flipper);

		//set animiations
		slideLeftIn = AnimationUtils.loadAnimation(this, R.anim.view_transition_in_left);
	    slideRightOut = AnimationUtils.loadAnimation(this, R.anim.view_transition_out_right);
	    slideLeftOut = AnimationUtils.loadAnimation(this, R.anim.view_transition_out_left);
	    slideRightIn = AnimationUtils.loadAnimation(this, R.anim.view_transition_in_right);
		
		//set image views
	    imageViewDay = (ImageView) findViewById(R.id.day);
		imageViewNight = (ImageView) findViewById(R.id.night);
		imageViewLock = (ImageView) findViewById(R.id.lock);
		imageViewUnlock = (ImageView) findViewById(R.id.unlock);
		imageViewNightLock = (ImageView) findViewById(R.id.nightlock);
		imageViewNightUnlock = (ImageView) findViewById(R.id.nightunlock);
		imageViewSettings = (ImageView) findViewById(R.id.settings);
		imageViewNightSettings = (ImageView) findViewById(R.id.nightsettings);

		//make everything invisible at startup
		imageViewLock.setVisibility(ImageView.GONE);
    	imageViewUnlock.setVisibility(ImageView.GONE);
    	imageViewNightLock.setVisibility(ImageView.GONE);
    	imageViewNightUnlock.setVisibility(ImageView.GONE);
    	imageViewNightSettings.setVisibility(ImageView.GONE);
    	imageViewSettings.setVisibility(ImageView.GONE);

		//set default day night setting
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		boolean found = false;
		for(Map.Entry<String,?> entry : settings.getAll().entrySet()){
        	if(entry.getKey().toString().equals("DayNightDisplay")){
        		updateDisplayNightDay(entry.getValue().toString());
        		currentDisplay = entry.getValue().toString();
        		found = true;
        		break;
        	}
		}
		if(!found){
			updateDisplayNightDay(DAY);
		}

		//update the UI with the current saved lock setting
		changeDisplayLockUnlock();

		//initialize gesture detector for swipe and tap
		gd = new GestureDetector(this, this);

     
		//handler updates the UI
        mHandler = new Handler() {

			public void handleMessage(Message msg) {
		        switch (msg.what) {
		        case MESSAGE_TOAST:
		        //send a toast message to device	
				mToast.setText(msg.getData().getString(TOAST));
					mToast.show();
		            break;
		        case UPDATE_LATLNG:

					//update the UI with GPS information
	            	Location location = (Location) msg.obj;
	            	if(location != null)
	            	{
	            		gpsData = new GpsData(location);
	            		setGPS(gpsData);
	            	}
	                break;
		        }
		    }

		};


     // Get a reference to the LocationManager object.
        mLocationManager2 = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		
		//alway use fine GPS locations
        useFineProvider();

	}


	//update the UI with GPS information
	public void setGPS(GpsData gpsData){
		
		//get the current settings
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

		//call UIUpdate class that asynchronously updates the UI
		UIUpdate up = new UIUpdate(this, gpsData, settings);
		up.execute();
		up.onPostExecute(null);
	}
	
	//loop over the entire table layout and update all row, textview and text colors to the currently selected day night display colors
	public void iterateTableLayoutAndUpdateColor(List<TableLayout> tableLayoutList, int color){
		if(tableLayoutList.size()>0){
			for(int i=0;i<=tableLayoutList.size()-1;i++){
				TableLayout tl = (TableLayout)tableLayoutList.get(i);
				//Log.i(TAG, "Reading table - Children=" + String.valueOf(tl.getChildCount()));
				for (int x=0; x<=tl.getChildCount()-1 ;x++ ){

					if(tl.getChildAt(x).getClass().getName().contains("TableRow")){
						tableRowList.add((TableRow)tl.getChildAt(x));
						//Log.i(TAG, "Adding Row");
					}
					if(tl.getChildAt(x).getClass().getName().contains("TextView")){
						TextView tv = (TextView)tl.getChildAt(x);
						if(tv.getTag() != null){
							if(!tv.getTag().equals("STATIC")) tv.setTextColor(color);
						}else{
							tv.setTextColor(color);
						}
					}
					if(tl.getChildAt(x).getClass().getName().contains("FontFitTextView")){
						FontFitTextView tv = (FontFitTextView)tl.getChildAt(x);
						if(tv.getTag() != null){
							if(!tv.getTag().equals("STATIC")) tv.setTextColor(color);
						}else{
							tv.setTextColor(color);
						}
					}
				}
			}
			tableLayoutList.clear();
			//Log.i(TAG, "Clearing table list");
			iterateTableRowAndUpdateColor(tableRowList, color);
		}
	}

	//light sensor change events
	public void onLightChanged(SensorEvent event) {

		lightData = new LightData(event.values[0]);
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		if(D) Log.i(TAG, "itemAutoDayNightSetting: " + String.valueOf(settings.getBoolean("itemAutoDayNightSetting", false)));
 	   	//if auto day night color update is enabled
		if(settings.getBoolean("itemAutoDayNightSetting", false)){
 		  //upate the diplay
		  autoUpdateDisplay();
   		}
   }

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

	// if auto update display is enabled, the app will check to see if its dark outside and automatically switch to 
	//night colors without have to change them through the day night setting
	private void autoUpdateDisplay() {
		if(lightData != null){

			java.util.Date currentDate = new java.util.Date();
	    	int secondsBetweenLastUpdate = (int)(currentDate.getTime() - lastDayNightDisplay.getTime())/1000;

	    	float currentLumens = Float.parseFloat(lightData.getLight());

	    	totalLumensOverDuration = totalLumensOverDuration + currentLumens;
	    	lumenCount = lumenCount + 1;

	    	if(D) Log.i(TAG, "Current lumenValue = " + String.valueOf(totalLumensOverDuration/lumenCount) + " secondsBetweenLastUpdate= " + String.valueOf(secondsBetweenLastUpdate));

	    	if(secondsBetweenLastUpdate > 10){
	    		lastDayNightDisplay = currentDate;

	    		if(totalLumensOverDuration/lumenCount > 50){
	    			if(!currentDisplay.equalsIgnoreCase(DAY)){
	    				currentDisplay = DAY;
		            	updateDisplayNightDay(DAY);
	    			}

				}else{
					if(!currentDisplay.equalsIgnoreCase(NIGHT)){
						currentDisplay = NIGHT;
						updateDisplayNightDay(NIGHT);
	    			}


				}

	    		lumenCount = 1;
	    		totalLumensOverDuration = currentLumens;
	    		if(D) Log.i(TAG, "Resetting lumens to = " + String.valueOf(totalLumensOverDuration/lumenCount));
	    	}
		}

	}

	//loop over the entire table Row and update all layout, textview and text colors to the currently selected day night display colors
	public void iterateTableRowAndUpdateColor(List<TableRow> tableRowList,int color){
		if(tableRowList.size()>0){
			for(int i=0;i<=tableRowList.size()-1;i++){
				TableRow tr = (TableRow)tableRowList.get(i);
				//Log.i(TAG, "Reading row - Children=" + String.valueOf(tr.getChildCount()));
				for (int x=0; x<=tr.getChildCount()-1 ;x++ ){

					if(tr.getChildAt(x).getClass().getName().contains("TableLayout")){
						tableLayoutList.add((TableLayout)tr.getChildAt(x));
						//Log.i(TAG, "Adding New Table");
					}
					if(tr.getChildAt(x).getClass().getName().contains("TextView")){
						TextView tv = (TextView)tr.getChildAt(x);
						if(tv.getTag() != null){
							if(!tv.getTag().equals("STATIC")) tv.setTextColor(color);
						}else{
							tv.setTextColor(color);
						}
					}
					if(tr.getChildAt(x).getClass().getName().contains("FontFitTextView")){
						FontFitTextView tv = (FontFitTextView)tr.getChildAt(x);
						if(tv.getTag() != null){
							if(!tv.getTag().equals("STATIC")) tv.setTextColor(color);
						}else{
							tv.setTextColor(color);
						}
					}
				}
			}
			tableRowList.clear();
			//Log.i(TAG, "Clearing row list");
			this.iterateTableLayoutAndUpdateColor(tableLayoutList, color);
		}

	}


	//change whether the display colors are day or night
	public void changeDisplayDayNight(){

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		if(settings.getString("DayNightDisplay", "DAY").equals(DAY)){
			vf.setBackgroundColor((int)settings.getInt("dayColorBackgroundSetting", Color.WHITE));
			imageViewDay.setVisibility(ImageView.VISIBLE);
			imageViewNight.setVisibility(ImageView.GONE);

			tableLayoutList = new ArrayList<TableLayout>();
			tableRowList = new ArrayList<TableRow>();

			for(int w = 0;w<=vf.getChildCount()-1;w++){
				if(vf.getChildAt(w) instanceof TableLayout){
					tableLayoutList.add((TableLayout)vf.getChildAt(w));
					//Log.i(TAG, "Adding Table " + String.valueOf(tableLayoutList.size() + DAY));
				}
			}

			this.iterateTableLayoutAndUpdateColor(tableLayoutList, (int)settings.getInt("dayColorTextSetting", Color.BLACK));

		}else{

			imageViewDay.setVisibility(ImageView.GONE);
			imageViewNight.setVisibility(ImageView.VISIBLE);

			vf.setBackgroundColor((int)settings.getInt("nightColorBackgroundSetting", Color.BLACK));
			tableLayoutList = new ArrayList<TableLayout>();
			tableRowList = new ArrayList<TableRow>();

			for(int w = 0;w<=vf.getChildCount()-1;w++){
				if(vf.getChildAt(w) instanceof TableLayout){
					tableLayoutList.add((TableLayout)vf.getChildAt(w));
					//Log.i(TAG, "Adding Table " + String.valueOf(tableLayoutList.size() + NIGHT));
				}
			}

			this.iterateTableLayoutAndUpdateColor(tableLayoutList, (int)settings.getInt("nightColorTextSetting", Color.RED));
		}

		changeDisplayLockUnlock();

		mToast.setText("Loading " + settings.getString("DayNightDisplay", "DAY") + "...");
		mToast.show();

	}


	//toggle between locked and unlocked states of the app
	public void changeDisplayLockUnlock(){

		//get share settings
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		//if locked
		if(disableInteraction){

			//if DAY display
			if(settings.getString("DayNightDisplay", "DAY").equals(DAY)){
				imageViewLock.setVisibility(ImageView.VISIBLE);
				imageViewUnlock.setVisibility(ImageView.GONE);
				imageViewNightLock.setVisibility(ImageView.GONE);
				imageViewNightUnlock.setVisibility(ImageView.GONE);
				imageViewSettings.setVisibility(ImageView.VISIBLE);
				imageViewNightSettings.setVisibility(ImageView.GONE);
				mToast.setText("locking day Display...");
			}else{
				imageViewLock.setVisibility(ImageView.GONE);
				imageViewUnlock.setVisibility(ImageView.GONE);
				imageViewNightLock.setVisibility(ImageView.VISIBLE);
				imageViewNightUnlock.setVisibility(ImageView.GONE);
				imageViewSettings.setVisibility(ImageView.GONE);
				imageViewNightSettings.setVisibility(ImageView.VISIBLE);
				mToast.setText("locking night Display...");
			}

		
		}else{
			//unlocked
			if(settings.getString("DayNightDisplay", "DAY").equals(DAY)){
				imageViewLock.setVisibility(ImageView.GONE);
				imageViewUnlock.setVisibility(ImageView.VISIBLE);
				imageViewNightLock.setVisibility(ImageView.GONE);
				imageViewNightUnlock.setVisibility(ImageView.GONE);
				imageViewSettings.setVisibility(ImageView.VISIBLE);
				imageViewNightSettings.setVisibility(ImageView.GONE);
				mToast.setText("unlocking day Display...");
			}else{
				imageViewLock.setVisibility(ImageView.GONE);
				imageViewUnlock.setVisibility(ImageView.GONE);
				imageViewNightLock.setVisibility(ImageView.GONE);
				imageViewNightUnlock.setVisibility(ImageView.VISIBLE);
				imageViewSettings.setVisibility(ImageView.GONE);
				imageViewNightSettings.setVisibility(ImageView.VISIBLE);
				mToast.setText("unlocking night Display...");
			}





		}

		mToast.show();

	}
	
	//when the day night icon is clicked, toggle between the two settings
	public void updateDisplayNightDay(String currentSetting)
	{

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		Editor editor = settings.edit();
		editor.putString("DayNightDisplay", currentSetting);
		editor.commit();

		changeDisplayDayNight();
	}

	/** Called when the user clicks the lock button */
    public void lockClick(View view) {
    	disableInteraction = false;
    	changeDisplayLockUnlock();
    }

    /** Called when the user clicks the unlock button */
    public void unlockClick(View view) {
    	disableInteraction = true;
    	changeDisplayLockUnlock();
    }

    /** Called when the user clicks the day button */
    public void dayClick(View view) {
    	updateDisplayNightDay(DAY);
    	lastDayNightDisplay = new java.util.Date();
    	currentDisplay = DAY;
    }

	//called when the user click the app settings button
    public void settingsClick(View view) {
    	Intent intent = new Intent();
        intent.setClass(HomeActivity.this, HomePreferencesActivity.class);
        startActivityForResult(intent, 0);
    }


    /** Called when the user clicks the night button */
    public void nightClick(View view) {
    	updateDisplayNightDay(NIGHT);
    	lastDayNightDisplay = new java.util.Date();
    	currentDisplay = NIGHT;
    }


	@Override
    protected void onPause() {
        // Ideally an app should implement onResume() and onPause()
        // to take appropriate action when the activity looses focus
        super.onPause();
        mSensorManager.unregisterListener(this, lightSensor);
        mSensorManager.unregisterListener(this, orientSensor);

        //if(btModel != null) btModel.stop();
        //whatHappened = whatHappened + "onPause fired,";
        if(D) Log.i(TAG, "What Happened: " + whatHappened);
//        myLabel.setText(whatHappened);
        mToast.cancel();
	}

	protected void onResume(Bundle savedInstanceState) {
		super.onResume();
		//when the app resumes from a pause
		mSensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(this, orientSensor, SensorManager.SENSOR_DELAY_NORMAL);
		whatHappened = whatHappened + "onResume fired,";
        if(D) Log.i(TAG, "What Happened: " + whatHappened);

		mToast.cancel();
	}

	@Override
    protected void onStart() {
		//when the app is started
		try{
			super.onStart();
			// Check if the GPS setting is currently enabled on the device.
	        // This verification should be done during onStart() because the system calls this method
	        // when the user returns to the activity, which ensures the desired location provider is
	        // enabled each time the activity resumes from the stopped state.
	        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

	        if (!gpsEnabled) {
	            // Build an alert dialog here that requests that the user enable
	            // the location services, then when the user clicks the "OK" button,
	            // call enableLocationSettings()
	            new EnableGpsDialogFragment().show(getSupportFragmentManager(), "enableGpsDialog");
	        }
		 }catch(Exception ex){

		 }
		//debuggin info
		whatHappened = whatHappened + "onStart fired,";
		if(D) Log.i(TAG, "What Happened: " + whatHappened);

	}

	//if GPS is not enabled on the device, load a dialog to enable it
	public class EnableGpsDialogFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.enable_gps)
                    .setMessage(R.string.enable_gps_dialog)
                    .setPositiveButton(R.string.enable_gps, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            enableLocationSettings();
                        }
                    })
                    .create();
        }
    }

	// Method to launch Settings
    private void enableLocationSettings() {
        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(settingsIntent);
    }
	
	//when the application is closed
	@Override
    protected void onDestroy() {
		super.onDestroy();
		//if(btModel != null) btModel.stop();
		whatHappened = whatHappened + "onDestroy fired,";
		if(D) Log.i(TAG, "What Happened: " + whatHappened);
//		myLabel.setText(whatHappened);
		mToast.cancel();
	}

	 // Stop receiving location updates whenever the Activity becomes invisible.
    @Override
    protected void onStop() {
        super.onStop();
        mLocationManager2.removeUpdates(listener);
        //debug info
        whatHappened = whatHappened + "onStop fired,";
        if(D) Log.i(TAG, "What Happened: " + whatHappened);
        mToast.cancel();
    }

	//restart happens when another activity comes in front of this activity, and the activity comes back to the foreground
    @Override
    protected void onRestart() {
    	super.onRestart();
    	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		for(Map.Entry<String,?> entry : settings.getAll().entrySet()){
        	if(entry.getKey().toString().equals("DayNightDisplay")){
        		updateDisplayNightDay(entry.getValue().toString());
        		currentDisplay = entry.getValue().toString();
        		break;
        	}
		}

    	mSensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    	setup();

    	whatHappened = whatHappened + "onRestart fired,";
    	if(D) Log.i(TAG, "What Happened: " + whatHappened);

    }

	//get Location object from location manager
    private Location requestUpdatesFromProvider(final String provider, final int errorResId) {

    	try{
    		Location location = null;
            if (mLocationManager2.isProviderEnabled(provider)) {
                mLocationManager2.requestLocationUpdates(provider, ONE_SECONDS, TEN_METERS, listener);
                location = mLocationManager2.getLastKnownLocation(provider);
            } else {
                Toast.makeText(this, errorResId, Toast.LENGTH_LONG).show();
            }
            return location;
    	}catch(Exception ex){
			 Toast.makeText(getBaseContext(), ex.getMessage() + " requestUpdatesFromProvider", Toast.LENGTH_SHORT).show();
			 return null;
		}
    }

    // Callback method for the "fine provider"
    public void useFineProvider() {
        try{
        	mUseFine = true;
            setup();
        }catch(Exception ex){
			 Toast.makeText(getBaseContext(), ex.getMessage() + " useFineProvider", Toast.LENGTH_SHORT).show();
		}

    }

	//set the active layout of the view flipper to the active View
    public void setActiveLayout(){
		vf.setDisplayedChild(activeFlip);
	}

    @Override
	 public boolean onOptionsItemSelected(MenuItem item) {

	  /*
	   * Because it's only ONE option in the menu.
	   * In order to make it simple, We always start SetPreferenceActivity
	   * without checking.
	   */

	   Intent intent = new Intent();
      intent.setClass(HomeActivity.this, HomePreferencesActivity.class);
      startActivityForResult(intent, 0);

      return true;
	 }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_home, menu);
		return true;
	}
	
	//When the view flipper is tapped or touched
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (gd.onTouchEvent(event)){
        	return true;
		}
		else
			return false;
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		return false;
	}
	
	//When the view flipper is flung left or right
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

		if(!disableInteraction){
			final float SWIPE_MAX_OFF_PATH = 200;
		    final float MIN_DISTANCE_SWIPE = 10;
		    final float MIN_VELOCITY = 10;

			int child = vf.getDisplayedChild();
			if (child+1 <= vf.getChildCount()-1) {
				activeFlip = child+1;
			}else{
				activeFlip = 0;
			}

			if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
	            return false;
	        // top to down swipe
	        if(e1.getX() - e2.getX() > MIN_DISTANCE_SWIPE && Math.abs(velocityX) > MIN_VELOCITY) {
	        	//swiping right to left
	        	vf.setInAnimation(slideRightIn);
	            vf.setOutAnimation(slideLeftOut);
	            vf.showPrevious();
	            vf.setInAnimation(null);
	        	vf.setOutAnimation(null);
	        }  else if (e2.getX() - e1.getX() > MIN_DISTANCE_SWIPE && Math.abs(velocityX) > MIN_VELOCITY) {
	        	//swiping left to right
	        	vf.setInAnimation(slideLeftIn);
	        	vf.setOutAnimation(slideRightOut);
	        	vf.showNext();
	        	vf.setInAnimation(null);
	        	vf.setOutAnimation(null);
	        }

			//Toast.makeText(getApplicationContext(), String.valueOf(child) + " " + String.valueOf(vf.getChildCount()) , Toast.LENGTH_LONG).show();
			//vf.setDisplayedChild(activeFlip);
		}


		return true;
	}
	
	//do nothing
	@Override
	public void onLongPress(MotionEvent arg0) {

	}
	
	//do nothing
	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		return false;
	}
	
	//do nothing
	@Override
	public void onShowPress(MotionEvent arg0) {

	}
	
	//when the view flipper is tapped
	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		//if unlocked
		if(!disableInteraction){
			//as long as the view flipper isn't flipping
			if(!vf.isFlipping()){
				//perform a left in and right out animation
				vf.setInAnimation(slideLeftIn);
	        	vf.setOutAnimation(slideRightOut);
	        	vf.showNext();
	        	vf.setInAnimation(null);
	        	vf.setOutAnimation(null);
			}
		}

		return false;
	}

   public void onSensorChanged(SensorEvent event) {
       // we received a sensor event. it is a good practice to check
       // that we received the proper event
   	switch(event.sensor.getType()){
       case Sensor.TYPE_ORIENTATION:
           //onOrientChanged(event);
           break;
       case Sensor.TYPE_LIGHT:

	   		onLightChanged(event);

	   		break;
   	}

   }

}





