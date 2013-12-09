package com.lacreatelit.android.photogallery.broadcastreceivers;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lacreatelit.android.photogallery.services.SearchPollService;

public class NewPhotoNotificationReceiver extends BroadcastReceiver {

	private static final String TAG = "NewPhotoNotificationReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
	
		Log.i(TAG, "Received result - " + getResultCode());
		
		// Some foreground activity cancelled the broadcast
		if(getResultCode() != Activity.RESULT_OK) {
			return;
		}
		
		int requestCode = intent.getIntExtra(
					SearchPollService.INTENT_EXTRA_KEY_NEW_PHOTO_REQUEST, 0);
		
		Notification notification = intent.getParcelableExtra(
				SearchPollService.INTENT_EXTRA_KEY_PHOTO_NOTIFICATION);
		
		NotificationManager notificationManager = (NotificationManager)context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		
		notificationManager.notify(requestCode, notification);
		
		

	}

}
