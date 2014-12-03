package com.oxygen.map;

import java.util.List;

import android.content.Context;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult.AddressComponent;
import com.baidu.mapapi.utils.DistanceUtil;
import com.oxygen.data.WallInfoUpload;
import com.oxygen.main.RadarFragment.MyLocationListener;

public class GetLocation implements OnGetGeoCoderResultListener {
	private static LocationClient mLocationClient;
	private static BDLocationListener myListener;
	public static double mCurrentLantitude;// 纬度
	public static double mCurrentLongitude;// 经度
	public static float mCurrentAccracy;// 精度：误差范围，定位圈圈
	private static Context context;
	private GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	private String address;
	private AVObject avobject;
	private String objectID;

	/**
	 * @param
	 * @return void
	 * @Description 初始化定位功能
	 */
	public static void initMyLocation(Context c) {
		context = c;
		mLocationClient = new LocationClient(context);
		myListener = new MyLocationListener();// 实例化定位监听类对象
		mLocationClient.registerLocationListener(myListener);// 注册定位监听
		// 定位参数的相关配置
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开GPS
		option.setCoorType("bd09ll"); // 设置坐标类型，百度坐标
		option.setScanSpan(3000);// 更新间隔时间
		mLocationClient.setLocOption(option);
		mLocationClient.start();// 开启定位API服务

	}

	/**
	 * @ClassName MyLocationListener
	 * @Description 实现内部接口BDLocationListener，重写定位监听的回调方法
	 */
	private static class MyLocationListener implements BDLocationListener {
		@Override
		// BDLocation封装了定位SDK的定位结果，在BDLocationListener的onReceive方法中获取。
		public void onReceiveLocation(BDLocation location) {
			if (location == null) {
				System.out.println("监听定位数据为空");
				return;
			}
			mCurrentAccracy = location.getRadius();
			mCurrentLantitude = location.getLatitude();
			mCurrentLongitude = location.getLongitude();
		}
	}

	public static boolean isLocationDone() {
		if (mCurrentLantitude == 0 || mCurrentLongitude == 0) {
			return false;
		}
		return true;
	}

	/**
	 * @param @param Lantitude
	 * @param @param Longitude
	 * @param @return
	 * @return int
	 * @Description 计算我到留言板的距离
	 */
	public static int getDistance(double Lantitude, double Longitude) {

		LatLng wallPosition = new LatLng(Lantitude, Longitude);

		LatLng mPosition = new LatLng(mCurrentLantitude, mCurrentLongitude);// 我的坐标

		return (int) DistanceUtil.getDistance(mPosition, wallPosition);
	}

	/**
	 * @param @param Lantitude
	 * @param @param Longitude
	 * @param @return
	 * @return LatLng
	 * @Description 将经度和纬度转换成百度地图坐标
	 */
	public static LatLng getPosition(double Lantitude, double Longitude) {
		return new LatLng(Lantitude, Longitude);
	}

	/**
	 * @param
	 * @return void
	 * @Description 结束定位
	 */
	public static void shutDownLocation() {
		mLocationClient.stop();
	}

	public void stopAddress(){
		mSearch.destroy();
	}
	
	/**
	 * @param
	 * @return void
	 * @Description 初始化位置搜索转换监听
	 */
	private void initLoactionSearch() {
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);
	}

	/**
	 * @param @param avo
	 * @return void
	 * @Description 留言板坐标转换地址
	 */
	public void getWallAddress() {
		initLoactionSearch();

		AVUser user = AVUser.getCurrentUser();
		AVQuery<AVObject> query = new AVQuery<AVObject>("WallInfo");
		query.whereEqualTo(WallInfoUpload.USER, user);
		query.orderByDescending("createdAt");
		query.findInBackground(new FindCallback<AVObject>() {

			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				// TODO Auto-generated method stub
				if (arg1 == null) {

					objectID = arg0.get(0).getObjectId();

					AVQuery<AVObject> query = new AVQuery<AVObject>("WallInfo");
					query.getInBackground(objectID,
							new GetCallback<AVObject>() {
								@Override
								public void done(AVObject arg0, AVException arg1) {
									// TODO Auto-generated method stub
									mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(new LatLng(
											arg0.getDouble(WallInfoUpload.LATITUDE),
											arg0.getDouble(WallInfoUpload.LONGITUDE))));// 请求解析该坐标
								}
							});

				}
			}
		});

	}

	private void saveWallAddress(){
		AVQuery<AVObject> query = new AVQuery<AVObject>("WallInfo");
		query.getInBackground(objectID, new GetCallback<AVObject>() {
			@Override
			public void done(AVObject arg0, AVException arg1) {
				// TODO Auto-generated method stub
				if (arg1 == null) {
					arg0.put("address", address);
					arg0.saveInBackground();// 保存地址
				}
			}
		});
	}
	
	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result != null && result.error == SearchResult.ERRORNO.NO_ERROR) {
			address = result.getAddress();// BaiDu API 解析坐标完成,得到地址结果

			saveWallAddress();

		}
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
	}

}
