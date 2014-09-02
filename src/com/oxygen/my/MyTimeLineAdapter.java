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
	private int[] randomBgNum;

	/**
	 * Constructor
	 * 
	 * @param context
	 * @param list
	 * @param type
	 */
	public MyTimeLineAdapter(Context context,
			List<HashMap<String, String>> list) {
		this.context = context;
		this.list = list;
//		this.type = type;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		int count = list.size();
		randomBgNum = new int[count];
		for(int i=0;i<count;i++){
			randomBgNum[i]=getRandom();
		}
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
		View view = null;
		type  = Integer.parseInt(list.get(position).get("type"));
		// 根据type不同的数据类型构造不同的View
		if (type == 1) {
			view = mInflater.inflate(R.layout.my_timeline_lv_left_item, null);
			TextView itemText = (TextView)view.findViewById(R.id.my_timeline_lv_item_tv);
			TextView dateText = (TextView)view.findViewById(R.id.my_timeline_lv_item_date_tv);
			itemText.setText(list.get(position).get("itemText"));
			dateText.setText(list.get(position).get("date")+"\n"+list.get(position).get("location"));
			
			
			Resources res=context.getResources();
			int id = res.getIdentifier("my_timeline_lv_item_left_"+randomBgNum[position], "drawable", context.getPackageName());
			itemText.setBackgroundResource(id);
				
				
//			// 获取城市名称
//			String cityName = list.get(position).get("data");
//			ImageView image = (ImageView) view.findViewById(R.id.weather_image);
//
//			if (cityName.equals("北京")) {
//				image.setImageResource(R.drawable.beijing);
//			} else if (cityName.equals("上海")) {
//				image.setImageResource(R.drawable.shanghai);
//
//			} else if (cityName.equals("广州")) {
//				image.setImageResource(R.drawable.guangzhou);
//
//			} else if (cityName.equals("深圳")) {
//				image.setImageResource(R.drawable.shenzhen);
//
//			}
//			TextView city = (TextView) view.findViewById(R.id.city);
//			city.setText(cityName);
		} else {
			view = mInflater.inflate(R.layout.my_timeline_lv_right_item, null);
			TextView itemText = (TextView)view.findViewById(R.id.my_timeline_lv_item_tv);
			TextView dateText = (TextView)view.findViewById(R.id.my_timeline_lv_item_date_tv);
			itemText.setText(list.get(position).get("itemText"));
			dateText.setText(list.get(position).get("date")+"\n"+list.get(position).get("location"));
			
			Resources res=context.getResources();
			int id = res.getIdentifier("my_timeline_lv_item_right_"+randomBgNum[position], "drawable", context.getPackageName());
			itemText.setBackgroundResource(id);
			
			
//			// 获取数据
//			String content = list.get(position).get("data");
//			// 分离数据
//			String[] items = content.split(",");
//
//			TextView weather = (TextView) view.findViewById(R.id.content);
//			weather.setText(items[0] + "天气: " + items[1] + ";温度:  " + items[2]);
//			TextView date = (TextView) view.findViewById(R.id.date);
//			date.setText(items[3]);

		}

		return view;
	}

	
	private int getRandom(){
		Random random = new Random();
		int max =4;
		int i = random.nextInt(max);//0~max范围
		return i;
	}
	
}
