package com.oxygen.my;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.oxygen.data.ImageLoader;
import com.oxygen.data.ImageLoader.ImageCallback;
import com.oxygen.wall.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MyMessageAdapter extends BaseAdapter {

	private List<HashMap<String, String>> list;
	private Context context;
	private int type;
	private int count;
	private ImageLoader imageLoader; 
	private ListView listView;
	
	
	public MyMessageAdapter(Context context, List<HashMap<String, String>> list, ListView listView){
		this.context = context;
		this.list = list;
		this.count = list.size();
		this.imageLoader = new ImageLoader();
		this.listView = listView;
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
		LayoutInflater mInflater = LayoutInflater.from(context);
		HashMap<String, String> map = (HashMap<String, String>) list.get(position);
		TextView nameText;
		TextView contentText;
		TextView timeText;
		ImageView myImage;
		
		if (convertView == null) {
			convertView = mInflater.inflate(
					R.layout.my_message_activity_item, null);
				
		}
		nameText = (TextView)convertView.findViewById(R.id.my_message_user_name);
		contentText = (TextView)convertView.findViewById(R.id.my_message_content);
		timeText = (TextView)convertView.findViewById(R.id.my_message_time);
		nameText.setText(map.get("username"));
		contentText.setText(map.get("content"));
		timeText.setText(map.get("time"));
			
		String imageUrl = map.get("imageUrl");
		if(imageUrl!=null && !(imageUrl.equals(""))){
			myImage = (ImageView)convertView.findViewById(R.id.my_message_my_image);
			myImage.setTag(imageUrl);
				
			Drawable cachedImage = imageLoader.loadDrawable(imageUrl, new ImageCallback() {  
				public void imageLoaded(Drawable imageDrawable, String imageUrl) {  
					ImageView imageViewByTag = (ImageView) listView.findViewWithTag(imageUrl);  
	                if (imageViewByTag != null) {  
	                	imageViewByTag.setImageDrawable(imageDrawable);  
	                 }  
	            }  
	        });
				
			if (cachedImage == null) {  
				myImage.setImageResource(R.drawable.my_image_round);  
	        }else{  
	            myImage.setImageDrawable(cachedImage);  
	        }
		}
		return convertView;
	}

}
