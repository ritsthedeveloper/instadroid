package com.ritesh.instagram.model;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;

import com.ritesh.instagram.IgConstants;
import com.ritesh.instagram.holder.IgUserInfoHolder;

/**
 * Basic info about the Instagram user.
 * 
 * @author ritesh
 */
public class IgUserInfoModel {
	
	private static final String DATA = "data";
	
	public static final String USR_ID = "id";
	public static final String USR_NAME = "username";
	public static final String USR_FULL_NAME = "full_name";
	public static final String USR_PROF_PIC_URL = "profile_picture";
	public static final String USR_BIO = "bio";
	public static final String USR_WEBSITE = "website";
	
	private static final String USR_COUNTS = "counts";
	private static final String USR_MEDIA = "media";
	private static final String USR_FOLLOWS = "follows";
	private static final String USR_FOLLOWED_BY = "followed_by";
	
	private IgUserInfoHolder mUserInfoHolder;
	
	public IgUserInfoModel(){
		
	}
	
	public IgUserInfoModel(String jsonResponse) {

		JSONObject jsonObj;
		try {
			jsonObj = (JSONObject) new JSONTokener(jsonResponse).nextValue();
			parseResponse(jsonObj);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void parseResponse(JSONObject jsonObj) {

		JSONObject internal_Obj;
		mUserInfoHolder = new IgUserInfoHolder();
		
		if (jsonObj.has(DATA)) {
			try {
				internal_Obj = jsonObj.getJSONObject(DATA);

				if (internal_Obj.has(USR_ID)) {
					mUserInfoHolder.setmUserId(internal_Obj.optString(USR_ID));
				}

				if (internal_Obj.has(USR_NAME)) {
					mUserInfoHolder.setmUserName(internal_Obj
							.optString(USR_NAME));
				}
				
				if (internal_Obj.has(USR_FULL_NAME)) {
					mUserInfoHolder.setmUserFullName(internal_Obj
							.optString(USR_FULL_NAME));

				}
				if (internal_Obj.has(USR_PROF_PIC_URL)) {
					mUserInfoHolder.setmUserProfPicUrl(internal_Obj
							.optString(USR_PROF_PIC_URL));

				}
				if (internal_Obj.has(USR_BIO)) {
					mUserInfoHolder.setmUserBio(internal_Obj
							.optString(USR_BIO));

				}
				if (internal_Obj.has(USR_WEBSITE)) {
					mUserInfoHolder.setmUserWebSite(internal_Obj
							.optString(USR_WEBSITE));
				}
				
				if (internal_Obj.has(USR_COUNTS)) {
					JSONObject obj = internal_Obj.getJSONObject(USR_COUNTS);

					if (obj.has(USR_MEDIA)) {
						mUserInfoHolder.setmUserMediaCount(obj
								.optString(USR_MEDIA));
					}
					
					if (obj.has(USR_FOLLOWS)) {
						mUserInfoHolder.setmUserFollowsCount(obj
								.optString(USR_FOLLOWS));

					}
					
					if (obj.has(USR_FOLLOWED_BY)) {
						mUserInfoHolder.setmUserFollowedByCount(obj
								.optString(USR_FOLLOWED_BY));
					}
				}
				
				
			} catch (JSONException e) {	
				Log.e(IgConstants.TAG, "Exception in UserInfoModel :"+e.getMessage());
				e.printStackTrace();				
			}

		}
	}
	
	public IgUserInfoHolder getDataHolder() {		
		return mUserInfoHolder;
	}
}
