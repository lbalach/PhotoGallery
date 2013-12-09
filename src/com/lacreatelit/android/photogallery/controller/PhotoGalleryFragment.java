package com.lacreatelit.android.photogallery.controller;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SearchView;

import com.lacreatelit.android.photogallery.R;
import com.lacreatelit.android.photogallery.model.GalleryItem;
import com.lacreatelit.android.photogallery.services.SearchPollService;
import com.lacreatelit.android.photogallery.utils.FlickrUtils;
import com.lacreatelit.android.photogallery.utils.ThumbnailDownloadThread;

public class PhotoGalleryFragment extends AbstractParentFragment {
	
	private static final String TAG = "PhotoGalleryFragment";
	
	GridView mGridView;
	ArrayList<GalleryItem> mItems;
	ThumbnailDownloadThread<ImageView>  mThumbnailThread;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Retain the Fragment even when the configuration changes. So even when
		// the underlying Activity is destroyed, the Fragment is retained
		setRetainInstance(true);
		setHasOptionsMenu(true);
		
		// Get the list of photos to be displayed
		updatePhotoList();
		
		// Set up the Search polling service
		//SearchPollService.setServiceAlarm(getActivity(), true);
		
		//Setup the background thread to download the thumbnails
		mThumbnailThread = new ThumbnailDownloadThread<ImageView>(new Handler());
		
		mThumbnailThread.setThumbnailDownloadCompleteListener(
				new ThumbnailDownloadThread.Listener<ImageView>() {
			public void onThumbnailDownloaded(ImageView imageView, 
					Bitmap thumbnail) {
				if(isVisible()) {
					imageView.setImageBitmap(thumbnail);
				}
			}
					
		});
		
		mThumbnailThread.start();
		mThumbnailThread.getLooper();
		Log.i(TAG, "Background thread started...");
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_photo_gallery, 
				container, 
				false);
		
		mGridView = (GridView)view.findViewById(R.id.gridView);
		setupAdapter();
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> gridView, View view, int pos,
					long id) {
				
				GalleryItem item = mItems.get(pos);
				Uri photoPageUri = Uri.parse(item.getPhotoPageUrl());
				Intent intentViewPhotoPage 
					= new Intent(Intent.ACTION_VIEW, photoPageUri);
				
				startActivity(intentViewPhotoPage);
			}
			
		});
		
		return view;
		
		}
	
	// This method is called by onCreateView() and onPostExecute(). For the 
	// latter call we need to check if the Fragment is still attached to the 
	// Activity
	private void setupAdapter() {
		
		if(getActivity() == null || mGridView == null)
			return;
		
		if(mItems != null) {
			
//			mGridView.setAdapter(new ArrayAdapter<GalleryItem>(getActivity(),
//					android.R.layout.simple_gallery_item, mItems));
			mGridView.setAdapter(new GalleryItemAdapter(mItems));
			
		} else {
			
			mGridView.setAdapter(null);
		}
 		
	}
	
	
	public void updatePhotoList() {
		
		new FetchRemoteDataTask().execute();
		
	}
	
	private class FetchRemoteDataTask extends AsyncTask<Void, Void, 
				ArrayList<GalleryItem>> {

		@Override
		protected ArrayList<GalleryItem> doInBackground(Void... params) {
			
//			try{
//				String result = new PhotoFetcher()
//					.getUrlData("http://www.google.com");
//				Log.i(TAG, "Fetched data: " + result);
//			} catch(IOException e) {
//				
//				Log.e(TAG, "Failed to fetch URL", e);
//			}
			Activity activity = getActivity();
			if(activity == null)
				return new ArrayList<GalleryItem>();
			
			return new PhotoFetcher().getPhotoList(activity);
			
		} // End of function definition

		@Override
		protected void onPostExecute(ArrayList<GalleryItem> result) {
			
			mItems = result;
			setupAdapter();
			
		}
		
	} // End of the private class definition
	
	private class GalleryItemAdapter extends ArrayAdapter<GalleryItem> {
		
		public GalleryItemAdapter(ArrayList<GalleryItem> galleryItems) {
			
			super(getActivity(), 0, galleryItems);
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			
			if(view == null) {
				view = getActivity().getLayoutInflater()
						.inflate(R.layout.view_gallery_item, parent, false);
			}
			
			ImageView imageView = (ImageView)view
					.findViewById(R.id.image_view_gallery_item);
			imageView.setImageResource(R.drawable.ic_launcher);
			
			GalleryItem galleryItem = getItem(position);
			mThumbnailThread.addToThumbnailRequestQueue(imageView, 
					galleryItem.getUrl());
			
			return view;
		}
					
	}

	@Override
	public void onDestroy() {
		
		super.onDestroy();
		mThumbnailThread.quit();
		Log.i(TAG, "Background thread destroyed");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mThumbnailThread.clearQueueData();
	}

	
	//==================Creating the options menu===============================
	
	// Specifies which menu layout to inflate
	@Override
	@TargetApi(11)
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_photo_gallery, menu);
		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			
			Log.i(TAG, "Using the post HoneyComb SearchView mechanism");
			// Get data about the search configuration (Searchable.xml) as 
			// SearchableInfo
			SearchManager searchManager = (SearchManager)getActivity()
					.getSystemService(Context.SEARCH_SERVICE);
			ComponentName componentName = getActivity().getComponentName();
			SearchableInfo searchInfo = searchManager
					.getSearchableInfo(componentName);
			
			// Get the SearchView
			MenuItem menuSearchItem = menu.findItem(R.id.menu_item_search);
			SearchView searchView = (SearchView)menuSearchItem.getActionView();
			
			// Set the search configuration for the SearchView
			searchView.setSearchableInfo(searchInfo);
			
		}
		
	}

	// Specify what actions need to be taken once the item has been selected
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()) {
		
		case R.id.menu_item_search:
			getActivity().onSearchRequested();
			return true;
			
		case R.id.menu_item_clear:
			PreferenceManager.getDefaultSharedPreferences(getActivity())
				.edit()
				.putString(FlickrUtils.PREF_KEY_SEARCH_QUERY, null)
				.commit();
			updatePhotoList();
			return true;
			
		case R.id.menu_item_new_photo_polling:
			boolean isStartAlarm = !SearchPollService
				.isServiceAlarmOn(getActivity());
			SearchPollService.setServiceAlarm(getActivity(), isStartAlarm);
			
			if(!isStartAlarm) 
				updatePhotoList();
			
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				getActivity().invalidateOptionsMenu();
			}
			
		default:
			return super.onOptionsItemSelected(item);
			
		}
		
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		
		super.onPrepareOptionsMenu(menu);
		MenuItem photoPollToggleMenu = menu
				.findItem(R.id.menu_item_new_photo_polling);
		if(SearchPollService.isServiceAlarmOn(getActivity())) {
			photoPollToggleMenu.setTitle(R.string.stop_polling);
		} else {
			photoPollToggleMenu.setTitle(R.string.start_polling);
		}
		
	}
	
	

}
