package com.oxygen.main;

import java.io.File;

import com.avos.avoscloud.AVUser;
import com.oxygen.ar.ARPopWindow;
import com.oxygen.ar.ARWallCreateActivity;
import com.oxygen.data.UserInfo;
import com.oxygen.my.MyPopWindow;
import com.oxygen.my.MySettingActivity;
import com.oxygen.wall.R;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @ClassName MainActivity
 * @Description 主界面
 * @author oxygen
 * @email oxygen0106@163.com
 * @date 2014-8-14 下午1:18:37
 */
public class MainActivity extends FragmentActivity implements
		View.OnClickListener {

	public Fragment wallSquareFragment;
	public Fragment wallNearbyFragment;
	public RadarFragment radarFragment;
	// public Fragment radarFragment;
	public Fragment favourFragment;
	public Fragment myFragment;
	

	public FrameLayout wallFrameLayout;
	public FrameLayout radarFrameLayout;
	public FrameLayout favourFrameLayout;
	public FrameLayout myFrameLayout;
	public FrameLayout arFrameLayout;
	public FrameLayout container;

	public ImageView wallImageView;
	public ImageView radarImageView;
	public ImageView favourImageView;
	public ImageView myImageView;
	public ImageView arImageView;

	public ImageView wallImageLine;
	public ImageView radarImageLine;
	public ImageView favourImageLine;
	public ImageView myImageLine;

	public TextView titleText;
	private ImageView mySetting;
	private TextView wallSquare;
	private TextView wallNearby;
	
	private ARPopWindow arPopWindow;

	FragmentManager fragmentManager;
	FragmentTransaction fragmentTransaction;

	LinearLayout layoutRadarFragment;

	LinearLayout layoutWallBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);// 自定义标题栏模式
		setContentView(R.layout.main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.main_title_bar);// 加载自定义标题栏
		// ActionBar actionBar = getActionBar();//API Level 11

		createFile();//检测用户身份
		
		titleText = (TextView) findViewById(R.id.title_tv);// 标题栏TextView
	
		mySetting = (ImageView) findViewById(R.id.my_setting);// 用户界面TitleBar
																// 设置按钮
		wallSquare=(TextView)findViewById(R.id.wall_square);
		wallNearby=(TextView)findViewById(R.id.wall_nearby);
		
		titleText.setText("留言板");// 设置TitleBar的TextView

		layoutWallBar=(LinearLayout)findViewById(R.id.wall_tab);
		wallFrameLayout = (FrameLayout) findViewById(R.id.framelayout_wall);// 菜单按钮
		radarFrameLayout = (FrameLayout) findViewById(R.id.framelayout_radar);
		favourFrameLayout = (FrameLayout) findViewById(R.id.framelayout_favour);
		myFrameLayout = (FrameLayout) findViewById(R.id.framelayout_my);
		arFrameLayout = (FrameLayout) findViewById(R.id.framelayout_ar);

		wallFrameLayout.setTag(1);// 按钮添加标签
		wallFrameLayout.setOnClickListener(this);// 按钮添加监听
		radarFrameLayout.setTag(2);
		radarFrameLayout.setOnClickListener(this);
		favourFrameLayout.setTag(3);
		favourFrameLayout.setOnClickListener(this);
		myFrameLayout.setTag(4);
		myFrameLayout.setOnClickListener(this);
		arFrameLayout.setTag(5);
		arFrameLayout.setOnClickListener(this);
		wallSquare.setTag(6);
		wallSquare.setOnClickListener(this);
		wallNearby.setTag(7);
		wallNearby.setOnClickListener(this);
		

		wallImageView = (ImageView) findViewById(R.id.image_wall);// 按钮图片
		radarImageView = (ImageView) findViewById(R.id.image_radar);
		favourImageView = (ImageView) findViewById(R.id.image_favour);
		myImageView = (ImageView) findViewById(R.id.image_my);
		arImageView = (ImageView) findViewById(R.id.image_ar);

		wallImageLine = (ImageView) findViewById(R.id.wall_image_line);// 按钮下划线
		radarImageLine = (ImageView) findViewById(R.id.radar_image_line);
		favourImageLine = (ImageView) findViewById(R.id.favour_image_line);
		myImageLine = (ImageView) findViewById(R.id.my_image_line);

		clickWallFrameLayout();// 默认菜单点击留言板

		fragmentManager = getSupportFragmentManager();// 获得FragmentManager
		wallSquareFragment = fragmentManager.findFragmentById(R.id.wallsquarefragment);// 加载Fragment
		wallNearbyFragment = fragmentManager.findFragmentById(R.id.wallnearbyfragment);
		radarFragment = new RadarFragment();
		favourFragment = fragmentManager.findFragmentById(R.id.explorefragment);
		myFragment = fragmentManager.findFragmentById(R.id.myfragment);

		fragmentTransaction = fragmentManager.beginTransaction()
				.hide(wallSquareFragment).hide(wallNearbyFragment).hide(favourFragment).hide(myFragment);// 隐藏Fragment
		fragmentTransaction.show(wallSquareFragment).commit();// 默认显示wallFragment
	}

	/**
	 * @Description 菜单按钮监听事件回调方法
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int tag = (Integer) v.getTag();
		// 一个fragmentTransaction对象只能commit()一次,此处重新获得一个对象来commit()
		fragmentTransaction = fragmentManager.beginTransaction()
				.hide(wallSquareFragment).hide(wallNearbyFragment).hide(radarFragment).hide(favourFragment)
				.hide(myFragment);
		switch (tag) {
		case 1:
			clickWallFrameLayout();

			
			fragmentTransaction.show(wallSquareFragment).commit();
			break;
		case 2:
			clickRadarFrameLayout();

			if (!radarFragment.isAdded()) {// 动态加载
				fragmentTransaction.add(R.id.fragment_container, radarFragment);
				fragmentTransaction.show(radarFragment).commit();
			} else {
				fragmentTransaction.show(radarFragment).commit();
			}
			break;

		case 3:
			clickFavourFrameLayout();

			fragmentTransaction.show(favourFragment).commit();
			break;
		case 4:
			clickMyFrameLayout();

			fragmentTransaction.show(myFragment).commit();
			setMySettingListener();
			break;
		case 5:
			clickARFrameLayout();
			setARPopWindow();
			break;
		case 6:
			fragmentTransaction.show(wallSquareFragment).commit();
			break;
		case 7:
			fragmentTransaction.show(wallNearbyFragment).commit();
			break;
		}

	}

	/**
	 * @param
	 * @return void
	 * @Description WallFrameLayout菜单按钮背景图片切换，TitleBar切换
	 */
	private void clickWallFrameLayout() {
		//titleText.setText("留言板");
		titleText.setVisibility(View.GONE);
		mySetting.setVisibility(View.GONE);
		layoutWallBar.setVisibility(View.VISIBLE);
		
		wallFrameLayout.setSelected(true);
		wallImageView.setSelected(true);
		wallImageLine.setSelected(true);
		radarFrameLayout.setSelected(false);
		radarImageView.setSelected(false);
		radarImageLine.setSelected(false);
		favourFrameLayout.setSelected(false);
		favourImageView.setSelected(false);
		favourImageLine.setSelected(false);
		myFrameLayout.setSelected(false);
		myImageView.setSelected(false);
		myImageLine.setSelected(false);
		arImageView.setSelected(false);
	}

	/**
	 * @param
	 * @return void
	 * @Description RadarFrameLayout菜单按钮背景图片切换，TitleBar切换
	 */
	private void clickRadarFrameLayout() {
		titleText.setText("雷达");
		mySetting.setVisibility(View.GONE);
		layoutWallBar.setVisibility(View.GONE);
		
		radarFrameLayout.setSelected(true);
		radarImageView.setSelected(true);
		radarImageLine.setSelected(true);
		wallFrameLayout.setSelected(false);
		wallImageView.setSelected(false);
		wallImageLine.setSelected(false);
		favourFrameLayout.setSelected(false);
		favourImageView.setSelected(false);
		favourImageLine.setSelected(false);
		myFrameLayout.setSelected(false);
		myImageView.setSelected(false);
		myImageLine.setSelected(false);
		arImageView.setSelected(false);
	}

	/**
	 * @param
	 * @return void
	 * @Description FavourFrameLayout菜单按钮背景图片切换，TitleBar切换
	 */
	private void clickFavourFrameLayout() {
		titleText.setText("探索");
		mySetting.setVisibility(View.GONE);
		layoutWallBar.setVisibility(View.GONE);

		favourFrameLayout.setSelected(true);
		favourImageView.setSelected(true);
		favourImageLine.setSelected(true);
		wallFrameLayout.setSelected(false);
		wallImageView.setSelected(false);
		wallImageLine.setSelected(false);
		radarFrameLayout.setSelected(false);
		radarImageView.setSelected(false);
		radarImageLine.setSelected(false);
		myFrameLayout.setSelected(false);
		myImageView.setSelected(false);
		myImageLine.setSelected(false);
		arImageView.setSelected(false);
	}

	/**
	 * @param
	 * @return void
	 * @Description MyFrameLayout菜单按钮背景图片切换，TitleBar切换
	 */
	private void clickMyFrameLayout() {
		titleText.setText("我的");
		mySetting.setVisibility(View.VISIBLE);
		layoutWallBar.setVisibility(View.GONE);

		myFrameLayout.setSelected(true);
		myImageView.setSelected(true);
		myImageLine.setSelected(true);
		wallFrameLayout.setSelected(false);
		wallImageView.setSelected(false);
		wallImageLine.setSelected(false);
		radarFrameLayout.setSelected(false);
		radarImageView.setSelected(false);
		radarImageLine.setSelected(false);
		favourFrameLayout.setSelected(false);
		favourImageView.setSelected(false);
		favourImageLine.setSelected(false);
		arImageView.setSelected(false);
	}

	/**
	 * @param
	 * @return void
	 * @Description ARFrameLayout菜单按钮背景图片切换，TitleBar切换
	 */
	private void clickARFrameLayout() {
//		titleText.setText("AR");
		mySetting.setVisibility(View.GONE);
		layoutWallBar.setVisibility(View.GONE);

		arImageView.setSelected(true);
		myFrameLayout.setSelected(false);
		myImageView.setSelected(false);
		myImageLine.setSelected(false);
		wallFrameLayout.setSelected(false);
		wallImageView.setSelected(false);
		wallImageLine.setSelected(false);
		radarFrameLayout.setSelected(false);
		radarImageView.setSelected(false);
		radarImageLine.setSelected(false);
		favourFrameLayout.setSelected(false);
		favourImageView.setSelected(false);
		favourImageLine.setSelected(false);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	// 设置Title Bar Setting按钮监听事件
	public void setMySettingListener() {
		mySetting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this,
						MySettingActivity.class);
				startActivity(intent);
			}
		});
	}
	
	private void setARPopWindow(){
		// 创建itemOnClick对象，初始化popWindow
		OnClickListener itemsOnClick = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				arPopWindow.dismiss();// 收回PopWindow
				switch (v.getId()) {
				case R.id.ar_pop_btn_camera:

Toast.makeText(MainActivity.this, "待添加AR方法", Toast.LENGTH_SHORT).show();
					//TODO AR拍摄方法
					break;
				
				case R.id.ar_pop_btn_cancel:
					break;
				case R.id.ar_pop_btn_wall:
					Intent intent=new Intent(MainActivity.this,ARWallCreateActivity.class);
					startActivity(intent);
					break;
				default:
					break;
				}
			}
		};

		arPopWindow = new ARPopWindow(MainActivity.this, itemsOnClick);
		// 设置layout在PopupWindow中显示的位置
		arPopWindow.showAtLocation(
				MainActivity.this.findViewById(R.id.main_layout), Gravity.BOTTOM
						| Gravity.CENTER_HORIZONTAL, 0, 0);

	}
	
	/**
	* @param 
	* @return void
	* @Description 根据当前用户状态，创建文件夹路径  
	*/
	private void createFile(){
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			File userFile = new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+"/Android/data/"+this.getPackageName()+"/user");
			File wallFile = new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+"/Android/data/"+this.getPackageName()+"/wall");
			if(userFile.exists()&&wallFile.exists()){
				return;
			}else if(userFile.exists()&&!wallFile.exists()){
				wallFile.mkdirs();
				return;
			}else if(!userFile.exists()&&wallFile.exists()){
				userFile.mkdirs();
				return;
			}else{
				userFile.mkdirs();
				wallFile.mkdirs();
			}
		}else{
			Toast.makeText(this, "未加载SD卡", Toast.LENGTH_LONG).show();
		}
		SharedPreferences sp = this.getSharedPreferences("user",
				Context.MODE_PRIVATE);
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"+sp.getBoolean("userStatus", true));
		if(sp.getBoolean("userStatus", true)){

			Toast.makeText(this, "尊敬的用户，欢迎回来！",Toast.LENGTH_LONG).show();
		}else{
			Toast.makeText(this, "游客止步！",Toast.LENGTH_LONG).show();
		}
	}
}
