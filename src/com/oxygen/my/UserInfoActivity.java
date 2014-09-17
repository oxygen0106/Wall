package com.oxygen.my;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.oxygen.wall.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class UserInfoActivity extends Activity {
	
	private List<Map<String,Object>> listData;
	private MyListView listView;
	private SimpleAdapter adapter = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_other_user_info);
		this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.my_other_user_info_title_bar);
		
		listView = (MyListView) findViewById(R.id.my_other_user_info_lv);
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
	
}
