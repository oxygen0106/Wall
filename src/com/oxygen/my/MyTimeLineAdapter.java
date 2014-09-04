package com.oxygen.my;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.oxygen.wall.R;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @ClassName MyTimeLineAdapter
 * @Description 自定义时间轴适配器，加载不同的Item布局
 * @author oxygen
 * @email oxygen0106@163.com
 * @date 2014-9-2 下午7:39:39
 */
public class MyTimeLineAdapter extends BaseAdapter {

	private List<HashMap<String, String>> list;
	private Context context;
	private int type;
	private int count;
	private int[] randomBgNum;

	/**
	 * Constructor
	 * 
	 * @param context
	 * @param list
	 * @param type
	 */
	public MyTimeLineAdapter(Context context, List<HashMap<String, String>> list) {
		this.context = context;
		this.list = list;
		this.count = list.size();
		randomBgNum = new int[count];// 初始化随机背景数组
		for (int i = 0; i < count; i++) {
			randomBgNum[i] = getRandom();
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub

		return count;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater mInflater = LayoutInflater.from(context);
		type = Integer.parseInt(list.get(position).get("type"));

		if (convertView == null) {
			// 根据type不同的数据类型构造不同的View
			if (type == 1) {
				convertView = mInflater.inflate(
						R.layout.my_timeline_lv_left_item, null);
				TextView itemText = (TextView) convertView
						.findViewById(R.id.my_timeline_lv_item_tv);
				TextView dateText = (TextView) convertView
						.findViewById(R.id.my_timeline_lv_item_date_tv);
				itemText.setText(list.get(position).get("itemText"));
				dateText.setText(list.get(position).get("date") + "\n"
						+ list.get(position).get("location"));

				Resources res = context.getResources();
				int id = res.getIdentifier("my_timeline_lv_item_left_"
						+ randomBgNum[position], "drawable",
						context.getPackageName());
				itemText.setBackgroundResource(id);

			} else {
				convertView = mInflater.inflate(
						R.layout.my_timeline_lv_right_item, null);
				TextView itemText = (TextView) convertView
						.findViewById(R.id.my_timeline_lv_item_tv);
				TextView dateText = (TextView) convertView
						.findViewById(R.id.my_timeline_lv_item_date_tv);
				itemText.setText(list.get(position).get("itemText"));
				dateText.setText(list.get(position).get("date") + "\n"
						+ list.get(position).get("location"));

				Resources res = context.getResources();
				int id = res.getIdentifier("my_timeline_lv_item_right_"
						+ randomBgNum[position], "drawable",
						context.getPackageName());
				itemText.setBackgroundResource(id);
			}
		} else {
			convertView = null;
			
			// 根据type不同的数据类型构造不同的View
			if (type == 1) {
				convertView = mInflater.inflate(
						R.layout.my_timeline_lv_left_item, null);
				TextView itemText = (TextView) convertView
						.findViewById(R.id.my_timeline_lv_item_tv);
				TextView dateText = (TextView) convertView
						.findViewById(R.id.my_timeline_lv_item_date_tv);
				itemText.setText(list.get(position).get("itemText"));
				dateText.setText(list.get(position).get("date") + "\n"
						+ list.get(position).get("location"));

				Resources res = context.getResources();
				int id = res.getIdentifier("my_timeline_lv_item_left_"
						+ randomBgNum[position], "drawable",
						context.getPackageName());
				itemText.setBackgroundResource(id);

			} else {
				convertView = mInflater.inflate(
						R.layout.my_timeline_lv_right_item, null);
				TextView itemText = (TextView) convertView
						.findViewById(R.id.my_timeline_lv_item_tv);
				TextView dateText = (TextView) convertView
						.findViewById(R.id.my_timeline_lv_item_date_tv);
				itemText.setText(list.get(position).get("itemText"));
				dateText.setText(list.get(position).get("date") + "\n"
						+ list.get(position).get("location"));

				Resources res = context.getResources();
				int id = res.getIdentifier("my_timeline_lv_item_right_"
						+ randomBgNum[position], "drawable",
						context.getPackageName());
				itemText.setBackgroundResource(id);
			}
		}

		return convertView;
	}

	/**
	 * @param @return
	 * @return int
	 * @Description 获得0-3的随机数
	 */
	private int getRandom() {
		Random random = new Random();
		int max = 4;
		int i = random.nextInt(max);// [0,max)范围
		return i;
	}

}
