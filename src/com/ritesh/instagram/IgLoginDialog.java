package com.ritesh.instagram;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ritesh.instagram.example.R;

/**
 * 
 * @author ritesh
 * 
 */

public class IgLoginDialog extends Dialog {

	private static final String TAG = "Instagram-WebView";
	
	static final FrameLayout.LayoutParams FILL = new FrameLayout.LayoutParams(
			ViewGroup.LayoutParams.MATCH_PARENT,
			ViewGroup.LayoutParams.MATCH_PARENT);
	static final int MARGIN = 4;
	static final int PADDING = 2;

	private Context mContext;
	private LinearLayout mContent;
	private WebView mWebView;
	private TextView mTitle;
	private ProgressDialog mPrgrsDlg;
	
	private OAuthDialogListener mListener;
	
	private String mUrl;

	public IgLoginDialog(Context context, String url,
			OAuthDialogListener listener) {
		super(context);
		mContext = context;
		mUrl = url;
		mListener = listener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mPrgrsDlg = new ProgressDialog(getContext());
		mPrgrsDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mPrgrsDlg.setMessage("Loading...");
		
		mContent = new LinearLayout(getContext());
		mContent.setOrientation(LinearLayout.VERTICAL);
		
		setUpTitle();
		setUpWebView();

		Display display = getWindow().getWindowManager().getDefaultDisplay();
		
		double[] dimensions = new double[2];

		if (display.getWidth() < display.getHeight()) {
		    dimensions[0] = 0.87 * display.getWidth();
		    dimensions[1] = 0.82 * display.getHeight();
		} else {
		    dimensions[0] = 0.75 * display.getWidth();
		    dimensions[1] = 0.75 * display.getHeight();
		}

		addContentView(mContent, new FrameLayout.LayoutParams(
			(int) dimensions[0], (int) dimensions[1]));		
	}

	/**
	 * Title for the webview within dialog.
	 */
	private void setUpTitle() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		Drawable icon = getContext().getResources().getDrawable(
				R.drawable.icon);
		
		mTitle = new TextView(getContext());
		mTitle.setText("Instagram");
		mTitle.setTextColor(Color.WHITE);
		mTitle.setTypeface(Typeface.DEFAULT_BOLD);
		mTitle.setBackgroundColor(Color.BLACK);
		mTitle.setPadding(MARGIN + PADDING, MARGIN, MARGIN, MARGIN);
		mTitle.setCompoundDrawablePadding(MARGIN + PADDING);
		mTitle.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
		
		mContent.addView(mTitle);
	}

	/**
	 * WebView to display login page.
	 */
	private void setUpWebView() {
		mWebView = new WebView(getContext());
		mWebView.setVerticalScrollBarEnabled(false);
		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.setWebViewClient(new OAuthWebViewClient());
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.loadUrl(mUrl);
		mWebView.setLayoutParams(FILL);
		
		mContent.addView(mWebView);
	}	

	private class OAuthWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.d(TAG, "Redirecting URL " + url);

			if (url.startsWith(IgConstants.CALLBACK_URL)) {
				String urls[] = url.split("=");
				mListener.onComplete(urls[1]);
				IgLoginDialog.this.dismiss();
				return true;
			}
			return false;
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			Log.d(TAG, "Page error: " + description);

			super.onReceivedError(view, errorCode, description, failingUrl);
			mListener.onError(description);
			IgLoginDialog.this.dismiss();
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			Log.d(TAG, "Loading URL: " + url);

			super.onPageStarted(view, url, favicon);
//			mPrgrsDlg.show();
			
			// Display the progress dialog, as the page loading started.
			if (null == mPrgrsDlg)
				mPrgrsDlg = new ProgressDialog(mContext);
			if (!mPrgrsDlg.isShowing()) {
				if (!((Activity) mContext).isFinishing())
					mPrgrsDlg.show();
			}
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			
			String title = mWebView.getTitle();
			if (title != null && title.length() > 0) {
				mTitle.setText(title);
			}
			Log.d(TAG, "onPageFinished URL: " + url);
//			mPrgrsDlg.dismiss();
			
			// Dismiss the progress dlg as the page loading is finished.
			if (null != mPrgrsDlg && mPrgrsDlg.isShowing()) {

				try {
					mPrgrsDlg.dismiss();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				}
			}

			mPrgrsDlg = null;			
		}
	}
	
	@Override
	public void onBackPressed() {

		// This listener is used exclusively for login process,
		// to finish LoginActitiy.
		mListener.onIgLoginDlgBackPressed(true);
		super.onBackPressed();
	}

	public interface OAuthDialogListener {
		public abstract void onComplete(String accessToken);
		public abstract void onError(String error);
		public void onIgLoginDlgBackPressed(boolean loginInterrupted);
	}
	
	// TODO: To dismiss the logindialog on backpress of progress dialog,
	// implement custom progressdialog with an interface to send backpress callback.

}