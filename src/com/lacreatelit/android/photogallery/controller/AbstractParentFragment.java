package com.lacreatelit.android.photogallery.controller;

import com.lacreatelit.android.photogallery.services.SearchPollService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.widget.Toast;

// The main intent of this class is to provide the infrastructure
// for receiving the broadcasts and hiding foreground notifications
public class AbstractParentFragment extends Fragment {
	
	public static final String TAG = "AbstractParentFragment";
	
	private BroadcastReceiver mOnShowNotificationReceiver = 
			new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			
			Toast.makeText(getActivity(), 
					"Got a broadcast: " + intent.getAction(), 
					Toast.LENGTH_LONG)
					.show();
		}
	};

	@Override
	public void onResume() {
		super.onPause();
		
		IntentFilter intentFilter = 
				new IntentFilter(SearchPollService.ACTION_SHOW_NOTIFICATION );
		
		getActivity().registerReceiver(
					mOnShowNotificationReceiver, 
					intentFilter, 
					SearchPollService.PERMISSION_PRIVATE_SHOW_NOTIFICATION, 
					null);
	}

	@Override
	public void onPause() {
		
		super.onResume();
		getActivity().unregisterReceiver(mOnShowNotificationReceiver);
	}
	
	

}
