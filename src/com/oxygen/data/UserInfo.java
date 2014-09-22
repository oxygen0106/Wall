package com.oxygen.data;

import android.content.Context;
import android.content.SharedPreferences;
import com.avos.avoscloud.AVObject;

public class UserInfo {

	public static final String USER_CLASS_NAME = "UserInfo";
	public static final String USER_ID = "objectId";
	public static final String USER_NAME = "username";
	public static final String USER_PWD = "password";
	public static final String MAIL = "email";
	public static final String PHONE = "phone";
	public static final String IMEI = "IMEI";
	public static final String NOTE = "userNote";
	public static final String USER_IMG = "userImage";

	private Context context;
	private AVObject uploadAVObject = new AVObject(USER_CLASS_NAME);
	private AVObject downloadAVObject;
	private String userID = "";
	private String userName = "";

	SharedPreferences sp = context.getSharedPreferences("user",
			Context.MODE_PRIVATE);

	public UserInfo(Context context, String userID) {
		this.context = context;
		this.userID = userID;
	}

	public String getUserID() {
		return downloadAVObject.getString(USER_ID);
	}

	public void setUserID(String mUserID) {
		uploadAVObject.put(USER_ID, mUserID);
	}

	public String getUserName() {
		return downloadAVObject.getString(USER_NAME);
	}

	public void setUserName(String mUserName) {
		uploadAVObject.put(USER_NAME, mUserName);
	}

	public String getUserPassWord() {
		return downloadAVObject.getString(USER_PWD);
	}

	public void setUserPassWord(String mPassWord) {
		uploadAVObject.put(USER_PWD, mPassWord);
	}

	public String getMail() {
		return downloadAVObject.getString(MAIL);
	}

	public void setMail(String mMail) {
		uploadAVObject.put(MAIL, mMail);
	}

	public String getIMEI() {
		return downloadAVObject.getString(IMEI);
	}

	public void setIMEI(String mIMEI) {
		uploadAVObject.put(IMEI, mIMEI);
	}

}
