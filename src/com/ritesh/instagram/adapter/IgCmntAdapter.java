package com.ritesh.instagram.adapter;

import java.util.ArrayList;

import android.app.Service;
import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ritesh.instagram.example.R;
import com.ritesh.instagram.holder.IgCommentHolder;
import com.ritesh.instagram.utils.AppUtils;
import com.ritesh.instagram.utils.ImageDownloader;

public class IgCmntAdapter extends BaseAdapter {
	
	private Context mContext;
	private LayoutInflater mInflater;
	private ArrayList<IgCommentHolder> mCmntList;

	public IgCmntAdapter(Context context) {

		mContext = context;
		mInflater = (LayoutInflater) context
				.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
	}

	public void setDataSource(ArrayList<IgCommentHolder> cmntList) {
		mCmntList = cmntList;
	}

	static class ViewHolder {
		// profile image.
		ImageView mImgVwProfile;
		// User Name
		TextView mTxtVwName;
		// Comment text
		TextView mTxtVwCmntTxt;
		// Created at
		TextView mTxtVwLog;		
	}
	
	@Override
	public int getCount() {
		if (mCmntList == null)
			return 0;
		else
			return mCmntList.size();
	}

	@Override
	public Object getItem(int position) {
		return mCmntList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		holder = new ViewHolder();
		convertView = mInflater.inflate(R.layout.listitem_cmnts, null);
		
		holder.mImgVwProfile = (ImageView) convertView
			    .findViewById(R.id.imgProfile);		
		holder.mTxtVwName = (TextView) convertView
				.findViewById(R.id.txt_ig_usr_name);
		holder.mTxtVwLog = (TextView) convertView
				.findViewById(R.id.txt_ig_created_at);
		holder.mTxtVwCmntTxt = (TextView) convertView
				.findViewById(R.id.txt_ig_text);
		
		convertView.setTag(holder);
		holder = (ViewHolder) convertView.getTag();
		
		IgCommentHolder cmntHolder = mCmntList.get(position);
		
		// Set the profile image
		if (AppUtils.isUrlImage(cmntHolder.getmCmntUserProPicUrl())) {

			ImageDownloader.setSquareImg(mContext, holder.mImgVwProfile,
					cmntHolder.getmCmntUserProPicUrl(), R.drawable.icon, false,
					true);
		}
		
		holder.mTxtVwName.setText(cmntHolder.getmCmntUserName());
		
		// Created time
		long longTime = Long.parseLong(cmntHolder.getmCmntCreatedTime());
		/**
		 * Instagram returns unix time stamp for the comments's created time,
		 * which is in seconds. Hence multiplying by 1000 to convert it to
		 * milliseconds.
		 */
		longTime = longTime * 1000;
		holder.mTxtVwLog.setText(DateUtils.getRelativeTimeSpanString(longTime,
			System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS));
		
		holder.mTxtVwCmntTxt.setText(cmntHolder.getmCmntText());
		
		
		return convertView;
	}

}
