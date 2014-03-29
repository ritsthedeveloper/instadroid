package com.ritesh.instagram.listener;

/**
 * Interface for status of the request.
 * 
 * @author ritesh
 *
 */
public interface IReqStatusListener {

	public abstract void onSuccess(int reqType);
	public abstract void onFail(int reqType,String error);
}
