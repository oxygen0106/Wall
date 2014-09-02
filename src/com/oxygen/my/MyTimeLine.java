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
import android.widget.TextView;

/**
* @ClassName MyTimeLine
* @Description 时间轴：显示我创建和我参与的留言板动态
* @author oxygen
* @email oxygen0106@163.com
* @date 2014-9-2 上午10:02:42
*/
public class MyTimeLine extends Activity {
	
	private ListView lv;
	private List<HashMap<String, String>> listData = null;
	private MyTimeLineAdapter adapter = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);// 自定义标题栏模式
		this.setContentView(R.layout.my_timeline);
		this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.my_title_bar);// 加载自定义标题栏
		
		lv = (ListView)findViewById(R.id.my_timeline_lv);
		setListView();
		
	}
	
	
	/**
	 * @param
	 * @return void
	 * @Description 设置下半部分ListView选项内容
	 */
	private void setListView() {
		listData = new ArrayList<HashMap<String, String>>();

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("type", "1");
		map.put("itemText", "Test Left");
		map.put("date","09/02/2014");
		map.put("location", "Chongqing, China.");
		listData.add(map);

		map = new HashMap<String, String>();
		map.put("type", "2");
		map.put("itemText", "Test Left");
		map.put("date","09/02/2014");
		map.put("location", "Chongqing, China.");
		listData.add(map);

		map = new HashMap<String, String>();
		map.put("type", "1");
		map.put("itemText", "Change");
		map.put("date","09/02/2014");
		map.put("location", "Chongqing, China.");
		listData.add(map);
		
		map = new HashMap<String, String>();
		map.put("type", "2");
		map.put("itemText", "Test Left");
		map.put("date","09/02/2014");
		map.put("location", "Chongqing, China.");
		listData.add(map);
		
		map = new HashMap<String, String>();
		map.put("type", "1");
		map.put("itemText", "Test Left");
		map.put("date","09/02/2014");
		map.put("location", "Chongqing, China.");
		listData.add(map);
		
		map = new HashMap<String, String>();
		map.put("type", "2");
		map.put("itemText", "Test Left");
		map.put("date","09/02/2014");
		map.put("location", "Chongqing, China.");
		listData.add(map);
		
		map = new HashMap<String, String>();
		map.put("type", "1");
		map.put("itemText", "Test Left");
		map.put("date","09/02/2014");
		map.put("location", "Chongqing, China.");
		listData.add(map);
		
		map = new HashMap<String, String>();
		map.put("type", "2");
		map.put("itemText", "Test Left");
		map.put("date","09/02/2014");
		map.put("location", "Chongqing, China.");
		listData.add(map);

		adapter = new MyTimeLineAdapter(this, listData);
		lv.setAdapter(adapter);
	}
}
