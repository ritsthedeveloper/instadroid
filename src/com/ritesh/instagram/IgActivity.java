package com.ritesh.instagram;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.ritesh.instagram.holder.IgCommentHolder;
import com.ritesh.instagram.holder.IgFeedHolder;
import com.ritesh.instagram.holder.IgUserInfoHolder;
import com.ritesh.instagram.listener.IAuthenticationListener;
import com.ritesh.instagram.listener.IFetchCmntsListener;
import com.ritesh.instagram.listener.IFetchIgFeedsListener;
import com.ritesh.instagram.listener.ILikeCmntListener;
import com.ritesh.instagram.listener.IReqStatusListener;
import com.ritesh.instagram.listener.IUserInfoFetchedListener;

public class IgActivity extends Activity implements IReqStatusListener{

	public static final String KEY_USER_ID = "user_id";
    public static final String KEY_MEDIA_ID = "media_id";
    public static final String KEY_CMNT_TXT = "cmnt_txt";
    public static final String KEY_NXT_PG_URL = "nxt_pg_url";
    public static final String KEY_FEED_COUNT = "feed_count";
    
	private IgWrapper mIgWrapper;
	private Intent mIgReqIntent;
	private int mIgReqType;
	
	private static ILikeCmntListener mIgActionListener;
    private static IFetchIgFeedsListener mIgFeedsListener;
    private static IFetchCmntsListener mIgFetchCmntListener;
    private static IAuthenticationListener mIgAuthListener;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// get the request type for Instagram
		mIgReqIntent = getIntent();
		mIgReqType = mIgReqIntent.getIntExtra(IgConstants.IG_REQUEST, 0);

		mIgWrapper = new IgWrapper(this,mAuthListener);	
		mIgWrapper.setReqStatusListener(this);
				
		/**
		 * Check for Instagram request type.
		 */
		switch (mIgReqType) {
		
		case IgConstants.IG_REQ_LOGIN:
		    // IG login request 
			mIgWrapper.loginInstagram();
		    break;

		case IgConstants.IG_REQ_USER_INFO:			
		    // IG user info 
			mIgWrapper.getUserInfo(getIntent()
			    .getStringExtra(KEY_USER_ID),mUserInfoListener);
		    break;
		case IgConstants.IG_REQ_USER_FEEDS:			
		    // IG user feeds 
//			mIgWrapper.getUserFeeds(getIntent()
//			    .getStringExtra(KEY_USER_ID),mUserFeedsListener);
			// IG user feeds
		    mIgWrapper.getUserFeeds(getIntent().getStringExtra(KEY_USER_ID),
			    getIntent().getIntExtra(KEY_FEED_COUNT, 20),
			    getIntent().getStringExtra(KEY_NXT_PG_URL),
			    mUserFeedsListener);
		    break;
		case IgConstants.IG_REQ_GET_CMNTS:			
		    // Comments on media
			mIgWrapper.getCommentsOnMedia(getIntent()
			    .getStringExtra(KEY_MEDIA_ID),mCmntsListener);
		    break;
		    
		default:
		    break;
		}
	}	
	
	public static void setIgAuthListener(IAuthenticationListener listener) {
		mIgAuthListener = listener;
	}
	
	public static void setIgActionListener(ILikeCmntListener listener) {
		mIgActionListener = listener;
	}

	public static void setIgFeedListener(IFetchIgFeedsListener listener) {
		mIgFeedsListener = listener;
	}

	public static void setIgFetchCmntListener(IFetchCmntsListener listener) {
		mIgFetchCmntListener = listener;
	}

	/**
	 * Authentication success or failure listener.
	 */
	private final IAuthenticationListener mAuthListener = new IAuthenticationListener() {

		@Override
		public void onAuthSuccess() {
			Log.d(IgConstants.TAG, "Finishing IG activity.");
			IgActivity.this.finish();

			Log.d(IgConstants.TAG, "In IG Auth success.");
			if (mIgReqType == IgConstants.IG_REQ_POST_LIKE) {

				mIgWrapper.postLikeOnMedia(IgConstants.IG_REQ_POST_LIKE,
						getIntent().getStringExtra(KEY_MEDIA_ID),
						mLikeCmntListener);
			} else if (mIgReqType == IgConstants.IG_REQ_POST_CMNT) {

				mIgWrapper.postCmntOnMedia(IgConstants.IG_REQ_POST_CMNT,
						getIntent().getStringExtra(KEY_CMNT_TXT), getIntent()
								.getStringExtra(KEY_MEDIA_ID),
						mLikeCmntListener);
			} else {

				if (null != mIgAuthListener) {
					mIgAuthListener.onAuthSuccess();
					mIgAuthListener = null;
				}
				Log.d(IgConstants.TAG, "Finishing IG activity.");
				IgActivity.this.finish();
			}
		}

		@Override
		public void onAuthFail(String error) {
			Log.d(IgConstants.TAG, "Auth Failure Error :" + error);
			Log.d(IgConstants.TAG, "Finishing IG activity.");
			
			mIgAuthListener.onAuthFail(error);
			
			IgActivity.this.finish();
		}
	};
	
	/**
	 * User Info success or failure listener.
	 */
	private final IUserInfoFetchedListener mUserInfoListener = new IUserInfoFetchedListener() {
		
		@Override
		public void onIgUsrInfoFetchingFailed() {
			IgActivity.this.finish();			
		}
		
		@Override
		public void onIgUsrInfoFetched(IgUserInfoHolder usrInfoHolder) {
			
			Log.d(IgConstants.TAG,
					"Full Name :" + usrInfoHolder.getmUserFullName());
			IgActivity.this.finish();		
		}
	};
	
	/**
	 * User Feeds success or failure listener.
	 */
	private final IFetchIgFeedsListener mUserFeedsListener = new IFetchIgFeedsListener() {
				
		@Override
		public void onIgFeedsFetched(ArrayList<IgFeedHolder> feedList,
				String nxtPgUrl) {
			
			IgActivity.this.finish();	
		}		
	};
	
	/**
	 * Comments fetch success or failure listener.
	 */
	private final IFetchCmntsListener mCmntsListener = new IFetchCmntsListener() {
		
		@Override
		public void onIgCmntsFetched(ArrayList<IgCommentHolder> cmntList) {			
			IgActivity.this.finish();
		}
		
		@Override
		public void onIgCmntsFailed() {			
			IgActivity.this.finish();
		}
	};
	
	/**
     * Post like,Comment success or failure listener.
     */
	private final ILikeCmntListener mLikeCmntListener = new ILikeCmntListener() {

		@Override
		public void onCopmplete(int reqType, int responseCode) {

			mIgActionListener.onCopmplete(reqType, responseCode);
			IgActivity.this.finish();
		}
	};

	@Override
	public void onSuccess(int reqType) {	
		
		Log.d(IgConstants.TAG, "Finishing IG activity.");
		Log.d(IgConstants.TAG, "Request success.");
		IgActivity.this.finish();
	}

	@Override
	public void onFail(int reqType, String error) {
		
		Log.d(IgConstants.TAG, "Finishing IG activity.");
		Log.d(IgConstants.TAG, "Failure Msg :"+error);
		IgActivity.this.finish();
	}	
}
