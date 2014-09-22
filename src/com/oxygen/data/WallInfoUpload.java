package com.oxygen.data;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.LogUtil.log;
import com.avos.avoscloud.SaveCallback;
import com.oxygen.ar.target.PostNewTarget;
import com.oxygen.map.GetLocation;

public class WallInfoUpload {

	private String objectID;
	private String mWallContent;
	private double mLatitude;// 纬度
	private double mLongitude;// 经度
	private String mImageURL;
	private String mUserID;
	private int mSupportCount;
	private int mCommentCount;

	public static final String CONTENT = "content";
	public static final String USER = "user";
	public static final String IMAGE = "image";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	public static final String IMAGEURL = "imageURL";
	public static final String SUPPORT = "supportCount";
	public static final String COMMENT = "commentCount";

	private AVObject avObject;
	private AVFile avFile;
	private Context mContext;

	public WallInfoUpload(String wallContent, String imageURL,
			Context context) {
		if (GetLocation.isLocationDone()) {
			mWallContent = wallContent;
			mImageURL = imageURL;
			mContext = context;
			try {
				uploadeData();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			// 定位失败
		}
	}

	private void uploadeData() throws FileNotFoundException, IOException {
		avObject = new AVObject("WallInfo");
		AVUser user=AVUser.getCurrentUser();
		avObject.put("user", user);
		avFile = AVFile.withAbsoluteLocalPath("image.png", mImageURL);
		avObject.put(IMAGE, avFile);
		avObject.put(CONTENT, mWallContent);
		avObject.put(LATITUDE, GetLocation.mCurrentLantitude);
		avObject.put(LONGITUDE, GetLocation.mCurrentLongitude);
		avObject.put(SUPPORT, 0);
		avObject.put(COMMENT, 0);
		Log.v("publish", "正在上传");
		avObject.saveInBackground(new SaveCallback() {
			public void done(AVException e) {
				if (e == null) {
					// 保存成功
					GetLocation getLocation = new GetLocation();
					getLocation.getAddress();//上传地址
					
					Log.v("publish", "保存成功");
					Toast.makeText(mContext, "发布成功", Toast.LENGTH_SHORT).show();
					AVQuery<AVObject> query = new AVQuery<AVObject>("WallInfo");
					AVUser user=AVUser.getCurrentUser();
					query.whereEqualTo(USER, user);
					query.orderByDescending("createdAt");
					query.getFirstInBackground(new GetCallback<AVObject>() {
						@Override
						public void done(AVObject arg0, AVException arg1) {
							// TODO Auto-generated method stub
							if (arg1 == null) {
								String objectId = arg0.getObjectId();
								Log.v("publish",objectId);	
								objectID=objectId;
								new AsyncTask<Void, Void, Void>() {
									protected Void doInBackground(Void... params) {
										try {
											Log.v("publish","PostNewTarget");	
											PostNewTarget pt = new PostNewTarget(objectID,
													mImageURL);		
											pt.run();
										} catch (Exception e) {
											e.printStackTrace();
										}
										
										return null;
								}
									@Override
									protected void onPostExecute(Void result) {
										Log.v("publish","Done");	
									}
								
								}.execute();
							}else
								Log.v("publish","查询出错");
						}

					});
				} else {
					// 保存失败，输出错误信息
					Log.v("publish", "保存失败");
					Toast.makeText(mContext, "发布失败", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	public void setmWallContent(String wallContent) {
		this.mWallContent = wallContent;
	}

	/*
	 * public void setmLatitude(double mLatitude) { this.mLatitude = mLatitude;
	 * }
	 * 
	 * public void setmLongitude(double mLongitude) { this.mLongitude =
	 * mLongitude; }
	 */

	public void setmImageURL(String imageURL) {
		this.mImageURL = imageURL;
	}

	public void setmUserID(String userID) {
		this.mUserID = userID;
	}
}
