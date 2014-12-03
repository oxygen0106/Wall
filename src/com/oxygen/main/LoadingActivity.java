package com.oxygen.main;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.avos.avoscloud.AVAnonymousUtils;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SaveCallback;
import com.oxygen.data.UserInfo;
import com.oxygen.my.MyLoginActivity;
import com.oxygen.wall.R;

public class LoadingActivity extends Activity {

	private ProgressBar progressBar;
	private LinearLayout logoLayout;
	
	private String IMEI = null;
	
	private boolean isFirstOpen;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.loading_activity);
		progressBar = (ProgressBar) findViewById(R.id.progress_bar);
		logoLayout = (LinearLayout)findViewById(R.id.loading_logo);
		logoLayout.setVisibility(View.INVISIBLE);
		firstOpen();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (isOpenNetwork()) {
			if(!thread.isAlive()){
				thread.start();
			}else{
				anotherThread.start();
			}
		} else {
			setNetworkDialog();// 设置网络
		}
	}

	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				startAnimation();
			}else{
				checkUserStatus();
			}
		};
	};
	
	Thread thread = new Thread() {
		public void run() {
			Message msg = Message.obtain();
			msg.what=0;
			handler.sendMessage(msg);
			Message msg2 = Message.obtain();
			msg2.what = 1;
			handler.sendMessageDelayed(msg2, 2500);
		};

	};
	
	Thread anotherThread = new Thread() {
		public void run() {
			Message msg = Message.obtain();
			msg.what = 1;
			handler.sendMessage(msg);
		};

	};
	
	private void startAnimation(){
		AnimationSet animset = new AnimationSet(true);
		Animation animation = null;
		animset.setInterpolator(new OvershootInterpolator(3F));
		animation = new TranslateAnimation(0, 0, 150, 0);
//		animation.setFillAfter(true);
		animation.setDuration(2000);
		RotateAnimation rotate = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		rotate.setDuration(1000);
		rotate.setFillAfter(true);
//		animset.addAnimation(rotate);
		animset.addAnimation(animation);
		
		logoLayout.setAnimation(animset);
		logoLayout.setVisibility(View.VISIBLE);
		
	}
	
	
	/**
	* @param 
	* @return void
	* @Description 若检测到是第一打开该应用，跳转至欢迎界面  
	*/
	private void firstOpen(){
		SharedPreferences openSP = this.getSharedPreferences("open",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = openSP.edit();

		isFirstOpen = openSP.getBoolean("isFirstOpen", true);
		if(isFirstOpen == true){
			editor.putBoolean("isFirstOpen", false);
			editor.commit();
			startActivity(new Intent(LoadingActivity.this, WelcomeActivity.class));
			LoadingActivity.this.finish();
		}
	}
	
	
	private void checkUserStatus() {
		AVUser currentUser = AVUser.getCurrentUser();
		if (currentUser != null) {// 本地存在用户缓存信息
			toMainActivity();
			// if (currentUser.getString("email")==null) {

		} else {
			IMEI = getIMEI();
			if (IMEI == null || IMEI.equals("")) {// 获取不到本机IMEI
				createAnonymousUser();
			} else {
				isImeiInCloud();// 检测IMEI判断是否为注册用户
			}

		}
	}

	private void createAnonymousUser() {
		AVAnonymousUtils.logIn(new LogInCallback<AVUser>() {
			@Override
			public void done(AVUser user, AVException e) {
				toMainActivity();
			}
		});
	}

	

	/**
	 * @param @return
	 * @return String
	 * @Description 获取IMEI
	 */
	public String getIMEI() {
		TelephonyManager telephonemanage = (TelephonyManager) LoadingActivity.this
				.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonemanage.getDeviceId();
	}

	public void isImeiInCloud() {
		AVQuery<AVUser> query = AVUser.getQuery();// 获取_User查询请求
		query.whereEqualTo(UserInfo.IMEI, IMEI);
		query.findInBackground(new FindCallback<AVUser>() {
			@Override
			public void done(List<AVUser> arg0, AVException arg1) {
				if (arg1 == null) {
					if (arg0.size() != 0) {// 服务器数据库存在该串号
						toMyLoginActivity();
					} else {// 查询串号不存在
						createAnonymousUser();
					}
				} else {
					createAnonymousUser();
				}
			}
		});
	}

	/**
	 * @param @return
	 * @return boolean
	 * @Description 当前网络是否可用
	 */
	public boolean isOpenNetwork() {
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connManager.getActiveNetworkInfo() != null) {
			return connManager.getActiveNetworkInfo().isAvailable();
		} else {
			return false;
		}
	}

	/**
	 * @param @return
	 * @return boolean
	 * @Description 网络设置提示
	 */
	private void setNetworkDialog() {
		if (isOpenNetwork() != true) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					LoadingActivity.this);
			builder.setTitle("没有可用的网络").setMessage("是否对网络进行设置?");

			builder.setPositiveButton("是",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = null;

							try {
								if (android.os.Build.VERSION.SDK_INT > 10) {
									intent = new Intent(
											android.provider.Settings.ACTION_WIRELESS_SETTINGS);
								} else {
									intent = new Intent();
									ComponentName comp = new ComponentName(
											"com.android.settings",
											"com.android.settings.WirelessSettings");
									intent.setComponent(comp);
									intent.setAction("android.intent.action.VIEW");
								}
								LoadingActivity.this.startActivity(intent);
								LoadingActivity.this.finish();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					})
					.setNegativeButton("否",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
									finish();
									startActivity(new Intent(
											LoadingActivity.this,
											MainActivity.class));
								}
							}).show();
		}
	}

	private void toMainActivity() {

		startActivity(new Intent(LoadingActivity.this, MainActivity.class));
		LoadingActivity.this.finish();
	}

	private void toMyLoginActivity() {
		Intent  intent = new Intent(LoadingActivity.this, MyLoginActivity.class);
		intent.putExtra("isExit", true);
		startActivity(intent);
		LoadingActivity.this.finish();
	}

}
