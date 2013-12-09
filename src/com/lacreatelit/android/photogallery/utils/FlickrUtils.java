package com.lacreatelit.android.photogallery.utils;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.R.xml;
import android.net.Uri;
import android.util.Log;

import com.lacreatelit.android.photogallery.model.GalleryItem;

public class FlickrUtils {
	
	public static final String PREF_KEY_SEARCH_QUERY = "searchQuery";
	public static final String PREF_KEY_LAST_RESULT_ID = "lastResultId";
	
	private static final String TAG = "Utils";
	
	private static final String URL_ENDPOINT 
						= "http://api.flickr.com/services/rest/";
	
	private static final String URL_PARAMS_API_KEY = "api_key"; 
	private static final String URL_API_KEY 
						= "ca54b7c5de953a302537548721ea9d3b";
	
	// Methods called on the Flickr API's
	private static final String URL_PARAMS_METHOD = "method";
	private static final String URL_METHOD_GET_RECENT_PHOTOS 
						= "flickr.photos.getRecent";
	private static final String URL_METHOD_SEARCH_PHOTOS
						= "flickr.photos.search";
	
	// Parameters to the methods called on the Flickr API's
	private static final String URL_PARAM_EXTRAS = "extras";
	private static final String URL_EXTRA_SMALL_URL = "url_s";
	private static final String URL_PARAM_TEXT = "text";
	
	// XML tags in the returned result photo xml's from Flickr
	private static final String XML_PHOTO_TAG = "photo";
	private static final String XML_ATTR_ID = "id";
	private static final String XML_ATTR_TITLE = "title";
	private static final String XML_ATTR_OWNER = "owner";
	
	public static String createRecentPhotosURL() {
		
		String url = Uri.parse(URL_ENDPOINT).buildUpon()
				.appendQueryParameter(URL_PARAMS_METHOD, 
						URL_METHOD_GET_RECENT_PHOTOS)
				.appendQueryParameter(URL_PARAMS_API_KEY, URL_API_KEY)
				.appendQueryParameter(URL_PARAM_EXTRAS, URL_EXTRA_SMALL_URL)
				.build().toString();
		
		return url;
		
	}
	
	public static String createSearchPhotosURL(String query) {
		
		String url = Uri.parse(URL_ENDPOINT)
				.buildUpon()
				.appendQueryParameter(URL_PARAMS_METHOD, 
						URL_METHOD_SEARCH_PHOTOS)
				.appendQueryParameter(URL_PARAMS_API_KEY, URL_API_KEY)
				.appendQueryParameter(URL_PARAM_EXTRAS, URL_EXTRA_SMALL_URL)
				.appendQueryParameter(URL_PARAM_TEXT, query)
				.build().toString();
		
		return url;
	}
	
	public static void createPhotoList(ArrayList<GalleryItem> photoList
			, String xmlString)  {
		
		try{
			
		XmlPullParserFactory xppFactory = XmlPullParserFactory.newInstance();
		XmlPullParser xmlPullParser = xppFactory.newPullParser();
		xmlPullParser.setInput(new StringReader(xmlString));
		
		populateModel(photoList, xmlPullParser);
		} catch (IOException ioe) {
			Log.e(TAG, "Failed to create photo list", ioe);
		} catch(XmlPullParserException xppe) {
			Log.e(TAG, "Failed to create photo list", xppe);
		}
		
	}
	
	private static void populateModel(ArrayList<GalleryItem> photoList,
			XmlPullParser xmlPullParser) throws XmlPullParserException, 
			IOException {
		
		int eventType = xmlPullParser.next();
		Log.d(TAG, "Populating the model data from XML");
		while(eventType != XmlPullParser.END_DOCUMENT) {
			
			if(eventType != XmlPullParser.START_TAG && 
					XML_PHOTO_TAG.equals(xmlPullParser.getName())) {
				
				String id = xmlPullParser.getAttributeValue(null, XML_ATTR_ID);
				String caption = xmlPullParser.getAttributeValue(null
						,XML_ATTR_TITLE);
				String smallUrl = xmlPullParser.getAttributeValue(null
						, URL_EXTRA_SMALL_URL);
				String owner = xmlPullParser.getAttributeValue(null,
						XML_ATTR_OWNER);
				
				GalleryItem galleryItem = new GalleryItem();
				galleryItem.setId(id);
				galleryItem.setCaption(caption);
				galleryItem.setUrl(smallUrl);
				galleryItem.setOwner(owner);
				
				photoList.add(galleryItem);
				
			}
			
			eventType = xmlPullParser.next();
			
		} // End of while loop
		
	}	

}
