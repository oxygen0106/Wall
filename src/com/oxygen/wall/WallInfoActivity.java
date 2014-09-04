package com.oxygen.wall;

import java.util.ArrayList;

import com.oxygen.data.WallComment;
import com.oxygen.data.WallInfo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class WallInfoActivity extends Activity {
	
	private ListView mListView;
	private ItemAdapter mAdapter;
	private WallInfo mWallData;
	private ArrayList<WallComment> mCommentData;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.v(null, "开始");
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);// 自定义标题栏模式
		setContentView(R.layout.wall_comment_activity);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.title_bar);// 加载自定义标题栏
		Log.v(null, "获取Bundle");
		Bundle b=getIntent().getExtras();
		mWallData=(WallInfo)b.get("Info");
		Log.v(null, "绑定数据");
		TextView t=(TextView)findViewById(R.id.wall_item).findViewById(R.id.distance);
	
		mCommentData=new ArrayList<WallComment>();
		mAdapter = new ItemAdapter(this,R.layout.wall_comment_item);
		mListView = (ListView)findViewById(R.id.comment_list);
		mListView.setAdapter(mAdapter);
	}
	
	private class ItemAdapter extends BaseAdapter{
		
		private LayoutInflater mInflater;
		private int mResource;
		private Context mContext;
		
		public ItemAdapter(Context context, int resource) {
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mResource = resource;
			mContext = context;
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mCommentData.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mCommentData.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View v;
			if (convertView == null) {
				v = mInflater.inflate(mResource, parent, false);
			} else {
				v = convertView;
			}
			bindView(position, v);
			return v;
		}
		private void bindView(int position, View view) {
			Log.v(null, "bindView");
			TextView commenterName = ((TextView) view.findViewById(R.id.commenterName));
			commenterName.setText(mCommentData.get(position).getCommenterName());
			TextView commentRank = (TextView) view.findViewById(R.id.commentRank);
			commentRank.setText(String.valueOf(position));
			//添加头像
			//ImageView commenterImage = ((ImageView) view.findViewById(R.id.commenterImage));
			TextView commentContent = (TextView) view.findViewById(R.id.commentContent);
			commentRank.setText(mCommentData.get(position).getCommentContent());
			TextView commentCreateTime = (TextView) view.findViewById(R.id.commentCreateTime);
			commentCreateTime.setText(mCommentData.get(position).getCommentCreateTime());
			
			/*commenterImage.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Log.v("Suport", "click");
				}
			});*/
		}
	} 
}
