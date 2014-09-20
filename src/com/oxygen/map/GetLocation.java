package com.oxygen.map;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.oxygen.main.RadarFragment.MyLocationListener;

public class GetLocation {
	private static LocationClient mLocationClient;
	private static BDLocationListener myListener;
	public static double mCurrentLantitude;// 纬度
	public static double mCurrentLongitude;// 经度
	public static float mCurrentAccracy;// 精度：误差范围，定位圈圈
	private static Context context;

	/**
	* @param 
	* @return void
	* @Description 初始化定位功能  
	*/
	public static void initMyLocation(Context c) {
		context=c;
		mLocationClient = new LocationClient(context);
		myListener =  new MyLocationListener();// 实例化定位监听类对象
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
	public static boolean isLocationDone(){
		if(mCurrentLantitude==0||mCurrentLongitude==0){
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
	public static int getDistance(double Lantitude,double Longitude){
		
		LatLng wallPosition = new LatLng(Lantitude,Longitude);
		
		LatLng mPosition = new LatLng(mCurrentLantitude,mCurrentLongitude);//我的坐标
		
		return (int)DistanceUtil. getDistance(mPosition, wallPosition);
	}
	
	/**
	* @param @param Lantitude
	* @param @param Longitude
	* @param @return
	* @return LatLng
	* @Description 将经度和纬度转换成百度地图坐标  
	*/
	public static LatLng getPosition(double Lantitude,double Longitude){
		return new LatLng(Lantitude,Longitude);
	}
	
	/**
	* @param 
	* @return void
	* @Description 结束定位  
	*/
	public static void shutDownLocation(){
		mLocationClient.stop();
	}
}
