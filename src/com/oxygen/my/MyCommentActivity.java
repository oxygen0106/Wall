package com.oxygen.my;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.oxygen.data.ImageLoader;
import com.oxygen.data.UserInfo;
import com.oxygen.data.ImageLoader.ImageCallback;
import com.oxygen.data.WallInfoDownload;
import com.oxygen.explore.ExplorePickPlaneActivity;
import com.oxygen.map.GetLocation;
import com.oxygen.wall.R;
import com.oxygen.wall.WallCommentActivity;
import com.oxygen.wall.WallCurrent;
import com.oxygen.wall.WallListView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MyCommentActivity extends Activity {
	private TextView titleText;
	private ImageView backBtn;

	
	private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	private WallListView listView;
	private List<HashMap<String,String>> listData = new ArrayList<HashMap<String, String>>();
	
	private ArrayList<Object> wallInfoDownload = new ArrayList<Object>();
	
	private CommentAdapter adapter;
	private String username;
	private String userImageUrl;
	private AVObject commentAVObeject;
	
	private AVUser currentUser;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		this.setContentView(R.layout.my_comment_activity);
		this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.my_setting_title_bar);

		titleText = (TextView) findViewById(R.id.my_setting_title_tv);
		backBtn = (ImageView) findViewById(R.id.my_setting_title_bar_back_btn);
		titleText.setText("评论");
		setBackBtnListener();
		
		listView = (WallListView) findViewById(R.id.my_comment_listview);
		
		getCurrentUser();
	}
	
	
	private void getListData(AVUser user) {//待修改

		if(user.getEmail()!=null){
			username = user.getString("username");
		}else{
			username = "游客";
		}
		AVFile userImage = user.getAVFile(UserInfo.USER_IMG);
		if(userImage!=null){
			userImageUrl = userImage.getUrl();
		}
		AVQuery<AVObject> query = AVQuery.getQuery("WallComment");
		query.include("wall");
		query.include("wall.user");
		query.whereEqualTo("user", user);
		query.findInBackground(new FindCallback<AVObject>() {

			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				// TODO Auto-generated method stub
				if (arg1 == null) {
					HashMap<String, String> map = null;
					int size = arg0.size();
					for (int i = 0; i < size; i++) {
						map = new HashMap<String, String>();
						commentAVObeject = arg0.get(i);
						AVObject wallAVObject = commentAVObeject.getAVObject("wall");
						AVUser author = wallAVObject.getAVUser("user");
						
						WallInfoDownload wid = new WallInfoDownload(wallAVObject, author);
						wallInfoDownload.add(wid);
						
						AVFile authorImage = author.getAVFile(UserInfo.USER_IMG);
						String authorImageUrl = "";
						if(authorImage!=null){
							authorImageUrl = authorImage.getUrl();
						}
						map.put("username", username);
						map.put("time", dateFormat.format(commentAVObeject.getCreatedAt()));
						map.put("content", commentAVObeject.getString("comment"));
						if(author.getEmail()!=null){
							map.put("authorName", author.getString("username"));
						}else{
							map.put("authorName", "游客");
						}
						map.put("authorImageUrl", authorImageUrl);
						map.put("wallContent", wallAVObject.getString("content"));

						listData.add(map);
					}
					adapter = new CommentAdapter(MyCommentActivity.this,
							listData, listView);
					listView.setAdapter(adapter);
					listSetListener();
				}
			}
		});
	}
	
	
	/**
	* @ClassName CommentAdapter
	* @Description 内部类Adapter
	*/
	private class CommentAdapter extends BaseAdapter {
		
		private List<HashMap<String, String>> list;
		private Context context;
		private int count;
		private ImageLoader imageLoader; 
		

		public CommentAdapter(Context context, List<HashMap<String, String>> list, ListView listView){
			this.context = context;
			this.list = list;
			this.count = list.size();
			this.imageLoader = new ImageLoader();
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
			TextView usernameText;
			ImageView myImage;
			TextView timeText;
			TextView commentContentText;
			TextView authorNameText;
			ImageView authorImage;
			TextView wallContentText;
			
			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.my_comment_item, null);
					
			}
			usernameText = (TextView)convertView.findViewById(R.id.my_comment_user_name);
			myImage = (ImageView)convertView.findViewById(R.id.my_comment_user_image);
			timeText = (TextView)convertView.findViewById(R.id.my_comment_time);
			commentContentText = (TextView)convertView.findViewById(R.id.my_comment_content);
			authorNameText  = (TextView)convertView.findViewById(R.id.my_comment_wall_author_name);
			authorImage  = (ImageView)convertView.findViewById(R.id.my_comment_wall_author_image);
			wallContentText = (TextView)convertView.findViewById(R.id.my_comment_wall_content);
			
			usernameText.setText(map.get("username"));
			commentContentText.setText(map.get("content"));
			wallContentText.setText(map.get("wallContent"));
			timeText.setText(map.get("time"));
			authorNameText.setText(map.get("authorName"));
			
			
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
			String authorImageUrl = map.get("authorImageUrl");
			if(authorImageUrl!=null && !(authorImageUrl.equals(""))){
				
				authorImage.setTag(authorImageUrl);
					
				Drawable cachedImage = imageLoader.loadDrawable(authorImageUrl,authorImage,
						new ImageCallback() {
							public void imageLoaded(Drawable imageDrawable,
									ImageView imageView) {
								imageView.setImageDrawable(imageDrawable);
							}
						});

				if (cachedImage == null) {
					authorImage.setImageResource(R.drawable.my_image_round);
				} else {
					authorImage.setImageDrawable(cachedImage);
				}
			}
			
			return convertView;
		}
	}
	
	private void setBackBtnListener() {
		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MyCommentActivity.this.finish();
			}
		});
	}
	
 private void listSetListener(){
	 listView.setOnItemClickListener(new OnItemClickListener() {
		 public void onItemClick(android.widget.AdapterView<?> arg0, View arg1, int position, long arg3) {
			 Log.v("~~~~", position+"");
			 WallCurrent.wid = (WallInfoDownload)wallInfoDownload.get(position-1);
			 startActivity(new Intent(MyCommentActivity.this, WallCommentActivity.class));
		 };
			 
			 
	 });
 }
 
 
 private String getUserID(){
		Intent intent = getIntent();
		String userID= intent.getStringExtra("userID");
		return userID;
	}
	
	private void getCurrentUser(){
		if(getUserID()==null||getUserID().equals("")){//当前用户
			currentUser = AVUser.getCurrentUser();
			getListData(currentUser);
		}else{
			AVQuery<AVUser> query = new  AVQuery<AVUser>("_User");
			query.whereEqualTo("objectId", getUserID());
			query.findInBackground(new FindCallback<AVUser>() {
				@Override
				public void done(List<AVUser> arg0, AVException arg1) {
					// TODO Auto-generated method stub
					if(arg1==null){
						currentUser = arg0.get(0);
						getListData(currentUser);
					}else{
						Toast.makeText(MyCommentActivity.this, "网络异常", Toast.LENGTH_LONG).show();
					}
				}
			});
		}
	}
 
 
 
}
