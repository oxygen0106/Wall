package com.oxygen.my;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.oxygen.data.UserInfo;
import com.oxygen.image.LoaderListener;
import com.oxygen.main.WelcomeActivity;
import com.oxygen.wall.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MyUserInfoActivity extends Activity {
	
	private TextView titleText; 
	private ImageView backBtn;
	
	private TextView nameText;
	private ImageView userImage;
	private List<Map<String,Object>> listData;
	private MyListView listView;
	private SimpleAdapter adapter = null;
	
	private String userID;
	private String userName;
	private String userImageUrl;
	
	private ImageLoadingListener animateFirstListener = LoaderListener.loaderListener;
	private DisplayImageOptions options = LoaderListener.getThumbNailOptions();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_user_info);
		this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.my_setting_title_bar);
		
		titleText = (TextView)findViewById(R.id.my_setting_title_tv);
		backBtn = (ImageView)findViewById(R.id.my_setting_title_bar_back_btn);
		titleText.setText("用户信息");
		setBackBtnListener();
		
		nameText = (TextView)findViewById(R.id.my_user_info_name_tv);
		userImage = (ImageView)findViewById(R.id.my_user_info_image);
		listView = (MyListView) findViewById(R.id.my_user_info_lv);
		setListView();
		getUserID();
		getUserInfo();
	}
	
	/**
	* @param 
	* @return void
	* @Description 初始化ListView数据信息  
	*/
	private void setListView() {
		listData = new ArrayList<Map<String, Object>>();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("text", "足迹");
		listData.add(map);

		map = new HashMap<String, Object>();
		map.put("text", "评论");
		listData.add(map);

		map = new HashMap<String, Object>();
		map.put("text", "留言板");
		listData.add(map);
		
		

		adapter = new SimpleAdapter(this, listData, R.layout.my_lv_item,
				new String[] { "text" }, new int[] { R.id.my_lv_item_tv });
		listView.setAdapter(adapter);
		setListListener();
	}
	
	private  void setListListener(){
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

				if (arg3 == 0) {
					Intent intent = new Intent(MyUserInfoActivity.this,
							MyTimeLineActivity.class);
					intent.putExtra("userID", userID);
					startActivity(intent);
//					MyUserInfoActivity.this.finish();
				}
				if (arg3 == 1) {
					Intent intent = new Intent(MyUserInfoActivity.this,
							MyCommentActivity.class);
					intent.putExtra("userID", userID);
					startActivity(intent);
//					MyUserInfoActivity.this.finish();
				}
				if (arg3 == 2) {
					Intent intent = new Intent(MyUserInfoActivity.this,
							MyWallInfoActivity.class);
					intent.putExtra("userID", userID);
					startActivity(intent);
//					MyUserInfoActivity.this.finish();
				}
				
			}
		});

	}
	
	
	private void getUserID(){
		Intent intent = getIntent();
		userID= intent.getStringExtra("userID");
		userName= intent.getStringExtra("userName");
		userImageUrl= intent.getStringExtra("userImageUrl");
	}
	private void setBackBtnListener(){
		backBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MyUserInfoActivity.this.finish();
			}
		});
	}
	
	private void getUserInfo(){
		ImageLoader.getInstance().displayImage(userImageUrl, userImage, options, animateFirstListener);
		nameText.setText(userName);
	}
	
}
