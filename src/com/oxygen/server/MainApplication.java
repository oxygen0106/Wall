package com.oxygen.server;

import android.app.Application;

import com.avos.avoscloud.AVObject;
import com.baidu.mapapi.SDKInitializer;

/**
 * @ClassName MapApplication
 * @Description 百度地图API全局初始化，AVOS云服务API全局初始化
 * @author oxygen
 * @email oxygen0106@163.com
 * @date 2014-8-13 下午12:40:08
 */
public class MainApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		
		/* AVOS云服务API初始化 */
		AVOSCloudServer.AVOSInit(this);

		/* 百度地图API初始化 */
		MapServer.MapInit(this);
		
	}

}