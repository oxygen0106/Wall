package com.oxygen.my;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.oxygen.my.MySlipButton.OnChangedListener;
import com.oxygen.wall.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @ClassName MySetting
 * @Description 设置界面
 * @author oxygen
 * @email oxygen0106@163.com
 * @date 2014-9-7 上午10:51:03
 */
public class MySettingActivity extends Activity {

	MySlipButton slipButton;

	private TextView titleText;
	private ImageView returnBtn;

	private MyListView myInfoList;
	private MyListView otherList;
	private MyListView clearList;

	List<Map<String, Object>> myInfoData;
	List<Map<String, Object>> otherData;
	List<Map<String, Object>> clearData;

	private SimpleAdapter myInfoAdapter = null;
	private SimpleAdapter otherAdapter = null;
	private SimpleAdapter clearAdapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		this.setContentView(R.layout.my_setting);
		this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.my_setting_title_bar);

		titleText = (TextView) findViewById(R.id.my_setting_title_tv);
		returnBtn = (ImageView) findViewById(R.id.my_setting_return);
		titleText.setText("设置");// 设置TitleBar的TextView

		myInfoList = (MyListView) findViewById(R.id.my_setting_user_info_lv);
		otherList = (MyListView) findViewById(R.id.my_setting_other_lv);
		clearList = (MyListView) findViewById(R.id.my_setting_clear_cache_lv);

		setMyInfoListView();
		setOtherListView();
		setClearListView();

		setListListener();// ListView设置监听
		setReturnListener();//返回按钮监听
		// slipButton = (MySlipButton) findViewById(R.id.my_setting_btn_slip);
		// slipButton.SetOnChangedListener(new OnChangedListener() {
		//
		// @Override
		// public void OnChanged(boolean CheckState) {
		// // TODO Auto-generated method stub
		// if(CheckState){
		// Toast.makeText(MySettingActivity.this, "打开设置",
		// Toast.LENGTH_SHORT).show();
		// }else{
		// Toast.makeText(MySettingActivity.this, "关闭设置",
		// Toast.LENGTH_SHORT).show();
		// }
		// }
		// });

	}

	/**
	 * @param
	 * @return void
	 * @Description myInfoList加载数据
	 */
	private void setMyInfoListView() {
		myInfoData = new ArrayList<Map<String, Object>>();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("text", "我的帐号");
		myInfoData.add(map);

		myInfoAdapter = new SimpleAdapter(this, myInfoData,
				R.layout.my_setting_user_info_item, new String[] { "text" },
				new int[] { R.id.my_setting_lv_item_tv });
		myInfoList.setAdapter(myInfoAdapter);
	}

	/**
	 * @param
	 * @return void
	 * @Description otherList加载数据
	 */
	private void setOtherListView() {
		otherData = new ArrayList<Map<String, Object>>();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("text", "使用说明");
		otherData.add(map);

		map = new HashMap<String, Object>();
		map.put("text", "关于");
		otherData.add(map);

		otherAdapter = new SimpleAdapter(this, otherData,
				R.layout.my_setting_user_info_item, new String[] { "text" },
				new int[] { R.id.my_setting_lv_item_tv });
		otherList.setAdapter(otherAdapter);
	}

	/**
	 * @param
	 * @return void
	 * @Description clearList加载数据
	 */
	private void setClearListView() {
		clearData = new ArrayList<Map<String, Object>>();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("text", "清理缓存");
		clearData.add(map);

		clearAdapter = new SimpleAdapter(this, clearData,
				R.layout.my_setting_user_info_item, new String[] { "text" },
				new int[] { R.id.my_setting_lv_item_tv });
		clearList.setAdapter(clearAdapter);
	}

	/**
	 * @param
	 * @return void
	 * @Description ListView设置监听事件
	 */
	private void setListListener() {
		// 我的帐号
		myInfoList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

				if (arg3 == 0) {
					Intent intent = new Intent(MySettingActivity.this,
							MyInfoActivity.class);
					startActivity(intent);
				}

			}
		});
		// 中间设置
		otherList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

				if (arg3 == 0) {
					Intent intent = new Intent(MySettingActivity.this,
							MyUserGuide.class);
					startActivity(intent);
				}
				if (arg3 == 1) {
					Intent intent = new Intent(MySettingActivity.this,
							MyAbout.class);
					startActivity(intent);
				}
			}
		});

		// 清理缓存
		clearList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

				if (arg3 == 0) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							MySettingActivity.this);
					builder.setTitle("清理缓存");
					builder.setMessage("确定要清理吗？");
					builder.setPositiveButton("确定", new OnClickListener() {
						ProgressDialog progressDialog;// 开启进度

						@Override
						public void onClick(DialogInterface dialog, int which) {

							progressDialog = new ProgressDialog(
									MySettingActivity.this);
							progressDialog.setMessage("正在清理...");
							Window window = progressDialog.getWindow();
							WindowManager.LayoutParams lp = window
									.getAttributes();
							lp.alpha = 0.7f;
							lp.dimAmount = 0.8f;
							window.setAttributes(lp);
							progressDialog.show();
							new AsyncTask<Void, Void, Void>() {
								protected Void doInBackground(Void... params) {
									try {
										Thread.sleep(1000);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									return null;
								};

								protected void onPostExecute(Void result) {
									progressDialog.cancel();
									Toast.makeText(MySettingActivity.this,
											"缓存已清理", Toast.LENGTH_SHORT).show();
								};
							}.execute();

						}
					});
					builder.setNegativeButton("取消", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					});

					// builder.setIcon(R.drawable.ic_launcher);
					AlertDialog alertDialog = builder.create();
					alertDialog.show();
				}
			}
		});
	}
	
	private void setReturnListener(){
		returnBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MySettingActivity.this.finish();
			}
		});
	}
}
