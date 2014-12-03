package com.oxygen.my;

import java.util.List;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.LogInCallback;

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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.transition.Visibility;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MyLoginActivity extends Activity implements OnClickListener{

//	private ARArcMenu arcMenu;
	
	private String userID;
	private String userName;
	private String passWord;
	private String mail;
	private String phone;
	private String IMEI;
	private String userImage;
	private String userNote;
	
	
	private TextView titleText;
	private ImageView backBtn;
	
	private Button loginBtn;
	private EditText userNameText;
	private EditText passWordText;
	private LinearLayout registerText;
	private LinearLayout forgetPwdText;
	private ProgressDialog progressDialog;
	private boolean QUIT_BACK = false;//是否重载返回键
	private boolean isExit = false;//是否退出
	private long mExitTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		this.setContentView(R.layout.my_login);
		this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.my_setting_title_bar);
		
		titleText = (TextView)findViewById(R.id.my_setting_title_tv);
		backBtn = (ImageView)findViewById(R.id.my_setting_title_bar_back_btn);
		titleText.setText("登录");
		backBtn.setOnClickListener(this);
		
		loginBtn = (Button)findViewById(R.id.my_login_btn);
		userNameText = (EditText)findViewById(R.id.my_login_username_tv);
		passWordText = (EditText)findViewById(R.id.my_login_pwd_tv);
		registerText = (LinearLayout)findViewById(R.id.my_login_register);
		forgetPwdText = (LinearLayout)findViewById(R.id.my_login_forget);
		
		loginBtn.setOnClickListener(this);
		userNameText.setOnClickListener(this);
		passWordText.setOnClickListener(this);
		registerText.setOnClickListener(this);
		forgetPwdText.setOnClickListener(this);

	}

	
	@Override
		protected void onResume() {
			// TODO Auto-generated method stub
			super.onResume();
			changBackKey();
		}
	@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
				case R.id.my_setting_title_bar_back_btn:
					MyLoginActivity.this.finish();
					break;
				case R.id.my_login_btn:
					
					userName = userNameText.getText().toString();
					passWord = passWordText.getText().toString();
					
					if(userName.contains(" ")||passWord.contains(" ")){
						userNameText.setText("");
						passWordText.setText("");
						Toast.makeText(MyLoginActivity.this, "您输入的帐号或密码有误！", Toast.LENGTH_LONG).show();
						break;
					}
					if(!userName.equals("")&&!passWord.equals("")){
						progressDialog = new ProgressDialog(MyLoginActivity.this);
						showProgress(progressDialog);
						login();//登录
						
					}else if(userName.equals("")&&!passWord.equals("")){
						Toast.makeText(MyLoginActivity.this, "请输入帐号", Toast.LENGTH_LONG).show();
					}else if(!userName.equals("")&&passWord.equals("")){
						Toast.makeText(MyLoginActivity.this, "请输入密码", Toast.LENGTH_LONG).show();
					}else{
						Toast.makeText(MyLoginActivity.this, "请输入您的帐号和密码", Toast.LENGTH_LONG).show();
					}
					break;
				case R.id.my_login_username_tv:
					break;
				case R.id.my_login_pwd_tv:
					break;
				case R.id.my_login_register:
					startActivity(new Intent(MyLoginActivity.this, MyRegisterActivity.class));
					break;
				case R.id.my_login_forget:
					break;
			}
		}
	
	/**
	* @param 
	* @return void
	* @Description 登录  
	*/
	private void login(){
		AVUser.logInInBackground(userName, passWord, new LogInCallback<AVUser>() {
			@Override
			public void done(AVUser arg0, AVException arg1) {
				// TODO Auto-generated method stub
				if(arg1==null){
					if(arg0!=null){
						userID = arg0.getObjectId();
						IMEI = getIMEI();
						setSP();
						putDataToUserInfo();
						progressDialog.dismiss();
						Toast.makeText(MyLoginActivity.this, "登录成功", Toast.LENGTH_LONG).show();
						startActivity(new Intent(MyLoginActivity.this, MainActivity.class));
						MyLoginActivity.this.finish();
					}else{
						progressDialog.dismiss();
						Toast.makeText(MyLoginActivity.this, "您输入的帐号或密码有误！", Toast.LENGTH_LONG).show();
					}
				}else{
					progressDialog.dismiss();
					Toast.makeText(MyLoginActivity.this, "网络异常", Toast.LENGTH_LONG).show();
				}
			}
		});
	}
	

	/**
	* @param @return
	* @return String
	* @Description 获取串号  
	*/
	public String getIMEI() {
		TelephonyManager telephonemanage = (TelephonyManager) MyLoginActivity.this
				.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonemanage.getDeviceId();
	}
	
	/**
	* @param 
	* @return void
	* @Description 登录成功后与服务器UserInfo用户数据同步，由游客转为注册用户  
	*/
private void putDataToUserInfo(){
	
	AVQuery<AVObject> query = new AVQuery<AVObject>(UserInfo.USER_CLASS_NAME);
	query.whereEqualTo(UserInfo.USER_NAME, userName);
	query.findInBackground(new FindCallback<AVObject>() {
		public void done(List<AVObject> arg0, AVException arg1) {
	        if (arg1 == null) {//服务器中存在该用户名
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
	        		
	        } else {
//	        	Toast.makeText(MyLoginActivity.this, "arg1!=null", Toast.LENGTH_LONG).show();
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
		SharedPreferences sp = MyLoginActivity.this.getSharedPreferences("user", Context.MODE_PRIVATE);
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
	
	
	
	/**
	* @param 
	* @return void
	* @Description 判断返回键状态  
	*/
	private void changBackKey(){

		if(isExit()){
			backBtn.setVisibility(View.GONE);
			QUIT_BACK = true;
		}
	}
	
	 //重写BACK键事件
	public boolean onKeyDown(int keyCode, KeyEvent event){
		 if (keyCode == KeyEvent.KEYCODE_BACK&&QUIT_BACK) {
             if ((System.currentTimeMillis() - mExitTime) > 2000) {
                     Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                     mExitTime = System.currentTimeMillis();

             } else {
            	 Toast.makeText(this, "退出应用", Toast.LENGTH_SHORT).show();
            	 android.os.Process.killProcess(android.os.Process.myPid());
                 System.exit(0);
             }
             return true;
     }
     return super.onKeyDown(keyCode, event);
}
	
	private boolean isExit(){
		Intent intent = getIntent();
		if(intent.getBooleanExtra("isExit", false)==true){
			return true;
		}else{
			return false;
		}
	}
    
}
