package com.ritesh.instagram;

import android.app.Application;

import com.ritesh.instagram.utils.AppUtils;
import com.ritesh.instagram.utils.ImageDownloader;

public class IgApplication extends Application {
	
    public static String PACKAGE_NAME;
    
    @Override
    public void onCreate() {    	
    	super.onCreate();
    	PACKAGE_NAME = getPackageName();
    	ImageDownloader.initAquery();
    }
    
    @Override
	public void onLowMemory() {
		AppUtils.Log(" LOW MEMORY");
		// clear all memory cached images when system is in low memory
		// note that you can configure the max image cache count, see
		// CONFIGURATION
		ImageDownloader.clearAllImages();
	}
}
