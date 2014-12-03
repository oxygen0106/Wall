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
import com.avos.avoscloud.GetCallback;
import com.oxygen.data.ImageLoader;
import com.oxygen.data.ImageLoader.ImageCallback;
import com.oxygen.wall.R;
import com.oxygen.wall.WallListView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

public class ExploreSignUPDataDetailActivity extends Activity {

	private TextView titleText;
	private ImageView backBtn;

	private WallListView listView;
	private List<HashMap<String, String>> listData;
	private SignUpDetailAdapter adapter;
	private SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd-hh:mm");
	private ImageView userImageView;
	private TextView userNameText;
	private TextView timeText;

	private ArrayList<String> userList = new ArrayList<String>();
	private ArrayList<String> timeList = new ArrayList<String>();
	private List<String> usernameList = new ArrayList<String>();
	private List<String> userImageUrlList = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.explore_sign_up_data_detail_activity);
		this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.my_setting_title_bar);

		titleText = (TextView) findViewById(R.id.my_setting_title_tv);
		backBtn = (ImageView) findViewById(R.id.my_setting_title_bar_back_btn);
		titleText.setText("签到");
		setBackBtnListener();

		listView = (WallListView) findViewById(R.id.explore_sign_up_data_lv);
	}

	private void getListData() {
		Intent intent = getIntent();
		String avobjectId = intent.getStringExtra("AVObjectId");

		AVUser user = AVUser.getCurrentUser();
		AVQuery<AVObject> query = AVQuery.getQuery("Attendance");
		query.getInBackground(avobjectId, new GetCallback<AVObject>() {

			@Override
			public void done(AVObject arg0, AVException arg1) {
				// TODO Auto-generated method stub
				if (arg1 == null) {
					timeList = (ArrayList<String>) arg0.getList("timeList");
					usernameList = (ArrayList<String>) arg0
							.getList("usernameList");
					userImageUrlList = (ArrayList<String>) arg0
							.getList("userImageUrlList");

				}
			}
		});
		HashMap<String, String> map = null;
		for (int i = 0; i < userList.size(); i++) {

			map = new HashMap<String, String>();
			map.put("time", timeList.get(i));
			map.put("username", usernameList.get(i));
			map.put("userImageUrl", userImageUrlList.get(i));
			listData.add(map);
		}

		adapter = new SignUpDetailAdapter(ExploreSignUPDataDetailActivity.this,
				listData, listView);
		listView.setAdapter(adapter);

	}

	/**
	 * @ClassName PickAdapter
	 * @Description 内部类Adapter
	 */
	private class SignUpDetailAdapter extends BaseAdapter {

		private List<HashMap<String, String>> list;
		private Context context;
		private int count;
		private ImageLoader imageLoader;
		private ListView listView;

		public SignUpDetailAdapter(Context context,
				List<HashMap<String, String>> list, ListView listView) {
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
			TextView timeText;
			ImageView myImageView;

			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.explore_sign_up_data_detail_lv_item, null);

			}
			timeText = (TextView) convertView
					.findViewById(R.id.explore_sign_up_data_detail_time);
			nameText = (TextView) convertView
					.findViewById(R.id.explore_sign_up_data_detail_user_name);
			nameText = (TextView) convertView
					.findViewById(R.id.explore_sign_up_data_detail_user_name);
			myImageView = (ImageView)findViewById(R.id.explore_sign_up_data_detail_user_image);

			timeText.setText(map.get("time"));
			nameText.setText(map.get("username"));
			
			String userImageUrl = map.get("userImageUrl");
			if (userImageUrl != null && !(userImageUrl.equals(""))) {

				myImageView.setTag(userImageUrl);
				Drawable cachedImage = imageLoader.loadDrawable(userImageUrl,myImageView,
						new ImageCallback() {
							public void imageLoaded(Drawable imageDrawable,
									ImageView imageView) {
								imageView.setImageDrawable(imageDrawable);
							}
						});

				if (cachedImage == null) {
					myImageView.setImageResource(R.drawable.my_image_round);
				} else {
					myImageView.setImageDrawable(cachedImage);
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
				ExploreSignUPDataDetailActivity.this.finish();
			}
		});
	}

}
