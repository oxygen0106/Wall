package com.oxygen.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.GetDataCallback;


public class UserInfo extends AVObject {

	public static final String USER_CLASS_NAME = "UserInfo";

	public static final String USER_ID = "mUserID";
	public static final String USER_NAME = "mUserName";
	public static final String USER_PWD = "mPassWord";
	public static final String MAIL = "mMail";
	public static final String IMEI = "mIMEI";
	public static final String USER_IMG = "mUserImage";


	public UserInfo(String str){
		super(str);
	}
	public Long getUserID() {
		return this.getLong(USER_ID);
	}

	public void setUserID(long mUserID) {
		this.put(USER_ID, mUserID);
	}

	public String getUserName() {
		return this.getString(USER_NAME);
	}

	public void setUserName(String mUserName) {
		this.put(USER_NAME, mUserName);
	}

	public String getUserPassWord() {
		return this.getString(USER_PWD);
	}

	public void setUserPassWord(String mPassWord) {
		this.put(USER_PWD, mPassWord);
	}

	public String getMail() {
		return this.getString(MAIL);
	}

	public void setMail(String mMail) {
		this.put(MAIL, mMail);
	}

	public String getIMEI() {
		return this.getString(IMEI);
	}

	public void setIMEI(String mIMEI) {
		this.put(IMEI, mIMEI);
	}

	/**
	* @param @return
	* @return Bitmap
	* @Description 获取用户头像  
	*/
	public Bitmap getUserImage() {
		String imageName = getUserID() + ".jpg";
		String path = Environment.getExternalStorageDirectory().getPath()
				+ "/Android/data/com.oxygen.wall/user/userImg" + imageName;
		File file = new File(path);
		if (file.exists()) {
			Bitmap bitmap = BitmapFactory.decodeFile(path);
			return bitmap;
		} else {
			AVFile imageFile = this.getAVFile(USER_IMG);
			imageFile.getDataInBackground(new GetDataCallback() {

				@Override
				public void done(byte[] data, AVException arg1) {
					// TODO Auto-generated method stub
					String imageName = getUserID() + ".jpg";
					String path = Environment.getExternalStorageDirectory()
							.getPath()
							+ "/Android/data/com.oxygen.wall/user/userImg"
							+ imageName;
					FileOutputStream imageOutput;
					try {
						imageOutput = new FileOutputStream(new File(path));
						imageOutput.write(data, 0, data.length);
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			Bitmap bitmap = BitmapFactory.decodeFile(path);
			return bitmap;
		}
	}

	public void setUserImage() {
		String imageName = getUserID() + ".jpg";
		String path = Environment.getExternalStorageDirectory().getPath()
				+ "/Android/data/com.oxygen.wall/user/userImg" + imageName;
		AVFile imageFile = null;
		try {
			imageFile = AVFile.withAbsoluteLocalPath(imageName, path);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.put(USER_IMG, imageFile);
	}
	
	public void convertBitmap(){
		
	}
}
