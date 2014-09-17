package com.oxygen.server;

import android.content.Context;

import com.baidu.mapapi.SDKInitializer;

/**
* @ClassName MapServer
* @Description 百度地图API初始化
* @author oxygen
* @email oxygen0106@163.com
* @date 2014-9-12 下午3:54:23
*/
public class MapServer {
	// 在使用 Baidu Map SDK 各组间之前初始化 context 信息
	public static void MapInit(Context context) {
		SDKInitializer.initialize(context);
	}
}
