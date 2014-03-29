package com.ritesh.instagram.example;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.ritesh.instagram.IgActivity;
import com.ritesh.instagram.IgConstants;
import com.ritesh.instagram.IgWrapper;
import com.ritesh.instagram.listener.IAuthenticationListener;

/**
 * Displays the various menus.
 * 
 * @author ritesh
 *
 */
public class MainActivity extends FragmentActivity implements OnClickListener {

	private Button mBtnLogin;
	private Button mBtnUserInfo;
	private Button mBtnUserPhoto;	
	
	private IgWrapper mIgWrapper;
	
	// test values
//	private String userId = "1108295922"; // id for testing video - "16183584";
	private String userId = "16183584";
	private String mediaId = "662788283210559156_1108295922";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);		

		mBtnLogin = (Button) findViewById(R.id.btn_login);
		mBtnUserInfo = (Button) findViewById(R.id.btn_user_info);
		mBtnUserPhoto = (Button) findViewById(R.id.btn_user_photo);		
		
		mIgWrapper = new IgWrapper(this);
		
		if (mIgWrapper.isLoggedIn()) {
			mBtnLogin.setText(getResources().getString(R.string.lbl_logout));
		}
		
		mBtnLogin.setOnClickListener(this);
		mBtnUserInfo.setOnClickListener(this);
		mBtnUserPhoto.setOnClickListener(this);		
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.btn_login) {

			if (mBtnLogin.getText().equals(
					getResources().getString(R.string.lbl_logout))) {
				// To logout instagram 
				mIgWrapper.logoutInstagram();
				
			} else {
				// To login instagram 
				Intent intent = new Intent(MainActivity.this,
						IgActivity.class);
				intent.putExtra(IgConstants.IG_REQUEST,
						IgConstants.IG_REQ_LOGIN);
				
				IgActivity.setIgAuthListener(mAuthListener);
				
				startActivity(intent);
			}

		} else if (v.getId() == R.id.btn_user_info) {
			
			// IG user info
			Intent intent = new Intent(MainActivity.this,
					IgActivity.class);
			intent.putExtra(IgConstants.IG_REQUEST,
					IgConstants.IG_REQ_USER_INFO);
			intent.putExtra(IgActivity.KEY_USER_ID, userId);
			startActivity(intent);
		}
		else if (v.getId() == R.id.btn_user_photo) {

			// To fetch the instagram feeds.
			Intent intent = new Intent(MainActivity.this,
					IgFeedActivity.class);			
			intent.putExtra(IgActivity.KEY_USER_ID, userId);
			startActivity(intent);
		}		
	}
	
	/**
	 * Authentication success or failure listener.
	 */
	private final IAuthenticationListener mAuthListener = new IAuthenticationListener() {

		@Override
		public void onAuthSuccess() {
			Log.d(IgConstants.TAG, "In IG Auth success. MainAct.");
			mBtnLogin.setText(getResources().getString(R.string.lbl_logout));
		}
		
		@Override
		public void onAuthFail(String error) {
			Log.d(IgConstants.TAG, "Auth Failure Error, MainAct:" + error);		
			mBtnLogin.setText("Login Failed, try again");
		}
	};
}