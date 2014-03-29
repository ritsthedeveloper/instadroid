
/*! * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * @File:
 *		STAVideoController.java
 * @Project:
 *		Stardom
 * @Abstract:
 *		
 * @Copyright:
 *     		Copyright © 2012-2013, Viacom 18 Media Pvt. Ltd 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/*! Revision history (Most recent first)
 Created by ritesh on 13-Jan-2014
 */

package com.ritesh.instagram.video;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.ritesh.instagram.example.R;
import com.ritesh.instagram.utils.AppUtils;

/**
 * @author ritesh
 *
 */
public class VideoController extends FrameLayout implements  View.OnClickListener{
    private static final String TAG = "VideoController";
    private boolean mShowing;
    private boolean mDragging;
    private static final int sDefaultTimeout = 0;
    private static final int FADE_OUT = 1;
    private static final int SHOW_PROGRESS = 2;

    private Context mContext;

    private MediaPlayerControl mPlayer;
    private ViewGroup mAnchor;
    private View mRoot;
    private ProgressBar mProgress;
    private TextView mEndTime, mCurrentTime;
    
    StringBuilder mFormatBuilder;
    Formatter mFormatter;

    private Handler mHandler = new MessageHandler(this);
    private FullScreenToggleListener mScreenToggleListener;

    private ImageView playButton;
    private ImageView zoomButton;// fullscreenButton
    private Context mActivity;
    
    // Parent Activity view 
    private View mSceenRootView;
    private ArrayList<Integer> mListViewId;
    
    private boolean mVidFullScreen = false;    

	public interface FullScreenToggleListener {
		void ToggleScreen();
	}

	public VideoController(Context context, AttributeSet attrs) {
		super(context, attrs);
		mRoot = null;
		mContext = context;
	}

	public VideoController(Context context, boolean useFastForward) {
		super(context);
		mContext = context;
	}

	public VideoController(Context context) {
		this(context, true);
	}
    
    /**
     * To set the duration of video.
     */
    public void setVideoDuartion(String vidDuration) {	
	
	if (null != vidDuration) {
	    // Displaying Total Duration time
	    mEndTime.setText(getFormatedDuration(vidDuration));
	} else {
	    // Displaying Total Duration time
	    mEndTime.setText("00:00");
	}
    }
    
    /**
     * To get the video duration formatted in hh:mm:ss format.
     */
	private String getFormatedDuration(String period) {

		String formattedTime = "";
		String[] timeArray = period.split(":");

		if (Integer.parseInt(timeArray[0]) > 0) {
			formattedTime = timeArray[0] + ":" + timeArray[1] + ":"
					+ timeArray[2].substring(0, 2);
		} else {
			formattedTime = timeArray[1] + ":" + timeArray[2].substring(0, 2);
		}

		return formattedTime;
	}
    
    /**
     * To get the screen status whether it is full screen or not.
     * @return
     */
    public boolean ismVidFullScreen() {
        return mVidFullScreen;
    }   

    /**
     * Set the Mediaplayer
     * @param player
     */
	public void setMediaPlayer(MediaPlayerControl player) {
		mPlayer = player;
		updatePausePlay();
		updateFullScreen();
	}

	public void setFullScreenToggleListener(FullScreenToggleListener listener) {
		mScreenToggleListener = listener;
	}
    
    /**
     * Set the screen mode image to Shrink image, when screen is 
     * in Fullscreen mode, to indicate user on pressing the video
     * will be back to default size. 
     */
	public void setShrinkVideoImage() {
		if (null != zoomButton) {
			zoomButton.setImageResource(R.drawable.ic_media_fullscreen_shrink);
			zoomButton.setTag(2);
		}
	}
    
    /**
     * Set the screen mode image to Stretch image, when screen is 
     * in normal mode, to indicate user on pressing the video
     * will be fullscreen. 
     */
	public void setFullScreenVideoImage() {
		if (null != zoomButton) {
			zoomButton.setImageResource(R.drawable.ic_media_fullscreen_stretch);
			zoomButton.setTag(1);
		}
	}
    
	public void setFullScreenImageVisibility(int visibility) {
		if (null != zoomButton) {
			zoomButton.setVisibility(visibility);
		}
	}
    
    /**
     * To hide the other views in parent to make video fullscreen.
     * @param Root - Parent view
     * @param list - list of the views in parent to be made invisible.
     */
	public void setScreenViewId(View Root, ArrayList<Integer> list) {
		mSceenRootView = Root;
		mListViewId = list;
	}

	public void setActivityContext(Activity actContext) {
		mActivity = actContext;
	}
    
    @Override
	public void onFinishInflate() {
		if (mRoot != null)
			initControllerView(mRoot);
	}
    
    /**
     * Set the view that acts as the anchor for the control view. This can for
     * example be a VideoView, or your Activity's main view.
     * 
     * @param view
     *            The view to which to anchor the controller when it is visible.
     */
	public void setAnchorView(ViewGroup view) {
		mAnchor = view;

		FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);

		removeAllViews();
		View v = makeControllerView();
		addView(v, frameParams);
	}

    /**
     * Create the view that holds the widgets that control playback. Derived
     * classes can override this to create their own.
     * 
     * @return The controller view.
     * @hide This doesn't work as advertised
     */
	protected View makeControllerView() {

		LayoutInflater inflate = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mRoot = inflate.inflate(R.layout.view_youtube_player_ctrls, null);
		initControllerView(mRoot);
		return mRoot;
	}

    /**
     * Init the views in Video controller
     * @param v - parent view of controller
     */
    private void initControllerView(View v) {

	playButton = (ImageView) v.findViewById(R.id.play_button);
	if (playButton != null) {
	    playButton.requestFocus();
	    playButton.setOnClickListener(mPauseListener);
	}
	zoomButton = (ImageView) v.findViewById(R.id.img_full_screen);
	if (zoomButton != null) {
	    zoomButton.requestFocus();
	    zoomButton.setOnClickListener(mFullscreenListener);
	}
	mProgress = (ProgressBar) v.findViewById(R.id.mediacontroller_progress);
	if (mProgress != null) {
	    if (mProgress instanceof SeekBar) {
		SeekBar seeker = (SeekBar) mProgress;
		seeker.setOnSeekBarChangeListener(mSeekListener);
	    }
	    mProgress.setMax(1000);
	}

	mEndTime = (TextView) v.findViewById(R.id.txt_total_time);
	mCurrentTime = (TextView) v.findViewById(R.id.txt_current_time);	

	mFormatBuilder = new StringBuilder();
	mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
	
	zoomButton.setOnClickListener(this);
	zoomButton.setTag(1);
    }

    /**
     * Show the controller on screen. It will go away automatically after 3
     * seconds of inactivity.
     */
    public void show() {
	show(sDefaultTimeout);
    }

    /**
     * Disable pause or seek buttons if the stream cannot be paused or seeked.
     * This requires the control interface to be a MediaPlayerControlExt
     */
    private void disableUnsupportedButtons() {
	if (mPlayer == null) {
	    return;
	}

	try {
	    /*
	     * if (mPauseButton != null && !mPlayer.canPause()) {
	     * mPauseButton.setEnabled(false); }
	     */
	    if (playButton != null && !mPlayer.canPause()) {
		playButton.setEnabled(false);
	    }

	} catch (IncompatibleClassChangeError ex) {
	    // We were given an old version of the interface, that doesn't have
	    // the canPause/canSeekXYZ methods. This is OK, it just means we
	    // assume the media can be paused and seeked, and so we don't
	    // disable
	    // the buttons.
	}
    }

    /**
     * Show the controller on screen. It will go away automatically after
     * 'timeout' milliseconds of inactivity.
     * 
     * @param timeout
     *            The timeout in milliseconds. Use 0 to show the controller
     *            until hide() is called.
     */
    public void show(int timeout) {
	if (!mShowing && mAnchor != null) {
	    setProgress();
	    /*
	     * if (mPauseButton != null) { mPauseButton.requestFocus(); }
	     */
	    if (playButton != null) {
		playButton.requestFocus();
	    }
	    disableUnsupportedButtons();

	    FrameLayout.LayoutParams tlp = new FrameLayout.LayoutParams(
		    ViewGroup.LayoutParams.MATCH_PARENT,
		    ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);

	    mAnchor.addView(this, tlp);
	    mShowing = true;
	}
	updatePausePlay();
	updateFullScreen();

	// cause the progress bar to be updated even if mShowing
	// was already true. This happens, for example, if we're
	// paused with the progress bar showing the user hits play.
	mHandler.sendEmptyMessage(SHOW_PROGRESS);

	Message msg = mHandler.obtainMessage(FADE_OUT);
	if (timeout != 0) {
	    mHandler.removeMessages(FADE_OUT);
	    mHandler.sendMessageDelayed(msg, timeout);
	}
    }

    /**
     * To check whether the controller is 
     * currently shown or not.
     * @return - true if controller is shown.
     */
    public boolean isShowing() {
	return mShowing;
    }

    /**
     * Remove the controller from the screen.
     */
    public void hide() {
	if (mAnchor == null) {
	    return;
	}

	try {
	    mAnchor.removeView(this);
	    mHandler.removeMessages(SHOW_PROGRESS);
	} catch (IllegalArgumentException ex) {
	    AppUtils.Log("MediaController", "already removed");
	}
	mShowing = false;
    }

    /**
     * To format the time.
     * 
     * @param timeMs - time in milliseconds
     * @return formatted time .
     */
	private String stringForTime(int timeMs) {
		int totalSeconds = timeMs / 1000;

		int seconds = totalSeconds % 60;
		int minutes = (totalSeconds / 60) % 60;
		int hours = totalSeconds / 3600;

		mFormatBuilder.setLength(0);
		if (hours > 0) {
			return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds)
					.toString();
		} else {
			return mFormatter.format("%02d:%02d", minutes, seconds).toString();
		}
	}

    /**
     * To set the progress as the video is being played.
     * Update the time elapsed, position of scrollbar.
     * @return
     */
	private int setProgress() {
		if (mPlayer == null || mDragging) {
			return 0;
		}

		int position = mPlayer.getCurrentPosition();
		int duration = mPlayer.getDuration();
		if (mProgress != null) {
			if (duration > 0) {
				// use long to avoid overflow
				long pos = 1000L * position / duration;
				mProgress.setProgress((int) pos);
			}
			int percent = mPlayer.getBufferPercentage();
			mProgress.setSecondaryProgress(percent * 10);
		}

		if (mEndTime != null)
			mEndTime.setText(stringForTime(duration));
		if (mCurrentTime != null)
			mCurrentTime.setText(stringForTime(position));

		return position;
	}
    
    /**
     * To reset the current time displayed to 0.
     * eg. On completion of video.
     */
	public void resetCurrentTime() {
		if (mCurrentTime != null)
			mCurrentTime.setText(stringForTime(0));
	}
    
    /**
     * To reset the play/pause button to play state.
     * eg. On completion of video.
     */
	public void resetPlayButton(boolean toPlayState) {
		if (toPlayState)
			playButton.setImageResource(R.drawable.ic_flicks_play);
		else
			playButton.setImageResource(R.drawable.ic_flicks_pause_normal);

		setEnabled(true);
	}
    
    /**
     * To reset full/shrink screen button to stretch mode.
     * eg. when orientation changes.
     */
	public void resetFullScrnBtn(boolean fullScreenBtn) {

		if (fullScreenBtn) {
			zoomButton.setImageResource(R.drawable.ic_media_fullscreen_stretch);
			zoomButton.setTag(1);
		} else {
			zoomButton.setImageResource(R.drawable.ic_media_fullscreen_shrink);
			zoomButton.setTag(2);
		}
	}
    
    /**
     * To reset the progress indicatior back to start position. 
     */
    
    public void resetProgress(){
	mProgress.setProgress(0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
	show(sDefaultTimeout);
	return true;
    }

    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
	show(sDefaultTimeout);
	return false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
	if (mPlayer == null) {
	    return true;
	}

	int keyCode = event.getKeyCode();
	final boolean uniqueDown = event.getRepeatCount() == 0
		&& event.getAction() == KeyEvent.ACTION_DOWN;
	if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK
		|| keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
		|| keyCode == KeyEvent.KEYCODE_SPACE) {
	    if (uniqueDown) {
		doPauseResume();
		show(sDefaultTimeout);
		/*
		 * if (mPauseButton != null) { mPauseButton.requestFocus(); }
		 */
		if (playButton != null) {
		    playButton.requestFocus();
		}
	    }
	    return true;
	}/*
	  * else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) { if (uniqueDown &&
	  * !mPlayer.isPlaying()) { mPlayer.start(); updatePausePlay();
	  * show(sDefaultTimeout); } return true; } else if (keyCode ==
	  * KeyEvent.KEYCODE_MEDIA_STOP || keyCode ==
	  * KeyEvent.KEYCODE_MEDIA_PAUSE) { if (uniqueDown &&
	  * mPlayer.isPlaying()) { mPlayer.pause(); updatePausePlay();
	  * show(sDefaultTimeout); } return true; } else if (keyCode ==
	  * KeyEvent.KEYCODE_VOLUME_DOWN || keyCode ==
	  * KeyEvent.KEYCODE_VOLUME_UP || keyCode ==
	  * KeyEvent.KEYCODE_VOLUME_MUTE) { // don't show the controls for
	  * volume adjustment return super.dispatchKeyEvent(event); }
	  */else if (keyCode == KeyEvent.KEYCODE_BACK
		|| keyCode == KeyEvent.KEYCODE_MENU) {
	    if (uniqueDown) {
		hide();
	    }
	    return true;
	}

	show(sDefaultTimeout);
	return super.dispatchKeyEvent(event);
    }

    private View.OnClickListener mPauseListener = new View.OnClickListener() {
	public void onClick(View v) {
	    doPauseResume();
	    show(sDefaultTimeout);
	}
    };

    private View.OnClickListener mFullscreenListener = new View.OnClickListener() {
	public void onClick(View v) {
	    doToggleFullscreen();
	    show(sDefaultTimeout);
	}
    };

    public void updatePausePlay() {

	if (mRoot == null || playButton == null || mPlayer == null) {
	    return;
	}

	if (mPlayer.isPlaying()) {
	    playButton.setImageResource(R.drawable.ic_flicks_pause_normal);
	} else {
	    playButton.setImageResource(R.drawable.ic_flicks_play);
	}
    }
    
    public void updateFullScreen() {
	/*
	 * if (mRoot == null || mFullscreenButton == null || mPlayer == null) {
	 * return; }
	 * 
	 * if (mPlayer.isFullScreen()) {
	 * mFullscreenButton.setImageResource(R.drawable.full_screen); } else {
	 * mFullscreenButton.setImageResource(R.drawable.full_screen); }
	 */
	if (mRoot == null || zoomButton == null || mPlayer == null) {
	    return;
	}
    }
    
    public void updateFullScreen(boolean isFullScreen){
	updateFullScreenView(isFullScreen);
    }

    private void doPauseResume() {
	if (mPlayer == null) {
	    return;
	}

	if (mPlayer.isPlaying()) {
	    mPlayer.pause();
	} else {
	    mPlayer.start();
	}
	updatePausePlay();
    }

    private void doToggleFullscreen() {
	if (mPlayer == null) {
	    return;
	}
	if (mScreenToggleListener != null)
	    mScreenToggleListener.ToggleScreen();	
    }
    
    private void updateFullScreenView(boolean isFullScreen) {
	FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(
		ViewGroup.LayoutParams.MATCH_PARENT,
		ViewGroup.LayoutParams.MATCH_PARENT);
	

	// When in fullscreen, the visibility of all other views than the player
	// should be set to GONE and the player should be laid out 
	// across the whole screen.
	if (isFullScreen) {   
	    
	    // set true to handle backpress in Parent.
	    mVidFullScreen = true;
	 		
	    updateScreenUi(View.GONE);
	    frameParams.width = LayoutParams.MATCH_PARENT;
	    frameParams.height = LayoutParams.MATCH_PARENT;    
	    
	} else {
	    
	    // set false to handle backpress in Parent.
	    mVidFullScreen = false;
	 		
	    updateScreenUi(View.VISIBLE);
	    frameParams.width = LayoutParams.WRAP_CONTENT;
	    frameParams.height = LayoutParams.WRAP_CONTENT;	    
	}
    }
    
    private void updateScreenUi(int visibility) {
	View view;

	if (mListViewId == null) {
	    /*Toast.makeText(
		    mContext,
		    "Please call setScreenViewId from your Activity "
		    + "to enable full screen",
		    Toast.LENGTH_SHORT).show();*/
	    return;
	}

	for (int id : mListViewId) {
	    view = mSceenRootView.findViewById(id);
	    if (null != view)
		view.setVisibility(visibility);
	}
    }

    // There are two scenarios that can trigger the seekbar listener to trigger:
    //
    // The first is the user using the touchpad to adjust the posititon of the
    // seekbar's thumb. In this case onStartTrackingTouch is called followed by
    // a number of onProgressChanged notifications, concluded by
    // onStopTrackingTouch.
    // We're setting the field "mDragging" to true for the duration of the
    // dragging
    // session to avoid jumps in the position in case of ongoing playback.
    //
    // The second scenario involves the user operating the scroll ball, in this
    // case there WON'T BE onStartTrackingTouch/onStopTrackingTouch
    // notifications,
    // we will simply apply the updated position without suspending regular
    // updates.
    private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
	public void onStartTrackingTouch(SeekBar bar) {
	    show(3600000);

	    mDragging = true;

	    // By removing these pending progress messages we make sure
	    // that a) we won't update the progress while the user adjusts
	    // the seekbar and b) once the user is done dragging the thumb
	    // we will post one of these messages to the queue again and
	    // this ensures that there will be exactly one message queued up.
	    mHandler.removeMessages(SHOW_PROGRESS);
	}

	public void onProgressChanged(SeekBar bar, int progress,
		boolean fromuser) {
	    if (mPlayer == null) {
		return;
	    }

	    if (!fromuser) {
		// We're not interested in programmatically generated changes to
		// the progress bar's position.
		return;
	    }

	    long duration = mPlayer.getDuration();
	    long newposition = (duration * progress) / 1000L;
	    mPlayer.seekTo((int) newposition);
	    if (mCurrentTime != null)
		mCurrentTime.setText(stringForTime((int) newposition));
	}

	public void onStopTrackingTouch(SeekBar bar) {
	    mDragging = false;
	    setProgress();
	    updatePausePlay();
	    show(sDefaultTimeout);

	    // Ensure that progress is properly updated in the future,
	    // the call to show() does not guarantee this because it is a
	    // no-op if we are already showing.
	    mHandler.sendEmptyMessage(SHOW_PROGRESS);
	}
    };

    @Override
    public void setEnabled(boolean enabled) {
	/*
	 * if (mPauseButton != null) { mPauseButton.setEnabled(enabled); }
	 */
	if (playButton != null) {
	    playButton.setEnabled(enabled);
	}
	if (mProgress != null) {
	    mProgress.setEnabled(enabled);
	}
	disableUnsupportedButtons();
	super.setEnabled(enabled);
    }

	private static class MessageHandler extends Handler {
		private final WeakReference<VideoController> mView;

		MessageHandler(VideoController view) {
			mView = new WeakReference<VideoController>(view);
		}

		@Override
		public void handleMessage(Message msg) {
			VideoController view = mView.get();
			if (view == null || view.mPlayer == null) {
				return;
			}

			int pos;
			switch (msg.what) {
			case FADE_OUT:
				view.hide();
				break;
			case SHOW_PROGRESS:
				pos = view.setProgress();
				if (!view.mDragging && view.mShowing
						&& view.mPlayer.isPlaying()) {
					msg = obtainMessage(SHOW_PROGRESS);
					sendMessageDelayed(msg, 1000 - (pos % 1000));
				}
				break;
			}
		}
	}
    
    /*public void doFullScreenAtLaunch(boolean fullScreen) {

	zoomButton.setImageResource(R.drawable.ic_media_fullscreen_shrink);
	zoomButton.setTag(2);

	updateFullScreenView(true);

	if (Configuration.ORIENTATION_PORTRAIT == getResources()
		.getConfiguration().orientation) {
	    ((Activity) mActivity)
		    .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}
    }*/
    
    
    
    @Override
    public void onClick(View v) {
	switch (v.getId()) {
	case R.id.img_full_screen:
	    if ((Integer) v.getTag() == 1) {
		
		((ImageView) v)
			.setImageResource(R.drawable.ic_media_fullscreen_shrink);
		v.setTag(2);	
		    
		updateFullScreenView(true);
		
		if (Configuration.ORIENTATION_PORTRAIT == getResources()
			.getConfiguration().orientation) {
		    ((Activity) mActivity)
			    .setRequestedOrientation(ActivityInfo
				    .SCREEN_ORIENTATION_LANDSCAPE);
		}
		
	    } else {
		
		((ImageView) v)
			.setImageResource(R.drawable.ic_media_fullscreen_stretch);
		v.setTag(1);	
		
		updateFullScreenView(false);
		
		if ((Configuration.ORIENTATION_LANDSCAPE == getResources()
			.getConfiguration().orientation)
			&& !isTablet(mActivity)) {
		    ((Activity) mActivity)
			    .setRequestedOrientation(ActivityInfo
				    .SCREEN_ORIENTATION_PORTRAIT);

		}
	    }
	    
	    break;
	}
    }
    
    // To identify whether device is tablet or phone.
    public boolean isTablet(Context context) {
	boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
	boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
	return (xlarge || large);
    }
}

