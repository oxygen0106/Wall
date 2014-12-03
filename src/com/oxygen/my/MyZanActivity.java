package com.oxygen.my;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.oxygen.data.ImageLoader;
import com.oxygen.data.UserInfo;
import com.oxygen.data.ImageLoader.ImageCallback;
import com.oxygen.explore.Shake;
import com.oxygen.explore.Shake.ShakeListener;
import com.oxygen.map.GetLocation;
import com.oxygen.wall.R;
import com.oxygen.wall.WallListView;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MyZanActivity extends Activity {

	private TextView titleText;
	private ImageView backBtn;

	private WallListView listView;
	private List<HashMap<String,String>> listData = new ArrayList<HashMap<String, String>>();
	private AVObject wallAVObject;
	private ZanAdapter adapter;
	private String currentUserName;

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

		listView = (WallListView) findViewById(R.id.my_zan_listview);
		getListData();

	}

	

	private void getListData() {//待修改
		AVUser user = AVUser.getCurrentUser();
		currentUserName = user.getString("username")+":";
		AVQuery<AVObject> query = AVQuery.getQuery("WallInfo");
		query.include("user");
		query.whereEqualTo("user", user);
		query.addDescendingOrder("createdAt");

		query.findInBackground(new FindCallback<AVObject>() {

			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				// TODO Auto-generated method stub
				if (arg1 == null) {
					int size = arg0.size();
					Log.v("zan", size+"我发布过的留言板~~~~");
					for (int i = 0; i < size; i++) {
						
						wallAVObject = arg0.get(i);
						
						AVQuery<AVObject> userQuery = AVRelation.reverseQuery("_User","Supports",wallAVObject);
						userQuery.findInBackground(new FindCallback<AVObject>() {
						            @Override
						            public void done(List<AVObject> users, AVException avException) {
						                if(avException==null){
						                		if(users.size()>0){
							                	HashMap<String, String> map = null;
							                	for(int i = 0;i<users.size();i++){
							                		Log.v("zan", users.size()+"这个留言板~有~~~得到赞的个数");
							                		AVUser user = (AVUser)users.get(i);
							                		map = new HashMap<String, String>();

							                		if(user.getEmail()==null){
							                			map.put("username","游客");
							                		}else{
							                			map.put("username",user.getString("username"));
							                		}
							                		AVFile userImage = user.getAVFile(UserInfo.USER_IMG);
							                		map.put("userImageUrl", userImage.getUrl());
							                		map.put("content",wallAVObject.getString("content"));
							                		Log.v("zan", wallAVObject.getString("content")+"---》内容");
	
							                		listData.add(map);
							                		Log.v("zan", listData.size()+"条数据");
							                	}
							                	
							                	Log.v("zan", "开始显示------");
												Log.v("zan", listData.size()+"条数据-所有");
												adapter = new ZanAdapter(MyZanActivity.this,
														listData, listView);
												listView.setAdapter(adapter);
							                }
						                }
						            }
						        });

					}
					
				}
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
			TextView currentUserNameText;
			TextView contentText;
			ImageView myImage;
			
			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.my_zan_item, null);
					
			}
			nameText = (TextView)convertView.findViewById(R.id.my_zan_user_name);
			currentUserNameText = (TextView)convertView.findViewById(R.id.my_zan_author_name);
			contentText = (TextView)convertView.findViewById(R.id.my_zan_content);
			myImage = (ImageView)convertView.findViewById(R.id.my_zan_user_image);
			nameText.setText(map.get("username"));
			contentText.setText(map.get("content"));
			currentUserNameText.setText(currentUserName);
				
			String userImageUrl = map.get("userImageUrl");
			if(userImageUrl!=null && !(userImageUrl.equals(""))){
				
				myImage.setTag(userImageUrl);
				Drawable cachedImage = imageLoader.loadDrawable(userImageUrl,myImage,
						new ImageCallback() {
							public void imageLoaded(Drawable imageDrawable,
									ImageView imageView) {
								imageView.setImageDrawable(imageDrawable);
							}
						});

				if (cachedImage == null) {
					myImage.setImageResource(R.drawable.my_image_round);
				} else {
					myImage.setImageDrawable(cachedImage);
				}
			}
			
			return convertView;
		}
	}
	
	
	/**
	* @param 
	* @return void
	* @Description 标题栏返回  
	*/
	private void setBackBtnListener() {
		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MyZanActivity.this.finish();
				
			}
		});
	}
}
