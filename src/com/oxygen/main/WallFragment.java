package com.oxygen.main;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.oxygen.data.WallInfo;
import com.oxygen.wall.R;
import com.oxygen.wall.WallCommentActivity;
import com.oxygen.wall.WallListView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.Toast;

/**
 * @ClassName WallFragment
 * @Description 留言板
 * @author oxygen
 * @email oxygen0106@163.com
 * @date 2014-8-18 下午09:35:28
 */

public class WallFragment extends Fragment {

	private ArrayList<WallInfo> mData;
	private int mFirstItem;
	private int mLastItem;
	private ItemAdapter mAdapter;
	private WallListView mListView;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v=inflater.inflate(R.layout.wall_fragment, container);
		mListView=(WallListView)v.findViewById(R.id.wall_listview);
		return v;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.v(null, "WallFragment.onActivityCreatedBegin");
		super.onActivityCreated(savedInstanceState);
		init();
	}
	private void init(){
		mData = getData();
		mAdapter = new ItemAdapter(this.getActivity(), R.layout.wall_list_item);
		//listview.setOnScrollListener(this);
		mListView.setAdapter(mAdapter);
		mListView.setonRefreshListener(new WallListView.OnRefreshListener() {
			public void onRefresh() {
				new AsyncTask<Void, Void, Void>() {
					protected Void doInBackground(Void... params) {
						try {
							Thread.sleep(1000);
						} catch (Exception e) {
							e.printStackTrace();
						}
						mData.addAll(getData());
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						mListView.onRefreshComplete();
						mAdapter.notifyDataSetChanged();
						mListView.changeRefreshFlag();
					}

				}.execute();
			}
			public void onIncrease(){
				new AsyncTask<Void, Void, Void>() {
					protected Void doInBackground(Void... params) {
						try {
							Thread.sleep(10000);
						} catch (Exception e) {
							e.printStackTrace();
						}
						mData.addAll(getData());
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						//mListView.onRefreshComplete();
						mAdapter.notifyDataSetChanged();
						mListView.changeIncreaseFlag();
					}

				}.execute();
			}
		});
	}
	private ArrayList<WallInfo> getData() {
		ArrayList<WallInfo> list = new ArrayList<WallInfo>();
		
		WallInfo wallInfo=new WallInfo();
		list.add(wallInfo);
		
		wallInfo=new WallInfo();
		list.add(wallInfo);
		
		wallInfo=new WallInfo();
		list.add(wallInfo);
		
		return list;
	}

	private class ItemAdapter extends BaseAdapter {
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
			return mData.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mData.get(position);
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
			TextView distance = ((TextView) view.findViewById(R.id.distance));
			distance.setText(String.valueOf(mData.get(position).getDistance()));
			LinearLayout support = (LinearLayout) view.findViewById(R.id.support);
			
			support.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Log.v("Suport", "click");
				}
			});
			view.setTag(position);
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					WallInfo w=(WallInfo)mData.get((Integer)v.getTag());

					Intent intent=new Intent(getActivity(),WallCommentActivity.class);
					intent.putExtra("Info", w);
					startActivity(intent);
				}
			});
		}
	}

	/*@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		if (mLastItem == mData.size()
				&& scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			Log.v("state", "onScrollStateChanged");
			Handler mHandler = new Handler();
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mData.addAll(getData());
					mAdapter.notifyDataSetChanged();
				}
			}, 1000);
		}else if(mFirstItem==0){
			
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		mFirstItem=firstVisibleItem;
		mLastItem = firstVisibleItem + visibleItemCount - 1;
		Log.v("onScrollFirstItem", String.valueOf(mFirstItem));
		Log.v("onScrollLastItem", String.valueOf(mLastItem));
	}*/
}
