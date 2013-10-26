package com.lacreatelit.android.photogallery.controller;

import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.Menu;

public class PhotoGalleryActivity extends SingleFragmentActivity {


	@Override
	protected Fragment createFragment() {
		
		return new PhotoGalleryFragment();
	}
}
