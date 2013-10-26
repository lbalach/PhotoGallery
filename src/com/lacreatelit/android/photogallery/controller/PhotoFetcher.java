package com.lacreatelit.android.photogallery.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.lacreatelit.android.photogallery.model.GalleryItem;
import com.lacreatelit.android.photogallery.utils.Utils;

import android.util.Log;

public class PhotoFetcher {
	
	private static final String TAG = "PhotoFetcher";
	
	public byte[] getURLBytes(String urlSpec) throws IOException {
		
		URL url = new URL(urlSpec);
		
		HttpURLConnection httpConnection = (HttpURLConnection)url
				.openConnection();
		try {
			
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			InputStream inputStream = httpConnection.getInputStream();
			
			if(httpConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				Log.d(TAG, "Unable to establish http connection");
				return null;
			}
			
			int bytesRead = 0;
			byte[] buffer = new byte[1024];
			
			while((bytesRead = inputStream.read(buffer)) > 0) {
				outputStream.write(buffer, 0, bytesRead);
			}
			outputStream.close();
			return outputStream.toByteArray();
			
		} finally {
			httpConnection.disconnect();
		}
		
	}
	
	public String getUrlData(String urlSpec) throws IOException {
		
		return new String(getURLBytes(urlSpec));
		
	}
	
	public ArrayList<GalleryItem> getPhotoList() {
		
		ArrayList<GalleryItem> photoList = null;
		try {
			
			String photoUrl = Utils.createURL();
			String xmlPhotoData = getUrlData(photoUrl);
			Log.d(TAG, "Recieved xml: " + xmlPhotoData);
			
			photoList = new ArrayList<GalleryItem>();
			Utils.createtPhotoList(photoList, xmlPhotoData);
			
		} catch (IOException e) {
			Log.e(TAG, "Failed to get photo list", e);
			photoList = null;
		}
		
		return photoList;
	}

}
