package com.ritesh.instagram.listener;

import java.util.ArrayList;

import com.ritesh.instagram.holder.IgCommentHolder;

/**
 * Listener for comments on a Media.
 * 
 * @author ritesh
 *
 */
public interface IFetchCmntsListener {

	public void onIgCmntsFetched(ArrayList<IgCommentHolder> cmntList);
	public void onIgCmntsFailed();
}
