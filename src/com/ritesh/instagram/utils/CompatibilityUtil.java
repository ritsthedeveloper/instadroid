package com.ritesh.instagram.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

/**
 * As Android projects grow in size, it becomes increasingly important that 
 * the code remains organized and well-structured. Providing a utility class 
 * for commonly called methods can help tremendously in reducing the complexity 
 * of the project, allowing you to structure your code in a readable and easily 
 * understandable way.
 */
public class CompatibilityUtil {
     
  /**
   * Get the current Android API level.
   */
  public static int getSdkVersion() {
    return Build.VERSION.SDK_INT;
  }
 
  /**
   * Determine if the device is running API level 8 or higher.
   */
  public static boolean isFroyo() {
    return getSdkVersion() >= Build.VERSION_CODES.FROYO;
  }
 
  /**
   * Determine if the device is running API level 11 or higher.
   */
  public static boolean isHoneycomb() {
    return getSdkVersion() >= Build.VERSION_CODES.HONEYCOMB;
  }
 
  /**
   * Determine if the device is a tablet (i.e. it has a large screen).
   * 
   * @param context The calling context.
   */
  public static boolean isTablet(Context context) {
    return (context.getResources().getConfiguration().screenLayout
            & Configuration.SCREENLAYOUT_SIZE_MASK)
            >= Configuration.SCREENLAYOUT_SIZE_LARGE;
  }
 
  /**
   * Determine if the device is a HoneyComb tablet.
   * 
   * @param context The calling context.
   */
  public static boolean isHoneycombTablet(Context context) {
    return isHoneycomb() && isTablet(context);
  }
 
  /**
   * This class can't be instantiated.
   */
  private CompatibilityUtil() { }
}