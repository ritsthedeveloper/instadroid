package com.ritesh.instagram;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.Context;

/**
 * Manage access token and user name. Uses shared preferences to store access
 * token and user name.
 * 
 * @author Thiago Locatelli <thiago.locatelli@gmail.com>
 * @author Lorensius W. L T <lorenz@londatiga.net>
 * 
 */
public class IgSession {

	private SharedPreferences sharedPref;
	private Editor editor;

	private static final String SHARED = "Instagram_Preferences";
	private static final String API_USERNAME = "username";
	private static final String API_ID = "id";
	private static final String API_NAME = "name";
	private static final String API_ACCESS_TOKEN = "access_token";	
    private static final String API_USER_IMAGE = "user_image";

	public IgSession(Context context) {
		sharedPref = context.getSharedPreferences(SHARED, Context.MODE_PRIVATE);
		editor = sharedPref.edit();
	}

	/**
	 * Store the Access Token and othe user info.
	 * 
	 * @param accessToken
	 * @param expireToken
	 * @param expiresIn
	 * @param username
	 * @param image
	 */
	public void storeAccessToken(String accessToken, String id, String username, String name, String image) {
		editor.putString(API_ID, id);
		editor.putString(API_NAME, name);
		editor.putString(API_ACCESS_TOKEN, accessToken);
		editor.putString(API_USERNAME, username);
		editor.putString(API_USER_IMAGE, image);
		editor.commit();
	}

	/**
	 * Store just the Access Token.
	 * 
	 * @param accessToken
	 */
	public void storeAccessToken(String accessToken) {
		editor.putString(API_ACCESS_TOKEN, accessToken);
		editor.commit();
	}

	/**
	 * Reset access token and user name
	 */
	public void resetAccessToken() {
		editor.putString(API_ID, null);
		editor.putString(API_NAME, null);
		editor.putString(API_ACCESS_TOKEN, null);
		editor.putString(API_USERNAME, null);
		editor.putString(API_USER_IMAGE, null);
		editor.commit();
	}

	/**
	 * Get user name
	 * 
	 * @return User name
	 */
	public String getUsername() {
		return sharedPref.getString(API_USERNAME, null);
	}
	
	/**
	 * Get the user ID
	 * @return
	 */
	public String getId() {
		return sharedPref.getString(API_ID, null);
	}
	
	/**
	 * Get the complete name of the user.
	 * @return
	 */
	public String getName() {
		return sharedPref.getString(API_NAME, null);
	}

	/**
	 * Get access token
	 * 
	 * @return Access token
	 */
	public String getAccessToken() {
		return sharedPref.getString(API_ACCESS_TOKEN, null);
	}
	
	/**
     * Get userImage url.
     * 
     * @return userImage
     */
    public String getUserImage() {
        return sharedPref.getString(API_USER_IMAGE, null);
    }

}