package com.lacreatelit.android.photogallery.controller;

import com.lacreatelit.android.photogallery.R;
import com.lacreatelit.android.photogallery.utils.FlickrUtils;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Menu;

public class PhotoGalleryActivity extends SingleFragmentActivity {

	private static final String TAG = "PhotoGalleryActivity";

	@Override
	protected Fragment createFragment() {
		
		return new PhotoGalleryFragment();
	}

	@Override
	protected void onNewIntent(Intent intent) {

		if(Intent.ACTION_SEARCH.equals(intent.getAction())) {

			String searchQuery = intent.getStringExtra(SearchManager.QUERY);
			Log.i(TAG, "Search Query: " + searchQuery);
			
			// Persist the search query so that it can be picked up by the 
			// fragment
			persistSearchQuery(searchQuery);
			
			updateFragment();
		}
		

	}
	
	private void updateFragment() {
		
		PhotoGalleryFragment fragment = 
				(PhotoGalleryFragment)getSupportFragmentManager()
				.findFragmentById(R.id.fragmentContainer);
		
		fragment.updatePhotoList();
		
	}
	
	private void persistSearchQuery(String searchQuery) {
		
		PreferenceManager.getDefaultSharedPreferences(this)
			.edit()
			.putString(FlickrUtils.PREF_KEY_SEARCH_QUERY, searchQuery)
			.commit();
	}
	
	
}
