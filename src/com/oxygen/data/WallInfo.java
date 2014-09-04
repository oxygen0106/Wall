package com.oxygen.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.baidu.mapapi.model.LatLng;

/**
 * @ClassName WallInfo
 * @Description 留言板信息类
 * @author oxygen
 * @email oxygen0106@163.com
 * @date 2014-8-18 下午4:30:32
 */
public class WallInfo implements Serializable {

	public static final long serialVersionUID = 1L;// 序列化ID

	private long mWallID;// 留言板ID
	private String mWallContent;
	private double mLatitude;// 纬度
	private double mLongitude;// 经度
	private String mImageURL;
	private long mUserID;
	private int mSupportCount;
	private int mCommentCount;
	private long mWallCreateTime;
	private double mDistance;//距离，本地计算
	private ArrayList<WallComment> mCommentList;
	
	public WallInfo() {
		
	}

	
	
	public WallInfo(long wallID, String wallContent, double latitude,
			double longitude, String imageURL, long userID,
			int supportCount, int commentCount) {
		super();
		this.mWallID = wallID;
		this.mWallContent = wallContent;
		this.mLatitude = latitude;
		this.mLongitude = longitude;
		this.mImageURL = imageURL;
		this.mUserID = userID;
		this.mSupportCount = supportCount;
		this.mCommentCount = commentCount;
	}



	public long getWallID() {
		return mWallID;
	}

	public void setWallID(long wallID) {
		this.mWallID = wallID;
	}

	public String getWallContent() {
		return mWallContent;
	}

	public void setWallContent(String wallContent) {
		this.mWallContent = wallContent;
	}

	public double getLatitude() {
		return mLatitude;
	}

	public void setLatitude(double latitude) {
		this.mLatitude = latitude;
	}

	public double getLongitude() {
		return mLongitude;
	}

	public void setLongitude(double longitude) {
		this.mLongitude = longitude;
	}

	/**
	* @param @return
	* @return LatLng
	* @Description 增加经纬度生成坐标点LatLng（百度地图API类）  
	*/
	public LatLng getLocation(){
		return new LatLng(mLatitude, mLongitude);
	}
	
	public String getImageURL() {
		return mImageURL;
	}

	public void setImageURL(String imageURL) {
		this.mImageURL = imageURL;
	}

	public long getUserID() {
		return mUserID;
	}

	public void setUserID(long userID) {
		this.mUserID = userID;
	}

	public int getSupportCount() {
		return mSupportCount;
	}

	public void setSupportCount(int supportCount) {
		this.mSupportCount = supportCount;
	}

	public int getmCommentCount() {
		return mCommentCount;
	}

	public void setCommentCount(int commentCount) {
		this.mCommentCount = commentCount;
	}
	
	public void computeDistance(double latitude,double longitude){
		
	}
	public double getDistance(){
		return this.mDistance;
	}


	public long getmWallCreateTime() {
		return mWallCreateTime;
	}
	
	public void setWallCreateTime(long wallCreateTime) {
		this.mWallCreateTime = wallCreateTime;
	}
	public ArrayList<WallComment> getWallComment(){
		return mCommentList;
	}
	public void addWallComment(ArrayList<WallComment> wallComment){
		if(mCommentList==null){
			mCommentList=wallComment;
		}else{
			mCommentList.addAll(wallComment);
		}
	}
}
