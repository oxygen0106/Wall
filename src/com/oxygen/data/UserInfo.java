package com.oxygen.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetDataCallback;

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
	
	private String path;

	SharedPreferences sp = context.getSharedPreferences("user",
			Context.MODE_PRIVATE);

	public UserInfo(Context context, String userID) {
		this.context = context;
		this.userID = userID;
	}

	// public UserInfo(Context context, AVObject avobject){
	// this.context = context;
	// this.avobject = avobject;
	// }
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

	/**
	 * @param @return
	 * @return Bitmap
	 * @Description 获取用户头像
	 */
	public Bitmap getUserImage(String userID) {
		
		String imageName = "myImage" + ".png";
		String path = Environment.getExternalStorageDirectory().getPath()
				+ "/Android/data/com.oxygen.wall/user/userImg" + imageName;
		File file = new File(path);
		if (file.exists()) {
			Bitmap bitmap = BitmapFactory.decodeFile(path);
			return bitmap;
		} else {
			AVFile imageFile = downloadAVObject.getAVFile(USER_IMG);
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
		// String imageName = getUserID() + ".jpg";
		String imageName = "/myImage" + ".png";
		path = Environment.getExternalStorageDirectory().getPath()
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
		uploadAVObject.put(USER_IMG, imageFile);
		uploadAVObject.saveInBackground();
	}

	/**
	* @param 
	* @return void
	* @Description 设置上传图片的路径
	*/
	private void setPath() {
		SharedPreferences sp = context.getSharedPreferences("user",
				Context.MODE_PRIVATE);
		if (!sp.getBoolean("userStatus", false)) {
			
		} else {

		}
	}
	
	/**
	* @param 
	* @return void
	* @Description 获取制定userID的AVObject对象  
	*/
	private void initDowanload(){
		AVQuery<AVObject> query = new AVQuery<AVObject>(USER_CLASS_NAME);
		try {
			downloadAVObject = query.get(userID);
		} catch (AVException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
