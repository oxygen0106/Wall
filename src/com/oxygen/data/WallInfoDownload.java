package com.oxygen.data;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.ImageView;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.baidu.mapapi.model.LatLng;
import com.oxygen.main.WallSquareFragment;
import com.oxygen.map.GetLocation;

/**
 * @ClassName WallInfo
 * @Description 留言板信息类
 * @author oxygen
 * @email oxygen0106@163.com
 * @date 2014-8-18 下午4:30:32
 */
public class WallInfoDownload {
	
	private String mWallID;// 留言板ID
	private String mWallContent;
	private double mLatitude;// 纬度
	private double mLongitude;// 经度
	private String mImageURL;
	private String mUserID;
	private String mUserName;
	private String mUserImageURL;
	private int mSupportCount;
	private int mCommentCount;
	private String mWallCreateTime;
	private int mDistance;//距离，本地计算
	public boolean isNear;
	private AVObject mObject;
	
	public WallInfoDownload(AVObject object) {
		AVUser user=(AVUser)object.get(WallInfoUpload.USER);
		mObject=object;
		mWallID=object.getObjectId();
		mWallContent=object.getString(WallInfoUpload.CONTENT);
		mLatitude=object.getDouble(WallInfoUpload.LATITUDE);
		mLongitude=object.getDouble(WallInfoUpload.LONGITUDE);
		AVFile image=object.getAVFile(WallInfoUpload.IMAGE);
		
		if(image==null){
			mImageURL = null;
		}else{
			mImageURL=image.getUrl();
		}

		//mImageURL=image.getThumbnailUrl(true, 300, 400);
		mUserID=user.getObjectId();
		AVFile userImage=user.getAVFile(UserInfo.USER_IMG);
		if(userImage==null){
			mUserImageURL = null;
		}else{
			mUserImageURL=userImage.getUrl();
		}
		if(user.getEmail()!=null){
			mUserName=user.getUsername();
		}else{
			mUserName = "游客";
		}

		mSupportCount=object.getInt(WallInfoUpload.SUPPORT);
		mCommentCount=object.getInt(WallInfoUpload.COMMENT);
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm");
		mWallCreateTime=df.format(object.getCreatedAt());
		computeDistance();
	}
	public WallInfoDownload(AVObject object, AVUser user){
		mObject=object;
		mWallID=object.getObjectId();
		mWallContent=object.getString(WallInfoUpload.CONTENT);
		mLatitude=object.getDouble(WallInfoUpload.LATITUDE);
		mLongitude=object.getDouble(WallInfoUpload.LONGITUDE);
		AVFile image=object.getAVFile(WallInfoUpload.IMAGE);
		if(image==null){
			mImageURL = null;
		}else{
			mImageURL=image.getUrl();
		}

		mUserID=user.getObjectId();

		AVFile userImage=user.getAVFile(UserInfo.USER_IMG);
		if(userImage==null){
			mUserImageURL = null;
		}else{
			mUserImageURL=userImage.getUrl();
		}
		
		if(user.getEmail()!=null){
			mUserName=user.getUsername();
		}else{
			mUserName = "游客";
		}
		
		mSupportCount=object.getInt(WallInfoUpload.SUPPORT);
		mCommentCount=object.getInt(WallInfoUpload.COMMENT);
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm");
		mWallCreateTime=df.format(object.getCreatedAt());
		computeDistance();
	}
	
	/**
	 * 
	 * @param wallID
	 * @param wallContent
	 * @param latitude
	 * @param longitude
	 * @param imageURL
	 * @param userID
	 * @param userName
	 * @param userImageURL
	 * @param supportCount
	 * @param commentCount
	 */
	public WallInfoDownload(String wallID, String wallContent, double latitude,
			double longitude, String imageURL, String userID,
			String userName, String userImageURL, int supportCount, int commentCount) {
		super();
		this.mWallID = wallID;
		this.mWallContent = wallContent;
		this.mLatitude = latitude;
		this.mLongitude = longitude;
		this.mImageURL = imageURL;
		this.mUserID = userID;
		this.mUserName=userName;
		this.mUserImageURL=userImageURL;
		this.mSupportCount = supportCount;
		this.mCommentCount = commentCount;
		computeDistance();
	}
	public AVObject getWallObject(){
		return mObject;
	}
	public String getWallID() {
		return mWallID;
	}

	public void setWallID(String wallID) {
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

	public String getUserID() {
		return mUserID;
	}

	public void setUserID(String userID) {
		this.mUserID = userID;
	}
	

	public String getUserName() {
		return mUserName;
	}

	public void setUserName(String userName) {
		this.mUserName = userName;
	}

	public String getUserImageURL() {
		return mUserImageURL;
	}

	public void setUserImageURL(String userImageURL) {
		this.mUserImageURL = userImageURL;
	}

	public int getSupportCount() {
		return mSupportCount;
	}

	public void setSupportCount(int supportCount) {
		this.mSupportCount = supportCount;
	}
	public void increaseSupportCount(){
		this.mSupportCount++;
	}
	public void decreaseSupportCount(){
		this.mSupportCount--;
	}
	public void increaseCommentCount(){
		this.mCommentCount++;
	}
	public void decreaseCommentCount(){
		this.mCommentCount--;
	}
	public int getCommentCount() {
		return mCommentCount;
	}

	public void setCommentCount(int commentCount) {
		this.mCommentCount = commentCount;
	}
	
	public void computeDistance(){
		mDistance=GetLocation.getDistance(mLatitude, mLongitude);
		if(mDistance<3000)
			isNear=true;
	}
	public int getDistance(){
		return this.mDistance;
	}
	public String getWallCreateTime() {
		return mWallCreateTime;
	}
	
	public void setWallCreateTime(String wallCreateTime) {
		this.mWallCreateTime = wallCreateTime;
	}
}
