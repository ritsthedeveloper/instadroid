package com.ritesh.instagram.listener;

/**
 * To listen Authentication events.
 * 
 * @author ritesh
 *
 */
public interface IAuthenticationListener {

	public abstract void onAuthSuccess();
	public abstract void onAuthFail(String error);
}
