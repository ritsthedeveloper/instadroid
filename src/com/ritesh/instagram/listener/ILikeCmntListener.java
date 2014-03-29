package com.ritesh.instagram.listener;

/**
 * To listen to like and comment 
 * 
 * @author ritesh
 *
 */
public interface ILikeCmntListener {

	public abstract void onCopmplete(int reqType,int responseCode);
}
