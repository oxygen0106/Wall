package com.oxygen.main;

import java.util.List;

import com.avos.avoscloud.AVAnonymousUtils;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.LogUtil.log;
import com.avos.avoscloud.SaveCallback;
import com.oxygen.data.UserInfo;
import com.oxygen.my.MyLoginActivity;
import com.oxygen.my.MyRegisterActivity;
import com.oxygen.wall.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.ProgressBar;
import android.widget.Toast;

public class LoadingActivity extends Activity {
	
	private ProgressBar progressBar;
	
	private String userID;
	private String userName;
	private String passWord;
	private String mail;
	private String phone;
	private String IMEI;
	private String userImage;
	private String userNote;
	
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading_activity);
		progressBar = (ProgressBar)findViewById(R.id.progress_bar);
		setSP();//初始化全局SharePreference
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		if(isOpenNetwork()){
			checkUserStatus();//检测并设置当前用户身份,判断是否跳转至登陆界面or主界面
		}else{
			setNetworkDialog();//设置网络
		}
	}
	/**
	* @param 
	* @return void
	* @Description 初始化全局SharePreference  
	*/
	private void setSP(){
		sp = this.getSharedPreferences("user",
				Context.MODE_PRIVATE);
		editor = sp.edit();

		IMEI = sp.getString("IMEI", null);
		userName = sp.getString("userName", null);
		userID = sp.getString("userID", null);
		
	}
	
	/**
	* @param 
	* @return void
	* @Description 当前用户身份判断  
	*/
	private void checkUserStatus(){
		
		if (userName != null) {// 判断userName是否存在
			AVQuery<AVUser> query = AVUser.getQuery();
			query.whereEqualTo(UserInfo.USER_NAME, userName);
			query.findInBackground(new FindCallback<AVUser>() {
				@Override
				public void done(List<AVUser> arg0, AVException arg1) {
					// TODO Auto-generated method stub
					if(arg1==null){
						userID = arg0.get(0).getObjectId();
						editor.putString("userID", userID);
						editor.putBoolean("userStatus", true);
						editor.commit();// 用户在状态设置为注册用户
						toMainActivity();
					}else{
						toMyLoginActivity();
					}
				}
			});
		} else {// 本地未保存userName
			IMEI = getIMEI();
			if(!IMEI.equals("")&&IMEI!=null){
				AVQuery<AVUser> query = AVUser.getQuery();
				query.whereEqualTo(UserInfo.IMEI, IMEI);
				query.findInBackground(new FindCallback<AVUser>() {
					@Override
					public void done(List<AVUser> arg0, AVException arg1) {
						// TODO Auto-generated method stub
						if(arg0.size()!=0){
							toMyLoginActivity();
						}else{
							AVUser currentUser = AVUser.getCurrentUser();
							if (currentUser != null) {
								userID = currentUser.getObjectId();
								editor.putBoolean("userStatus", false);
								editor.commit();
								toMainActivity();
							}else{
								createAnonymousUser();
							}
						}
					}
				});
			}else{
				AVUser currentUser = AVUser.getCurrentUser();
				if (currentUser != null) {
					userID = currentUser.getObjectId();
					editor.putBoolean("userStatus", false);
					editor.commit();
					toMainActivity();
				}else{
					createAnonymousUser();
				}
			}
			
		}
	}
	/**
	* @param @return
	* @return String
	* @Description 获取串号  
	*/
	public String getIMEI() {
		TelephonyManager telephonemanage = (TelephonyManager) LoadingActivity.this
				.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonemanage.getDeviceId();
	}

	/**
	* @param @param Imei
	* @return void
	* @Description 在服务器中查询是否存在该串号。
	* 1.若存在，则关联查询userName，判断是否存在userName。若有userName，则为已注册，跳至登录，否则设置为游客。
	* 2.若不存在，设置为游客，并将该串号提交至服务器。  
	*/
	public void isImeiInCloud(String Imei) {
//System.out.println(UserInfo.USER_CLASS_NAME+UserInfo.IMEI+IMEI);
		
		AVQuery<AVUser> query = AVUser.getQuery();//获取_User查询请求
		query.whereEqualTo(UserInfo.IMEI, IMEI);
		query.findInBackground(new FindCallback<AVUser>() {
			@Override
			public void done(List<AVUser> arg0, AVException arg1) {
		        	if(arg0.size()!=0){//服务器数据库存在该串号
		        		AVQuery<AVUser> query = AVUser.getQuery();//获取_User查询请求
		        		query.whereEqualTo(UserInfo.IMEI, IMEI);
		        		query.whereNotEqualTo(UserInfo.USER_NAME, "");
		        		query.findInBackground(new FindCallback<AVUser>() {
		        			@Override
		        		    public void done(List<AVUser> arg0, AVException arg1) {
		        		    	if(arg0.size()!=0){//服务器存在和该串号关联的用户名
		        		    			Toast.makeText(LoadingActivity.this, "尊敬的用户，欢迎回来！",Toast.LENGTH_LONG).show();
		        		    			toMyLoginActivity();
		        		    	}else {//服务器存在该串号，但不存在关联的用户名，由于AVUser的用户名强制性，不可能出现该情况
		        		    		Toast.makeText(LoadingActivity.this, "网络异常", Toast.LENGTH_LONG).show();
		        		    	}
		        			}
		        		});
		        		
		        	}else{//查询串号不存在
						if(arg0.get(0).isAnonymous()){
							userID = arg0.get(0).getObjectId();
    		    			editor.putBoolean("userStatus", false);
    		    			editor.commit();
    		    			saveSharePreference();
    		    			toMainActivity();
						}else{
			        		SharedPreferences sp = LoadingActivity.this.getSharedPreferences("user",Context.MODE_PRIVATE);
							SharedPreferences.Editor editor = sp.edit();
							editor.putBoolean("userStatus", false);// 用户在状态设置为游客
							editor.commit();
							createAnonymousUser();//创建匿名用户帐号
						}
		        	}
		    }
		});
	}
	
	/**
	* @param 
	* @return void
	* @Description 检测服务器是否保存当前的IMEI，从服务器获取游客的objectId作为userID, 完成后跳转至主界面
	*/
	private void getUserID(){
		if(userID!=null){//本地存在userID
			toMainActivity();
			return;
		}else{//本地未保存userID
			AVQuery<AVUser> query = AVUser.getQuery();//获取_User查询请求
			query.whereEqualTo(UserInfo.USER_NAME, userName);
			query.findInBackground(new FindCallback<AVUser>() {
				@Override
				public void done(List<AVUser> arg0, AVException arg1) {
			        if (arg0.size() != 0) {//服务器存在该用户名
			        	userID = arg0.get(0).getObjectId();// 保存userID
						saveSharePreference();	
						toMainActivity();
			        }else{
			        	Toast.makeText(LoadingActivity.this, arg1.getMessage(), Toast.LENGTH_LONG).show();
			        	toMyLoginActivity();
			        }
				}
			});
		}
	}
	
	

	/**
	* @param @return
	* @return boolean
	* @Description 当前网络是否可用 
	*/
	public boolean isOpenNetwork(){
		ConnectivityManager connManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);  
	    if(connManager.getActiveNetworkInfo() != null) {  
	        return connManager.getActiveNetworkInfo().isAvailable();
	    }else{
	    	return false;
	    }
	}
	
	/**
	* @param @return
	* @return boolean
	* @Description 网络设置提示  
	*/
	private void setNetworkDialog(){
		if(isOpenNetwork()!=true){
			AlertDialog.Builder builder = new AlertDialog.Builder(LoadingActivity.this);  
            builder.setTitle("没有可用的网络").setMessage("是否对网络进行设置?");  
              
            builder.setPositiveButton("是", new DialogInterface.OnClickListener() {  
                @Override  
                public void onClick(DialogInterface dialog, int which) {  
                    Intent intent = null;  
                      
                    try {   
                        if(android.os.Build.VERSION.SDK_INT > 10) {  
                            intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);  
                        }else {  
                            intent = new Intent();  
                            ComponentName comp = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");  
                            intent.setComponent(comp);  
                            intent.setAction("android.intent.action.VIEW");  
                        }  
                        LoadingActivity.this.startActivity(intent);
                        LoadingActivity.this.finish();
                    } catch (Exception e) {   
                        e.printStackTrace();  
                    }  
                }  
            }).setNegativeButton("否", new DialogInterface.OnClickListener() {  
                @Override  
                public void onClick(DialogInterface dialog, int which) {  
                    dialog.cancel();          
                    finish(); 
                    startActivity(new Intent(LoadingActivity.this,MainActivity.class));
                }  
            }).show(); 
		}
	}
	

	private void toMainActivity(){
		startActivity(new Intent(LoadingActivity.this,MainActivity.class));
		LoadingActivity.this.finish();
	}
	
	private void toMyLoginActivity(){
		startActivity(new Intent(LoadingActivity.this,MyLoginActivity.class));
		LoadingActivity.this.finish();
	}
	
	private void saveSharePreference(){
		editor.putString("userName", userName);
		editor.putString("userID", userID);
		editor.putString("IMEI", IMEI);
		editor.commit();
	}
	
	private void createAnonymousUser(){
		AVAnonymousUtils.logIn(new LogInCallback<AVUser>() {
		      @Override
		      public void done(AVUser user, AVException e) {
		    	  userID = user.getObjectId();
		    	  user.saveInBackground(new SaveCallback() {
					
					@Override
					public void done(AVException arg0) {
						// TODO Auto-generated method stub
						if(arg0==null){
							editor.putBoolean("userStatus", false);
							editor.commit();
							saveSharePreference();
					          toMainActivity();
						}else{
							Toast.makeText(LoadingActivity.this, "网络异常", Toast.LENGTH_LONG).show();
						}
					}
				});
		          
		      }
		    });
	}
	
	private void initPushMessageId(){
		AVInstallation.getCurrentInstallation().saveInBackground();
	}
}
