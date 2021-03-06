package com.lacreatelit.android.photogallery.controller;

import com.lacreatelit.android.photogallery.R;
import com.lacreatelit.android.photogallery.R.id;
import com.lacreatelit.android.photogallery.R.layout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public abstract class SingleFragmentActivity extends FragmentActivity {
	
	protected abstract Fragment createFragment();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = 
        		fragmentManager.findFragmentById(R.id.fragmentContainer);
        
        if(fragment == null) {
        	
        	fragment = createFragment();
        	fragmentManager.beginTransaction()
        		.add(R.id.fragmentContainer, fragment)
        		.commit();
        	
        }
    }

}
