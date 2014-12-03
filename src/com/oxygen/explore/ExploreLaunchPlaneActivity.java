package com.oxygen.explore;

import java.util.Currency;
import java.util.List;
import java.util.Random;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.oxygen.explore.Shake.ShakeListener;
import com.oxygen.map.GetLocation;
import com.oxygen.my.MyMessageActivity;
import com.oxygen.my.MyPopWindow;
import com.oxygen.wall.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ExploreLaunchPlaneActivity extends Activity implements
		OnClickListener, OnGetGeoCoderResultListener {

	private TextView titleText;
	private ImageView backBtn;

	private EditText launchText;
	private Button btnOK;

	private TextView tipText;
	private LinearLayout tipLayout;

	private ExploreLaunchPopWindow myPopWindow;

	private Shake shake;
	private AVObject avo;

	private GeoCoder mSearch = null;
	private String address;
	private ProgressDialog dialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.explore_launch_plane);
		this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.my_setting_title_bar);

		titleText = (TextView) findViewById(R.id.my_setting_title_tv);
		backBtn = (ImageView) findViewById(R.id.my_setting_title_bar_back_btn);
		titleText.setText("掷飞机");
		setBackBtnListener();

		launchText = (EditText) findViewById(R.id.explore_launch_content_tv);
		btnOK = (Button) findViewById(R.id.explore_launch_content_ok);
		tipText = (TextView) findViewById(R.id.explore_launch_tip);
		tipLayout = (LinearLayout) findViewById(R.id.explore_launch_tip_layout);
		btnOK.setOnClickListener(this);
		
		isLaunchAgain();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// mSearch.destroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.explore_launch_content_ok:
			String content = launchText.getText().toString();
			if (content.equals("") || content == null) {
				Toast.makeText(ExploreLaunchPlaneActivity.this, "请填写完整内容",
						Toast.LENGTH_LONG).show();
			} else {
				AVUser user = AVUser.getCurrentUser();
				AVObject avo = new AVObject("Plane");
				avo.put("content", content);
				avo.put("user", user);
				avo.saveInBackground();
				hideInputView();
			}
			break;
		default:
			break;
		}
	}

	/**
	 * @param
	 * @return void
	 * @Description 返回键
	 */
	private void setBackBtnListener() {
		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ExploreLaunchPlaneActivity.this.finish();
			}
		});
	}

	private void hideInputView() {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				Log.v("plane", "开启线程转主线程");
				launchText.setVisibility(View.GONE);
				btnOK.setVisibility(View.GONE);
				tipLayout.setVisibility(View.VISIBLE);
				setShakeListener();// 开启摇一摇
				initLoactionSearch();// 初始化地理位置
			}
		}.execute();
	}

	private void setShakeListener() {
		shake = new Shake(ExploreLaunchPlaneActivity.this);
		shake.setShakeListener(new ShakeListener() {

			@Override
			public void onShakeListener() {
				// TODO Auto-generated method stub
				
				AVUser user = AVUser.getCurrentUser();
				AVQuery<AVObject> query = new AVQuery<AVObject>("Plane");
				// query.include("user");
				query.whereEqualTo("user", user);
				query.addDescendingOrder("createdAt");
				query.findInBackground(new FindCallback<AVObject>() {

					@Override
					public void done(List<AVObject> arg0, AVException arg1) {
						// TODO Auto-generated method stub
						if (arg1 == null) {
							showProgressDialog();
							avo = arg0.get(0);
							double latitude = GetLocation.mCurrentLantitude
									+ getRandom();
							double longitude = GetLocation.mCurrentLongitude
									+ getRandom();
							mSearch.reverseGeoCode(new ReverseGeoCodeOption()
									.location(new LatLng(latitude, longitude)));
							avo.put("latitude", latitude);
							avo.put("longitude", longitude);
							avo.saveInBackground();
							shake.stopShakeListener();// 注销传感器
						}
					}
				});

			}
		});

	}

	/**
	* @param @return
	* @return double
	* @Description 产生-5到+5的随机浮点数  
	*/
	private double getRandom() {
		int max = 5;
		Random random = new Random();
		double i = max - random.nextDouble() * 10;// [0,10)范围
		return i;
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result != null && result.error == SearchResult.ERRORNO.NO_ERROR) {
			address = result.getAddress();// BaiDu API 解析坐标完成,得到地址结果
			if (!address.equals("")) {
				avo.put("address", address);
				avo.saveInBackground(new SaveCallback() {

					@Override
					public void done(AVException arg0) {
						// TODO Auto-generated method stub
						startActivity(new Intent(
								ExploreLaunchPlaneActivity.this,
								ExploreLaunchResultActivity.class));
						ExploreLaunchPlaneActivity.this.finish();
						stopProgressDialog();
					}
				});
			}

		}
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
	}

	private void initLoactionSearch() {
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);
	}

	
	/**
	* @param 
	* @return void
	* @Description 是否为重新发射  
	*/
	private void isLaunchAgain(){
		Intent intent = getIntent();
		if (intent.getBooleanExtra("again", false)) {
			launchText.setVisibility(View.GONE);
			btnOK.setVisibility(View.GONE);
			tipLayout.setVisibility(View.VISIBLE);
			setShakeListener();// 开启摇一摇
			initLoactionSearch();// 初始化地理位置
		}
	}
	
	private void showProgressDialog(){
		dialog = new ProgressDialog(this);
		dialog.setMessage("飞行中...");
		Window window = dialog.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.alpha = 0.7f;
		lp.dimAmount = 0.8f;
		window.setAttributes(lp);
		dialog.show();
	}
	
	private void stopProgressDialog(){
		dialog.dismiss();
	}
}
