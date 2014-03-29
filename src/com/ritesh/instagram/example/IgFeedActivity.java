package com.ritesh.instagram.example;


import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.ritesh.instagram.IgActivity;
import com.ritesh.instagram.IgConstants;
import com.ritesh.instagram.IgWrapper;
import com.ritesh.instagram.adapter.IgFeedAdapter;
import com.ritesh.instagram.holder.IgFeedHolder;
import com.ritesh.instagram.listener.IFetchIgFeedsListener;

/**
 * To display all the feeds
 * 
 * @author ritesh
 *
 */
public class IgFeedActivity extends FragmentActivity{

	private ListView mLstVwFeeds;
	private View mEmptyView;
	
	private ArrayList<IgFeedHolder> mIgFeedList;
	private IgFeedAdapter mFeedAdapter;	
	
	private String mIgUserId; 
    private String mIgNxtPageUrl = null;
    private int mFeedCount = 20; // default feed count 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ig_feeds);
		init();
	}
	
	private void init(){
		
		mLstVwFeeds = (ListView) findViewById(R.id.lst_ig_feeds);
		mEmptyView = findViewById(R.id.txt_empty_view);			
	}
	
	@Override
    public void onResume() {
        super.onResume();
        
		if ((mFeedAdapter == null || mFeedAdapter.getCount() <= 0))
			getInstaFeeds();     
    }
	
	private void getInstaFeeds(){
		
		mIgUserId = getIntent().getStringExtra(IgActivity.KEY_USER_ID);
		IgWrapper wrapper = new IgWrapper(this);
		wrapper.getUserFeeds(mIgUserId, mFeedCount, mIgNxtPageUrl,
				mUserFeedsListener);
	}
	
	/**
	 * User Feeds success or failure listener.
	 */
	private final IFetchIgFeedsListener mUserFeedsListener = new IFetchIgFeedsListener() {
				
		@Override
		public void onIgFeedsFetched(ArrayList<IgFeedHolder> feedList,
				String nxtPgUrl) {
			
			if (null != feedList) {
				Log.d(IgConstants.TAG,
						"Feeds fetched, size :" + feedList.size());
				Log.d(IgConstants.TAG, "Nxt Pg url :" + nxtPgUrl);

				mIgNxtPageUrl = nxtPgUrl;
				setIgFeedsAdapter(feedList);
			}
		}		
	};
	
	private void setIgFeedsAdapter(ArrayList<IgFeedHolder> feedList) {

		// List of all feeds to use in this activity.
		if (null == mIgFeedList){
			mIgFeedList = feedList;
		}else{
			mIgFeedList.addAll(feedList);
		}

		if (null == mFeedAdapter) {

			mFeedAdapter = new IgFeedAdapter(this, feedList);
			mLstVwFeeds.setAdapter(mFeedAdapter);
			mLstVwFeeds.setOnScrollListener(new EndlessScrollListener());
			mLstVwFeeds.setEmptyView(mEmptyView);
		} else {
			// update lists(pagination)
			mFeedAdapter.updateFeedList(feedList);
			mFeedAdapter.notifyDataSetChanged();
		}
	}
	
	/**
     * Endless scroll listener for Ig feeds.
     * @author ritesh
     *
     */
	private class EndlessScrollListener implements OnScrollListener {

		// True if we are still waiting for the last set of data to load.
		private boolean loading = true;
		// The total number of items in the dataset after the last load
		private int previousTotal = 0;

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {

			// first we check if we are waiting for the previous load to
			// finish
			if (loading) {
				// If it’s still loading, we check to see if the dataset
				// count has changed, if so we conclude it has finished
				// loading and update the and total item count.

				if (totalItemCount > previousTotal) {
					loading = false;
					previousTotal = totalItemCount;
				}
			}

			if (!loading
					&& (totalItemCount - visibleItemCount) <= (firstVisibleItem + 2)) {

				if (null != mIgNxtPageUrl) {
					getInstaFeeds();
					loading = true;
				}
			}

		}

		public void resetLoading() {
			loading = false;
		}
	}
}
