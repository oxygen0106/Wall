package com.oxygen.main;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfigeration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MyLocationConfigeration.LocationMode;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.model.LatLng;
import com.oxygen.map.RadarView;
import com.oxygen.wall.R;
import com.oxygen.data.WallInfoDownload;
//import com.oxygen.map.MyOrientationListener;
//import com.oxygen.map.MyOrientationListener.OnOrientationListener;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

/**
 * @ClassName RadarFragment
 * @Description 在地图上显示用户附近的虚拟留言板，及相关界面交互
 * @author oxygen
 * @email oxygen0106@163.com
 * @date 2014-8-18 下午09:31:15
 */
public class RadarFragment extends Fragment {

	int WALLS_INIT_OK = 1;// 从服务器获取留言板数据完成
	int RADAR_SWEEP_STOP = -1;// 停止雷达扫描
	int RADAR_VIEW_GONE = 3;// 设置radarView消失
	Activity activity;
	public SupportMapFragment mapView;
	BaiduMap baiduMap;
	BaiduMapOptions options;
	MapStatus mMapStatus;
	FrameLayout mapViewContainer;
	LinearLayout layoutBtnLocation;
	public LocationClient mLocationClient;
	public BDLocationListener myListener;
	public double mCurrentLantitude;
	public double mCurrentLongitude;
	public float mCurrentAccracy;
	MyLocationData locData;
	BitmapDescriptor mCurrentMarkerIcon = BitmapDescriptorFactory
			.fromResource(R.drawable.route_map_icon_nav);// 定位点图标;
	MyLocationConfigeration config;// 定位模式设置

	BitmapDescriptor wallMarkerIcon = BitmapDescriptorFactory
			.fromResource(R.drawable.wall_marker);// 留言板标记图标bitmap
	Marker[] wallMarker;// 附件留言板在地图上的标记
	List<WallInfoDownload> aroundWalls;// 附近留言板信息
	private InfoWindow mInfoWindow;// 地图弹窗
	LinearLayout markerPopupWindow;// 弹窗Layout
	TextView tvPopup;// 弹窗内TextView
	ImageView markerArrow; // 弹窗内ImageView

	LinearLayout radarViewLayout;// 雷达View的ViewGroup
	RadarView radarView;// 雷达View
	Thread radarSweepThread;
	Animation radarAnimBound;
	Animation radarAnimEnter;// 雷达载入动画
	Animation radarAnimExit;// 雷达退出动画
	Button btnLocation;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		// Debug.startMethodTracing();//TraceView调试开始
		super.onAttach(activity);
		this.activity = activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			mCurrentLantitude = savedInstanceState.getDouble("Lantitude");
			mCurrentLongitude = savedInstanceState.getDouble("Longitude");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.radar_fragment, container, false);
		mapViewContainer = (FrameLayout) view
				.findViewById(R.id.mapview_container);

		mMapStatus = new MapStatus.Builder()
				.target(new LatLng(mCurrentLantitude, mCurrentLongitude))
				.zoom(16f).build();// 设置地图显示的起始位置和默认缩放比例
		options = new BaiduMapOptions().zoomControlsEnabled(false)
				.scaleControlEnabled(false).mapStatus(mMapStatus);// 不显示缩放控件和比例尺
		mapView = SupportMapFragment.newInstance(options);
		FragmentManager manager = getChildFragmentManager();// 嵌套Fragment
		manager.beginTransaction().add(R.id.mapview_container, mapView)
				.commit();// 显示地图

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		if (mCurrentLantitude != 0.0 || mCurrentLantitude != 39.98871) {
			outState.putDouble("Lantitude", mCurrentLantitude);
			outState.putDouble("Longitude", mCurrentLongitude);
		}
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		addRadarView();// 显示雷达View动画

		new Thread(new GetWallsInfoThread()).start();// 开启获取服务器留言板线程
		// Debug.stopMethodTracing();//TraceView调试结束
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		baiduMap.setMyLocationEnabled(false);
		mLocationClient.stop();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mCurrentMarkerIcon.recycle();// 回收 bitmap 资源
		wallMarkerIcon.recycle();// 回收 bitmap 资源
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			if (msg.what == WALLS_INIT_OK) {
				// TODO
				initMyLocation();// 初始化LocationClient,设置监听器和定位参数实例，开启定位功能
				initOverlay();// 获取留言板信息完毕，在地图上添加Marker图层
				RADAR_SWEEP_STOP = 1;// 告诉RadarRefresh线程应该尽快结束扫描
			} else if (msg.what == RADAR_VIEW_GONE) {
				radarAnimExit = AnimationUtils.loadAnimation(activity,
						R.anim.radar_anim_exit);
				radarView.startAnimation(radarAnimExit);// 加载退出动画

				radarView.setVisibility(View.GONE);// 雷达消失
			}
		}
	};

	/**
	 * @param
	 * @return void
	 * @Description 初始化 LocationClient及参数，添加定位监听
	 */
	public void initMyLocation() {

		// mapView = SupportMapFragment.newInstance(options);

		// FragmentManager manager = getChildFragmentManager();// 嵌套Fragment
		// manager.beginTransaction().add(R.id.mapview_container, mapView)
		// .commit();// 显示地图
		baiduMap = mapView.getBaiduMap();// MapView的管理器 baiduMap
		mLocationClient = new LocationClient(activity.getApplicationContext());
		myListener = new MyLocationListener();// 实例化定位监听类对象
		mLocationClient.registerLocationListener(myListener);// 注册定位监听
		// 定位参数的相关配置
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开GPS
		option.setCoorType("bd09ll"); // 设置坐标类型，百度坐标
		option.setScanSpan(5000);// 更新间隔时间
		mLocationClient.setLocOption(option);
		mLocationClient.start();// 开启定位API服务
		config = new MyLocationConfigeration(LocationMode.FOLLOWING, false,
				mCurrentMarkerIcon);
		baiduMap.setMyLocationConfigeration(config);
		baiduMap.setMyLocationEnabled(true);// 开启定位功能

		LayoutInflater.from(activity).inflate(
				R.layout.radar_fragment_btn_location, mapViewContainer, true);
		btnLocation = (Button) getView().findViewById(R.id.btn_location);
		btnLocation.setOnClickListener(new OnClickListener() {

			/**
			 * @Description 定位按钮+雷达扫描
			 */
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MapStatusUpdate u = MapStatusUpdateFactory
						.newLatLng(new LatLng(mCurrentLantitude,
								mCurrentLongitude));
				baiduMap.animateMapStatus(u);

				radarSweepThread.interrupt();
				radarView.setVisibility(View.VISIBLE);// 设置可见
				radarView.startAnimation(radarAnimEnter);// 开始进入动画
				radarSweepThread = new Thread(new RadarRefresh());// 雷达扫描线程
				radarSweepThread.start();

			}
		});

	}

	/**
	 * @ClassName MyLocationListener
	 * @Description 实现内部接口BDLocationListener，重写定位监听的回调方法
	 * @author oxygen
	 * @email oxygen0106@163.com
	 * @date 2014-8-24 下午12:31:19
	 */
	public class MyLocationListener implements BDLocationListener {
		@Override
		// BDLocation封装了定位SDK的定位结果，在BDLocationListener的onReceive方法中获取。
		public void onReceiveLocation(BDLocation location) {
			if (location == null || mapView == null)// mapView 销毁后不在处理新接收的位置
				return;
			// 构造定位数据对象，从location中获取
			locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					.latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();

			baiduMap.setMyLocationData(locData);
			// 更新当前坐标数据
			mCurrentAccracy = location.getRadius();
			mCurrentLantitude = location.getLatitude();
			mCurrentLongitude = location.getLongitude();
		}
	}

	/**
	 * @param
	 * @return void
	 * @Description 监听定位方向，重新MyOrientationListener传感器监听的回调方法
	 */
	/*
	 * public void initMyOrientation(){ myOrientationListener = new
	 * MyOrientationListener(getApplicationContext());
	 * myOrientationListener.setOnOrientationListener(new
	 * OnOrientationListener(){
	 * 
	 * @Override//回调方法 public void onOrientationChanged(float x) { myDirection =
	 * x; // 构造定位数据 MyLocationData locData = new MyLocationData.Builder()
	 * .accuracy(mCurrentAccracy) // 此处设置获取到的方向信息，顺时针0-360
	 * .direction(myDirection) .latitude(mCurrentLantitude)
	 * .longitude(mCurrentLongitude).build(); // 设置定位数据
	 * baiduMap.setMyLocationData(locData); // 设置自定义图标 BitmapDescriptor
	 * mCurrentMarker = BitmapDescriptorFactory
	 * .fromResource(R.drawable.route_map_icon_nav); MyLocationConfigeration
	 * config = new MyLocationConfigeration( LocationMode.FOLLOWING, true,
	 * mCurrentMarker); baiduMap.setMyLocationConfigeration(config); } }); }
	 */

	/**
	 * @ClassName GetWallsInfoThread
	 * @Description 从服务器获取留言板信息网络线程
	 */
	private class GetWallsInfoThread implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub

			getWallsInfo();// 本地模拟数据

			// TODO 添加判断：当获取留言板信息完毕，发送该消息
			Message m = Message.obtain();
			m.what = WALLS_INIT_OK;
			handler.sendMessage(m);
		}
	}

	/**
	 * @param
	 * @return void
	 * @Description 获取模拟附近留言板信息 ，本地模拟
	 */
	public void getWallsInfo() {
		aroundWalls = new ArrayList<WallInfoDownload>();
		// aroundWalls.add(new WallInfo(1, 007, new LatLng(39.98871, 116.43234),
		// 1000));//天安门坐标
		// aroundWalls.add(new WallInfo(1, 007, new LatLng(29.536881,
		// 106.611048), 1000));//信科坐标
		aroundWalls.add(new WallInfoDownload("1", "", 29.533881, 106.611048,"","0",0,0));
		aroundWalls.add(new WallInfoDownload("1", "", 29.539881, 106.611048,"","0",0,0));
		aroundWalls.add(new WallInfoDownload("3", "", 29.536881, 106.608048,"","0",0,0));
		aroundWalls.add(new WallInfoDownload("4", "", 29.536881, 106.614048,"","0",0,0));
	}

	/**
	 * @param
	 * @return void
	 * @Description 在地图上添加留言板标记，并实例化对应的Marker
	 */
	public void initOverlay() {
		int num = aroundWalls.size();
		wallMarker = new Marker[num];// 根据附近留言板数量实例化Marker数组大小
		for (int i = 0; i < num; i++) {

			OverlayOptions oo = new MarkerOptions()
					.position(aroundWalls.get(i).getLocation())
					.icon(wallMarkerIcon).zIndex(9 - i);// zIndex()在Z轴上的比例
			wallMarker[i] = (Marker) (baiduMap.addOverlay(oo));// 在地图中添加标记，并实例化Marker
		}
		wallMarkerClickListener();// 添加所有留言板标记Marker的监听事件
	}

	/**
	 * @param @param view
	 * @return void
	 * @Description 清除所有地图标记覆盖
	 */
	public void clearOverlay(View view) {
		baiduMap.clear();
	}

	/**
	 * @param
	 * @return void
	 * @Description 留言板标记Marker监听
	 */
	public void wallMarkerClickListener() {
		baiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(final Marker marker) {
				// TODO Auto-generated method stub
				LayoutInflater inflater = LayoutInflater.from(activity);
				ViewGroup root = (ViewGroup) getView().findViewById(
						R.id.mapview_container);
				View v = inflater.inflate(R.layout.radar_marker_popup, root, false);

				markerPopupWindow = (LinearLayout) v
						.findViewById(R.id.marker_popup_container);
				// markerPopupWindow.setBackgroundResource(R.drawable.popup);
				markerPopupWindow.getBackground().setAlpha(200);// 设置弹窗背景Alpha值
				tvPopup = (TextView) v.findViewById(R.id.tv_wallId);//
				markerArrow = (ImageView) v.findViewById(R.id.marker_arrow);
				final LatLng markerLatLng = marker.getPosition();
				Point p = baiduMap.getProjection().toScreenLocation(
						markerLatLng);
				p.y -= 40;// 向上移动至Marker的顶部
				LatLng llInfo = baiduMap.getProjection().fromScreenLocation(p);// 转换成坐标
				OnInfoWindowClickListener listener = null;

				for (int i = 0; i < wallMarker.length; i++) {
					if (marker == wallMarker[i]) {
						tvPopup.setText("ID: 00"
								+ aroundWalls.get(i).getWallID());
						listener = new OnInfoWindowClickListener() {
							public void onInfoWindowClick() {// 点击弹窗事件处理
								clickInfoWindow(); // 弹窗点击事件执行
							}
						};
					}
				}
				mInfoWindow = new InfoWindow(markerPopupWindow, llInfo,
						listener);
				baiduMap.showInfoWindow(mInfoWindow);
				return true;
			}
		});
	}

	/**
	 * @param
	 * @return void
	 * @Description Marker弹窗点击事件执行
	 */
	public void clickInfoWindow() {
		// TODO
		baiduMap.hideInfoWindow();
	}

	/**
	 * @param
	 * @return void
	 * @Description 添加自定义雷达View
	 */
	public void addRadarView() {

		LayoutInflater inflater = LayoutInflater
				.from(activity.getApplication());
		View v = inflater.inflate(R.layout.radar_radarview, mapViewContainer, true);
		radarViewLayout = (LinearLayout) v.findViewById(R.id.radarViewLayout);
		radarViewLayout.getBackground().setAlpha(10);
		radarView = (RadarView) getView().findViewById(R.id.RadarView);// 实例化radarView
		radarView.setVisibility(View.VISIBLE);// 设置可见
		radarAnimEnter = AnimationUtils.loadAnimation(activity,
				R.anim.radar_anim_enter);// 初始化radarView进入动画
		radarView.startAnimation(radarAnimEnter);// 开始进入动画

		radarSweepThread = new Thread(new RadarRefresh());// 雷达扫描线程
		radarSweepThread.start();

	}

	/**
	 * @ClassName Refresh
	 * @Description 雷达动画刷新线程类
	 */
	private class RadarRefresh implements Runnable {
		int i = 1;

		int flag = 0;

		@Override
		public void run() {
			// TODO Auto-generated method stub

			while (!Thread.currentThread().isInterrupted() && i > 0) {
				try {
					radarView.postInvalidate();// 刷新radarView, 执行onDraw();
					Thread.sleep(10);// 停止当前线程，让UI线程更新View
				} catch (InterruptedException e) {
					i = -1;
					break;
				}

				if (RADAR_SWEEP_STOP == 1 && flag == 0) {// 收到主线程停止扫描消息，设置最后扫描次数
					i = 1080;
					flag = 1;
				} else if (RADAR_SWEEP_STOP == 1 && flag == 1) {
					i--;
				}

			}

			if (i == 0) {
				Message mGone = Message.obtain();
				mGone.what = RADAR_VIEW_GONE;
				handler.sendMessage(mGone);
				return;
			}
		}

	}

	
	
	
	
}
