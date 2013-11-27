package com.lacreatelit.android.photogallery.services;

import java.util.ArrayList;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import com.lacreatelit.android.photogallery.controller.PhotoFetcher;
import com.lacreatelit.android.photogallery.model.GalleryItem;

public class SearchPollService extends IntentService {
	
	private static final String TAG = "SearchPollService";
	private static final int ALARM_REPEAT_INTERVAL = 1000 * 15; // 15 seconds
	
	public SearchPollService(){
		
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		if(!isBackgroundNetworkAvailable()) {
			Log.e(TAG, "Background network not available");
			return;
		}
		
		PhotoFetcher photoFetcher = new PhotoFetcher();
		ArrayList<GalleryItem> photoList = photoFetcher.getPhotoList(this);
		
		if(photoList.size() == 0) {
			Log.i(TAG, "No photos retrieved");
			return;
		}
		
		String lastResultId = photoFetcher.retrieveLastResultId(this);
		String latestResultId = photoList.get(0).getId();
		
		if(!latestResultId.equals(lastResultId)){
			Log.i(TAG, "Got a new result set");
		} else {
			Log.i(TAG, "Got old result set");
		}
		
		photoFetcher.saveResultId(this, latestResultId);

	}
	
	
	private boolean isBackgroundNetworkAvailable() {
		
		ConnectivityManager cm = (ConnectivityManager)getSystemService(
				CONNECTIVITY_SERVICE);
		
		@SuppressWarnings("deprecation")
		boolean isNetworkAvailable = (cm.getBackgroundDataSetting() &&
				cm.getActiveNetworkInfo() != null);
		
		return isNetworkAvailable;
		
		
	}
	
	public static void setServiceAlarm(
			Context context, boolean isOn){
		
		Intent intent = new Intent(context, SearchPollService.class);
		PendingIntent pendingIntent = PendingIntent
				.getService(context, 0, intent, 0);
		
		AlarmManager alarmManager = (AlarmManager)context
				.getSystemService(ALARM_SERVICE);
		
		
		if(isOn) {
			alarmManager.setRepeating(AlarmManager.RTC, 
					System.currentTimeMillis(),
					ALARM_REPEAT_INTERVAL, 
					pendingIntent);
		} else {
			
			alarmManager.cancel(pendingIntent);
			pendingIntent.cancel();
		}
		
	}
	
	// The PendingIntent is a proxy / marker for the Alarm. So if the Alarm is 
	// is not active, then the corresponding PendingIntent will be null. Hence the
	// FLAG_NO_CREATE should be used, so that the PendingIntent is not created
	// if it does not exist. Else the getService() call creates the PendingIntent
	// if it does not exist
	public static boolean isServiceAlarmOn(Context context) {
		
		Intent intent = new Intent(context, SearchPollService.class);
		PendingIntent pendingIntent = PendingIntent
				.getService(context, 0, intent, PendingIntent.FLAG_NO_CREATE);
		
		return (pendingIntent != null);
	}

}
