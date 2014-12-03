package com.oxygen.explore;

import java.util.List;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult.AddressComponent;
import com.oxygen.map.GetLocation;
import com.oxygen.wall.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class ExploreLaunchResultActivity extends Activity implements
		OnClickListener ,OnGetGeoCoderResultListener{

	private TextView titleText;
	private ImageView backBtn;

	private LinearLayout mapViewContainer;
	private MapView mMapView = null;
	private TextView resultText;
	private Button againBtn;

	private LatLng location;
	private String address;
	private AddressComponent addressDetail;
	private MapStatus mMapStatus;
	private BaiduMap baiduMap;
	private BaiduMapOptions options;
	
	private GeoCoder mSearch = null;
	private String fromAddress;
	private AVObject avo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.explore_launch_result);
		this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.my_setting_title_bar);

		titleText = (TextView) findViewById(R.id.my_setting_title_tv);
		backBtn = (ImageView) findViewById(R.id.my_setting_title_bar_back_btn);
		titleText.setText("查看结果");
		setBackBtnListener();

		mapViewContainer = (LinearLayout) findViewById(R.id.explore_launch_result_map_container);
		resultText = (TextView) findViewById(R.id.explore_launch_result_tv);
		againBtn = (Button) findViewById(R.id.explore_launch_again_btn);
		againBtn.setOnClickListener(this);

		getPlaneLocation();
		initLoactionSearch();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// mMapView.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mMapView.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mMapView.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.explore_launch_again_btn:
			Intent intent = new Intent(ExploreLaunchResultActivity.this,
					ExploreLaunchPlaneActivity.class);
			intent.putExtra("again", true);
			startActivity(intent);
			ExploreLaunchResultActivity.this.finish();
			Log.v("plane", "跳转到再飞一次");
			break;
		default:
			break;
		}
	}

	/**
	 * @param
	 * @return void
	 * @Description 获取飞机坐标
	 */
	private void getPlaneLocation() {
		AVUser user = AVUser.getCurrentUser();
		AVQuery<AVObject> query = new AVQuery<AVObject>("Plane");
		query.whereEqualTo("user", user);
		query.addDescendingOrder("createdAt");
		query.findInBackground(new FindCallback<AVObject>() {

			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				// TODO Auto-generated method stub
				if (arg1 == null) {
					avo = arg0.get(0);
					location = new LatLng(avo.getDouble("latitude"), avo
							.getDouble("longitude"));
					address = avo.getString("address");
					setMapView();
					setResultText();
				}
			}
		});
	}

	/**
	 * @param
	 * @return void
	 * @Description 设置地图显示
	 */
	private void setMapView() {
		mMapStatus = new MapStatus.Builder().target(location).zoom(12f).build();// 设置地图显示的起始位置和默认缩放比例
		options = new BaiduMapOptions().zoomControlsEnabled(false)
				.scaleControlEnabled(false).mapStatus(mMapStatus);// 不显示缩放控件和比例尺
		mMapView = new MapView(this, options);
		mapViewContainer.addView(mMapView, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);

		mMapView.removeViewAt(1);// remove baiduMap logo
		mMapView.removeViewAt(2);// 去掉 比例尺
		baiduMap = mMapView.getMap();
		OverlayOptions oo = new MarkerOptions().position(location).icon(
				BitmapDescriptorFactory.fromResource(R.drawable.wall_marker));
		baiduMap.addOverlay(oo);

	}

	private void setResultText() {
		int distance = GetLocation.getDistance(location.latitude,
				location.longitude);
		resultText.setText("纸飞机飞出" + distance / 1000 + "公里远，\n" + "降落在"
				+ address + ".");
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
				ExploreLaunchResultActivity.this.finish();
			}
		});
	}

	
	private void initLoactionSearch() {
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);
		mSearch.reverseGeoCode(new ReverseGeoCodeOption()
		.location(new LatLng(GetLocation.mCurrentLantitude,GetLocation.mCurrentLongitude)));
	}
	
	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result != null && result.error == SearchResult.ERRORNO.NO_ERROR) {
			address = result.getAddress();// BaiDu API 解析坐标完成,得到地址结果
			if (!address.equals("")) {
				avo.put("fromAddress", address);
				avo.saveInBackground();
			}

		}
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
	}

	
	
}
