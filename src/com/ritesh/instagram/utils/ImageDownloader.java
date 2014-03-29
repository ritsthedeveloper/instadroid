package com.ritesh.instagram.utils;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.util.AQUtility;
import com.ritesh.instagram.IgApplication;

public class ImageDownloader {
	
	public static final int THREADCOUNT = 8;
    public static final String IMG_PATH = "Android/data/"
	    + IgApplication.PACKAGE_NAME + "/images";
    public static final String IMG_LARGE = "/large";
    public static final String IMG_SMALL = "/small";

	public static void initAquery() {

		// set the max number of concurrent network connections, default is 4
		AjaxCallback.setNetworkLimit(8);

		// set the max number of icons (image width <= 50) to be cached in
		// memory, default is 20
		BitmapAjaxCallback.setIconCacheLimit(20);

		// set the max number of images (image width > 50) to be cached in
		// memory, default is 20
		BitmapAjaxCallback.setCacheLimit(10);

		// set the max size of the memory cache, default is 1M pixels (4MB)
		BitmapAjaxCallback.setMaxPixelLimit(2000000);

		// Default path
		// File ext = Environment.getExternalStorageDirectory();
		// File cacheDir = new File(ext, "Instagram");
		// AQUtility.setCacheDir(cacheDir);
	}
	
	public static void clearAllImages() {

		// starts cleaning when cache size is larger than 3M
		long triggerSize = 3000000; 
		// remove the least recently used files until
	    // cache size is less than 2M
		long targetSize = 2000000; 

		// remove the least recently used files until
		ImgClearTask imgTask = new ImgClearTask(IMG_PATH, triggerSize,
				targetSize); 
		imgTask.execute();
	}
	
	public static void clearImages(boolean large) {

		// starts cleaning when cache size is larger than 1M
		long triggerSize = 2000000;
		// remove the least recently used files until
		// cache size is less than 2M
		long targetSize = 1000000;

		String path = null;
		
		if (large)
			path = IMG_PATH + IMG_LARGE;
		else
			path = IMG_PATH + IMG_SMALL;

		ImgClearTask imgTask = new ImgClearTask(path, triggerSize, targetSize);
		imgTask.execute();
	}
	
	public static class ImgClearTask extends AsyncTask<Void, Void, Void> {

		String path;
		long mTrig;
		long mTarget;

		/**
		 * 
		 */
		public ImgClearTask(String imgPath, long trig, long target) {
			path = imgPath;
			mTrig = trig;
			mTarget = target;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Void doInBackground(Void... params) {
			File ext = Environment.getExternalStorageDirectory();
			File cacheDir = new File(ext, IMG_PATH + IMG_LARGE);
			AQUtility.cleanCache(cacheDir, mTrig, mTarget);
			return null;
		}
	}
	
	/**
     * 
     * 
     * @param view
     * @param url
     */
	public static void downloadImage(ImageView view, String url) {

		File ext = Environment.getExternalStorageDirectory();
		File cacheDir = new File(ext, IMG_PATH + IMG_LARGE);
		AQUtility.setCacheDir(cacheDir);

		url = getDomailWithUrl(url);
		AQuery aq = new AQuery(view);

		aq.image(url, false, true, 0, 0, new BitmapAjaxCallback() {
			@SuppressLint("NewApi")
			@Override
			public void callback(String url, ImageView iv, Bitmap bm,
					AjaxStatus status) {

				iv.setImageBitmap(bm);
			}
		});
	}
	
    /**
     * For Image Views Only
     * 
     * @param view
     * @param url
     */
	private static void downloadSquareImage(View view, String url,
			final int defimg, boolean isLarge, final boolean isfitImage) {

		String imgPath;

		if (isLarge)
			imgPath = IMG_PATH + IMG_LARGE;
		else
			imgPath = IMG_PATH + IMG_SMALL;

		File ext = Environment.getExternalStorageDirectory();
		File cacheDir = new File(ext, imgPath);
		AQUtility.setCacheDir(cacheDir);

		url = getDomailWithUrl(url);
		final AQuery aq = new AQuery(view);
		aq.image(url, false, true, 0, 0, new BitmapAjaxCallback() {
			@SuppressLint("NewApi")
			@Override
			public void callback(String url, final ImageView iv,
					final Bitmap bm, AjaxStatus status) {

				if (bm == null) {
					iv.setBackgroundResource(defimg);
					return;
				}

				if (isfitImage) {
					Matrix matrix = new Matrix();
					float scaleVal = (float) iv.getWidth()
							/ (float) bm.getWidth();

					AppUtils.Log("bm: " + bm.getWidth() + " vw: "
							+ iv.getWidth() + " sc: " + scaleVal);
					
					matrix.setScale(scaleVal, scaleVal);
					if (bm.getHeight() * scaleVal < iv.getHeight())
						matrix.postTranslate(0,
								(iv.getHeight() / 2 - (bm.getHeight()
										* scaleVal / 2)));
					else
						matrix.postTranslate(0, 0);
					iv.setImageMatrix(matrix);
					iv.setScaleType(ScaleType.MATRIX);
				}

				iv.setImageBitmap(bm);

			}
		});
	}
	
	private static String getDomailWithUrl(String url) {
		if (url == null)
			return "";

		return url;
	}
	
	public static void setSquareImg(ImageView imgvw, String url, int defimg,
			boolean isLarge, boolean bFitImage) {
		// check for empty url
		if (TextUtils.isEmpty(url)) {
			return;
		}
		// download circular image from server
		downloadSquareImage(imgvw, url, defimg, isLarge, bFitImage);
	}
	
	public static void setSquareImg(Context con, ImageView imgvw, String url,
			int defimg, boolean isLarge, boolean bFitImage) {

		url = getDomailWithUrl(url);

		imgvw.setBackgroundResource(defimg);

		// check for empty url
		if (TextUtils.isEmpty(url)) {
			return;
		}
		// download image from server
		downloadSquareImage(imgvw, url, defimg, isLarge, bFitImage);
	}
}
