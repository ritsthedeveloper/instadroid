package com.ritesh.instagram;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.ritesh.instagram.IgLoginDialog.OAuthDialogListener;
import com.ritesh.instagram.listener.IAuthenticationListener;
import com.ritesh.instagram.listener.IFetchCmntsListener;
import com.ritesh.instagram.listener.IFetchIgFeedsListener;
import com.ritesh.instagram.listener.ILikeCmntListener;
import com.ritesh.instagram.listener.IReqStatusListener;
import com.ritesh.instagram.listener.IUserInfoFetchedListener;
import com.ritesh.instagram.model.IgCommentModel;
import com.ritesh.instagram.model.IgFeedModel;
import com.ritesh.instagram.model.IgUserInfoModel;

/**
 * Instagram Manager to make the webservice calls.
 * 
 * @author ritesh
 *
 */
public class IgManager {

	private static final String TAG = "InstagramAPI";	
	
	private static final String HTTP_POST = "POST";
	private static final String HTTP_GET = "GET";
	private static final String HTTP_DELETE = "DELETE";

	// for authentication	
	private static final String AUTH_URL = "https://api.instagram.com/oauth/authorize/";
	// for getting the token and user details
	private static final String TOKEN_URL = "https://api.instagram.com/oauth/access_token";
	// for some information from Instagram as per the request
	private static final String API_URL = "https://api.instagram.com/v1";
	
	private IgSession mSession;
	private IgLoginDialog mDialog;
	private IAuthenticationListener mAuthListener;
	private IReqStatusListener mReqStatusListener;
	private ProgressDialog mPrgrsDlg;
	private Context mContext;
	
	private String mAuthUrl;
	private String mTokenUrl;
	private String mAccessToken;
	
	public IgManager(Context context) {
		
		mContext = context;
		
		mSession = new IgSession(context);			
		mPrgrsDlg = new ProgressDialog(context);
		mPrgrsDlg.setCancelable(false);
	}
	
	public void setAuthListener(IAuthenticationListener listener) {
		mAuthListener = listener;
	}
	
	public void setReqStatusListener(IReqStatusListener listener) {
		mReqStatusListener = listener;
	}
	
	/**
	 * Get the Token URL
	 * @return
	 */
	public void getTokenUrl() {

		mTokenUrl = TOKEN_URL + "?client_id=" + IgConstants.CLIENT_ID
				+ "&client_secret=" + IgConstants.CLIENT_SECRET
				+ "&redirect_uri=" + IgConstants.CALLBACK_URL
				+ "&grant_type=authorization_code";
	}
	
	/**
	 * Get the Authentication url to get the accesstoken.
	 * 
	 * @return
	 */
	public void getAuthUrl() {

		mAuthUrl = AUTH_URL
				+ "?client_id="
				+ IgConstants.CLIENT_ID
				+ "&redirect_uri="
				+ IgConstants.CALLBACK_URL
				+ "&response_type=code&display=touch&scope=likes+comments+relationships";
	}
	
	/**
	 * Check if access token is valid
	 * 
	 * @return
	 */
	public boolean hasAccessToken() {
		return (mSession.getAccessToken() == null) ? false : true;
	}
	
	/**
	 * Get the access token.
	 * @return
	 */
	public String getAccessToken(){
		return mSession.getAccessToken();
	}
	
	/**
     * Reset Access token
     */
	public void resetAccessToken() {
		mSession.resetAccessToken();
	}
	
	/**
	 * Get the user id.
	 * @return
	 */
	public String getUserId(){
		return mSession.getId();
	}
    
    /**
     * authorizes the user Login
     */
    public void showLoginDialog() {
    	
    	OAuthDialogListener listener = new OAuthDialogListener() {
			@Override
			public void onComplete(String code) {
				new GetAccessTokenTask().execute(code);
			}

			@Override
			public void onError(String error) {
				mAuthListener.onAuthFail("Authorization failed");
			}
			
			@Override
		    public void onIgLoginDlgBackPressed(boolean loginInterrupted) {
			mAuthListener.onAuthFail("Authorization Interrupted.");		
		    }
		};

		getAuthUrl();
		
		mDialog = new IgLoginDialog(mContext, mAuthUrl, listener);
		mDialog.show();
    }
    
    /**
     * Get the user information.
     * 
     * @param userId - Id of the user whose information is to be fetched.
     * @param listener - Callback to send the result back. 
     */
    public void getUserinfo(String userId , IUserInfoFetchedListener listener){
    	new GetUserInfoTask(listener).execute(userId);
    }
    
    /**
     * Get the users posts.
     * 
     * @param userId- Id of the user whose posts are to be fetched.
     * @param listener - Callback to send the result back. 
     */
    public void getUserFeeds(String userId, IFetchIgFeedsListener listener){
    	new GetUserImagesTask(listener).execute(userId);
    }
    
    /**
     * Get the users posts with pagination.
     * 
     * @param userId - Id of the user whose posts are to be fetched.
     * @param nxtPgUrl - Next page url, obtained in previous response.
     * @param listener - Callback to send the result back.
     */
    public void getUserFeeds(String userId, int feedCount,String nxtPgUrl,IFetchIgFeedsListener listener) {
	new GetUserImagesTask(feedCount,nxtPgUrl,listener).execute(userId);
    }
    
    /**
     * Get the comments on a media.
     * 
     * @param mediaId  - Id of the media for which comments are to be fetched.
     * @param listener - Callback to send the result back.
     */
    public void getCommentsOnMedia(String mediaId,IFetchCmntsListener listener){
		new GetCmntOnMediaTask(listener).execute(mediaId);
	}
    
    /**
     * To Post a like on a media.
     * 
     * @param mediaId - Id of the media to be liked.
     * @param listener - Callback to send the result back.
     */
//    public void postLikeOnMedia(String mediaId,ILikeCmntListener listener){
//    	new PostLikeOnMediaTask(listener).execute(mediaId);
//    }
    
    /**
     * To Post a like on a media.
     * 
     * @param reqType - type of the request ie. like
     * @param mediaId - Id of the media to be liked.
     * @param listener - Callback to send the result back.
     */
    public void postLikeOnMedia(int reqType,String mediaId, ILikeCmntListener listener) {
	new PostLikeOnMediaTask(reqType,listener).execute(mediaId);
    }
    
    /**
     * To Post a Comment on a media.
     * 
     * @param reqType - type of request ie. Comment
     * @param cmntTxt - Comment text.
     * @param mediaId - Id of the media for which comment is to be posted.
     * @param listener - Callback to send the result back.
     */
    public void postCmntOnMedia(int reqType,String cmntTxt,String mediaId,ILikeCmntListener listener){
    	new PostCmntOnMediaTask(reqType,cmntTxt,listener).execute(mediaId);
    }
    
    /**
     * To undo like on a post.
     * 
     * @param mediaId - Id of the media for which like is to be undone.
     * @param listener - Callback to send the result back.
     */
    public void deleteLikeOnMedia(String mediaId,ILikeCmntListener listener){
    	
    }
    
    
    /**
     * Async Task to get the AccessToken  
     */    
    class GetAccessTokenTask extends AsyncTask<String, Boolean, Boolean> {
    	
    	@Override
    	protected void onPreExecute() {
    	    super.onPreExecute();
    	    mPrgrsDlg.setMessage("Getting access token ...");
    	    // If activity is finishing , don't show the dialog
    	    // as it will cause bad window token exception
    	    if (!((Activity) mContext).isFinishing())
    		mPrgrsDlg.show();
    	}
    	
    	@Override
    	protected Boolean doInBackground(String... params) {
    		
    		Log.d(TAG, "Getting access token");
			
			try {
				URL url = new URL(TOKEN_URL);				
				Log.d(TAG, "Opening Token URL " + url.toString());
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setRequestMethod(HTTP_POST);
				urlConnection.setDoInput(true);
				urlConnection.setDoOutput(true);
				
				OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
				writer.write("client_id="+IgConstants.CLIENT_ID+
							"&client_secret="+IgConstants.CLIENT_SECRET+
							"&grant_type=authorization_code" +
							"&redirect_uri="+IgConstants.CALLBACK_URL+
							"&code=" + params[0]);				
				
			    writer.flush();
			    
				String response = streamToString(urlConnection.getInputStream());
				Log.d(TAG, "response " + response);
				
				JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
				
				mAccessToken = jsonObj.getString("access_token");
				Log.i(TAG, "Got access token: " + mAccessToken);
				
				String id = jsonObj.getJSONObject("user").getString("id");
				String user = jsonObj.getJSONObject("user").getString("username");
				String name = jsonObj.getJSONObject("user").getString("full_name");					
				String userImage = jsonObj.getJSONObject("user").getString(
                        "profile_picture");
				
				mSession.storeAccessToken(mAccessToken, id, user, name, userImage);
				
				return true;
			} catch (Exception ex) {				
				ex.printStackTrace();
				return false;
			}			
    	}
    	
    	@Override
		protected void onPostExecute(Boolean result) {

    		if(null != mPrgrsDlg && mPrgrsDlg.isShowing())
    			mPrgrsDlg.dismiss();
    		
			if (result) {
				if(null != mAuthListener)
				mAuthListener.onAuthSuccess();
			} else {
				if(null != mAuthListener)
				mAuthListener.onAuthFail("Failed to get access token");
			}
		}
    }
    
    /**
     * Async Task to get the user info.  
     */ 
	class GetUserInfoTask extends AsyncTask<String, IgUserInfoModel, IgUserInfoModel> {

		IUserInfoFetchedListener mUserInfoListener;		
		
		public GetUserInfoTask(){			
		}
		
        public GetUserInfoTask(IUserInfoFetchedListener listener){
        	mUserInfoListener = listener;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mPrgrsDlg.setMessage("Getting user info ...");
			// If activity is finishing , don't show the dialog
		    // as it will cause bad window token exception
		    if (!((Activity) mContext).isFinishing())
			mPrgrsDlg.show();
		}

		@Override
		protected IgUserInfoModel doInBackground(String... params) {

			Log.d(TAG, "Fetching user info");

			try {
				URL url = new URL(API_URL + "/users/" + params[0]
						+ "/?access_token=" + getAccessToken());

				Log.d(TAG, "Opening URL " + url.toString());
				HttpURLConnection urlConnection = (HttpURLConnection) url
						.openConnection();
				urlConnection.setRequestMethod(HTTP_GET);
				// urlConnection.setDoInput(true);
				// urlConnection.setDoOutput(true);
				urlConnection.connect();
				String response = streamToString(urlConnection.getInputStream());

				Log.d(TAG, "Fetch user info response :" + response);

				IgUserInfoModel userInfoModel = new IgUserInfoModel(response);	

				return userInfoModel;
				
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}			
		}

		@Override
		protected void onPostExecute(IgUserInfoModel model) {

			if(null != mPrgrsDlg && mPrgrsDlg.isShowing())
    			mPrgrsDlg.dismiss();
			
			if (null != model) {
				mUserInfoListener.onIgUsrInfoFetched(model.getDataHolder());				
			} else {
				mUserInfoListener.onIgUsrInfoFetchingFailed();
			}		
		}
	}
	
	 /**
     * Async Task to get the Images (Feeds).  
     */
    
	class GetUserImagesTask extends AsyncTask<String, IgFeedModel, IgFeedModel>{
		
		IFetchIgFeedsListener mFeedListener;
		String mNxtUrl;
		String mCount = "20"; // Default feedcount.	
		
		public GetUserImagesTask (){
			
		}
		
		public GetUserImagesTask(IFetchIgFeedsListener listener){
			mFeedListener = listener;
		}
		
		public GetUserImagesTask(int feedCount,String nxtUrl ,IFetchIgFeedsListener listener) {
		    this(listener);
		    mCount = feedCount+"";
		    mNxtUrl = nxtUrl;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mPrgrsDlg.setMessage("Getting user photos ...");
			// If activity is finishing , don't show the dialog
		    // as it will cause bad window token exception
		    if (!((Activity) mContext).isFinishing())
			mPrgrsDlg.show();
		}
		
		@Override
		protected IgFeedModel doInBackground(String... params) {
			
			Log.d(TAG, "Fetching user photos");
			
			try {
				URL url = null;
				
				if (null == mNxtUrl) {
					
					if (hasAccessToken())
						url = new URL(API_URL + "/users/" + params[0]
								+ "/media/recent" + "/?access_token="
								+ getAccessToken());
					else
						url = new URL(API_URL + "/users/" + params[0]
								+ "/media/recent" + "/?client_id="
								+ IgConstants.CLIENT_ID);
				} else {
					url = new URL(mNxtUrl);
				}
				
				HttpURLConnection urlConnection = (HttpURLConnection) url
						.openConnection();
				urlConnection.setRequestMethod(HTTP_GET);
				urlConnection.connect();
				
				String response = streamToString(urlConnection.getInputStream());

				Log.d(TAG, "User photos response :" + response);	
				
				IgFeedModel feedModel = new IgFeedModel(response);					
				
				return feedModel;
			}catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}			
		};
		
		@Override
		protected void onPostExecute(IgFeedModel feedModel) {
			
			if (null != mPrgrsDlg && mPrgrsDlg.isShowing())
				mPrgrsDlg.dismiss();
			
			if (null != feedModel) {
				mFeedListener.onIgFeedsFetched(feedModel.getUserIgFeeds(),
						feedModel.getIgNxtPageUrl());
			} else {
				mFeedListener.onIgFeedsFetched(null, null);
			}
		};
	}
	
	/**
     * Async Task to get the Comments on a Media.  
     */
    
	class GetCmntOnMediaTask extends AsyncTask<String, IgCommentModel, IgCommentModel>{
		
		IFetchCmntsListener mCmntListener;
		public GetCmntOnMediaTask(){}
		
		public GetCmntOnMediaTask(IFetchCmntsListener listener){
			mCmntListener = listener;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mPrgrsDlg.setMessage("Getting comments on media ...");
			// If activity is finishing , don't show the dialog
		    // as it will cause bad window token exception
		    if (!((Activity) mContext).isFinishing())
			mPrgrsDlg.show();
		}
		
		@Override
		protected IgCommentModel doInBackground(String... params) {
			
			Log.d(TAG, "Fetching comments on media");

			try {
				URL url = new URL(API_URL + "/media/" + params[0] +"/comments"
						+ "/?access_token=" + getAccessToken());

				Log.d(TAG, "Opening URL " + url.toString());
				HttpURLConnection urlConnection = (HttpURLConnection) url
						.openConnection();
				urlConnection.setRequestMethod(HTTP_GET);
				// urlConnection.setDoInput(true);
				// urlConnection.setDoOutput(true);
				urlConnection.connect();
				String response = streamToString(urlConnection.getInputStream());

				Log.d(TAG, "Fetch comment response :" + response);

				IgCommentModel cmntModel = new IgCommentModel(response);				
				return cmntModel;
				
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}		
			
		}
		
		@Override
		protected void onPostExecute(IgCommentModel model) {
			
			if (null != mPrgrsDlg && mPrgrsDlg.isShowing())
				mPrgrsDlg.dismiss();
            
            if(null != model){
            	mCmntListener.onIgCmntsFetched(model.getCommentList());
            }else{
            	mCmntListener.onIgCmntsFailed();
            }
		}
	}
	
	/**
	 * Asynctask to post like on a media.
	 * 
	 * @author ritesh
	 *
	 */
	class PostLikeOnMediaTask extends AsyncTask<String, Integer, Integer>{
		
		ILikeCmntListener mLikeListener;
		int mReqType;
		
		public PostLikeOnMediaTask(){}
		
		public PostLikeOnMediaTask(ILikeCmntListener listener) {
			mLikeListener = listener;
		}
		
		public PostLikeOnMediaTask(int reqType, ILikeCmntListener listener) {
			this(listener);
			mReqType = reqType;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mPrgrsDlg.setMessage("Posting like on media ...");
			// If activity is finishing , don't show the dialog
		    // as it will cause bad window token exception
		    if (!((Activity) mContext).isFinishing())
			mPrgrsDlg.show();
		}
		
		@Override
		protected Integer doInBackground(String... params) {
		
			Log.d(TAG, "Posting like on media");

			try {
				
				URL url = new URL(API_URL + "/media/" + params[0] + "/likes");

				Log.d(TAG, "Opening URL " + url.toString());
				HttpURLConnection urlConnection = (HttpURLConnection) url
						.openConnection();
				urlConnection.setRequestMethod(HTTP_POST);				
				// urlConnection.setDoInput(true);
				// urlConnection.setDoOutput(true);
				
				/*List<NameValuePair> postParams = new ArrayList<NameValuePair>();
				postParams.add(new BasicNameValuePair("access_token", getAccessToken()));
				
				OutputStream os = urlConnection.getOutputStream();
				BufferedWriter writer = new BufferedWriter(
				        new OutputStreamWriter(os, "UTF-8"));
				writer.write(getQuery(postParams));
				writer.flush();
				writer.close();
				os.close();
				
				urlConnection.connect();*/
				
				OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
				writer.write("access_token="+getAccessToken());				
				
			    writer.flush();
				
				String response = streamToString(urlConnection.getInputStream());

				Log.d(TAG, "Post like response :" + response);	
				
                JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
							
				int responseCode = jsonObj.getJSONObject("meta").getInt("code");
				
				return responseCode;
			} catch (Exception ex) {
				ex.printStackTrace();
				return 0;
			}			
		}
		
		@Override
		protected void onPostExecute(Integer responseCode) {
			
			if (null != mPrgrsDlg && mPrgrsDlg.isShowing())
            mPrgrsDlg.dismiss();
			
			mLikeListener.onCopmplete(mReqType,responseCode);
		}
	}
	
	/**
	 * Asynctask to post comment on a media.
	 * 
	 * @author ritesh
	 *
	 */
	class PostCmntOnMediaTask extends AsyncTask<String, Integer, Integer>{
		
		ILikeCmntListener mCmntListener;
		int mReqType;
		String mCmntTxt;
		
		public PostCmntOnMediaTask(){}
		
		public PostCmntOnMediaTask(ILikeCmntListener listener) {
			mCmntListener = listener;
		}
		
		public PostCmntOnMediaTask(int reqType,String cmntTxt,ILikeCmntListener listener) {
		    this(listener);
		    mReqType = reqType;
		    mCmntTxt = cmntTxt;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mPrgrsDlg.setMessage("Posting Commment on media ...");
			// If activity is finishing , don't show the dialog
		    // as it will cause bad window token exception
		    if (!((Activity) mContext).isFinishing())
			mPrgrsDlg.show();
		}
		
		@Override
		protected Integer doInBackground(String... params) {
		
			Log.d(TAG, "Posting cmnt on media");

			try {
				
				URL url = new URL(API_URL + "/media/" + params[0] + "/comments");

				Log.d(TAG, "Opening URL " + url.toString());
				HttpURLConnection urlConnection = (HttpURLConnection) url
						.openConnection();
				urlConnection.setRequestMethod(HTTP_POST);				
				// urlConnection.setDoInput(true);
				// urlConnection.setDoOutput(true);				
				
				OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
				writer.write("access_token="+getAccessToken()+
						"&text="+mCmntTxt);				
				
			    writer.flush();
				
				String response = streamToString(urlConnection.getInputStream());

				Log.d(TAG, "Post cmnt response :" + response);	
				
                JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
							
				int responseCode = jsonObj.getJSONObject("meta").getInt("code");
				
				return responseCode;
			} catch (Exception ex) {
				ex.printStackTrace();
				return 0;
			}			
		}
		
		@Override
		protected void onPostExecute(Integer responseCode) {
			
			if (null != mPrgrsDlg && mPrgrsDlg.isShowing())
				mPrgrsDlg.dismiss();

			mCmntListener.onCopmplete(mReqType, responseCode);
		}
	}
	
	/**
	 * Asynctask to undo like on a media.
	 * 
	 * @author ritesh
	 *
	 */
	class DeleteLikeOnPostTask extends AsyncTask<String, Integer, Integer>{
		
		ILikeCmntListener mCmntListener;
		int mReqType;
		
		public DeleteLikeOnPostTask(){}
		
		public DeleteLikeOnPostTask(ILikeCmntListener listener) {
			mCmntListener = listener;
		}
		
		public DeleteLikeOnPostTask(int reqType,ILikeCmntListener listener) {
		    this(listener);
		    mReqType = reqType;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mPrgrsDlg.setMessage("Undoing like on media ...");
			// If activity is finishing , don't show the dialog
		    // as it will cause bad window token exception
		    if (!((Activity) mContext).isFinishing())
			mPrgrsDlg.show();
		}
		
		@Override
		protected Integer doInBackground(String... params) {
			
			Log.d(TAG, "Undoing like on media");

			try {
				URL url = new URL(API_URL + "/media/" + params[0] +"/likes"
						+ "/?access_token=" + getAccessToken());

				Log.d(TAG, "Opening URL " + url.toString());
				HttpURLConnection urlConnection = (HttpURLConnection) url
						.openConnection();
				urlConnection.setRequestMethod(HTTP_DELETE);
				// urlConnection.setDoInput(true);
				// urlConnection.setDoOutput(true);
				urlConnection.connect();
				
				String response = streamToString(urlConnection.getInputStream());
				
                Log.d(TAG, "Undo like response :" + response);	
				
                JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
							
				int responseCode = jsonObj.getJSONObject("meta").getInt("code");
				
				return responseCode;
				
			} catch (Exception ex) {
				ex.printStackTrace();
				return 0;
			}	
			
		}
		
		@Override
		protected void onPostExecute(Integer responseCode) {
			
			if (null != mPrgrsDlg && mPrgrsDlg.isShowing())
				mPrgrsDlg.dismiss();

			mCmntListener.onCopmplete(mReqType, responseCode);
		}
	}
	
	/**
	 * To convert Stream to String.
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
    private String streamToString(InputStream is) throws IOException {
		String str = "";

		if (is != null) {
			StringBuilder sb = new StringBuilder();
			String line;

			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is));

				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}

				reader.close();
			} finally {
				is.close();
			}

			str = sb.toString();
		}

		return str;
	}
    
	/*private String getQuery(List<NameValuePair> params)
			throws UnsupportedEncodingException {
		StringBuilder result = new StringBuilder();
		boolean first = true;

		for (NameValuePair pair : params) {
			if (first)
				first = false;
			else
				result.append("&");

			result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
			result.append("=");
			result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
		}

		return result.toString();
	}*/
}
