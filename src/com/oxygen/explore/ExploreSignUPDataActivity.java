package com.oxygen.explore;

import java.text.SimpleDateFormat;
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
import com.oxygen.my.MySettingActivity;
import com.oxygen.my.MyWallInfoActivity;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ExploreSignUPDataActivity extends Activity {

	private TextView titleText;
	private ImageView backBtn;

	private WallListView listView;
	private List<HashMap<String, String>> listData;
	private SignUpAdapter adapter;
	private SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd-hh:mm");
	private ImageView userImageView;
	private TextView userNameText;
	private TextView timeText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.explore_sign_up_data_activity);
		this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.my_setting_title_bar);

		titleText = (TextView) findViewById(R.id.my_setting_title_tv);
		backBtn = (ImageView) findViewById(R.id.my_setting_title_bar_back_btn);
		titleText.setText("签到记录");
		setBackBtnListener();

		listView = (WallListView) findViewById(R.id.explore_sign_up_data_lv);


		getListData();
	}

	private void getListData() {
		AVUser user = AVUser.getCurrentUser();
		AVQuery<AVObject> query = AVQuery.getQuery("Attendance");
		query.include("user");
		query.whereEqualTo("user", user);
		query.addDescendingOrder("createdAt");
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
						map.put("time", dateFormat.format(avo.getCreatedAt()));
						map.put("count", size + "位");
						map.put("AVObjectId", avo.getObjectId());
						listData.add(map);
					}
					adapter = new SignUpAdapter(ExploreSignUPDataActivity.this,
							listData, listView);
					listView.setAdapter(adapter);
					setListListener();
				}
			}
		});
	}

	/**
	 * @ClassName PickAdapter
	 * @Description 内部类Adapter
	 */
	private class SignUpAdapter extends BaseAdapter {

		private List<HashMap<String, String>> list;
		private Context context;
		private int count;

		public SignUpAdapter(Context context,
				List<HashMap<String, String>> list, ListView listView) {
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
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			LayoutInflater mInflater = LayoutInflater.from(context);
			HashMap<String, String> map = (HashMap<String, String>) list
					.get(position);
			TextView countText;
			TextView timeText;

			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.explore_sign_up_data_lv_item, null);

			}
			timeText = (TextView) convertView
					.findViewById(R.id.explore_sign_up_data_time);
			countText = (TextView) convertView
					.findViewById(R.id.explore_sign_up_data_count);

			timeText.setText(map.get("time"));
			countText.setText(map.get("count"));
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
				ExploreSignUPDataActivity.this.finish();
			}
		});
	}

	private void setListListener() {
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				String avobjectId = listData.get(position).get("AVObjectId");
				Intent intent = new Intent(ExploreSignUPDataActivity.this, ExploreSignUPDataDetailActivity.class);
				intent.putExtra("AVObjectId", avobjectId);
				startActivity(intent);
			}
		});
	}
}
