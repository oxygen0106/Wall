package com.oxygen.server;

import android.app.Application;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SignUpCallback;
import com.baidu.mapapi.SDKInitializer;
import com.oxygen.data.UserInfo;
import com.oxygen.map.GetLocation;
import com.oxygen.my.MyRegisterActivity;

/**
 * @ClassName MapApplication
 * @Description 百度地图API全局初始化，AVOS云服务API全局初始化，定位初始化
 * @author oxygen
 * @email oxygen0106@163.com
 * @date 2014-8-13 下午12:40:08
 */
public class MainApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		
		/* AVOS云后台服务器API初始化 */
		AVOSCloudServer.AVOSInit(this);

		/* 百度地图API初始化 */
		MapServer.MapInit(this);
		
		/* 定位初始化 */
		GetLocation.initMyLocation(this);
		
		
	}

}