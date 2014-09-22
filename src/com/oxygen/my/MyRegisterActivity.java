package com.oxygen.my;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.SignUpCallback;
import com.oxygen.data.UserInfo;
import com.oxygen.main.LoadingActivity;
import com.oxygen.main.MainActivity;
import com.oxygen.my.MySlipButton.OnChangedListener;
import com.oxygen.wall.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MyRegisterActivity extends Activity implements OnClickListener{
	
	private TextView titleText; 
	private ImageView backBtn;
	
	private String userID;
	private String userName;
	private String passWord;
	private String mail;
	private String phone;
	private String IMEI;
	private String userImage;
	private String userNote;
	
	private EditText userNameText;
	private EditText passWordText;
	private EditText mailText;
	private Button  registerBtn;
	
	private ProgressDialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		this.setContentView(R.layout.my_register);
		this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.my_setting_title_bar);
		
		titleText = (TextView)findViewById(R.id.my_setting_title_tv);
		backBtn = (ImageView)findViewById(R.id.my_setting_title_bar_back_btn);
		titleText.setText("注册");
		setBackBtnListener();
		
		userNameText = (EditText)findViewById(R.id.my_register_username);
		passWordText = (EditText)findViewById(R.id.my_register_password);
		mailText = (EditText)findViewById(R.id.my_register_mail);
		registerBtn = (Button)findViewById(R.id.my_register_btn);
		
		IMEI = getIMEI();
		userNameText.setOnClickListener(this);
		passWordText.setOnClickListener(this);
		mailText.setOnClickListener(this);
		registerBtn.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.my_register_username:
			break;
		case R.id.my_register_password:
			break;
		case R.id.my_register_mail:
			break;
		case R.id.my_register_btn:
			userName = userNameText.getText().toString();
			passWord = passWordText.getText().toString();
			mail = mailText.getText().toString();
			if(userName.contains(" ")||passWord.contains(" ")||mail.contains(" ")){//包含空格
				Toast.makeText(MyRegisterActivity.this, "请正确填写注册信息", Toast.LENGTH_LONG).show();
				userNameText.setText("");
				passWordText.setText("");
				mailText.setText("");
				break;
			}
			if(!(mail.contains("@")&&mail.contains("."))){//邮箱格式不正确
				Toast.makeText(MyRegisterActivity.this, "请填写有效的邮箱地址", Toast.LENGTH_LONG).show();
				break;
			}
			if(!userName.equals("")&&!passWord.equals("")&&!mail.equals("")){
				progressDialog = new ProgressDialog(MyRegisterActivity.this);
				showProgress(progressDialog);
				register();//提交至服务器
			}else{
				Toast.makeText(MyRegisterActivity.this, "请填写完整注册信息", Toast.LENGTH_LONG).show();
			}
			break;
		}
	}
	
	/**
	* @param 
	* @return void
	* @Description 完成注册  
	*/
	private void register(){
		AVUser currentUser = AVUser.getCurrentUser();
		if(currentUser!=null&&currentUser.isAnonymous()){
			currentUser.setUsername(userName);
			currentUser.setPassword(passWord);
			currentUser.setEmail(mail);
			//选填内容
			currentUser.put(UserInfo.IMEI, IMEI);
			currentUser.put(UserInfo.PHONE, phone);
			currentUser.put(UserInfo.NOTE, userNote);
			currentUser.put(UserInfo.USER_IMG, userImage);
			currentUser.saveInBackground(new SaveCallback() {
				
				@Override
				public void done(AVException arg0) {
					// TODO Auto-generated method stub
					if(arg0==null){
						setSP();
			        	putDataToUserInfo();
			        	progressDialog.dismiss();
						Toast.makeText(MyRegisterActivity.this, "成功注册", Toast.LENGTH_LONG).show();
			            startActivity(new Intent(MyRegisterActivity.this, MainActivity.class));
					}else{
						progressDialog.dismiss();
						Toast.makeText(MyRegisterActivity.this, arg0.getMessage(), Toast.LENGTH_LONG).show();
					}
				}
			});
		}else{
		
		AVUser user = new AVUser();
		user.setUsername(userName);
		user.setPassword(passWord);
		user.setEmail(mail);
		
		
		user.put(UserInfo.IMEI, IMEI);
		user.put(UserInfo.PHONE, phone);
		user.put(UserInfo.NOTE, userNote);
		user.put(UserInfo.USER_IMG, userImage);//TODO
		user.signUpInBackground(new SignUpCallback() {
		    public void done(AVException e) {
		        if (e == null) {
		        	AVQuery<AVUser> query = AVUser.getQuery();//获取_User查询请求
		        	query.whereEqualTo(UserInfo.USER_NAME, userName);
		        	query.findInBackground(new FindCallback<AVUser>() {
		        		
						@Override
						public void done(List<AVUser> arg0, AVException arg1) {
							// TODO Auto-generated method stub
							if(arg1==null){
								userID = arg0.get(0).getObjectId();
								setSP();
					        	putDataToUserInfo();
					        	progressDialog.dismiss();
								Toast.makeText(MyRegisterActivity.this, "成功注册", Toast.LENGTH_LONG).show();
					            startActivity(new Intent(MyRegisterActivity.this, MainActivity.class));
					            
							}else{
								progressDialog.dismiss();
								Toast.makeText(MyRegisterActivity.this, arg1.getMessage(), Toast.LENGTH_LONG).show();
							}
						}
					});
		            
		        } else {
		        	progressDialog.dismiss();
		        	Toast.makeText(MyRegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
		        }
		    }
		});
		}
	}
	
	/**
	* @param @return
	* @return String
	* @Description 获取串号  
	*/
	public String getIMEI() {
		TelephonyManager telephonemanage = (TelephonyManager) MyRegisterActivity.this
				.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonemanage.getDeviceId();
	}
	
	/**
	* @param 
	* @return void
	* @Description 与服务器用户数据同步  
	*/
	private void putDataToUserInfo(){
		
		AVQuery<AVObject> query = new AVQuery<AVObject>(UserInfo.USER_CLASS_NAME);
		query.whereEqualTo(UserInfo.USER_NAME, userName);
		query.findInBackground(new FindCallback<AVObject>() {
			public void done(List<AVObject> arg0, AVException arg1) {
		        if (arg1 == null) {
		        	String UserInfoID = arg0.get(0).getObjectId();
		        	AVQuery<AVObject> query = new AVQuery<AVObject>(UserInfo.USER_CLASS_NAME);
		        	AVObject avobjectUserInfo;
		        		query.getInBackground(UserInfoID, new GetCallback<AVObject>() {
							@Override
							public void done(AVObject arg0, AVException arg1) {
								// TODO Auto-generated method stub
								if(arg1==null){
									arg0.put(UserInfo.USER_NAME, userName);
									arg0.put(UserInfo.MAIL, mail);
									arg0.put(UserInfo.USER_PWD, passWord);
									arg0.saveInBackground();
								}else{
									System.out.println("MyRegisterActivity.putDataToUserInfo().Exception"+arg1.getMessage());
								}
							}
						});
		        		
		        } else{
		        	AVObject avobject = new AVObject(UserInfo.USER_CLASS_NAME);
		    		avobject.put(UserInfo.USER_NAME, userName);
		    		avobject.put(UserInfo.IMEI, IMEI);
		    		avobject.put(UserInfo.USER_PWD, passWord);
		        	avobject.saveInBackground();
		        }
		    }
		});
	}
	
	/**
	* @param 
	* @return void
	* @Description 同步数据至本地  
	*/
	private void setSP(){
		SharedPreferences sp = MyRegisterActivity.this.getSharedPreferences("user", Context.MODE_PRIVATE);
    	SharedPreferences.Editor editor = sp.edit();
    	editor.putBoolean("userStatus", true);
    	editor.putString("userName", userName);
    	editor.putString("IMEI", IMEI);
    	editor.putString("userID", userID);
    	editor.commit();
	}
	
	/**
	* @param 
	* @return void
	* @Description 显示进度  
	*/
	private void showProgress(ProgressDialog progressDialog){
		
		Window window = progressDialog.getWindow();
		WindowManager.LayoutParams lp = window
				.getAttributes();
		lp.alpha = 0.7f;
		lp.dimAmount = 0.8f;
		window.setAttributes(lp);
		progressDialog.show();
	}
	
	private void setBackBtnListener(){
		backBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MyRegisterActivity.this.finish();
			}
		});
	}
	
}
