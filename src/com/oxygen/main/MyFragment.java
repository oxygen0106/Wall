package com.oxygen.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.avos.avoscloud.AVUser;
import com.oxygen.data.UserInfo;
import com.oxygen.my.MyInfoActivity;
import com.oxygen.my.UserInfoActivity;
import com.oxygen.my.MyListView;
import com.oxygen.my.MyPopWindow;
import com.oxygen.my.MyRoundView;
import com.oxygen.my.MySettingActivity;
import com.oxygen.my.MyTimeLineActivity;
import com.oxygen.wall.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
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
 * 
 */
public class MyFragment extends Fragment {

	private boolean userStatus;
	private String userName;

	private ImageView myImage;
	private MyListView myListView;
	private LinearLayout midLayoutMsg;
	private LinearLayout midLayoutRoad;
	private LinearLayout midLayoutZan;
	private TextView userNameText;

	private List<Map<String, Object>> listData = null;
	private SimpleAdapter adapter = null;

	private Activity activity;

	public String userImagePath;
	public File file;
	public Uri imageUri;

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
		userNameText = (TextView) view.findViewById(R.id.my_name_tv);

		currentUserStatus();// 检测当前用户身份

		SharedPreferences mSP = PreferenceManager
				.getDefaultSharedPreferences(activity);
		if (mSP.getString("MyImgURL", null) != null) {
			String imgFilename = mSP.getString("MyImgURL", null);
			System.out.println(imgFilename);
			Bitmap bitmap = BitmapFactory.decodeFile(imgFilename);
			if (bitmap != null) {// 如果该路径系下的文件不存在，加载默认图片背景
				myImage.setImageBitmap(bitmap);// 将图片显示在ImageView里
			}
		}
		setListView();// 初始化ListView内容

		addSetListener();// 添加监听事件

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

	}

	/**
	 * @param
	 * @return void
	 * @Description 添加My界面各个按钮的监听事件
	 */
	private void addSetListener() {

		// 1. 头像添加监听事件
		myImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(activity, MyInfoActivity.class);
				startActivity(intent);
			}
		});

		// 2. 添加中间栏监听
		midLayoutMsg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),
						UserInfoActivity.class);
				startActivity(intent);
			}
		});
		midLayoutRoad.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),
						MyTimeLineActivity.class);
				startActivity(intent);
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
		map.put("text", "留言板");
		listData.add(map);

		map = new HashMap<String, Object>();
		map.put("text", "评论");
		listData.add(map);

		map = new HashMap<String, Object>();
		map.put("text", "收藏");
		listData.add(map);

		map = new HashMap<String, Object>();
		map.put("text", "好友");
		listData.add(map);

		adapter = new SimpleAdapter(activity, listData, R.layout.my_lv_item,
				new String[] { "text" }, new int[] { R.id.my_lv_item_tv });
		myListView.setAdapter(adapter);

		myListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

				if (arg3 == 0) {
					Toast.makeText(activity, "留言板", Toast.LENGTH_SHORT).show();
				}
				if (arg3 == 1) {
					Toast.makeText(activity, "评论", Toast.LENGTH_SHORT).show();
				}
				if (arg3 == 2) {
					Toast.makeText(activity, "收藏", Toast.LENGTH_SHORT).show();
				}
				if (arg3 == 3) {
					Intent intent = new Intent(activity,
							MySettingActivity.class);
					startActivity(intent);
				}

			}
		});
	}

	/**
	 * @param
	 * @return void
	 * @Description 判断当前用户身份,设置用户名，建立路径
	 */
	private void currentUserStatus() {
		AVUser currentUser = AVUser.getCurrentUser();
		if (currentUser.isAnonymous()) {
			userStatus = false;
			userNameText.setText("游客");
			
		} else {
			userStatus = true;
			userNameText.setText(currentUser.getString(UserInfo.USER_NAME));
			userImagePath = Environment.getExternalStorageDirectory()
					.getAbsolutePath()
					+ "/Android/data/"
					+ activity.getPackageName() + "/user/myImage.png";
			file = new File(userImagePath);
			if(file.exists()){
				Bitmap bitmap = BitmapFactory.decodeFile(userImagePath);
				if (bitmap != null) {// 如果该路径系下的文件不存在，加载默认图片背景
					myImage.setImageBitmap(bitmap);// 将图片显示在ImageView里
				}
			}
		}
	}
}
