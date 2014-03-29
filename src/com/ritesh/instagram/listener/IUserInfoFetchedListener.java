package com.ritesh.instagram.listener;

import com.ritesh.instagram.holder.IgUserInfoHolder;

public interface IUserInfoFetchedListener {
	public void onIgUsrInfoFetched(IgUserInfoHolder usrInfoHolder);

	public void onIgUsrInfoFetchingFailed();
}
