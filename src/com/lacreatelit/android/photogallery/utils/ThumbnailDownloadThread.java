package com.lacreatelit.android.photogallery.utils;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.lacreatelit.android.photogallery.controller.PhotoFetcher;

public class ThumbnailDownloadThread<Token> extends HandlerThread {

	private static final String TAG = "ThumbnailDownloadThread";
	private static final int MESSAGE_DOWNLOAD = 0;
	
	Handler mHandler;
	Map<Token, String> mUrlToTokenMap = 
			Collections.synchronizedMap(new HashMap<Token, String>());
	
	public ThumbnailDownloadThread() {
		
		super(TAG);
		
	}
	
	// Code to write to the message queue
	// So the strategy is to write the token into the message queue and populate
	// a local map with the url to token mapping. The Looper will then use the 
	// token to call a locally defined function with the token. The locally 
	// defined function will use the token to look up the required data (the url
	// in this case) and do the processing on the data
	// 
	public void addToThumbnailRequestQueue(Token token, String url) {
		Log.i(TAG, "Got an URL: " + url);
		mUrlToTokenMap.put(token, url);
		
		mHandler.obtainMessage(MESSAGE_DOWNLOAD, token)
			.sendToTarget();
	}

	
	// Code to read from the message queue
	@SuppressLint("HandlerLeak")
	@Override
	protected void onLooperPrepared() {

		mHandler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				
				if(msg.what == MESSAGE_DOWNLOAD) {
					@SuppressWarnings("unchecked")
					Token token = (Token)msg.obj;
					Log.i(TAG, "Got request for Url: " + 
									mUrlToTokenMap.get(token));
					handleRequest(token);
					
				}
			}
		};

	}
	
	
	private void handleRequest(final Token token) {
		try{
			final String url = mUrlToTokenMap.get(token);
			
			if(url == null) {
				Log.i(TAG, "Got null url");
				return;
			}
			
			byte[] bitmapBytes = new PhotoFetcher().getURLBytes(url);
			final Bitmap bitmap = BitmapFactory
					.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
			Log.i(TAG, "Bitmap created");
			
		} catch (IOException e) {
			Log.e(TAG, "Error creating image", e);
		}
	}
	
}
