package com.ritesh.instagram;

import android.content.Context;

import com.ritesh.instagram.listener.IAuthenticationListener;
import com.ritesh.instagram.listener.IFetchCmntsListener;
import com.ritesh.instagram.listener.IFetchIgFeedsListener;
import com.ritesh.instagram.listener.ILikeCmntListener;
import com.ritesh.instagram.listener.IReqStatusListener;
import com.ritesh.instagram.listener.IUserInfoFetchedListener;

public class IgWrapper {

	private Context mContext;
	private IgManager mIgManager;
	private IReqStatusListener mReqListener;
	
	/**
	 * initializes Instagram wrapper
	 * 
	 * @param context
	 */
	public IgWrapper(Context context) {
		
		mContext = context;		
		mIgManager = new IgManager(context);					
	}
	
    public IgWrapper(Context context,IAuthenticationListener listener) {
		
		this(context);		
		mIgManager.setAuthListener(listener);			
	}
	
	/**
     * checks if the user is logged in with Instagram
     * 
     * @return
     */
	public boolean isLoggedIn() {
		return (mIgManager.hasAccessToken() ? true : false);
	}	
	
	/**
     * login through Instagram
     */
    public void loginInstagram() {
    	if (!isLoggedIn())
			mIgManager.showLoginDialog();
		else {
			return;
		}
    }
    
    /**
     * logs out the user from Instagram
     */
	public void logoutInstagram() {
		mIgManager.resetAccessToken();
	}
	
	public void setReqStatusListener(IReqStatusListener listener) {
		mReqListener = listener;
		mIgManager.setReqStatusListener(listener);
	}
	
	/**
	 * Get all the Information about the authenticated User.
	 *   
	 * @param userId
	 * @param listener
	 */
	public void getUserInfo(String userId, IUserInfoFetchedListener listener){
		mIgManager.getUserinfo(userId, listener);
	}
	
	/**
	 * Get all the feeds(posts) of a particular user.
	 * 
	 * @param userId
	 * @param listener
	 */
	public void getUserFeeds(String userId,IFetchIgFeedsListener listener){
		mIgManager.getUserFeeds(userId,listener);
	}
	
	/**
     * Get all the feeds(posts) of a particular user.
     * 
     * @param userId
     * @param nxtPgUrl
     * @param listener
     */
	public void getUserFeeds(String userId, int feedCount, String nxtPgUrl,
			IFetchIgFeedsListener listener) {
		mIgManager.getUserFeeds(userId, feedCount, nxtPgUrl, listener);
	}
	
	/**
     * Get all the comments on a particular media.
     * 
     * @param mediaId
     * @param listener
     */
	public void getCommentsOnMedia(String mediaId, IFetchCmntsListener listener) {
		if (!isLoggedIn()) {
			loginInstagram();
		} else {
			mIgManager.getCommentsOnMedia(mediaId, listener);
		}
	}
	
	/**
     * To Post a like on a media.
     * 
     * @param mediaId
     * @param listener
     */
//    public void postLikeOnMedia(String mediaId,ILikeCmntListener listener){
//    	mIgManager.postLikeOnMedia(mediaId, listener);    	
//    }
    
    /**
     * To Post a like on a media.
     * 
     * @param reqType - type of request.
     * @param mediaId
     * @param listener
     */
	public void postLikeOnMedia(int reqType, String mediaId,
			ILikeCmntListener listener) {

		if (!isLoggedIn()) {
			loginInstagram();
		} else {
			mIgManager.postLikeOnMedia(reqType, mediaId, listener);
		}
	}
    
    /**
     * To Post a Comment on a media.
     * 
     * @param mediaId
     * @param listener
     */
    
	public void postCmntOnMedia(int reqType, String cmntTxt, String mediaId,
			ILikeCmntListener listener) {
		if (!isLoggedIn()) {
			loginInstagram();
		} else {
			mIgManager.postCmntOnMedia(reqType, cmntTxt, mediaId, listener);
		}
	}
}
