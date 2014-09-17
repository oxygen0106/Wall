package com.oxygen.my;

import java.util.List;
import java.util.Map;

import com.oxygen.wall.R;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyInfoAdapter extends BaseAdapter {

	private List<Map<String, Object>> list;
	private Context context;
	private int type;
	private int count;

	public MyInfoAdapter(Context context, List<Map<String, Object>> list) {
		this.context = context;
		this.list = list;
		this.count = list.size();
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
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		type = (Integer) list.get(position).get("type");
		if (convertView == null) {
			if (type == 0) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.my_info_lv_image_item, null);
				TextView itemText = (TextView) convertView
						.findViewById(R.id.my_info_lv_image_item_tv);
				ImageView imageView = (ImageView) convertView
						.findViewById(R.id.my_info_lv_image);
				itemText.setText((String) list.get(position).get("text"));

			} else {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.my_info_lv_other_item, null);
				TextView itemText = (TextView) convertView
						.findViewById(R.id.my_info_lv_other_item_tv);
				TextView contentView = (TextView) convertView
						.findViewById(R.id.my_info_lv_other_content_tv);
				itemText.setText((String) list.get(position).get("text"));
				contentView.setText((String) list.get(position).get("content"));
			}
		} else {
			convertView = null;
			if (type == 0) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.my_info_lv_image_item, null);
				TextView itemText = (TextView) convertView
						.findViewById(R.id.my_info_lv_image_item_tv);
				ImageView imageView = (ImageView) convertView
						.findViewById(R.id.my_info_lv_image);
				itemText.setText((String) list.get(position).get("text"));

			} else {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.my_info_lv_other_item, null);
				TextView itemText = (TextView) convertView
						.findViewById(R.id.my_info_lv_other_item_tv);
				TextView contentView = (TextView) convertView
						.findViewById(R.id.my_info_lv_other_content_tv);
				itemText.setText((String) list.get(position).get("text"));
				contentView.setText((String) list.get(position).get("content"));
			}
		}
		return convertView;
	}
	
	
}
