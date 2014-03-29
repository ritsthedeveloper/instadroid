package com.ritesh.instagram.holder;

public class IgCommentHolder {

	/*
	 * Single cmnt
	 * 
	 * "created_time": "1393231401", "text": "Pagination not allowed", "from": {
	 * "username": "robostardom", "profile_picture":
	 * "http://images.ak.instagram.com/profiles/profile_1108295922_75sq_1392868642.jpg"
	 * , "id": "1108295922", "full_name": "Robo Stardom" }, "id":
	 * "662795492027952409"
	 */

	private String mCmntCreatedTime;
	private String mCmntText;
	private String mCmntUserName;
	private String mCmntUserProPicUrl;
	private String mCmntUserId;
	private String mCmntUserFullName;
	private String mCmntId;

	public String getmCmntCreatedTime() {
		return mCmntCreatedTime;
	}

	public void setmCmntCreatedTime(String mCmntCreatedTime) {
		this.mCmntCreatedTime = mCmntCreatedTime;
	}

	public String getmCmntText() {
		return mCmntText;
	}

	public void setmCmntText(String mCmntText) {
		this.mCmntText = mCmntText;
	}

	public String getmCmntUserName() {
		return mCmntUserName;
	}

	public void setmCmntUserName(String mCmntUserName) {
		this.mCmntUserName = mCmntUserName;
	}

	public String getmCmntUserProPicUrl() {
		return mCmntUserProPicUrl;
	}

	public void setmCmntUserProPicUrl(String mCmntUserProPicUrl) {
		this.mCmntUserProPicUrl = mCmntUserProPicUrl;
	}

	public String getmCmntUserId() {
		return mCmntUserId;
	}

	public void setmCmntUserId(String mCmntUserId) {
		this.mCmntUserId = mCmntUserId;
	}

	public String getmCmntUserFullName() {
		return mCmntUserFullName;
	}

	public void setmCmntUserFullName(String mCmntUserFullName) {
		this.mCmntUserFullName = mCmntUserFullName;
	}

	public String getmCmntId() {
		return mCmntId;
	}

	public void setmCmntId(String mCmntId) {
		this.mCmntId = mCmntId;
	}

}
