package com.oxygen.my;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.oxygen.wall.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MyUserInfoActivity extends Activity {
	
	private TextView titleText; 
	private ImageView backBtn;
	
	private List<Map<String,Object>> listData;
	private MyListView listView;
	private SimpleAdapter adapter = null;
	
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
		
		listView = (MyListView) findViewById(R.id.my_user_info_lv);
		setListView();
	}
	
	/**
	* @param 
	* @return void
	* @Description 初始化ListView数据信息  
	*/
	private void setListView() {
		listData = new ArrayList<Map<String, Object>>();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("text", "已创建");
		listData.add(map);

		map = new HashMap<String, Object>();
		map.put("text", "收藏");
		listData.add(map);

		map = new HashMap<String, Object>();
		map.put("text", "反馈意见");
		listData.add(map);
		
		

		adapter = new SimpleAdapter(this, listData, R.layout.my_lv_item,
				new String[] { "text" }, new int[] { R.id.my_lv_item_tv });
		listView.setAdapter(adapter);
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
	
}
