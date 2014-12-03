package com.oxygen.data;

import java.text.SimpleDateFormat;
import java.util.List;

import android.graphics.Bitmap;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;

public class WallCommentDownload {
	private static final String TAG="WallCommentActivity";
	
	private String mCommentID;
	private String mCommentContent;
	private String mCommentCreateTime;
	private String mCommenterName;
	private String mCommenterImageURL;
	private String mCommenterID;
	private Bitmap mCommenterImageBitmap;
	private AVUser user;
	
	public WallCommentDownload(AVObject comment){
		Log.v(TAG, "WallCommentDownload1");
	

		mCommentID=comment.getObjectId();

		mCommentContent=comment.getString(WallCommentUpload.COMMENT);

		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm");
		mCommentCreateTime=df.format(comment.getCreatedAt());
		user=(AVUser) comment.get(WallCommentUpload.USER);
		mCommenterID=user.getString("objectId");
		AVFile userImage=user.getAVFile(UserInfo.USER_IMG);
		if(userImage!=null){
			mCommenterImageURL=userImage.getUrl();
		}else{
			mCommenterImageURL = null;
		}
		if(user.getEmail()!=null){
			mCommenterName=user.getUsername();
		}else{
			mCommenterName = "游客";
		}

	}
	public String getCommentID() {
		return mCommentID;
	}
	public String getCommenterID() {
		return mCommenterID;
	}
	public String getCommentContent() {
		return mCommentContent;
	}

	public void setCommentContent(String commentContent) {
		this.mCommentContent = commentContent;
	}

	public String getCommentCreateTime() {
		return mCommentCreateTime;
	}

	public void setCommentCreateTime(String commentTime) {
		this.mCommentCreateTime = commentTime;
	}

	public String getCommenterName() {
		return mCommenterName;
	}

	public void setCommenterName(String commenterName) {
		this.mCommenterName = commenterName;
	}

	public String getCommenterImage() {
		return mCommenterImageURL;
	}
	public Bitmap getCommenterImageBitmap(){
		return mCommenterImageBitmap;
	}
	public void setCommenterImageBitmap(Bitmap b){
		mCommenterImageBitmap = b;;
	}
}
