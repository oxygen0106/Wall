package com.oxygen.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.oxygen.my.MyListView;
import com.oxygen.my.MyPopWindow;
import com.oxygen.wall.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @ClassName MyFragment
 * @Description 用户界面包括三部分：头像（上），按钮（中），ListView（下）
 * @author oxygen
 * @email oxygen0106@163.com
 * @date 2014-8-18 下午09:47:52
 */
public class MyFragment extends Fragment {

	private ImageView myImage;
	private MyListView myListView;
	private MyPopWindow myPopWindow;
	private LinearLayout midLayoutMsg;
	private LinearLayout midLayoutRoad;
	private LinearLayout midLayoutZan;

	private List<Map<String, Object>> listData = null;
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

		myImage = (ImageView) view.findViewById(R.id.my_image);
		midLayoutMsg = (LinearLayout) view.findViewById(R.id.my_mid_layout_msg);
		midLayoutRoad = (LinearLayout) view
				.findViewById(R.id.my_mid_layout_road);
		midLayoutZan = (LinearLayout) view.findViewById(R.id.my_mid_layout_zan);
		myListView = (MyListView) view.findViewById(R.id.my_lv);

		setListView();// 初始化ListView内容

		addSetListener();// 添加监听事件

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	private void addSetListener() {

		// 1. 头像添加监听事件
		myImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 创建itemOnClick对象，初始化popWindow
				OnClickListener itemsOnClick = new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						myPopWindow.dismiss();
						switch (v.getId()) {
						case R.id.my_pop_btn_camera:
							Intent intent = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE); // 打开相机
							startActivityForResult(intent, 1);
							break;
						case R.id.my_pop_btn_cancel:
							break;
						default:
							break;
						}
					}
				};

				myPopWindow = new MyPopWindow(activity, itemsOnClick);
				// 设置layout在PopupWindow中显示的位置
				myPopWindow.showAtLocation(
						activity.findViewById(R.id.main_layout), Gravity.BOTTOM
								| Gravity.CENTER_HORIZONTAL, 0, 0);
			}
		});

		// 2. 添加中间栏监听
		midLayoutMsg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(activity, "Test", Toast.LENGTH_SHORT).show();
			}
		});
		midLayoutRoad.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(activity, "Test", Toast.LENGTH_SHORT).show();
			}
		});
		midLayoutZan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(activity, "Test", Toast.LENGTH_SHORT).show();
			}
		});
		
	}

	/**
	 * @param
	 * @return void
	 * @Description 设置下半部分ListView选项内容
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
		
		

		adapter = new SimpleAdapter(activity, listData, R.layout.my_lv_item,
				new String[] { "text" }, new int[] { R.id.tv_system_title });
		myListView.setAdapter(adapter);
	}
}
