package com.oxygen.my;

import java.util.HashMap;
import java.util.List;

import com.oxygen.data.ImageLoader;
import com.oxygen.data.ImageLoader.ImageCallback;
import com.oxygen.wall.R;
import com.oxygen.wall.WallListView;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MyZanActivity extends Activity {

	private TextView titleText;
	private ImageView backBtn;

	private WallListView listView;
	private List<HashMap<String,String>> listData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		this.setContentView(R.layout.my_zan_activity);
		this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.my_setting_title_bar);

		titleText = (TextView) findViewById(R.id.my_setting_title_tv);
		backBtn = (ImageView) findViewById(R.id.my_setting_title_bar_back_btn);
		titleText.setText("赞");
		setBackBtnListener();

		listView = (WallListView) findViewById(R.id.wall_listview);

	}

	private void setBackBtnListener() {
		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MyZanActivity.this.finish();
			}
		});
	}

	/**
	* @ClassName ZanAdapter
	* @Description 内部类Adapter
	*/
	private class ZanAdapter extends BaseAdapter {
		
		private List<HashMap<String, String>> list;
		private Context context;
		private int type;
		private int count;
		private ImageLoader imageLoader; 
		private ListView listView;
		

		public ZanAdapter(Context context, List<HashMap<String, String>> list, ListView listView){
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
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			LayoutInflater mInflater = LayoutInflater.from(context);
			HashMap<String, String> map = (HashMap<String, String>) list.get(position);
			TextView nameText;
			TextView contentText;
			TextView timeText;
			ImageView myImage;
			ImageView wallImage;
			
			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.my_zan_item, null);
					
			}
			nameText = (TextView)convertView.findViewById(R.id.my_zan_user_name);
			contentText = (TextView)convertView.findViewById(R.id.my_zan_content);
			timeText = (TextView)convertView.findViewById(R.id.my_zan_time);
			myImage = (ImageView)convertView.findViewById(R.id.my_zan_user_image);
			wallImage = (ImageView)convertView.findViewById(R.id.my_zan_wall_image);
			nameText.setText(map.get("username"));
			contentText.setText(map.get("content"));
			timeText.setText(map.get("time"));
				
			String userImageUrl = map.get("userImageUrl");
			if(userImageUrl!=null && !(userImageUrl.equals(""))){
				
				myImage.setTag(userImageUrl);
				Drawable cachedImage = imageLoader.loadDrawable(userImageUrl, new ImageCallback() {  
					public void imageLoaded(Drawable imageDrawable, String userImageUrl) {  
						ImageView imageViewByTag = (ImageView) listView.findViewWithTag(userImageUrl);  
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
			String wallImageUrl = map.get("wallImageUrl");
			if(wallImageUrl!=null && !(wallImageUrl.equals(""))){
				
				wallImage.setTag(wallImageUrl);
					
				Drawable cachedImage = imageLoader.loadDrawable(wallImageUrl, new ImageCallback() {  
					public void imageLoaded(Drawable imageDrawable, String wallImageUrl) {  
						ImageView imageViewByTag = (ImageView) listView.findViewWithTag(wallImageUrl);  
		                if (imageViewByTag != null) {  
		                	imageViewByTag.setImageDrawable(imageDrawable);  
		                 }  
		            }  
		        });
					
				if (cachedImage == null) {  
					wallImage.setImageResource(R.drawable.my_image_round);  
		        }else{  
		        	wallImage.setImageDrawable(cachedImage);  
		        }
			}
			
			return convertView;
		}
	}
}
