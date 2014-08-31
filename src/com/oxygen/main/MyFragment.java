package com.oxygen.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.oxygen.my.MyListView;
import com.oxygen.wall.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

/**
 * @ClassName MyFragment
 * @Description 用户
 * @author oxygen
 * @email oxygen0106@163.com
 * @date 2014-8-18 下午09:47:52
 */
public class MyFragment extends Fragment {

	private MyListView myListView;

	private List<Map<String, String>> listData = null;
	private SimpleAdapter adapter = null;

	private Activity activity;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		this.activity = activity;
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.my_fragment, container, false);
		myListView = (MyListView) view.findViewById(R.id.my_lv);
		setListData();
		adapter = new SimpleAdapter(activity, listData, R.layout.my_lv_item,
				new String[] { "text" }, new int[] { R.id.tv_system_title });
		myListView.setAdapter(adapter);
		
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}
	
	private void setListData(){  
        listData = new ArrayList<Map<String,String>>();  
   
        Map<String,String> map = new HashMap<String, String>();  
        map.put("text", "分享");  
        listData.add(map);  
   
        map = new HashMap<String, String>();  
        map.put("text", "权限");  
        listData.add(map);  
   
        map = new HashMap<String, String>();  
        map.put("text", "反馈意见");  
        listData.add(map);  
    
    }  
}
