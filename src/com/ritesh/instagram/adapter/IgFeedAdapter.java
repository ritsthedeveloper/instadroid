package com.ritesh.instagram.adapter;

import java.util.ArrayList;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ritesh.instagram.IgActivity;
import com.ritesh.instagram.IgConstants;
import com.ritesh.instagram.IgWrapper;
import com.ritesh.instagram.example.IgVideoDetailActivity;
import com.ritesh.instagram.example.R;
import com.ritesh.instagram.example.fragment.IgCmntDialogFrag;
import com.ritesh.instagram.holder.IgFeedHolder;
import com.ritesh.instagram.listener.IAuthenticationListener;
import com.ritesh.instagram.listener.ILikeCmntListener;
import com.ritesh.instagram.utils.AppUtils;
import com.ritesh.instagram.utils.ImageDownloader;

public class IgFeedAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private ArrayList<IgFeedHolder> mIgFeedList;

	public IgFeedAdapter(Context context) {

		mContext = context;
		mInflater = (LayoutInflater) context
				.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
	}
	
	public IgFeedAdapter(Context context,ArrayList<IgFeedHolder> igFeedList) {
		this(context);
		mIgFeedList = igFeedList;
	}

	public void setDataSource(ArrayList<IgFeedHolder> igFeedList) {
		mIgFeedList = igFeedList;
	}
	
	public void updateFeedList(ArrayList<IgFeedHolder> igFeedList) {
		if (mIgFeedList != null) {
			mIgFeedList.addAll(igFeedList);
		} else {
			mIgFeedList = igFeedList;
		}
	}

	static class ViewHolder {
		// profile image.
		ImageView mImgVwProfile;
		// User Name
		TextView mTxtVwName;
		// Feed text
		TextView mTxtVwFeedTxt;
		// Created at
		TextView mTxtVwLog;
		TextView mTxtVwLikeCount;
		TextView mTxtVwCmntCount;
		ImageView mImgVwLike;
		ImageView mImgVwCmnt;
		ImageView mImgVwFeed;
		ImageView mImgVwPlay;
	}

	@Override
	public int getCount() {
		if (mIgFeedList == null)
			return 0;
		else
			return mIgFeedList.size();
	}

	@Override
	public Object getItem(int position) {
		return mIgFeedList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		holder = new ViewHolder();
		convertView = mInflater.inflate(R.layout.listitem_instagram_feed, null);

		holder.mImgVwProfile = (ImageView) convertView
				.findViewById(R.id.imgvw_ig_prof_pic);
		holder.mTxtVwName = (TextView) convertView
				.findViewById(R.id.txt_ig_usr_name);
		holder.mTxtVwLog = (TextView) convertView
				.findViewById(R.id.txt_ig_created_at);
		holder.mTxtVwFeedTxt = (TextView) convertView
				.findViewById(R.id.txt_ig_caption);
		holder.mTxtVwLikeCount = (TextView) convertView
				.findViewById(R.id.txt_ig_like_count);
		holder.mTxtVwCmntCount = (TextView) convertView
				.findViewById(R.id.txt_ig_cmnt_count);
		holder.mImgVwLike = (ImageView) convertView
				.findViewById(R.id.imgvw_ig_like);
		holder.mImgVwCmnt = (ImageView) convertView
				.findViewById(R.id.imgvw_ig_cmnt);
		holder.mImgVwFeed = (ImageView) convertView
				.findViewById(R.id.imgvw_ig_media);
		holder.mImgVwPlay = (ImageView) convertView
				.findViewById(R.id.imgvw_play);

		convertView.setTag(holder);
		holder = (ViewHolder) convertView.getTag();
		
		final IgFeedHolder feedHolder = mIgFeedList.get(position);
		
		// Set the profile image
		if (AppUtils.isUrlImage(feedHolder.getmUserInfo().getmUserProfPicUrl())) {

			ImageDownloader.setSquareImg(mContext,holder.mImgVwProfile,
					feedHolder.getmUserInfo().getmUserProfPicUrl(),
					R.drawable.icon,false,true);	    
		}
		
		// User Name
		holder.mTxtVwName.setText(feedHolder.getmUserInfo().getmUserName());
		
		// Created time
		long longTime = Long.parseLong(feedHolder.getmCreatedTime());
		/**
		 * Instagram returns unix time stamp for the feed's created time, which
		 * is in seconds. Hence multiplying by 1000 to convert it to
		 * milliseconds.
		 */
		longTime = longTime * 1000;
		holder.mTxtVwLog.setText(DateUtils.getRelativeTimeSpanString(longTime,
			System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS));		
		
		// Feed image
		feedHolder.getmThmbnlUrl();
		// Feed image
		if (AppUtils.isUrlImage(feedHolder.getmStdResImgUrl())) {

		    // to display full in width.
		    ImageDownloader.setSquareImg(holder.mImgVwFeed,
			    feedHolder.getmStdResImgUrl(),
			    R.drawable.default_image_square, true, true);
		    
			if (feedHolder.getmFeedType() != null
					&& feedHolder.getmFeedType().equalsIgnoreCase(
							IgConstants.IG_MEDIA_VIDEO)) {
				holder.mImgVwPlay.setVisibility(View.VISIBLE);
			} else {
				holder.mImgVwPlay.setVisibility(View.GONE);
			}
		    
		    myMediaListener(holder, holder.mImgVwFeed, position);
		} else {
		    holder.mImgVwFeed.setImageResource(android.R.color.transparent);
		}
		
		// Caption
		holder.mTxtVwFeedTxt.setText(feedHolder.getmCaptionText());		
		
		// Like count
		int likeCnt = Integer.parseInt(feedHolder.getmLikesCount());
		holder.mTxtVwLikeCount.setText(AppUtils
				.getStringFormattedNumber(likeCnt)
				+ AppUtils.getStringLikes(likeCnt) + ", ");

		// Comment Count
		int cmntCnt = Integer.parseInt(feedHolder.getmCmntCount());
		holder.mTxtVwCmntCount.setText(AppUtils.getStringFormattedNumber(
				cmntCnt).trim()
				+ AppUtils.getStringComments(cmntCnt));
		
		holder.mImgVwLike.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				IgWrapper wrapper = new IgWrapper(mContext);
				wrapper.postLikeOnMedia(IgConstants.IG_REQ_POST_LIKE,feedHolder.getmMediaId(),
						mPostLikeCmntListener);
			}
		});
		
		/*holder.mImgVwCmnt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				IgWrapper wrapper = new IgWrapper(mContext);
				wrapper.postCmntOnMedia(IgConstants.IG_REQ_POST_CMNT,"",feedHolder.getmMediaId(),
						mPostLikeCmntListener);
			}
		});*/
		
		holder.mImgVwCmnt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				IgWrapper wrapper = new IgWrapper(mContext);

				if (wrapper.isLoggedIn()) {
					IgCmntDialogFrag cmntDlgFrag = new IgCmntDialogFrag(
							mContext, feedHolder.getmMediaId());
					cmntDlgFrag.show(((FragmentActivity) mContext)
							.getSupportFragmentManager(), "IgCmntDialog");
				} else {

					// To login instagram
					Intent intent = new Intent(mContext, IgActivity.class);
					intent.putExtra(IgConstants.IG_REQUEST,
							IgConstants.IG_REQ_LOGIN);

					IgActivity.setIgAuthListener(new IAuthenticationListener() {

						@Override
						public void onAuthSuccess() {
							IgCmntDialogFrag cmntDlgFrag = new IgCmntDialogFrag(
									mContext, feedHolder.getmMediaId());

							FragmentTransaction transaction = ((FragmentActivity) mContext)
									.getSupportFragmentManager()
									.beginTransaction();
							transaction.add(cmntDlgFrag, "IgCmntDialog");
							transaction.commitAllowingStateLoss();
						}

						@Override
						public void onAuthFail(String error) {

						}
					});

					mContext.startActivity(intent);
				}				
			}
		});
		
		return convertView;
	}
	
	public void myMediaListener(final ViewHolder phHolder, ImageView imgView,
			final int position) {
		imgView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				IgFeedHolder feedHolder = mIgFeedList.get(position);

				Intent intent = null;
				Bundle args = new Bundle();

				if (feedHolder.getmFeedType() != null
						&& feedHolder.getmFeedType().equalsIgnoreCase(
								IgConstants.IG_MEDIA_VIDEO)) {

					args.putString("video_url", feedHolder.getmVidStdResUrl());

					intent = new Intent(mContext, IgVideoDetailActivity.class);

					intent.putExtras(args);
					mContext.startActivity(intent);
				}

			}
		});
	}
	
	private ILikeCmntListener mPostLikeCmntListener = new ILikeCmntListener() {

		@Override
		public void onCopmplete(int reqType, int responseCode) {

			if (responseCode == 200) {
				Log.d(IgConstants.TAG, "Post like/cmnt Success.");
				if (reqType == IgConstants.IG_REQ_POST_LIKE) {
					Log.d(IgConstants.TAG, " Like done succesfully.");
				} else {
					Log.d(IgConstants.TAG, " Comment posted succesfully.");
				}
			} else {
				Log.d(IgConstants.TAG, "Post like/cmnt failed.");
			}

		}
	};
}
