package com.ritesh.instagram.listener;

import java.util.ArrayList;

import com.ritesh.instagram.holder.IgFeedHolder;

/**
 * Listener for Instagram feeds.
 * 
 * @author ritesh
 *
 */
public interface IFetchIgFeedsListener {
	public void onIgFeedsFetched(ArrayList<IgFeedHolder> feedList,
		    String nxtPgUrl);
}