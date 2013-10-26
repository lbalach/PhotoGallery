package com.lacreatelit.android.photogallery.controller;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.lacreatelit.android.photogallery.R;
import com.lacreatelit.android.photogallery.model.GalleryItem;
import com.lacreatelit.android.photogallery.utils.ThumbnailDownloadThread;

public class PhotoGalleryFragment extends Fragment {
	
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
		
		// Execute the AsyncTask
		new FetchRemoteDataTask().execute();
		
		//Setup the background thread to download the thumbnails
		mThumbnailThread = new ThumbnailDownloadThread<ImageView>();
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
			
			return new PhotoFetcher().getPhotoList();
			
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
	
	

}