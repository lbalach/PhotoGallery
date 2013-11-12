package com.lacreatelit.android.photogallery.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class SearchPollService extends IntentService {
	
	private static final String TAG = "SearchPollService";
	
	public SearchPollService(){
		
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		Log.i(TAG, "Recieved intent: " + intent);

	}

}
