package com.oxygen.server;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.avos.avoscloud.*;
import com.avos.avoscloud.search.AVSearchQuery;

import com.oxygen.data.UserInfo;
import com.oxygen.data.WallInfo;

/**
* @ClassName AVOSInit
* @Description AVOS云服务初始化
* @author oxygen
* @email oxygen0106@163.com
* @date 2014-9-12 下午3:47:09
*/
public class AVOSCloudServer {
	public static void AVOSInit(Context ctx) {
		// AVOS 初始化应用 Id 和 应用 Key.
		AVOSCloud.initialize(ctx,
				"miuybi5x1e26e6s2u4hvd20487hnrmwy5ct86u63pa6gy4ul",
				"u0btliibsze22sg249dgd8zlkczeiytkzqhr54k9z1rdaevh");
		// 启用崩溃错误报告
		AVAnalytics.enableCrashReport(ctx, true);
		// 注册子类
//		AVObject.registerSubclass(WallInfo.class);
		

//		UserInfo user = new UserInfo(UserInfo.USER_CLASS_NAME);
//		user.setUserID(0001);
//		user.setUserName("X1");
//		user.saveInBackground();
//		user.setUserID(0002);
//		user.setUserName("X2");
//		user.saveInBackground();
	}
	
}
