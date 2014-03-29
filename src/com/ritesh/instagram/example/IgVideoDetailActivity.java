package com.ritesh.instagram.example;

import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.ritesh.instagram.video.VideoController;
import com.ritesh.instagram.video.VideoView;

public class IgVideoDetailActivity extends FragmentActivity implements
		OnPreparedListener, OnErrorListener, OnCompletionListener {

	private VideoView mVidView;
	private VideoController mVidController;

	private String mVidUrl;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Always landscape irrespective of device type.
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		setContentView(R.layout.activity_ig_vid_detail);

		initViews();

		// Get the video info from Extras
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			if (bundle.containsKey("video_url")) {
				mVidUrl = bundle.getString("video_url");
				setUpVideoView();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// TODO: Show progress bar

		mVidView.start();
	}

	/**
	 * initialize the views
	 */

	private void initViews() {
		mVidView = (VideoView) findViewById(R.id.video);
	}

	/*
	 * Prepare the video player and pass the video url.
	 */
	private void setUpVideoView() {

		Uri uri = Uri.parse(mVidUrl);
		mVidController = new VideoController(this, false);
		mVidController.setActivityContext(this);

		if (null != mVidView) {
			mVidView.setMediaController(mVidController);
			mVidView.setVideoURI(uri);
			mVidView.setOnPreparedListener(this);
			mVidView.setOnCompletionListener(this);
			mVidView.setmFlksVid(true);
		}
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		if (mVidController != null) {
			if (mVidController.isShowing())
				mVidController.hide();
			mVidController.show();
			mVidController.setFullScreenImageVisibility(View.INVISIBLE);
		}

		// TODO: Hide progress bar
	}

	@Override
	public void onCompletion(MediaPlayer mp) {

		// TODO: Show progress bar

		mVidController = null;

		Uri uri = Uri.parse(mVidUrl);
		mVidController = new VideoController(this, false);
		mVidController.setFullScreenImageVisibility(View.GONE);
		mVidController.setActivityContext(this);

		if (null != mVidView) {

			mVidView.setMediaController(mVidController);
			mVidView.setVideoURI(uri);
			mVidView.setmFlksVid(true);
			mVidView.seekTo(0);
			mVidView.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer mp) {

					// TODO: Hide progress bar

					mVidView.pause();

					if (mVidController != null) {
						mVidController.show();
						mVidController
								.setFullScreenImageVisibility(View.INVISIBLE);
					}
				}
			});
			mVidView.setOnCompletionListener(this);
		}
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		return false;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (null != mVidController && mVidController.isShowing())
			mVidController.hide();

		mVidView.pause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (null != mVidView)
			mVidView.stopPlayback();

		mVidView = null;
		mVidController = null;
	}
}
