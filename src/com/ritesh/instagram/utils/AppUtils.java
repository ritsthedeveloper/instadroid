package com.ritesh.instagram.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * 
 * @author ritesh
 *
 */
public class AppUtils {

	public static final String TAG = "JOKER";
	
	/**
	 * Toggle this boolean constant's value to turn on/off logging
	 * within the class. 
	 */
	public static final boolean LOG_VERBOSE = true;
	public static final boolean LOG_DEBUG = true;
	public static final boolean LOG_WARN = true;
	
	/**
     * prints the Log based on LOG_DEBUG
     * 
     * @param msg
     */
	public static void Log(String msg) {
		if (LOG_DEBUG) {
			Log.d(TAG, msg);
		}
	}

	public static void Log(String tag, String msg) {
		if (LOG_DEBUG) {
			Log.d(tag, msg);
		}
	}
    
	/**
     * To check whether particular application is installed or not.
     * 
     * @param context
     * @param uri     - Package name of the application.
     * 
     * @return
     */
	public static boolean appInstalledOrNot(Context context, String uri) {
		
		PackageManager pm = context.getPackageManager();
		boolean app_installed = false;
		try {
			pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
			app_installed = true;
			Log("App installed.");
		} catch (PackageManager.NameNotFoundException e) {
			app_installed = false;
		}
		return app_installed;
	}

    /**
     *  To identify whether device is tablet or phone.
     *  
     * @param context
     * @return
     */
	public static boolean isTablet(Context context) {
		boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
		boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
		return (xlarge || large);
	}
    
    /**  
     *  To get the formatted number with suffix like K, M.
     */
	public static String getNumberWithSuffix(long count) {
		if (count < 1000)
			return "" + count;
		int exp = (int) (Math.log(count) / Math.log(1000));
		// kMGTPE
		return String.format("%.1f %c", count / Math.pow(1000, exp),
				"kM".charAt(exp - 1));
	}
	
	/**
	 * Get the time in hh:mm:ss format
	 * 
	 * @param milliseconds
	 * @return
	 */
	public static String getHMSFormat(int milliseconds) {

		int seconds = (int) (milliseconds / 1000) % 60;
		int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
		int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);

		return String.format("%02d:%02d:%02d", hours, minutes, seconds);

	}
	
	/**
     * return Md5 of a String
     * 
     * @param in
     * @return
     */
	public static String getMd5(String in) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
			digest.reset();
			digest.update(in.getBytes());
			byte[] a = digest.digest();
			int len = a.length;
			StringBuilder sb = new StringBuilder(len << 1);
			for (int i = 0; i < len; i++) {
				sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
				sb.append(Character.forDigit(a[i] & 0x0f, 16));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
     * checks if internet is On
     * 
     * @param context
     * @return boolean
     */
	public static boolean isInternetOn(Context context) {
		ConnectivityManager conn = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = conn.getActiveNetworkInfo();
		if (networkInfo != null) {
			return networkInfo.isConnectedOrConnecting();
		}
		return false;
	}
	
	/**
     * checks whether the url is image url or not.
     * 
     * @param url
     * @return boolean
     */
	public static boolean isUrlImage(String url) {
		if (url.contains("png") || url.contains("jpeg") || url.contains("jpg"))
			return true;
		return false;
	}
	
	/**
     * verifies the email is valid
     * 
     * @param email
     * @return
     */

	public static boolean isEmailValid(String email) {
		return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}
	
	/**
     * Returns the count in the format ##,##,###
     */
	public static String getStringFormattedNumber(int count) {
		NumberFormat formatter = null;
		if (count > 999999) {
			count /= 1000000;
			formatter = new DecimalFormat(",##,###M");
		} else if (count > 999) {
			count /= 1000;
			formatter = new DecimalFormat(",##,###K");
		} else {
			formatter = new DecimalFormat(",##,###");
		}

		return "" + formatter.format(count);
	}
	
	public static String getStringLikes(long count) {
		return count != 1 ? " Likes" : " Like";
	}

	public static String getStringComments(long count) {
		return count != 1 ? " Comments" : " Comment";
	}
}
