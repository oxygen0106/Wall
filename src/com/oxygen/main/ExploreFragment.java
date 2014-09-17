package com.oxygen.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.oxygen.wall.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @ClassName FavourFragment
 * @Description 探索界面Fragment
 * @author oxygen
 * @email oxygen0106@163.com
 * @date 2014-9-16 下午09:33:01
 */
public class ExploreFragment extends Fragment {

	Activity activity;
	
	ListView listView;
	List<Map<String,Object>> listData;
	SimpleAdapter adapter;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		this.activity = activity;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.explore_fragment, container,
				false);
		listView = (ListView) view.findViewById(R.id.explore_lv);
		setListView();//listView载入数据
		
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	private void setListView(){
		listData = new ArrayList<Map<String, Object>>();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("text", "摇一摇");
		listData.add(map);

		map = new HashMap<String, Object>();
		map.put("text", "晃一晃");
		listData.add(map);
		
		map = new HashMap<String, Object>();
		map.put("text", "扭一扭");
		listData.add(map);
		
		adapter = new SimpleAdapter(activity, listData, R.layout.explore_lv_item, new String[] { "text" }, new int[] { R.id.explore_lv_item_tv });
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

				if (arg3 == 0) {
					Toast.makeText(activity, "摇一摇", Toast.LENGTH_SHORT).show();
				}
				if (arg3 == 1) {
					Toast.makeText(activity, "晃一晃", Toast.LENGTH_SHORT).show();
				}
				if (arg3 == 2) {
					Toast.makeText(activity, "扭一扭", Toast.LENGTH_SHORT).show();
				}

			}
		});
	}
}
