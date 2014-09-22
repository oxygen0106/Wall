package com.oxygen.my;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.oxygen.data.UserInfo;
import com.oxygen.data.WallInfoUpload;
import com.oxygen.wall.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MyMessageActivity extends Activity {

	private TextView titleText; 
	private ImageView backBtn;
	
	private ListView messageList;
	private List<HashMap<String, String>> listData;
//	private final static String publicMsgId = "541ea7ade4b0eabcaeb2e1f8";
	private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	private MyMessageAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.my_message_activity);
		this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.my_setting_title_bar);
		
		titleText = (TextView)findViewById(R.id.my_setting_title_tv);
		backBtn = (ImageView)findViewById(R.id.my_setting_title_bar_back_btn);
		titleText.setText("消息");
		setBackBtnListener();
		
		messageList = (ListView) findViewById(R.id.my_message_lv);
		listData = new ArrayList<HashMap<String, String>>();
		getListData();

	}

	private void getListData() {
		
		AVQuery<AVObject> queryAdmin = AVQuery.getQuery("Message");
		queryAdmin.include("user");
		queryAdmin.whereEqualTo("targetUser", null);//目标用户为空，为管理员消息
		queryAdmin.addDescendingOrder("createdAt");
		queryAdmin.findInBackground(new FindCallback<AVObject>() {
			public void done(List<AVObject> arg0, AVException arg1) {
				if (arg1 == null) {
					HashMap<String, String> map;
					AVUser admin = arg0.get(0).getAVUser("user");
					AVFile userImage = admin.getAVFile(UserInfo.USER_IMG);
					final String adminImageUrl = userImage.getUrl();
					
					for (int i = 0; i < arg0.size(); i++) {
						map = new HashMap<String, String>();
						map.put("username", "AR团队");
						map.put("content",
								arg0.get(i).getString("content"));
						map.put("time", dateFormat.format(arg0.get(
								i).getCreatedAt()));
						map.put("imageUrl", adminImageUrl);
						listData.add(map);
						
					}
					refreshUserMessage();// 搜索添加用户消息
				}else{
					Log.v("msg",arg1.getMessage()+"异常消息~~~~~~");
				}
			};
		});
	}

	private void refreshUserMessage() {
		AVUser currentUser = AVUser.getCurrentUser();
		AVQuery<AVObject> queryTargetUserIsCurrentUser = AVQuery.getQuery("Message");
		queryTargetUserIsCurrentUser.include("user");
		queryTargetUserIsCurrentUser.whereEqualTo("targetUser", currentUser);//消息的目标用户等于当前用户
		queryTargetUserIsCurrentUser.addDescendingOrder("createdAt");
//		AVQuery<AVObject> queryAdmin = AVQuery.getQuery("Message");
//		queryAdmin.include("user");
//		queryAdmin.whereEqualTo("targetUser", null);//目标用户为空，为管理员消息
//		queryAdmin.addDescendingOrder("createdAt");
		
//		List<AVQuery<AVObject>> queries = new ArrayList<AVQuery<AVObject>>();
//		queries.add(queryTargetUserIsCurrentUser);
//		queries.add(queryAdmin);
//		AVQuery<AVObject> mainQuery = AVQuery.or(queries);//异常信息，"或查询"不支持include()
		
		queryTargetUserIsCurrentUser.findInBackground(new FindCallback<AVObject>() {

			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				// TODO Auto-generated method stub
				if (arg1 == null) {
					HashMap<String, String> map;
					for (int i = 0; i < arg0.size(); i++) {
						map = new HashMap<String, String>();
						
						AVUser user = arg0.get(i).getAVUser("user");
						AVFile userImage = user.getAVFile(UserInfo.USER_IMG);
						
						map.put("username", user.getString("username"));
						map.put("content", arg0.get(i).getString("content"));
						map.put("time",
								dateFormat.format(arg0.get(i).getCreatedAt()));

						map.put("imageUrl", userImage.getUrl());

						listData.add(map);
					}
Log.v("msg", listData.size() + "~~~~~总消息数量~~~~");
					adapter = new MyMessageAdapter(MyMessageActivity.this, listData, messageList);
					messageList.setAdapter(adapter);
				}else{
Log.v("msg", arg1.getMessage() + "~~~~~用户消息错误~~~~");
				}
			}
		});
	}
	
	private void setBackBtnListener(){
		backBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MyMessageActivity.this.finish();
			}
		});
	}
	
}
