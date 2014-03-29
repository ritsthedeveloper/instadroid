package com.ritesh.instagram.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;

import com.ritesh.instagram.IgConstants;
import com.ritesh.instagram.holder.IgFeedHolder;
import com.ritesh.instagram.holder.IgUserInfoHolder;

public class IgFeedModel {

	private static final String DATA = "data";

	private static final String KEY_CMNTS = "comments";
	private static final String KEY_COUNT = "count";
	private static final String KEY_CREATED_TIME = "created_time";
	private static final String KEY_LIKES = "likes";
	private static final String KEY_IMAGES = "images";
	private static final String KEY_LOW_RESO = "low_resolution";
	private static final String KEY_THMBNL = "thumbnail";
	private static final String KEY_STD_RESO = "standard_resolution";
	private static final String KEY_URL = "url";
	private static final String KEY_CAPTION = "caption";
	private static final String KEY_TEXT = "text";
	private static final String KEY_USER = "user";
	private static final String KEY_ID = "id";
	private static final String KEY_FEED_TYPE = "type";
	private static final String KEY_VIDEOS = "videos";
	private static final String KEY_USER_LIKED = "user_has_liked";
	private static final String KEY_PAGINATION = "pagination";
	private static final String KEY_NXT_URL = "next_url";
	private static final String KEY_NXT_MAX_ID = "next_max_id";

	private ArrayList<IgFeedHolder> mIgFeedList;
	private String mNxtPageUrl;

	public IgFeedModel() {

	}

	public IgFeedModel(String jsonResponse) {

		JSONObject jsonObj;
		try {
			jsonObj = (JSONObject) new JSONTokener(jsonResponse).nextValue();
			parseResponse(jsonObj);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void parseResponse(JSONObject jsonObj) {

		JSONArray feedsArray;

		// Pagination link
		if (jsonObj.has(KEY_PAGINATION)) {
			JSONObject jsonPage;
			try {
				jsonPage = jsonObj.getJSONObject(KEY_PAGINATION);
				// Get the next url
				if (jsonPage.has(KEY_NXT_URL)) {
					mNxtPageUrl = jsonPage.optString(KEY_NXT_URL);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (jsonObj.has(DATA)) {
			try {

				feedsArray = jsonObj.getJSONArray(DATA);
				mIgFeedList = new ArrayList<IgFeedHolder>();
				// no. of feeds ie. posts
				int arrayLength = feedsArray.length();
				Log.d(IgConstants.TAG, "Total number of items in Array:"
						+ arrayLength);

				for (int i = 0; i < arrayLength; i++) {
					Log.d(IgConstants.TAG, " IG post Num :" + i);

					JSONObject singleFeedObject = feedsArray.getJSONObject(i);
					IgFeedHolder igFeedHolder = new IgFeedHolder();

					// Get the comment object.
					if (singleFeedObject.has(KEY_CMNTS)) {
						JSONObject jsonCmnt = singleFeedObject
								.getJSONObject(KEY_CMNTS);

						// Get the comments count
						if (jsonCmnt.has(KEY_COUNT)) {
							igFeedHolder.setmCmntCount(jsonCmnt
									.optString(KEY_COUNT));
						}
					}

					// Post created time
					if (singleFeedObject.has(KEY_CREATED_TIME)) {
						igFeedHolder.setmCreatedTime(singleFeedObject
								.optString(KEY_CREATED_TIME));
					}

					// Media ID
					if (singleFeedObject.has(KEY_ID)) {
						igFeedHolder.setmMediaId(singleFeedObject
								.optString(KEY_ID));
					}

					// Get the like object.
					if (singleFeedObject.has(KEY_LIKES)) {
						JSONObject jsonLike = singleFeedObject
								.getJSONObject(KEY_LIKES);

						// Get the likes count
						if (jsonLike.has(KEY_COUNT)) {
							igFeedHolder.setmLikesCount(jsonLike
									.optString(KEY_COUNT));
						}
					}

					// Get the Images object.
					if (singleFeedObject.has(KEY_IMAGES)) {
						JSONObject jsonImage = singleFeedObject
								.getJSONObject(KEY_IMAGES);

						// Get the low resolution image object
						if (jsonImage.has(KEY_LOW_RESO)) {

							JSONObject obj = jsonImage
									.getJSONObject(KEY_LOW_RESO);
							if (obj.has(KEY_URL)) {
								// Get the low resolution image url
								igFeedHolder.setmLowResImgUrl(obj
										.optString(KEY_URL));
							}
						}

						// Get the thmbnl image object
						if (jsonImage.has(KEY_THMBNL)) {

							JSONObject obj = jsonImage
									.getJSONObject(KEY_THMBNL);
							if (obj.has(KEY_URL)) {
								// Get the thmbnl image url
								igFeedHolder.setmThmbnlUrl(obj
										.optString(KEY_URL));
							}
						}

						// Get the std resolution image object
						if (jsonImage.has(KEY_STD_RESO)) {

							JSONObject obj = jsonImage
									.getJSONObject(KEY_STD_RESO);
							if (obj.has(KEY_URL)) {
								// Get the std resolution image url
								igFeedHolder.setmStdResImgUrl(obj
										.optString(KEY_URL));
							}
						}
					}

					// Get the caption object.
					if (singleFeedObject.has(KEY_CAPTION)) {
						JSONObject jsonCaption = singleFeedObject
								.getJSONObject(KEY_CAPTION);

						// Get the caption text
						if (jsonCaption.has(KEY_TEXT)) {
							igFeedHolder.setmCaptionText(jsonCaption
									.optString(KEY_TEXT));
						}
					}

					// Get the feed type (Image or Video.)
					if (singleFeedObject.has(KEY_FEED_TYPE)) {
						igFeedHolder.setmFeedType(singleFeedObject
								.optString(KEY_FEED_TYPE));
					}

					// Get the Video object.
					if (singleFeedObject.has(KEY_VIDEOS)) {
						JSONObject jsonVideo = singleFeedObject
								.getJSONObject(KEY_VIDEOS);

						// Get the low resolution video object
						if (jsonVideo.has(KEY_LOW_RESO)) {

							JSONObject obj = jsonVideo
									.getJSONObject(KEY_LOW_RESO);
							if (obj.has(KEY_URL)) {
								// Get the low resolution video url
								igFeedHolder.setmVidLowResUrl(obj
										.optString(KEY_URL));
							}
						}

						// Get the std resolution video object
						if (jsonVideo.has(KEY_STD_RESO)) {

							JSONObject obj = jsonVideo
									.getJSONObject(KEY_STD_RESO);
							if (obj.has(KEY_URL)) {
								// Get the std resolution video url
								igFeedHolder.setmVidStdResUrl(obj
										.optString(KEY_URL));
							}
						}
					}

					// If user has liked the media or not
					if (singleFeedObject.has(KEY_USER_LIKED)) {
						igFeedHolder.setmLikeStatus(singleFeedObject
								.getBoolean(KEY_USER_LIKED));
					}

					// Get the user object.
					if (singleFeedObject.has(KEY_USER)) {

						JSONObject jsonUser = singleFeedObject
								.getJSONObject(KEY_USER);
						IgUserInfoHolder infoHolder = new IgUserInfoHolder();
						if (jsonUser.has(IgUserInfoModel.USR_ID)) {
							infoHolder.setmUserId(jsonUser
									.optString(IgUserInfoModel.USR_ID));
						}

						if (jsonUser.has(IgUserInfoModel.USR_NAME)) {
							infoHolder.setmUserName(jsonUser
									.optString(IgUserInfoModel.USR_NAME));
						}

						if (jsonUser.has(IgUserInfoModel.USR_FULL_NAME)) {
							infoHolder.setmUserFullName(jsonUser
									.optString(IgUserInfoModel.USR_FULL_NAME));

						}
						if (jsonUser.has(IgUserInfoModel.USR_PROF_PIC_URL)) {
							infoHolder
									.setmUserProfPicUrl(jsonUser
											.optString(IgUserInfoModel.USR_PROF_PIC_URL));

						}
						if (jsonUser.has(IgUserInfoModel.USR_BIO)) {
							infoHolder.setmUserBio(jsonUser
									.optString(IgUserInfoModel.USR_BIO));

						}
						if (jsonUser.has(IgUserInfoModel.USR_WEBSITE)) {
							infoHolder.setmUserWebSite(jsonUser
									.optString(IgUserInfoModel.USR_WEBSITE));
						}

						igFeedHolder.setmUserInfo(infoHolder);
					}

					mIgFeedList.add(igFeedHolder);
				}

			} catch (JSONException e) {
				Log.e(IgConstants.TAG,
						"Exception in IgFeedModel :" + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public ArrayList<IgFeedHolder> getUserIgFeeds() {
		return mIgFeedList;
	}

	public String getIgNxtPageUrl() {
		return mNxtPageUrl;
	}

}
