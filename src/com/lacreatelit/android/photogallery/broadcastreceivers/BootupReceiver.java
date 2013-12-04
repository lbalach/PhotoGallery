package com.lacreatelit.android.photogallery.broadcastreceivers;

import com.lacreatelit.android.photogallery.services.SearchPollService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class BootupReceiver extends BroadcastReceiver {

	private static final String TAG = "BootupReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		Log.i(TAG, "Received Broadcast intent - " + intent.getAction());
		
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		boolean isOn = preferences
				.getBoolean(SearchPollService.PREF_IS_ALARM_ON, false);
		SearchPollService.setServiceAlarm(context, isOn);

	}

}
