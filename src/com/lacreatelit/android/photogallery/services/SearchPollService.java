package com.lacreatelit.android.photogallery.services;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.lacreatelit.android.photogallery.R;
import com.lacreatelit.android.photogallery.controller.PhotoFetcher;
import com.lacreatelit.android.photogallery.controller.PhotoGalleryActivity;
import com.lacreatelit.android.photogallery.model.GalleryItem;

public class SearchPollService extends IntentService {
	
	private static final String TAG = "SearchPollService";
	//private static final int ALARM_REPEAT_INTERVAL = 1000 * 60 * 5; // 5 minutes
	private static final int ALARM_REPEAT_INTERVAL = 1000 * 5; // 5 seconds
	public static final String PREF_IS_ALARM_ON = "isAlarmOn";
	
	// Actions to be broadcast
	public static final String ACTION_SHOW_NOTIFICATION = 
			"com.lacreatelit.android.photogallery.SHOW_NOTIFICATION";
	
	// Permission used by the Service
	public static final String PERMISSION_PRIVATE_SHOW_NOTIFICATION = 
			"com.lacreatelit.android.photogallery.PRIVATE_SHOW_NOTIFICATION";
	
	// Intent extra keys
	public static final String INTENT_EXTRA_KEY_NEW_PHOTO_REQUEST = 
			"newPhotoRequest";
	public static final String INTENT_EXTRA_KEY_PHOTO_NOTIFICATION = 
			"photoNotification";
	
	// Intent extra values
	public static final int INTENT_EXTRA_VALUE_NEW_PHOTO_REQUEST = 0;
	
	
	
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
			
			showBackgroundNotification();
			
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
		
		// Save the current alarm state
		PreferenceManager.getDefaultSharedPreferences(context)
			.edit()
			.putBoolean(PREF_IS_ALARM_ON, isOn)
			.commit();
		
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
	
	@SuppressWarnings("unused")
	private void publishNewResultNotification() {
		
		Notification notification = createNewPhotosNotification();
		
		NotificationManager notificationManager = (NotificationManager)
				getSystemService(NOTIFICATION_SERVICE);
		
		notificationManager.notify(0, notification);
		
	}
	
	private Notification createNewPhotosNotification() {

		Resources resources = getResources();
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, 
				new Intent(this, PhotoGalleryActivity.class), 0);
		
		Notification notification = new NotificationCompat.Builder(this)
			.setTicker(resources.getString(R.string.new_pictures_title))
			.setSmallIcon(android.R.drawable.ic_menu_report_image)
			.setContentTitle(resources.getString(R.string.new_pictures_title))
			.setContentText(resources.getString(R.string.new_pictures_text))
			.setContentIntent(pendingIntent)
			.setAutoCancel(true)
			.build();
		
		return notification;
		
	}
	
	private void showBackgroundNotification() {
		
		Intent intent = new Intent(ACTION_SHOW_NOTIFICATION);
		intent.putExtra(INTENT_EXTRA_KEY_NEW_PHOTO_REQUEST, 
				INTENT_EXTRA_VALUE_NEW_PHOTO_REQUEST);
		
		Notification notification = createNewPhotosNotification();
		intent.putExtra(INTENT_EXTRA_KEY_PHOTO_NOTIFICATION, notification);
		
		sendOrderedBroadcast(intent, 
				PERMISSION_PRIVATE_SHOW_NOTIFICATION,
				null, null, Activity.RESULT_OK, null, null);
		
	}
	
	

}
