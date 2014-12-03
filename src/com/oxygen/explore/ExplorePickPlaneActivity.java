package com.oxygen.explore;

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
import com.oxygen.data.ImageLoader;
import com.oxygen.data.UserInfo;
import com.oxygen.data.ImageLoader.ImageCallback;
import com.oxygen.map.GetLocation;
import com.oxygen.my.MyMessageActivity;
import com.oxygen.my.MyMessageAdapter;
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

public class ExplorePickPlaneActivity extends Activity {

	private TextView titleText;
	private ImageView backBtn;

	private String authorName = null;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

	private WallListView listView;
	private List<HashMap<String, String>> listData;
	private PickAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.explore_pick_plane_activity);
		this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.my_setting_title_bar);

		titleText = (TextView) findViewById(R.id.my_setting_title_tv);
		backBtn = (ImageView) findViewById(R.id.my_setting_title_bar_back_btn);
		titleText.setText("捡飞机");
		setBackBtnListener();

		listView = (WallListView) findViewById(R.id.explore_pick_lv);
		listData = new ArrayList<HashMap<String, String>>();
		getListData();
	}

	private void getListData() {
		double mLatitude = GetLocation.mCurrentLantitude;
		double mLongitude = GetLocation.mCurrentLongitude;
		AVUser user = AVUser.getCurrentUser();
		AVQuery<AVObject> query = AVQuery.getQuery("Plane");
		query.include("user");
		query.whereEqualTo("user", user);
		query.whereLessThan("latitude", mLatitude + 10);
		query.whereGreaterThan("latitude", mLatitude-10);
		query.whereLessThan("longitude", mLongitude + 10);
		query.whereGreaterThan("longitude", mLongitude-10);
		query.findInBackground(new FindCallback<AVObject>() {

			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				// TODO Auto-generated method stub
				if (arg1 == null) {
					HashMap<String, String> map = null;
					int size = arg0.size();
					for (int i = 0; i < size; i++) {
						map = new HashMap<String, String>();
						AVObject avo = arg0.get(i);
						AVUser author = avo.getAVUser("user");
						AVFile userImage = author.getAVFile(UserInfo.USER_IMG);
						map.put("username", author.getString("username"));
						map.put("userImageUrl", userImage.getUrl());
						map.put("content", avo.getString("content"));
						map.put("time", dateFormat.format(avo.getCreatedAt()));
						if (!avo.getString("fromAddress").equals("")
								&& avo.getString("fromAddress") != null) {
							map.put("fromAddress", avo.getString("fromAddress"));

						} else {
							map.put("fromAddress", "火星");
						}
						listData.add(map);
					}
					adapter = new PickAdapter(ExplorePickPlaneActivity.this,
							listData, listView);
					listView.setAdapter(adapter);
				}
			}
		});
	}

	/**
	 * @ClassName PickAdapter
	 * @Description 内部类Adapter
	 */
	private class PickAdapter extends BaseAdapter {

		private List<HashMap<String, String>> list;
		private Context context;
		private int type;
		private int count;
		private ImageLoader imageLoader;
		private ListView listView;

		public PickAdapter(Context context, List<HashMap<String, String>> list,
				ListView listView) {
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
			HashMap<String, String> map = (HashMap<String, String>) list
					.get(position);
			TextView nameText;
			TextView contentText;
			TextView timeText;
			TextView fromAddress;
			ImageView myImage;
			ImageView wallImage;

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.explore_pick_lv_item,
						null);

			}
			nameText = (TextView) convertView
					.findViewById(R.id.explore_pick_author_name);
			contentText = (TextView) convertView
					.findViewById(R.id.explore_pick_content);
			timeText = (TextView) convertView
					.findViewById(R.id.explore_pick_time);
			fromAddress = (TextView) convertView
					.findViewById(R.id.explore_pick_fromAddress);
			myImage = (ImageView) convertView
					.findViewById(R.id.explore_pick_author_image);

			nameText.setText(map.get("username"));
			contentText.setText(map.get("content"));
			timeText.setText(map.get("time"));
			fromAddress.setText("来自" + map.get("fromAddress") + "的纸飞机");

			String userImageUrl = map.get("userImageUrl");
			if (userImageUrl != null && !(userImageUrl.equals(""))) {

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
	 * @Description 返回键
	 */
	private void setBackBtnListener() {
		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ExplorePickPlaneActivity.this.finish();
			}
		});
	}
}
