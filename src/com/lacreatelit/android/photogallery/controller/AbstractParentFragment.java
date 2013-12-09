package com.lacreatelit.android.photogallery.controller;

import com.lacreatelit.android.photogallery.services.SearchPollService;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

// The main intent of this class is to provide the infrastructure
// for receiving the broadcasts and hiding foreground notifications
public class AbstractParentFragment extends Fragment {
	
	public static final String TAG = "AbstractParentFragment";
	
	private BroadcastReceiver mOnShowNotificationReceiver = 
			new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			
			Log.i(TAG, "Cancelling notification");
			
			// The result code of this broadcast receiver that is seen by the
			// next broadcast receiver. The source needs to send out a
			// sendOrderedBroadcast(...) call
			setResultCode(Activity.RESULT_CANCELED);
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
