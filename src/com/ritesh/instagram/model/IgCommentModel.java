package com.ritesh.instagram.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;

import com.ritesh.instagram.IgConstants;
import com.ritesh.instagram.holder.IgCommentHolder;

public class IgCommentModel {

	/*
	 * Single cmnt response
	 * 
	 * "created_time": "1393231401", "text": "Pagination not allowed", "from": {
	 * "username": "robostardom", "profile_picture":
	 * "http://images.ak.instagram.com/profiles/profile_1108295922_75sq_1392868642.jpg"
	 * , "id": "1108295922", "full_name": "Robo Stardom" }, "id":
	 * "662795492027952409"
	 */

	private static final String DATA = "data";

	private static final String KEY_CREATED_TIME = "created_time";
	private static final String KEY_TEXT = "text";
	private static final String KEY_FROM = "from";
	private static final String KEY_USR_NAME = "username";
	private static final String KEY_PROF_PIC_URL = "profile_picture";
	private static final String KEY_FULL_NAME = "full_name";
	private static final String KEY_ID = "id";

	private ArrayList<IgCommentHolder> mCmntList;	

	public IgCommentModel() {
	}

	public IgCommentModel(String jsonResponse) {

		JSONObject jsonObj;
		try {
			jsonObj = (JSONObject) new JSONTokener(jsonResponse).nextValue();
			parseResponse(jsonObj);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void parseResponse(JSONObject jsonObj) {

		JSONArray cmntsArray;

		if (jsonObj.has(DATA)) {

			try {

				cmntsArray = jsonObj.getJSONArray(DATA);
				mCmntList = new ArrayList<IgCommentHolder>();

				// no. of comments
				int arrayLength = cmntsArray.length();
				Log.d(IgConstants.TAG,
						"Total number of comments in Array:" + arrayLength);

				for (int i = 0; i < arrayLength; i++) {
					Log.d(IgConstants.TAG, " Cmnt Num :" + i);

					JSONObject singleCmntObject = cmntsArray.getJSONObject(i);
					IgCommentHolder cmntHolder = new IgCommentHolder();
					// Comment created time
					if (singleCmntObject.has(KEY_CREATED_TIME)) {
						cmntHolder.setmCmntCreatedTime(singleCmntObject
								.optString(KEY_CREATED_TIME));
					}

					// Comment text
					if (singleCmntObject.has(KEY_TEXT)) {
						cmntHolder.setmCmntText(singleCmntObject
								.getString(KEY_TEXT));
					}

					// Commented by
					if (singleCmntObject.has(KEY_FROM)) {

						JSONObject obj = singleCmntObject.getJSONObject(KEY_FROM);

						// User name of commentor
						if (obj.has(KEY_USR_NAME)) {
							cmntHolder.setmCmntUserName(obj
									.getString(KEY_USR_NAME));
						}

						// Profile pic url
						if (obj.has(KEY_PROF_PIC_URL)) {
							cmntHolder.setmCmntUserProPicUrl(obj
									.getString(KEY_PROF_PIC_URL));
						}
					}

					// Comment id.
					if (singleCmntObject.has(KEY_ID)) {
						cmntHolder.setmCmntId(singleCmntObject.getString(KEY_ID));
					}
					
					mCmntList.add(cmntHolder);
				}

			} catch (JSONException e) {
				Log.e(IgConstants.TAG, "Exception in IgCmntModel :"+e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	public ArrayList<IgCommentHolder> getCommentList() {		
		return mCmntList;
	}

}
